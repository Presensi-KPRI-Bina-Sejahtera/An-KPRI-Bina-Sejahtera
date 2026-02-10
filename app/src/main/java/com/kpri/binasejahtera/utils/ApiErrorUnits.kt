package com.kpri.binasejahtera.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kpri.binasejahtera.data.remote.dto.BaseResponse
import okhttp3.ResponseBody

object ApiErrorUtils {
    fun parseMessage(errorBody: ResponseBody?): String {
        return try {
            val errorJson = errorBody?.string()
            if (!errorJson.isNullOrEmpty()) {
                val gson = Gson()
                val type = object : TypeToken<BaseResponse<Any>>() {}.type
                val parsedResponse: BaseResponse<Any> = gson.fromJson(errorJson, type)
                parsedResponse.message
            } else {
                "Terjadi kesalahan pada server"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Gagal memproses respon server"
        }
    }
}