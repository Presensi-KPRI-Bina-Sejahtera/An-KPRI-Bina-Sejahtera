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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

// untuk draft laporan keuangan/daily report (biar data ngga ilang klo mencet back)
data class ReportDraft(
    val pemasukan: String = "",
    val keterangan_pemasukan: String? = null,
    val pengeluaran: String = "",
    val keterangan_pengeluaran: String? = null,
    val deposits: List<DepositDraftItem> = emptyList()
)

data class DepositDraftItem(
    val id: Long = System.currentTimeMillis(),
    var name: String = "",
    var amount: String = "",
    var isSimpanan: Boolean = true
)

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

// status pengiriman laporan (mitigasi sinyal ngilang)
data class UploadStatus(
    var isCashflowSent: Boolean = false,
    var isDepositSent: Boolean = false
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

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState = _homeState.asStateFlow()

    private val _attendanceEvent = Channel<AttendanceEvent>()
    val attendanceEvent = _attendanceEvent.receiveAsFlow()

    private val _confirmationState = MutableStateFlow(ConfirmationUiState())
    val confirmationState = _confirmationState.asStateFlow()

    private val _reportDraft = MutableStateFlow(ReportDraft())
    val reportDraft = _reportDraft.asStateFlow()

    // mencegah memory leak/dupe process
    private var durationJob: Job? = null
    private var homeDataJob: Job? = null
    private var profileJob: Job? = null
    private var locationJob: Job? = null

    private var cachedOfficeLocation: OfficeResponse? = null
    private var uploadStatus = UploadStatus()

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
        loadHomeData()
        loadUserLocation()
    }

    // pull to refresh dengan timout biar ngga stuck loading
    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                withTimeoutOrNull(5000) {
                    val jobs = listOfNotNull(
                        loadUserLocation(),
                        loadProfile(),
                        loadHomeData(isRefresh = true)
                    )
                    joinAll(*jobs.toTypedArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun loadUserLocation(): Job {
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
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
        return locationJob!!
    }

    private fun updateGreetingAndDate() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val currentTime = hour + (minute / 60.0)
        val greetingText = when (currentTime) {
            in 3.0..<11.5 -> "Selamat Pagi,"
            in 11.5..<15.0 -> "Selamat Siang,"
            in 15.0..<18.0 -> "Selamat Sore,"
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

    private fun loadProfile(): Job {
        profileJob?.cancel()
        profileJob = viewModelScope.launch {
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
        return profileJob!!
    }

    fun loadHomeData(isRefresh: Boolean = false): Job {
        homeDataJob?.cancel()
        homeDataJob = viewModelScope.launch {
            launch {
                attendanceRepository.getOfficeLocation(forceUpdate = isRefresh).collect { result ->
                    if (result is Resource.Success) {
                        _homeState.value = _homeState.value.copy(
                            officeAddress = result.data?.address ?: "Lokasi kantor tidak ditemukan",
                            officeMapsUrl = result.data?.mapsUrl
                        )
                    }
                }
            }

            // status presensi hari ini
            launch {
                attendanceRepository.getAttendanceStatus(forceUpdate = isRefresh).collect { result ->
                    if (result is Resource.Success) {
                        val data = result.data

                        _homeState.value = _homeState.value.copy(
                            checkInTime = data?.jamMasuk ?: "--:--:--",
                            checkOutTime = data?.jamPulang ?: "--:--:--",
                            isCheckIn = data?.sudahMasuk == true && !data.sudahPulang,
                            isCheckOut = data?.sudahPulang == true,
                            workDuration = data?.workDurationText ?: "0 jam 00 menit"
                        )

                        // jika user sedang kerja, nyalakan timer lokal
                        if (data?.sudahMasuk == true && !data.sudahPulang && data.jamMasuk != null) {
                            startDurationTimer(data.jamMasuk)
                        } else {
                            durationJob?.cancel()
                        }
                    }
                }
            }
        }
        return homeDataJob!!
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

            if(isCheckInTarget) {
                uploadStatus = UploadStatus()
            }

            // cek status presensi
            launch {
                attendanceRepository.getAttendanceStatus(forceUpdate = false).collect { result ->
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
                    attendanceRepository.getOfficeLocation(forceUpdate = false).collect { result ->
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

    fun refreshUserLocation() {
        viewModelScope.launch {
            getUserLocation()
        }
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

    // refresh data untuk presensi (untuk tombol refresh)
    fun refreshPresenceData(isCheckIn: Boolean) {
        viewModelScope.launch {
            _confirmationState.update { it.copy(
                isLoadingLocation = true,
                officeName = "Memuat data...",
                officeAddress = "Sedang memperbarui info...",
                error = null
            )}

            val statusJob = launch {
                attendanceRepository.getAttendanceStatus(forceUpdate = true).collect { result ->
                    if (result is Resource.Success) {
                        val data = result.data
                        val alreadyDone = if (isCheckIn) data?.sudahMasuk == true else data?.sudahPulang == true
                        _confirmationState.update { it.copy(isAlreadyDone = alreadyDone) }
                    }
                }
            }

            val officeJob = launch {
                attendanceRepository.getOfficeLocation(forceUpdate = true).collect { result ->
                    if (result is Resource.Success && result.data != null) {
                        val office = result.data
                        cachedOfficeLocation = office

                        _confirmationState.update { it.copy(
                            officeLat = office.latitude.toDoubleOrNull() ?: 0.0,
                            officeLong = office.longitude.toDoubleOrNull() ?: 0.0,
                            officeName = office.name,
                            officeAddress = office.address,
                            maxRadius = office.maxDistance.toDouble()
                        )}
                    } else if (result is Resource.Error) {
                        _confirmationState.update { it.copy(error = result.message) }
                    }
                }
            }

            joinAll(statusJob, officeJob)

            getUserLocation()
        }
    }

    // simpen draft dailyreport
    fun updateReportDraft(
        pemasukan: String,
        keteranganPemasukan: String?,
        pengeluaran: String,
        keteranganPengeluaran: String?,
        deposits: List<DepositDraftItem>
    ) {
        _reportDraft.value = ReportDraft(
            pemasukan = pemasukan,
            keterangan_pemasukan = keteranganPemasukan,
            pengeluaran = pengeluaran,
            keterangan_pengeluaran = keteranganPengeluaran,
            deposits = deposits
        )
    }

    fun performAttendance(isCheckIn: Boolean) {
        val lat = _confirmationState.value.userLat
        val long = _confirmationState.value.userLong

        viewModelScope.launch {
            _isLoading.value = true

            if (!isCheckIn) {
                val report = _reportDraft.value
                val cleanPemasukan = report.pemasukan.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L
                val cleanPengeluaran = report.pengeluaran.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L

                if (!uploadStatus.isCashflowSent) {
                    var isCashflowFailed = false
                    val cashflowReq = CashflowRequest(
                        pemasukan = cleanPemasukan,
                        keterangan_pemasukan = if (report.keterangan_pemasukan.isNullOrBlank()) null else report.keterangan_pemasukan,
                        pengeluaran = cleanPengeluaran,
                        keterangan_pengeluaran = if (report.keterangan_pengeluaran.isNullOrBlank()) null else report.keterangan_pengeluaran
                    )

                    reportRepository.sendCashflow(cashflowReq).collect { res ->
                        if (res is Resource.Error) {
                            _attendanceEvent.send(AttendanceEvent.Error("Gagal kirim keuangan: ${res.message}. Silakan coba lagi."))
                            isCashflowFailed = true
                        } else if (res is Resource.Success) {
                            uploadStatus.isCashflowSent = true
                        }
                    }
                    if (isCashflowFailed) {
                        _isLoading.value = false
                        return@launch
                    }
                }

                if (!uploadStatus.isDepositSent) {
                    val validDeposits = report.deposits.filter {
                        it.name.isNotBlank() && it.amount.isNotBlank()
                    }.map { item ->
                        DepositItemDto(
                            memberName = item.name,
                            type = if (item.isSimpanan) "simpanan" else "angsuran",
                            amount = item.amount.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L
                        )
                    }

                    if (validDeposits.isNotEmpty()) {
                        var isDepositFailed = false
                        val depositReq = DepositRequest(validDeposits)
                        reportRepository.sendDeposits(depositReq).collect { res ->
                            if (res is Resource.Error) {
                                _attendanceEvent.send(AttendanceEvent.Error("Gagal kirim setoran: ${res.message}. Keuangan tersimpan, coba lagi untuk deposit & presensi."))
                                isDepositFailed = true
                            } else if (res is Resource.Success) {
                                uploadStatus.isDepositSent = true
                            }
                        }
                        if (isDepositFailed) {
                            _isLoading.value = false
                            return@launch
                        }
                    } else {
                        uploadStatus.isDepositSent = true
                    }
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
                        val msg = "Berhasil $type pukul ${data?.time}"

                        // reset status draft & upload flag setelah sukses total
                        if(!isCheckIn) {
                            uploadStatus = UploadStatus()
                            _reportDraft.value = ReportDraft()
                        }

                        _attendanceEvent.send(AttendanceEvent.Success(msg))
                        loadHomeData(isRefresh = true)
                    }
                    is Resource.Error -> {
                        val errorMsg = if (!isCheckIn && uploadStatus.isCashflowSent) {
                            "Laporan Keuangan MASUK, tapi Presensi GAGAL: ${result.message}. Silakan tekan tombol Presensi lagi."
                        } else {
                            result.message ?: "Gagal presensi"
                        }
                        _attendanceEvent.send(AttendanceEvent.Error(errorMsg))
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