package com.nichetrendradar.data.models

data class Niche(
    val id: Int? = null,
    val name: String,
    val keywords: List<String>,
    val platforms: List<String>
)

data class Trend(
    val trend_id: String,
    val title: String,
    val score: Int,
    val growth_label: String,
    val source_summary: String
)

data class ContentIdea(
    val title: String,
    val hook: String,
    val outline: List<String>,
    val cta: String
)

data class IdeaResponse(val ideas: List<ContentIdea>)