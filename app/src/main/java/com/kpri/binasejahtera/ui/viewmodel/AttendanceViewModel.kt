package com.kpri.binasejahtera.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kpri.binasejahtera.data.remote.dto.CashflowRequest
import com.kpri.binasejahtera.data.remote.dto.DepositItemDto
import com.kpri.binasejahtera.data.remote.dto.DepositRequest
import com.kpri.binasejahtera.data.remote.dto.OfficeResponse
import com.kpri.binasejahtera.data.repository.AttendanceRepository
import com.kpri.binasejahtera.data.repository.ProfileRepository
import com.kpri.binasejahtera.data.repository.ReportRepository
import com.kpri.binasejahtera.utils.LocationHelper
import com.kpri.binasejahtera.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

// home/dashboard
data class HomeUiState(
    val greeting: String = "Selamat Datang,",
    val userName: String = "Memuat...",
    val userPhoto: String? = null,
    val currentDate: String = "",
    val checkInTime: String = "--:--:--",
    val checkOutTime: String = "--:--:--",
    val workDuration: String = "0 jam 00 menit",
    val currentAddress: String = "Mencari lokasi...",
    val officeMapsUrl: String? = null,
    val officeAddress: String = "Memuat alamat kantor...",
    val isCheckIn: Boolean = false,
    val isCheckOut: Boolean = false
)

// konfirmasi presensi
data class ConfirmationUiState(
    val userLat: Double = 0.0,
    val userLong: Double = 0.0,
    val officeLat: Double = 0.0,
    val officeLong: Double = 0.0,
    val officeName: String = "Memuat...",
    val officeAddress: String = "...",
    val maxRadius: Double = 50.0,
    val currentDistance: Double = 0.0,
    val isSafe: Boolean = false,
    val isLoadingLocation: Boolean = true,
    val isAlreadyDone: Boolean = false,
    val error: String? = null
)

