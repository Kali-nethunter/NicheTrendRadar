package com.nichetrendradar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nichetrendradar.data.models.*
import com.nichetrendradar.data.network.RetrofitClient
import com.nichetrendradar.data.repository.TrendRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = TrendRepository(RetrofitClient.instance)

    private val _trendsState = MutableStateFlow<UiState<List<Trend>>>(UiState.Idle)
    val trendsState: StateFlow<UiState<List<Trend>>> = _trendsState

    private val _ideasState = MutableStateFlow<UiState<List<ContentIdea>>>(UiState.Idle)
    val ideasState: StateFlow<UiState<List<ContentIdea>>> = _ideasState

    private val _savedState = MutableStateFlow<UiState<List<ContentIdea>>>(UiState.Idle)
    val savedState: StateFlow<UiState<List<ContentIdea>>> = _savedState

    private val _createNicheState = MutableStateFlow<UiState<Niche>>(UiState.Idle)
    val createNicheState: StateFlow<UiState<Niche>> = _createNicheState

    var currentNiche: Niche? = null
        private set

    var selectedPlatform: String = "YouTube"
        private set

    fun setPlatform(platform: String) {
        selectedPlatform = platform
        currentNiche?.id?.let { fetchTrends(it, platform) }
    }

    fun createNiche(
        name: String,
        keywords: List<String>,
        platforms: List<String>,
        onCreated: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _createNicheState.value = UiState.Loading
            try {
                val niche = Niche(
                    name = name,
                    keywords = keywords,
                    platforms = platforms
                )
                val result = repository.createNiche(niche)
                val id = (result["niche_id"] as? Number)?.toInt()
                    ?: throw IllegalStateException("Backend did not return niche_id")

                currentNiche = niche.copy(id = id)
                _createNicheState.value = UiState.Success(currentNiche!!)
                onCreated?.invoke()
            } catch (e: Exception) {
                _createNicheState.value = UiState.Error(
                    e.message ?: "Could not connect to the Niche Trend Radar server"
                )
            }
        }
    }

    fun clearCreateNicheState() {
        _createNicheState.value = UiState.Idle
    }

    fun fetchTrends(nicheId: Int, platform: String = selectedPlatform) {
        selectedPlatform = platform
        viewModelScope.launch {
            _trendsState.value = UiState.Loading
            try {
                _trendsState.value = UiState.Success(repository.getTrends(nicheId, platform))
            } catch (e: Exception) {
                _trendsState.value =
                    UiState.Error(e.message ?: "Failed to fetch trends")
            }
        }
    }

    fun generateIdeas(trendTitle: String) {
        viewModelScope.launch {
            _ideasState.value = UiState.Loading
            try {
                val response = repository.generateIdeas(
                    trendTitle,
                    currentNiche?.name ?: "",
                    selectedPlatform
                )
                _ideasState.value = UiState.Success(response.ideas)
            } catch (e: Exception) {
                _ideasState.value =
                    UiState.Error(e.message ?: "Failed to generate ideas")
            }
        }
    }

    fun saveIdea(idea: ContentIdea) {
        viewModelScope.launch {
            try {
                repository.saveIdea(idea)
            } catch (e: Exception) {
                _savedState.value = UiState.Error(
                    e.message ?: "Failed to save idea"
                )
            }
        }
    }

    fun loadSavedIdeas() {
        viewModelScope.launch {
            _savedState.value = UiState.Loading
            try {
                _savedState.value = UiState.Success(repository.getSavedIdeas())
            } catch (e: Exception) {
                _savedState.value =
                    UiState.Error(e.message ?: "Failed to load saved ideas")
            }
        }
    }
}