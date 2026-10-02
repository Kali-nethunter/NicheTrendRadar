# Niche Trend Radar — Android Project

This repository contains the Android-native Kotlin/Jetpack Compose client for Niche Trend Radar.

## Architecture
- Kotlin
- Jetpack Compose
- MVVM + Repository Pattern
- Retrofit + OkHttp
- Coroutines + Flow
- Coil
- Compose Navigation

## Backend contract
The Android client expects the existing FastAPI REST backend. Configure the backend base URL in `app/src/main/java/com/nichetrendradar/config/ApiConfig.kt`.

## Build
Use the included GitHub Actions workflow to build a debug APK, or install Gradle 8.1 locally and run `gradle assembleDebug`.

## Security
Do not commit API keys, production credentials, signing keystores, or passwords.