// nampung data di laporan
data class PendingReportData(
    val pemasukan: Long,
    val pengeluaran: Long,
    val deposits: List<DepositItemDto>
)

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val profileRepository: ProfileRepository,
    private val reportRepository: ReportRepository,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState = _homeState.asStateFlow()

    private val _attendanceEvent = Channel<AttendanceEvent>()
    val attendanceEvent = _attendanceEvent.receiveAsFlow()

    private val _confirmationState = MutableStateFlow(ConfirmationUiState())
    val confirmationState = _confirmationState.asStateFlow()

    private var durationJob: Job? = null

    private var cachedOfficeLocation: OfficeResponse? = null

    private var pendingReportData: PendingReportData? = null

    init {
        loadInitialData()
        startRealtimeUpdate()
    }

    private fun loadInitialData() {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        _homeState.value = _homeState.value.copy(
            currentDate = dateFormat.format(Date())
        )
        updateGreetingAndDate()
        loadProfile()
        loadDashboardData()
        loadUserLocation()
    }

    private fun loadUserLocation() {
        viewModelScope.launch {
            try {
                val location = locationHelper.getCurrentLocation()

                if (location != null) {
                    val address = locationHelper.getAddressName(location.latitude, location.longitude)

                    _homeState.value = _homeState.value.copy(
                        currentAddress = address ?: "Alamat tidak ditemukan"
                    )
                } else {
                    _homeState.value = _homeState.value.copy(
                        currentAddress = "Gagal memuat GPS (Pastikan GPS aktif)"
                    )
                }
            } catch (e: Exception) {
                _homeState.value = _homeState.value.copy(
                    currentAddress = "Gagal memuat lokasi"
                )
            }
        }
    }

    private fun updateGreetingAndDate() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val currentTime = hour + (minute / 60.0)

        val greetingText = when {
            currentTime in 3.0..<11.5 -> "Selamat Pagi,"
            currentTime in 11.5..<15.0 -> "Selamat Siang,"
            currentTime in 15.0..<18.0 -> "Selamat Sore,"
            else -> "Selamat Malam,"
        }

        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))

        _homeState.value = _homeState.value.copy(
            greeting = greetingText,
            currentDate = dateFormat.format(calendar.time)
        )
    }

    private fun startRealtimeUpdate() {
        viewModelScope.launch {
            while(isActive) {
                updateGreetingAndDate()
                delay(60000)
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            profileRepository.getProfile().collect { result ->
                if (result is Resource.Success) {
                    val data = result.data
                    _homeState.value = _homeState.value.copy(
                        userName = data?.name ?: "User KPRI",
                        userPhoto = data?.profileImage
                    )
                }
            }
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            // ngecek klo cache kosong baru request API
            if (cachedOfficeLocation == null) {
                attendanceRepository.getOfficeLocation().collect { result ->
                    if (result is Resource.Success) {
                        cachedOfficeLocation = result.data
                        _homeState.value = _homeState.value.copy(
                            officeAddress = result.data?.address ?: "Lokasi kantor tidak ditemukan",
                            officeMapsUrl = result.data?.mapsUrl
                        )
                    }
                }
            } else {
                // pake data cache
                _homeState.value = _homeState.value.copy(
                    officeAddress = cachedOfficeLocation?.address ?: "Lokasi kantor tidak ditemukan"
                )
            }

            // status presensi hari ini
            attendanceRepository.getAttendanceStatus().collect { result ->
                if (result is Resource.Success) {
                    val data = result.data
                    val jamMasuk = data?.jamMasuk ?: "--:--:--"
                    val jamPulang = data?.jamPulang ?: "--:--:--"

                    val sudahMasuk = data?.sudahMasuk ?: false
                    val sudahPulang = data?.sudahPulang ?: false

                    _homeState.value = _homeState.value.copy(
                        checkInTime = jamMasuk,
                        checkOutTime = jamPulang,
                        isCheckIn = sudahMasuk && !sudahPulang,
                        isCheckOut = sudahPulang,
                        workDuration = data?.workDurationText ?: "0 jam 00 menit"
                    )

                    // jika user sedang kerja, nyalakan timer lokal
                    if (sudahMasuk && !sudahPulang && data?.jamMasuk != null) {
                        startDurationTimer(data.jamMasuk)
                    } else {
                        durationJob?.cancel() // stop timer jika sudah pulang/belum masuk
                    }
                }
            }
        }
    }

    private fun startDurationTimer(startTimeStr: String) {
        durationJob?.cancel()
        durationJob = viewModelScope.launch {
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

            val startTime = try {
                timeFormat.parse(startTimeStr)?.time ?: return@launch
            } catch (e: Exception) { return@launch }

            while (isActive) {
                val now = System.currentTimeMillis()
                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val fullFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                try {
                    // penggabungan tanggal hari ini + jam masuk buat timestamp yg akurat
                    val startDateTime = fullFormat.parse("$todayStr $startTimeStr")?.time ?: now
                    val diff = now - startDateTime

                    if (diff > 0) {
                        val hours = diff / (1000 * 60 * 60)
                        val minutes = (diff / (1000 * 60)) % 60
                        // ini klo mau nambah detiknya.
                        // val seconds = (diff / 1000) % 60

                        _homeState.value = _homeState.value.copy(
                            workDuration = "$hours jam $minutes menit"
                        )
                    } else {
                        // fallback klo semisal diff negatif (misal jam di hp nya user ngaco sedikit)
                        _homeState.value = _homeState.value.copy(
                            workDuration = "0 jam 0 menit"
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // bisa diubah ke 1000 klo mau live per satu detik
                delay(60000)
            }
        }
    }

    fun initPresenceConfirmation(isCheckInTarget: Boolean) {
        viewModelScope.launch {
            _confirmationState.value = _confirmationState.value.copy(
                isLoadingLocation = true,
                error = null
            )

            // cek status presensi
            launch {
                attendanceRepository.getAttendanceStatus().collect { result ->
                    if (result is Resource.Success) {
                        val data = result.data

                        val alreadyDone = if (isCheckInTarget) {
                            data?.sudahMasuk == true
                        } else {
                            data?.sudahPulang == true
                        }

                        _confirmationState.value = _confirmationState.value.copy(
                            isAlreadyDone = alreadyDone
                        )
                    }
                }
            }

            launch {
                val office = cachedOfficeLocation
                if (office != null) {
                    setupOfficeLocation(office)
                } else {
                    attendanceRepository.getOfficeLocation().collect { result ->
                        if (result is Resource.Success && result.data != null) {
                            cachedOfficeLocation = result.data
                            setupOfficeLocation(result.data)
                        } else {
                            _confirmationState.value = _confirmationState.value.copy(
                                isLoadingLocation = false,
                                error = result.message ?: "Gagal memuat lokasi kantor"
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun setupOfficeLocation(office: OfficeResponse) {
        val offLat = office.latitude.toDoubleOrNull() ?: 0.0
        val offLong = office.longitude.toDoubleOrNull() ?: 0.0

        _confirmationState.value = _confirmationState.value.copy(
            officeLat = offLat,
            officeLong = offLong,
            officeName = office.name,
            officeAddress = office.address,
            maxRadius = office.maxDistance.toDouble(),
            error = null
        )
        // lokasi hp user
        getUserLocation()
    }

    private suspend fun getUserLocation() {
        val location = locationHelper.getCurrentLocation()
        if (location != null) {
            val userLat = location.latitude
            val userLong = location.longitude

            // hitung jarak dari user ke kantor
            val officeLat = _confirmationState.value.officeLat
            val officeLong = _confirmationState.value.officeLong
            val radius = _confirmationState.value.maxRadius

            val distance = locationHelper.calculateDistance(
                userLat, userLong, officeLat, officeLong
            )

            val isSafe = distance <= radius

            _confirmationState.value = _confirmationState.value.copy(
                userLat = userLat,
                userLong = userLong,
                currentDistance = distance.toDouble(),
                isSafe = isSafe,
                isLoadingLocation = false,
                error = null
            )
        } else {
            _confirmationState.value = _confirmationState.value.copy(
                isLoadingLocation = false,
                error = "Gagal mendapatkan lokasi GPS. Pastikan GPS aktif."
            )
        }
    }

    fun setPendingReport(pemasukan: String, pengeluaran: String, deposits: List<DepositItemDto>) {
        val cleanPemasukan = pemasukan.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L
        val cleanPengeluaran = pengeluaran.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L

        pendingReportData = PendingReportData(
            pemasukan = cleanPemasukan,
            pengeluaran = cleanPengeluaran,
            deposits = deposits
        )
    }

    fun performAttendance(isCheckIn: Boolean) {
        val lat = _confirmationState.value.userLat
        val long = _confirmationState.value.userLong

        viewModelScope.launch {
            _isLoading.value = true

            if (!isCheckIn && pendingReportData != null) {
                val report = pendingReportData!!
                var isReportFailed = false

                // kirim cashflow
                val cashflowReq = CashflowRequest(report.pemasukan, report.pengeluaran)
                reportRepository.sendCashflow(cashflowReq).collect { res ->
                    if (res is Resource.Error) {
                        _attendanceEvent.send(AttendanceEvent.Error("Gagal kirim keuangan: ${res.message}"))
                        isReportFailed = true
                    }
                }
                if (isReportFailed) {
                    _isLoading.value = false
                    return@launch
                }

                // kirim deposit
                if (report.deposits.isNotEmpty()) {
                    val depositReq = DepositRequest(report.deposits)
                    reportRepository.sendDeposits(depositReq).collect { res ->
                        if (res is Resource.Error) {
                            _attendanceEvent.send(AttendanceEvent.Error("Gagal kirim setoran: ${res.message}"))
                            isReportFailed = true
                        }
                    }
                }
                if (isReportFailed) {
                    _isLoading.value = false
                    return@launch
                }
            }

            val flow = if (isCheckIn) {
                attendanceRepository.checkIn(lat, long)
            } else {
                attendanceRepository.checkOut(lat, long)
            }

            flow.collect { result ->
                _isLoading.value = false
                when (result) {
                    is Resource.Success -> {
                        val data = result.data
                        val type = if (isCheckIn) "Masuk" else "Pulang"
                        val msg = "Berhasil $type pukul ${data?.time} (Jarak: ${data?.distance}m)"
                        pendingReportData = null

                        _attendanceEvent.send(AttendanceEvent.Success(msg))
                        loadDashboardData()
                    }
                    is Resource.Error -> {
                        _attendanceEvent.send(AttendanceEvent.Error(result.message ?: "Gagal presensi"))
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        durationJob?.cancel()
    }

    sealed class AttendanceEvent {
        data class Success(val message: String) : AttendanceEvent()
        data class Error(val message: String) : AttendanceEvent()
    }
}