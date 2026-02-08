package com.kpri.binasejahtera.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kpri.binasejahtera.data.local.TokenManager
import com.kpri.binasejahtera.data.remote.dto.ChangePasswordRequest
import com.kpri.binasejahtera.data.remote.dto.GoogleLoginRequest
import com.kpri.binasejahtera.data.remote.dto.LoginRequest
import com.kpri.binasejahtera.data.repository.AuthRepository
import com.kpri.binasejahtera.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _authEvent = Channel<AuthEvent>()
    val authEvent = _authEvent.receiveAsFlow()

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn = _isUserLoggedIn.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val token = tokenManager.token.first()
            _isUserLoggedIn.value = !token.isNullOrEmpty()
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            repository.login(LoginRequest(email, pass)).collect { result ->
                handleAuthResult(result, "Login berhasil")
            }
        }
    }

    fun googleLogin(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val request = GoogleLoginRequest(idToken = idToken)

            repository.googleLogin(request).collect { result ->
                handleAuthResult(result, "Login Google berhasil")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout().collect { result ->
                when (result) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        _isUserLoggedIn.value = false
                        _authEvent.send(AuthEvent.Success("Berhasil logout"))
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        // force logout di UI meski API error
                        _isUserLoggedIn.value = false
                        _authEvent.send(AuthEvent.Error(result.message ?: "Gagal logout"))
                    }
                }
            }
        }
    }

    fun changePassword(current: String, new: String, confirm: String) {
        viewModelScope.launch {
            repository.changePassword(ChangePasswordRequest(current, new, confirm)).collect { result ->
                when (result) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        _authEvent.send(AuthEvent.Success(result.data ?: "Password berhasil diubah"))
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _authEvent.send(AuthEvent.Error(result.message ?: "Gagal ubah password"))
                    }
                }
            }
        }
    }

    // Helper untuk handle hasil dari API saat login biasa & google
    private suspend fun <T> handleAuthResult(result: Resource<T>, successMsg: String) {
        when (result) {
            is Resource.Loading -> _isLoading.value = true
            is Resource.Success -> {
                _isLoading.value = false
                _isUserLoggedIn.value = true
                _authEvent.send(AuthEvent.Success(successMsg))
            }
            is Resource.Error -> {
                _isLoading.value = false
                _authEvent.send(AuthEvent.Error(result.message ?: "Terjadi kesalahan"))
            }
        }
    }

    sealed class AuthEvent {
        data class Success(val message: String) : AuthEvent()
        data class Error(val message: String) : AuthEvent()
    }
}