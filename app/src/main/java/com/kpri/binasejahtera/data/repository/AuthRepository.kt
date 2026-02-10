package com.kpri.binasejahtera.data.repository

import com.google.gson.Gson
import com.kpri.binasejahtera.data.local.TokenManager
import com.kpri.binasejahtera.data.remote.ApiService
import com.kpri.binasejahtera.data.remote.dto.BaseResponse
import com.kpri.binasejahtera.data.remote.dto.ChangePasswordRequest
import com.kpri.binasejahtera.data.remote.dto.GoogleLoginRequest
import com.kpri.binasejahtera.data.remote.dto.LoginRequest
import com.kpri.binasejahtera.data.remote.dto.LoginResponse
import com.kpri.binasejahtera.utils.ApiErrorUtils
import com.kpri.binasejahtera.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager,
    private val attendanceRepository: AttendanceRepository,
    private val profileRepository: ProfileRepository
) {

    // fungsi helper untuk parse error
    private fun parseError(errorBody: ResponseBody?): String {
        return try {
            val errorJson = errorBody?.string()
            if (!errorJson.isNullOrEmpty()) {
                val gson = Gson()
                val type = object : com.google.gson.reflect.TypeToken<BaseResponse<Any>>() {}.type
                val parsedResponse: BaseResponse<Any> = gson.fromJson(errorJson, type)
                parsedResponse.message
            } else {
                "Terjadi kesalahan pada server"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Gagal memproses respon server"
        }
    }

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
                val errorMsg = ApiErrorUtils.parseMessage(response.errorBody())
                emit(Resource.Error(errorMsg))
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
                val errorMsg = ApiErrorUtils.parseMessage(response.errorBody())
                emit(Resource.Error(errorMsg))
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
                attendanceRepository.clearCache()
                profileRepository.clearCache()
                emit(Resource.Success("Berhasil Logout"))
            } else {
                val errorMsg = ApiErrorUtils.parseMessage(response.errorBody())
                emit(Resource.Error(errorMsg))
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
                val errorMsg = ApiErrorUtils.parseMessage(response.errorBody())
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }
}