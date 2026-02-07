package com.kpri.binasejahtera.data.remote.dto

import com.google.gson.annotations.SerializedName


// --- Home & Info ---
// response GET /employee/home
data class HomeDataResponse(
    @SerializedName("shift_name")
    val shiftName: String?,

    @SerializedName("start_time")
    val startTime: String?,

    @SerializedName("end_time")
    val endTime: String?,

    @SerializedName("today_attendance")
    val todaylog: TodayAttendanceDto?
)

data class TodayAttendanceDto(
    @SerializedName("in_time")
    val inTime: String?,

    @SerializedName("out_time")
    val outTime: String?,
)

// response GET /office
data class OfficeResponse(
    @SerializedName("name")
    val name: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("radius")
    val radiusMeter: Double
)

// --- Check In & Out ---
// request POST /employee/attendance/check-in
data class CheckInRequest(
    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("address")
    val address: String,

    @SerializedName("status")
    val status: String = "Hadir"
)

// request POST /employee/attendance/check-out
data class CheckOutRequest(
    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("address")
    val address: String
)