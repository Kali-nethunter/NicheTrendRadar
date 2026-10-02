package com.nichetrendradar.data.repository

import com.nichetrendradar.data.models.*
import com.nichetrendradar.data.network.TrendApiService

class TrendRepository(private val api: TrendApiService) {
    suspend fun createNiche(niche: Niche) = api.createNiche(niche)
    suspend fun getNiches() = api.getNiches()
    suspend fun getTrends(id: Int, platform: String) = api.getTrends(id, platform)

    suspend fun generateIdeas(trendTopic: String, niche: String, platform: String) =
        api.generateIdeas(
            mapOf(
                "trend_topic" to trendTopic,
                "niche" to niche,
                "platform" to platform
            )
        )

    suspend fun saveIdea(idea: ContentIdea) = api.saveIdea(idea)
    suspend fun getSavedIdeas() = api.getSavedIdeas()
    suspend fun deleteIdea(ideaId: Int) = api.deleteIdea(ideaId)
}
