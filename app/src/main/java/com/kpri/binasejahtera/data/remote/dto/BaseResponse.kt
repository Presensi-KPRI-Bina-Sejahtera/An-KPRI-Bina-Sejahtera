package com.kpri.binasejahtera.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BaseResponse<T> (
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("errors")
    val errors: Map<String, List<String>>? = null
)