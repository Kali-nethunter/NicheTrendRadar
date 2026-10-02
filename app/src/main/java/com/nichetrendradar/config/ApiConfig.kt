package com.nichetrendradar.config

object ApiConfig {
    private const val BASE_URL = "http://10.0.2.2:8000/"
    fun getBaseUrl(): String = BASE_URL
}