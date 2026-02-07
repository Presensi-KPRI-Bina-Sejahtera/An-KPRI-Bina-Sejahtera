package com.kpri.binasejahtera.data.repository

import com.kpri.binasejahtera.data.remote.ApiService
import com.kpri.binasejahtera.data.remote.dto.CashflowRequest
import com.kpri.binasejahtera.data.remote.dto.DepositRequest
import com.kpri.binasejahtera.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val api: ApiService
) {
    fun sendCashflow(request: CashflowRequest): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.sendCashflow(request)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()?.message ?: "Laporan Keuangan terkirim"))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal mengirim laporan"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }

    fun sendDeposits(request: DepositRequest): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.sendDeposits(request)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()?.message ?: "Setoran berhasil disimpan"))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal menyimpan setoran"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan jaringan"))
        }
    }
}