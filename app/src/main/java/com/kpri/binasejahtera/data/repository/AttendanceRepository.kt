package com.kpri.binasejahtera.data.repository

import com.kpri.binasejahtera.data.remote.ApiService
import com.kpri.binasejahtera.data.remote.dto.*
import com.kpri.binasejahtera.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AttendanceRepository @Inject constructor(
    private val api: ApiService
) {
    fun getHomeData(): Flow<Resource<HomeDataResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getHomeData()
            if (response.isSuccessful && response.body()?.data != null) {
                emit(Resource.Success(response.body()!!.data!!))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal memuat data home"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }

    fun getOfficeLocation(): Flow<Resource<OfficeResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getOfficeLocation()
            if (response.isSuccessful && response.body()?.data != null) {
                emit(Resource.Success(response.body()!!.data!!))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal memuat lokasi kantor"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }

    fun checkIn(request: CheckInRequest): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.checkIn(request)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()?.message ?: "Presensi Masuk berhasil"))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal Presensi Masuk"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }

    fun checkOut(request: CheckOutRequest): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.checkOut(request)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()?.message ?: "Presensi Pulang berhasil"))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal Presensi Pulang"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }
}