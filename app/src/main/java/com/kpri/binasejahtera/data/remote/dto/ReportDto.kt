package com.kpri.binasejahtera.data.remote.dto

import com.google.gson.annotations.SerializedName

// --- Cashflow ---
data class CashflowRequest(
    @SerializedName("pemasukan")
    val income: Long,

    @SerializedName("pengeluaran")
    val expense: Long
)

// --- Deposit ---
data class DepositRequest(
    @SerializedName("deposits")
    val deposits: List<DepositItemDto>
)

data class DepositItemDto(
    @SerializedName("for_name")
    val memberName: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("value")
    val amount: Long
)