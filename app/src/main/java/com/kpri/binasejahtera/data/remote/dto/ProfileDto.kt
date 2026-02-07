package com.kpri.binasejahtera.data.remote.dto

import com.google.gson.annotations.SerializedName

// GET /profile/me
data class ProfileResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("profile_image")
    val profileImage: String?,

    @SerializedName("has_password")
    val hasPassword: Boolean
)

// PUT /profile/update
data class UpdateProfileRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("username")
    val username: String
)