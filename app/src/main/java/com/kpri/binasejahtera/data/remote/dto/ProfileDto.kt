package com.kpri.binasejahtera.data.remote.dto

import com.google.gson.annotations.SerializedName

// response GET /employee/profile
data class ProfileResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone_number")
    val phoneNumber: String?,

    @SerializedName("profile_photo_url")
    val photoUrl: String?,

    @SerializedName("position")
    val role: String,

    @SerializedName("shift_name")
    val shiftName: String?
)

// request POST /employee/profile/update
data class UpdateProfileRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone_number")
    val phoneNumber: String
)