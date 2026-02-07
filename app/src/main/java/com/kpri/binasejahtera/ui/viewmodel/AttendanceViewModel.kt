package com.kpri.binasejahtera.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kpri.binasejahtera.data.remote.dto.CheckInRequest
import com.kpri.binasejahtera.data.remote.dto.CheckOutRequest
import com.kpri.binasejahtera.data.remote.dto.HomeDataResponse
import com.kpri.binasejahtera.data.remote.dto.OfficeResponse
import com.kpri.binasejahtera.data.repository.AttendanceRepository
import com.kpri.binasejahtera.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: AttendanceRepository
) : ViewModel() {

    private val _homeData = MutableStateFlow<HomeDataResponse?>(null)
    val homeData = _homeData.asStateFlow()

    private val _officeLocation = MutableStateFlow<OfficeResponse?>(null)
    val officeLocation = _officeLocation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _attendanceEvent = Channel<AttendanceEvent>()
    val attendanceEvent = _attendanceEvent.receiveAsFlow()

    fun loadHomeData() {
        viewModelScope.launch {
            repository.getHomeData().collect { result ->
                when (result) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        _homeData.value = result.data
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _attendanceEvent.send(AttendanceEvent.Error(result.message ?: "Gagal memuat data home"))
                    }
                }
            }
        }
    }

    fun loadOfficeLocation() {
        viewModelScope.launch {
            repository.getOfficeLocation().collect { result ->
                when (result) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        _officeLocation.value = result.data
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _attendanceEvent.send(AttendanceEvent.Error(result.message ?: "Gagal memuat lokasi kantor"))
                    }
                }
            }
        }
    }

    fun checkIn(lat: Double, long: Double, address: String) {
        viewModelScope.launch {
            repository.checkIn(CheckInRequest(lat, long, address)).collect { result ->
                when (result) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        _attendanceEvent.send(AttendanceEvent.Success("Presensi masuk berhasil!"))
                        loadHomeData()
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _attendanceEvent.send(AttendanceEvent.Error(result.message ?: "Gagal presensi"))
                    }
                }
            }
        }
    }

    fun checkOut(lat: Double, long: Double, address: String) {
        viewModelScope.launch {
            repository.checkOut(CheckOutRequest(lat, long, address)).collect { result ->
                when (result) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        _attendanceEvent.send(AttendanceEvent.Success("Presensi pulang berhasil!"))
                        loadHomeData()
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