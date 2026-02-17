package com.kpri.binasejahtera.data.remote.dto

import com.google.gson.annotations.SerializedName

// --- Cashflow ---
data class CashflowRequest(
    @SerializedName("pemasukan")
    val pemasukan: Long,

    @SerializedName("keterangan_pemasukan")
    val keterangan_pemasukan: String? = null,

    @SerializedName("pengeluaran")
    val pengeluaran: Long,

    @SerializedName("keterangan_pengeluaran")
    val keterangan_pengeluaran: String? = null
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