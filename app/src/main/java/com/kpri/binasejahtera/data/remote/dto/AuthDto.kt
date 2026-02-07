package com.kpri.binasejahtera.data.remote.dto

import com.google.gson.annotations.SerializedName

// login
data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("user")
    val user: UserDto
)

// data user saat login
data class UserDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("role")
    val role: String
)

// google login
data class GoogleLoginRequest(
    @SerializedName("token")
    val token: String
)

// change password
data class ChangePasswordRequest(
    @SerializedName("current_password")
    val currentPass: String,

    @SerializedName("new_password")
    val newPass: String,

    @SerializedName("new_password_confirmation")
    val confirmPass: String
)
