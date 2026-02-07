package com.kpri.binasejahtera.data.remote

import com.kpri.binasejahtera.data.remote.dto.BaseResponse
import com.kpri.binasejahtera.data.remote.dto.CashflowRequest
import com.kpri.binasejahtera.data.remote.dto.ChangePasswordRequest
import com.kpri.binasejahtera.data.remote.dto.CheckInRequest
import com.kpri.binasejahtera.data.remote.dto.CheckOutRequest
import com.kpri.binasejahtera.data.remote.dto.DepositRequest
import com.kpri.binasejahtera.data.remote.dto.GoogleLoginRequest
import com.kpri.binasejahtera.data.remote.dto.HomeDataResponse
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
import retrofit2.http.Part

interface ApiService {
    // --- Auth ---
    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<BaseResponse<LoginResponse>>

    @POST("auth/google")
    suspend fun googleLogin(
        @Body request: GoogleLoginRequest
    ): Response<BaseResponse<LoginResponse>>

    @POST("logout")
    suspend fun logout(): Response<BaseResponse<Any>>

    @POST("change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<BaseResponse<Any>>

    // --- Profile ---
    @GET("employee/profile")
    suspend fun getProfile(): Response<BaseResponse<ProfileResponse>>

    @POST("employee/profile/update")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<BaseResponse<ProfileResponse>>

    @Multipart
    @POST("employee/profile/upload-photo")
    suspend fun uploadPhoto(
        @Part photo: MultipartBody.Part
    ): Response<BaseResponse<Any>>

    // --- Home & Office ---
    @GET("employee/home")
    suspend fun getHomeData(): Response<BaseResponse<HomeDataResponse>>

    @GET("office")
    suspend fun getOfficeLocation(): Response<BaseResponse<OfficeResponse>>

    // --- Attendance ---
    @POST("employee/attendance/check-in")
    suspend fun checkIn(
        @Body request: CheckInRequest
    ): Response<BaseResponse<Any>>

    @POST("employee/attendance/check-out")
    suspend fun checkOut(
        @Body request: CheckOutRequest
    ): Response<BaseResponse<Any>>

    // --- Report ---
    // cashflow
    @POST("employee/cashflow")
    suspend fun sendCashflow(
        @Body request: CashflowRequest
    ): Response<BaseResponse<Any>>

    // deposit
    @POST("employee/deposit")
    suspend fun sendDeposits(
        @Body request: DepositRequest
    ): Response<BaseResponse<Any>>
}