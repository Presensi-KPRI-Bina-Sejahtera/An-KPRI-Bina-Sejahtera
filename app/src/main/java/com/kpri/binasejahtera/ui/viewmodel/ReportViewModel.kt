package com.kpri.binasejahtera.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kpri.binasejahtera.data.remote.dto.CashflowRequest
import com.kpri.binasejahtera.data.remote.dto.DepositItemDto
import com.kpri.binasejahtera.data.remote.dto.DepositRequest
import com.kpri.binasejahtera.data.repository.ReportRepository
import com.kpri.binasejahtera.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _reportEvent = Channel<ReportEvent>()
    val reportEvent = _reportEvent.receiveAsFlow()

    fun submitReport(
        income: String,
        expense: String,
        deposits: List<DepositItemDto>
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            // hapus karakter non-digit biar aman saat parsing
            val cleanIncome = income.replace(Regex("[^0-9]"), "")
            val cleanExpense = expense.replace(Regex("[^0-9]"), "")

            // kirim pemasukan cashflow
            val incomeLong = income.toLongOrNull() ?: 0L
            val expenseLong = expense.toLongOrNull() ?: 0L

            var isCashflowError = false
            repository.sendCashflow(CashflowRequest(incomeLong, expenseLong)).collect { cashResult ->
                if (cashResult is Resource.Error) {
                    _isLoading.value = false
                    _reportEvent.send(ReportEvent.Error("Gagal kirim keuangan: ${cashResult.message}"))
                    isCashflowError = true
                }
            }
            if (isCashflowError) return@launch

            // kirim setoran (klo ada)
            if (deposits.isNotEmpty()) {
                var isDepositError = false
                repository.sendDeposits(DepositRequest(deposits)).collect { depositResult ->
                    if (depositResult is Resource.Error) {
                        _isLoading.value = false
                        _reportEvent.send(ReportEvent.Error("Gagal kirim setoran: ${depositResult.message}"))
                        isDepositError = true
                    }
                }
                if (isDepositError) return@launch
            }

            _isLoading.value = false
            _reportEvent.send(ReportEvent.Success("Laporan Harian berhasil dikirim!"))
        }
    }

    sealed class ReportEvent {
        data class Success(val message: String) : ReportEvent()
        data class Error(val message: String) : ReportEvent()
    }
}