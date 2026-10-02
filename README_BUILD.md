# Niche Trend Radar - Android Build

Android-native Kotlin + Jetpack Compose client for the Niche Trend Radar FastAPI backend.

## API base URL

Edit:
app/src/main/java/com/nichetrendradar/config/ApiConfig.kt

- Emulator: http://10.0.2.2:8000/
- Physical phone: use a reachable backend URL
- Production: use an HTTPS API URL

## GitHub Actions

The workflow at .github/workflows/android-build.yml builds a debug APK and uploads it as an artifact named niche-trend-radar-debug-apk.

## Local build

Use Gradle 8.1 + JDK 17:

gradle assembleDebug
gradle assembleRelease

The release build currently uses the debug signing key for testing only. Configure a private production keystore before distribution.

Do not commit API keys, signing keystores, or credentials.