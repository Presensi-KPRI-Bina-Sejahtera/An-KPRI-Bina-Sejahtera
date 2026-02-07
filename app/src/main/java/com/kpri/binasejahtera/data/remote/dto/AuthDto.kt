package com.kpri.binasejahtera.data.remote.dto

import com.google.gson.annotations.SerializedName

// --- Login ---
// POST "auth/login"
data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("device_name")
    val deviceName: String = "android"
)

data class LoginResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("user")
    val user: UserDto
)


data class UserDto(
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
    val profileImage: String?
)

// google login
data class GoogleLoginRequest(
    @SerializedName("id_token")
    val idToken: String,

    @SerializedName("device_name")
    val deviceName: String = "android"
)

// change password
data class ChangePasswordRequest(
    @SerializedName("current_password")
    val currentPass: String,

    @SerializedName("password")
    val newPass: String,

    @SerializedName("password_confirmation")
    val confirmPass: String
)
