package com.kpri.binasejahtera.data.repository

import com.kpri.binasejahtera.data.remote.ApiService
import com.kpri.binasejahtera.data.remote.dto.ProfileResponse
import com.kpri.binasejahtera.data.remote.dto.UpdateProfileRequest
import com.kpri.binasejahtera.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: ApiService
) {
    fun getProfile(): Flow<Resource<ProfileResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getProfile()
            if (response.isSuccessful && response.body()?.data != null) {
                emit(Resource.Success(response.body()!!.data!!))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal memuat profil"))
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
                emit(Resource.Success(response.body()!!.data!!))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal update profil"))
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
                emit(Resource.Success(response.body()?.message ?: "Foto berhasil diupload"))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal upload foto"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }
}