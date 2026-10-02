package com.nichetrendradar.data.network

import com.nichetrendradar.data.models.*
import retrofit2.http.*

interface TrendApiService {
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
}
