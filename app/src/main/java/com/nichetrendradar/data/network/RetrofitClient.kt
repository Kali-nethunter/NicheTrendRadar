package com.nichetrendradar.data.network

import com.nichetrendradar.config.ApiConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = if (::appContext.isInitialized) {
                    appContext.getSharedPreferences("niche_trend_radar", Context.MODE_PRIVATE)
                        .getString("auth_token", null)
                } else null
                val request = chain.request().newBuilder().apply {
                    if (!token.isNullOrBlank() && !request().url.encodedPath.contains("/api/auth/login") && !request().url.encodedPath.contains("/api/auth/signup")) {
                        addHeader("Authorization", "Bearer $token")
                    }
                }.build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()
    }

    val instance: TrendApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
.client(client)
            .build()
            .create(TrendApiService::class.java)
    }
}