package com.kpri.binasejahtera.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kpri.binasejahtera.data.remote.dto.OfficeResponse
import com.kpri.binasejahtera.data.repository.AttendanceRepository
import com.kpri.binasejahtera.data.repository.ProfileRepository
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
import java.util.Date
import java.util.Locale
import javax.inject.Inject

// home/dashboard
data class HomeUiState(
    val userName: String = "Memuat...",
    val userPhoto: String? = null,
    val currentDate: String = "",
    val checkInTime: String = "--:--:--",
    val checkOutTime: String = "--:--:--",
    val workDuration: String = "0 jam 00 menit",
    val currentAddress: String = "Mencari lokasi...",
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
    val maxRadius: Double = 50.0, // max radius 50m
    val currentDistance: Double = 0.0,
    val isSafe: Boolean = false,
    val isLoadingLocation: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val profileRepository: ProfileRepository,
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

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        _homeState.value = _homeState.value.copy(
            currentDate = dateFormat.format(Date())
        )
        loadProfile()
        loadDashboardData()
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
                            officeAddress = result.data?.address ?: "Lokasi kantor tidak ditemukan"
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

    fun updateUserLocation(address: String) {
        _homeState.value = _homeState.value.copy(currentAddress = address)
    }

    private fun startDurationTimer(startTimeStr: String) {
        durationJob?.cancel()
        durationJob = viewModelScope.launch {
            val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val startTime = try {
                format.parse(startTimeStr)?.time ?: return@launch
            } catch (e: Exception) { return@launch }

            while (isActive) {
                val now = System.currentTimeMillis()
                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val fullFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                try {
                    val startMillis = fullFormat.parse("$todayStr $startTimeStr")?.time ?: now
                    val diff = now - startMillis

                    if (diff > 0) {
                        val hours = diff / (1000 * 60 * 60)
                        val minutes = (diff / (1000 * 60)) % 60
                        _homeState.value = _homeState.value.copy(
                            workDuration = "$hours jam $minutes menit"
                        )
                    }
                } catch (e: Exception) {
                    // fallback jika parsing gagal
                }

                delay(60000)
            }
        }
    }

    fun initPresenceConfirmation() {
        viewModelScope.launch {
            _confirmationState.value = _confirmationState.value.copy(isLoadingLocation = true)

            // ambil data kantor terutama dari cache. klo cache null baru fetch ulang
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

    private suspend fun setupOfficeLocation(office: OfficeResponse) {
        val offLat = office.latitude.toDoubleOrNull() ?: 0.0
        val offLong = office.longitude.toDoubleOrNull() ?: 0.0

        _confirmationState.value = _confirmationState.value.copy(
            officeLat = offLat,
            officeLong = offLong,
            officeName = office.name,
            officeAddress = office.address,
            maxRadius = office.maxDistance.toDouble()
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
                isLoadingLocation = false
            )
        } else {
            _confirmationState.value = _confirmationState.value.copy(
                isLoadingLocation = false,
                error = "Gagal mendapatkan lokasi GPS. Pastikan GPS aktif."
            )
        }
    }

    fun performAttendance(isCheckIn: Boolean) {
        val lat = _confirmationState.value.userLat
        val long = _confirmationState.value.userLong

        viewModelScope.launch {
            _isLoading.value = true

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