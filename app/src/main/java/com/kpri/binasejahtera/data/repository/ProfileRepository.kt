package com.kpri.binasejahtera.data.repository

import com.kpri.binasejahtera.data.remote.ApiService
import com.kpri.binasejahtera.data.remote.dto.ProfileResponse
import com.kpri.binasejahtera.data.remote.dto.UpdateProfileRequest
import com.kpri.binasejahtera.utils.ApiErrorUtils
import com.kpri.binasejahtera.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val api: ApiService
) {
    // --- Local Cache ---
    private var cachedProfile: ProfileResponse? = null
    fun getProfile(forceUpdate: Boolean = false): Flow<Resource<ProfileResponse>> = flow {

        // chcek cache dulu
        if (!forceUpdate && cachedProfile != null) {
            emit(Resource.Success(cachedProfile!!))
            return@flow
        }

        // ambil api klo kosong
        emit(Resource.Loading())
        try {
            val response = api.getProfile()
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!

                cachedProfile = data

                emit(Resource.Success(data))
            } else {
                val errorMsg = ApiErrorUtils.parseMessage(response.errorBody())
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }

    fun updateProfile(request: UpdateProfileRequest): Flow<Resource<ProfileResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.updateProfile(request)
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!

                cachedProfile = data

                emit(Resource.Success(data))
            } else {
                val errorMsg = ApiErrorUtils.parseMessage(response.errorBody())
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }

    fun uploadPhoto(photo: MultipartBody.Part): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.uploadPhoto(photo)
            if (response.isSuccessful) {
                cachedProfile = null
                emit(Resource.Success(response.body()?.message ?: "Foto berhasil diupload"))
            } else {
                val errorMsg = ApiErrorUtils.parseMessage(response.errorBody())
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }

    fun clearCache() {
        cachedProfile = null
    }
}