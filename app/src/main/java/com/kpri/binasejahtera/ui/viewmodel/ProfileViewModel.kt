package com.kpri.binasejahtera.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kpri.binasejahtera.data.remote.dto.ProfileResponse
import com.kpri.binasejahtera.data.remote.dto.UpdateProfileRequest
import com.kpri.binasejahtera.data.repository.ProfileRepository
import com.kpri.binasejahtera.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileResponse?>(null)
    val profileState = _profileState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _profileEvent = Channel<ProfileEvent>()
    val profileEvent = _profileEvent.receiveAsFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            repository.getProfile().collect { result ->
                when (result) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        _profileState.value = result.data
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _profileEvent.send(ProfileEvent.Error(result.message ?: "Gagal memuat profil"))
                    }
                }
            }
        }
    }

    fun updateProfile(name: String, email: String, username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val request = UpdateProfileRequest(name, email, username)

            repository.updateProfile(request).collect { result ->
                when (result) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        _profileState.value = result.data
                        _profileEvent.send(ProfileEvent.Success("Profil berhasil diperbarui"))
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _profileEvent.send(ProfileEvent.Error(result.message ?: "Gagal update profil"))
                    }
                }
            }
        }
    }

    fun uploadPhoto(photo: MultipartBody.Part) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.uploadPhoto(photo).collect { result ->
                when (result) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        _profileEvent.send(ProfileEvent.Success("Foto berhasil diupload"))
                        loadProfile()
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _profileEvent.send(ProfileEvent.Error(result.message ?: "Gagal upload foto"))
                    }
                }
            }
        }
    }

    sealed class ProfileEvent {
        data class Success(val message: String) : ProfileEvent()
        data class Error(val message: String) : ProfileEvent()
    }
}