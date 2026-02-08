package com.kpri.binasejahtera.data.remote

import com.kpri.binasejahtera.data.remote.dto.AttendanceActionResponse
import com.kpri.binasejahtera.data.remote.dto.AttendanceRequest
import com.kpri.binasejahtera.data.remote.dto.AttendanceStatusResponse
import com.kpri.binasejahtera.data.remote.dto.BaseResponse
import com.kpri.binasejahtera.data.remote.dto.CashflowRequest
import com.kpri.binasejahtera.data.remote.dto.ChangePasswordRequest
import com.kpri.binasejahtera.data.remote.dto.DepositRequest
import com.kpri.binasejahtera.data.remote.dto.GoogleLoginRequest
import com.kpri.binasejahtera.data.remote.dto.LoginRequest
import com.kpri.binasejahtera.data.remote.dto.LoginResponse
import com.kpri.binasejahtera.data.remote.dto.OfficeResponse
import com.kpri.binasejahtera.data.remote.dto.ProfileResponse
import com.kpri.binasejahtera.data.remote.dto.UpdateProfileRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface ApiService {

    // --- Auth ---
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<BaseResponse<LoginResponse>>

    @POST("auth/login-google")
    suspend fun googleLogin(
        @Body request: GoogleLoginRequest
    ): Response<BaseResponse<LoginResponse>>

    @POST("auth/logout")
    suspend fun logout(): Response<BaseResponse<Any>>

    // --- Profile ---
    @GET("profile/me")
    suspend fun getProfile(): Response<BaseResponse<ProfileResponse>>

    @PUT("profile/update")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<BaseResponse<ProfileResponse>>

    @PUT("profile/update-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<BaseResponse<Any>>

    @Multipart
    @PUT("profile/photo")
    suspend fun uploadPhoto(
        @Part photo: MultipartBody.Part
    ): Response<BaseResponse<Any>>

    // --- Home & Office ---
    @GET("employee/attendance")
    suspend fun getAttendanceStatus(): Response<BaseResponse<AttendanceStatusResponse>>

    @GET("employee/presence-location")
    suspend fun getOfficeLocation(): Response<BaseResponse<OfficeResponse>>

    // --- Attendance ---
    @POST("employee/attendance/check-in")
    suspend fun checkIn(
        @Body request: AttendanceRequest
    ): Response<BaseResponse<AttendanceActionResponse>>

    @POST("employee/attendance/check-out")
    suspend fun checkOut(
        @Body request: AttendanceRequest
    ): Response<BaseResponse<AttendanceActionResponse>>

    // --- Report ---
    @POST("employee/cashflow")
    suspend fun sendCashflow(
        @Body request: CashflowRequest
    ): Response<BaseResponse<Any>>

    @POST("employee/deposit")
    suspend fun sendDeposits(
        @Body request: DepositRequest
    ): Response<BaseResponse<Any>>
}