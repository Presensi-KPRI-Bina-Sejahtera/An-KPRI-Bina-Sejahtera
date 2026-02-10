package com.kpri.binasejahtera.data.repository

import com.kpri.binasejahtera.data.remote.ApiService
import com.kpri.binasejahtera.data.remote.dto.AttendanceActionResponse
import com.kpri.binasejahtera.data.remote.dto.AttendanceRequest
import com.kpri.binasejahtera.data.remote.dto.AttendanceStatusResponse
import com.kpri.binasejahtera.data.remote.dto.OfficeResponse
import com.kpri.binasejahtera.utils.ApiErrorUtils
import com.kpri.binasejahtera.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(
    private val api: ApiService
) {
    // --- Local Cache ---
    // buat nyimpen data di local selama app berjalan
    private var cachedStatus: AttendanceStatusResponse? = null
    private var cachedOffice: OfficeResponse? = null

    fun getAttendanceStatus(
        forceUpdate: Boolean = false
    ): Flow<Resource<AttendanceStatusResponse>> = flow {

        // cek cache dulu
        if (!forceUpdate && cachedStatus != null) {
            emit(Resource.Success(cachedStatus!!))
            return@flow // stop, no internet untuk load data
        }

        // klo g ada cache, baru load dari api
        emit(Resource.Loading())
        try {
            val response = api.getAttendanceStatus()
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                // simpen ke cache
                cachedStatus = data
                emit(Resource.Success(data))
            } else {
                val errorMsg = ApiErrorUtils.parseMessage(response.errorBody())
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }

    fun getOfficeLocation(forceUpdate: Boolean = false): Flow<Resource<OfficeResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getOfficeLocation()
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                // sama kaya diatas
                cachedOffice = data
                emit(Resource.Success(data))
            } else {
                val errorMsg = ApiErrorUtils.parseMessage(response.errorBody())
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }

    fun checkIn(lat: Double, long: Double): Flow<Resource<AttendanceActionResponse>> = flow {
        emit(Resource.Loading())
        try {
            val request = AttendanceRequest(latitude = lat, longitude = long)
            val response = api.checkIn(request)
            val result = response.body()

            if (response.isSuccessful && result?.data != null) {
                // hapus cache karena user habis presensi
                cachedStatus = null
                emit(Resource.Success(result.data))
            } else {
                val errorMsg = ApiErrorUtils.parseMessage(response.errorBody())
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }

    fun checkOut(lat: Double, long: Double): Flow<Resource<AttendanceActionResponse>> = flow {
        emit(Resource.Loading())
        try {
            val request = AttendanceRequest(latitude = lat, longitude = long)
            val response = api.checkOut(request)
            val result = response.body()

            if (response.isSuccessful && result?.data != null) {
                // sama kaya yg diatas
                cachedStatus = null
                emit(Resource.Success(result.data))
            } else {
                val errorMsg = ApiErrorUtils.parseMessage(response.errorBody())
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }

    // logout
    fun clearCache() {
        cachedStatus = null
        cachedOffice = null
    }
}