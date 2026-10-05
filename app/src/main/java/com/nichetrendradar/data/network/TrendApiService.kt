package com.nichetrendradar.data.network

import com.nichetrendradar.data.models.*
import retrofit2.http.*

interface TrendApiService {
    @POST("api/auth/signup")
    suspend fun signup(@Body request: AuthRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("api/auth/logout")
    suspend fun logout(): Map<String, String>

    @GET("api/auth/me")
    suspend fun me(): AuthResponse

    @POST("api/auth/change-password")
    suspend fun changePassword(@Body request: Map<String, String>): Map<String, String>

    @POST("api/auth/logout-all")
    suspend fun logoutAll(): Map<String, String>

    @DELETE("api/auth/account")
    suspend fun deleteAccount(): Map<String, String>

    @GET("api/account/export")
    suspend fun exportAccount(): Map<String, Any>

    @POST("api/niches")
    suspend fun createNiche(@Body niche: Niche): Map<String, Any>

    @GET("api/niches")
    suspend fun getNiches(): List<Niche>

    @GET("api/trends")
    suspend fun getTrends(
        @Query("niche_id") nicheId: Int,
        @Query("platform") platform: String
    ): List<Trend>

    @POST("api/ideas/generate")
    suspend fun generateIdeas(@Body request: Map<String, String>): IdeaResponse

    @POST("api/ideas/save")
    suspend fun saveIdea(@Body idea: ContentIdea): Map<String, String>

    @GET("api/ideas/saved")
    suspend fun getSavedIdeas(): List<ContentIdea>

    @DELETE("api/ideas/{idea_id}")
    suspend fun deleteIdea(@Path("idea_id") ideaId: Int): Map<String, String>

    @DELETE("api/ideas/clear")
    suspend fun clearSavedIdeas(): Map<String, String>

    @DELETE("api/radar/history")
    suspend fun clearRadarHistory(): Map<String, String>
}
