package com.kpri.binasejahtera.utils

import com.kpri.binasejahtera.data.local.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // ngambil token secara synchronus (blocking) karena Interceptor jalan di bg thread
        val token = runBlocking {
            tokenManager.token.first()
        }

        val requestBuilder = chain.request().newBuilder()

        // klo token ada, kasih ke header
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        // nambah header accept json biar server tau kita minta JSON
        requestBuilder.addHeader("Accept", "application/json")

        return chain.proceed(requestBuilder.build())
    }
}