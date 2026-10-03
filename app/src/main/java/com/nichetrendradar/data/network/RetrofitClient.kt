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
                val original = chain.request()
                val token = if (::appContext.isInitialized) {
                    appContext.getSharedPreferences("niche_trend_radar", Context.MODE_PRIVATE)
                        .getString("auth_token", null)
                } else null
                val builder = original.newBuilder()
                if (!token.isNullOrBlank() &&
                    !original.url.encodedPath.contains("/api/auth/login") &&
                    !original.url.encodedPath.contains("/api/auth/signup")
                ) {
                    builder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(builder.build())
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