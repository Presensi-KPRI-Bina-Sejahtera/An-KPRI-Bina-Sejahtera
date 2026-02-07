package com.kpri.binasejahtera.data.remote.dto

import com.google.gson.annotations.SerializedName


// --- Home & Info ---
// GET employee/attendance
data class AttendanceStatusResponse(
    @SerializedName("jam_masuk")
    val jamMasuk: String?,

    @SerializedName("sudah_masuk")
    val sudahMasuk: Boolean,

    @SerializedName("jam_pulang")
    val jamPulang: String?,

    @SerializedName("sudah_pulang")
    val sudahPulang: Boolean,

    @SerializedName("total_work_hours")
    val totalWorkHours: Int?,

    @SerializedName("total_work_minutes")
    val totalWorkMinutes: Int?,

    @SerializedName("work_duration_text")
    val workDurationText: String?
)

// GET employee/presence-location
data class OfficeResponse(
    @SerializedName("name")
    val name: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("latitude")
    val latitude: String,

    @SerializedName("longitude")
    val longitude: String,

    @SerializedName("max_distance")
    val maxDistance: Int,

    @SerializedName("maps")
    val mapsUrl: String?
)

// --- Check In & Out ---

/*
POST /employee/attendance/check-in
POST /employee/attendance/check-out
*/

data class AttendanceRequest(
    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double
)