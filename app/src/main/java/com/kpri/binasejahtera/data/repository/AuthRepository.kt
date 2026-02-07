package com.kpri.binasejahtera.data.repository

import com.kpri.binasejahtera.data.local.TokenManager
import com.kpri.binasejahtera.data.remote.ApiService
import com.kpri.binasejahtera.data.remote.dto.ChangePasswordRequest
import com.kpri.binasejahtera.data.remote.dto.GoogleLoginRequest
import com.kpri.binasejahtera.data.remote.dto.LoginRequest
import com.kpri.binasejahtera.data.remote.dto.LoginResponse
import com.kpri.binasejahtera.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {

    // login kredensial biasa
    fun login(request: LoginRequest): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.login(request)
            val result = response.body()
            if (response.isSuccessful && result != null && result.data != null) {
                tokenManager.saveToken(result.data.token)
                emit(Resource.Success(result.data))
            } else {
                emit(Resource.Error(result?.message ?: "Login gagal"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    // login dengan akun google
    fun googleLogin(request: GoogleLoginRequest): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.googleLogin(request)
            val result = response.body()
            if (response.isSuccessful && result != null && result.data != null) {
                tokenManager.saveToken(result.data.token)
                emit(Resource.Success(result.data))
            } else {
                emit(Resource.Error(result?.message ?: "Google login gagal"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    // logout
    fun logout(): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.logout()
            if (response.isSuccessful) {
                tokenManager.clearToken()
                emit(Resource.Success("Berhasil Logout"))
            } else {
                emit(Resource.Error("Gagal Logout"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }

    // ubah pass
    fun changePassword(request: ChangePasswordRequest): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.changePassword(request)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()?.message ?: "Password berhasil diubah"))
            } else {
                val msg = response.body()?.message ?: "Gagal mengubah password"
                emit(Resource.Error(msg))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }
}