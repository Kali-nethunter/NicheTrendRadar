package com.nichetrendradar.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nichetrendradar.data.models.*
import com.nichetrendradar.data.network.RetrofitClient
import com.nichetrendradar.data.repository.TrendRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = application.getSharedPreferences("niche_trend_radar", Context.MODE_PRIVATE)

    val isLoggedIn: Boolean
        get() = !preferences.getString("auth_token", null).isNullOrBlank()

    val accountEmail: String?
        get() = preferences.getString("account_email", null)

    private val _authState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle)
    val authState: StateFlow<UiState<AuthResponse>> = _authState

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

    init {
        currentNiche = loadSavedNiche()
    }

    private fun loadSavedNiche(): Niche? {
        val id = preferences.getInt("niche_id", -1)
        val name = preferences.getString("niche_name", null) ?: return null
        val keywords = preferences.getString("niche_keywords", "")
            ?.split("\u001F")?.filter { it.isNotBlank() } ?: emptyList()
        val platforms = preferences.getString("niche_platforms", "")
            ?.split("\u001F")?.filter { it.isNotBlank() } ?: emptyList()
        return Niche(
            id = id.takeIf { it > 0 },
            name = name,
            keywords = keywords,
            platforms = platforms
        )
    }

    private fun persistNiche(niche: Niche) {
        preferences.edit()
            .putInt("niche_id", niche.id ?: -1)
            .putString("niche_name", niche.name)
            .putString("niche_keywords", niche.keywords.joinToString("\u001F"))
            .putString("niche_platforms", niche.platforms.joinToString("\u001F"))
            .apply()
    }

    var selectedPlatform: String = "YouTube"
        private set

    // Preserve IDs returned by save even when a legacy backend omits IDs in GET responses.
    private val knownSavedIdeaIds = mutableMapOf<String, Int>()

    private fun ideaKey(idea: ContentIdea): String =
        listOf(idea.title, idea.hook, idea.outline.joinToString("\u001F"), idea.cta).joinToString("\u001E")

    private fun attachKnownIds(ideas: List<ContentIdea>): List<ContentIdea> =
        ideas.map { idea ->
            if (idea.id != null) {
                knownSavedIdeaIds[ideaKey(idea)] = idea.id
                idea
            } else {
                idea.copy(id = knownSavedIdeaIds[ideaKey(idea)])
            }
        }

    fun authenticate(email: String, password: String, createAccount: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            try {
                val response = if (createAccount) repository.signup(email, password) else repository.login(email, password)
                preferences.edit().putString("auth_token", response.token).putString("account_email", response.email).putInt("user_id", response.user_id).apply()
                _authState.value = UiState.Success(response)
                onSuccess()
            } catch (e: HttpException) {
                val body = e.response()?.errorBody()?.string()?.trim()
                _authState.value = UiState.Error(body ?: "Authentication failed (${e.code()})")
            } catch (e: Exception) {
                _authState.value = UiState.Error(e.message ?: "Authentication failed")
            }
        }
    }

    fun clearAuthState() { _authState.value = UiState.Idle }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            try { if (isLoggedIn) repository.logout() } catch (_: Exception) { }
            preferences.edit().remove("auth_token").remove("account_email").remove("user_id").remove("niche_id").remove("niche_name").remove("niche_keywords").remove("niche_platforms").apply()
            currentNiche = null
            _trendsState.value = UiState.Idle
            _savedState.value = UiState.Idle
            onComplete()
        }
    }

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
                persistNiche(currentNiche!!)
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

    fun saveIdea(idea: ContentIdea, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            try {
                val result = repository.saveIdea(idea)
                result["idea_id"]?.toIntOrNull()?.let { savedId ->
                    knownSavedIdeaIds[ideaKey(idea)] = savedId
                }
                _savedState.value = UiState.Success(
                    attachKnownIds(repository.getSavedIdeas())
                )
                onResult(true, result["status"] ?: "Saved to Library")
            } catch (e: Exception) {
                val message = e.message ?: "Failed to save idea"
                _savedState.value = UiState.Error(message)
                onResult(false, message)
            }
        }
    }

    private val _selectedSavedIdea = MutableStateFlow<ContentIdea?>(null)
    val selectedSavedIdea: StateFlow<ContentIdea?> = _selectedSavedIdea

    fun selectSavedIdea(idea: ContentIdea) {
        _selectedSavedIdea.value = idea
    }

    fun deleteSavedIdea(
        idea: ContentIdea,
        onResult: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            try {
                // Prefer the server ID already attached to the selected idea.
                // If an older/stale UI object has no ID, refresh the Library first
                // and try to recover the matching server-side record.
                var ideaId = idea.id

                if (ideaId == null) {
                    val refreshedIdeas = repository.getSavedIdeas()
                    val matchingIdea = refreshedIdeas.firstOrNull {
                        it.title == idea.title &&
                            it.hook == idea.hook &&
                            it.cta == idea.cta &&
                            it.outline == idea.outline
                    }
                    ideaId = matchingIdea?.id
                }

                if (ideaId == null) {
                    throw IllegalStateException(
                        "This saved idea has no server ID. Please refresh the Library and try again."
                    )
                }

                repository.deleteIdea(ideaId)
                knownSavedIdeaIds.remove(ideaKey(idea))
                _savedState.value = UiState.Success(
                    attachKnownIds(repository.getSavedIdeas())
                )
                _selectedSavedIdea.value = null
                onResult(true, "Deleted from Library")
            } catch (e: Exception) {
                val message = when (e) {
                    is HttpException -> {
                        val body = e.response()?.errorBody()?.string()?.trim()
                        if (!body.isNullOrEmpty()) {
                            "Server error ${e.code()}: $body"
                        } else {
                            "Server error ${e.code()}: ${e.message()}"
                        }
                    }
                    else -> e.message ?: "Failed to delete idea"
                }
                onResult(false, message)
            }
        }
    }


    fun changePassword(currentPassword: String, newPassword: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.changePassword(currentPassword, newPassword)
                onResult(true, "Password changed successfully")
            } catch (e: HttpException) {
                val body = e.response()?.errorBody()?.string()?.trim()
                onResult(false, body ?: ("Could not change password (" + e.code() + ")"))
            } catch (e: Exception) {
                onResult(false, e.message ?: "Could not change password")
            }
        }
    }

    fun signOutAllDevices(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.logoutAll()
                onResult(true, "All other sessions have been signed out")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Could not sign out other sessions")
            }
        }
    }

    fun clearSavedIdeas(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.clearSavedIdeas()
                knownSavedIdeaIds.clear()
                _savedState.value = UiState.Success(emptyList())
                onResult(true, "Saved ideas cleared")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Could not clear saved ideas")
            }
        }
    }

    fun clearRadarHistory(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.clearRadarHistory()
                onResult(true, "Radar history cleared")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Could not clear radar history")
            }
        }
    }

    fun exportAccount(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val data = repository.exportAccount()
                val json = org.json.JSONObject(data).toString(2)
                onResult(true, json)
            } catch (e: Exception) {
                onResult(false, e.message ?: "Could not export account data")
            }
        }
    }

    fun deleteAccount(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteAccount()
                preferences.edit().clear().apply()
                currentNiche = null
                _trendsState.value = UiState.Idle
                _savedState.value = UiState.Idle
                onResult(true, "Account deleted")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Could not delete account")
            }
        }
    }

    fun loadSavedIdeas() {
        viewModelScope.launch {
            _savedState.value = UiState.Loading
            try {
                _savedState.value = UiState.Success(
                    attachKnownIds(repository.getSavedIdeas())
                )
            } catch (e: Exception) {
                _savedState.value =
                    UiState.Error(e.message ?: "Failed to load saved ideas")
            }
        }
    }
}