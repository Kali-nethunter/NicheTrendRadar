package com.nichetrendradar.data.network

import com.nichetrendradar.config.ApiConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val instance: TrendApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(TrendApiService::class.java)
    }
}