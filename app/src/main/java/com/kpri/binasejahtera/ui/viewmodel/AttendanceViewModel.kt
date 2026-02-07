package com.kpri.binasejahtera.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kpri.binasejahtera.data.repository.AttendanceRepository
import com.kpri.binasejahtera.data.repository.ProfileRepository
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

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState = _homeState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _attendanceEvent = Channel<AttendanceEvent>()
    val attendanceEvent = _attendanceEvent.receiveAsFlow()

    private var durationJob: Job? = null

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
            attendanceRepository.getOfficeLocation().collect { result ->
                if (result is Resource.Success) {
                    _homeState.value = _homeState.value.copy(
                        officeAddress = result.data?.address ?: "Lokasi kantor tidak ditemukan"
                    )
                }
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

    fun checkIn(lat: Double, long: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            attendanceRepository.checkIn(lat, long).collect { result ->
                when (result) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        _attendanceEvent.send(AttendanceEvent.Success("Presensi masuk berhasil!"))
                        loadDashboardData()
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _attendanceEvent.send(AttendanceEvent.Error(result.message ?: "Gagal presensi"))
                    }
                }
            }
        }
    }

    fun checkOut(lat: Double, long: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            attendanceRepository.checkOut(lat, long).collect { result ->
                when (result) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        _attendanceEvent.send(AttendanceEvent.Success("Presensi pulang berhasil!"))
                        loadDashboardData()
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _attendanceEvent.send(AttendanceEvent.Error(result.message ?: "Gagal presensi"))
                    }
                }
            }
        }
    }

    sealed class AttendanceEvent {
        data class Success(val message: String) : AttendanceEvent()
        data class Error(val message: String) : AttendanceEvent()
    }
}