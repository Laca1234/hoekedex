package hu.hoekedex.app

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import hu.hoekedex.app.data.Profile
import hu.hoekedex.app.data.ProfileImage
import hu.hoekedex.app.data.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HoekedexViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProfileRepository(application)

    var uiState by mutableStateOf(HoekedexUiState())
        private set

    val selectedProfile: Profile?
        get() = uiState.profiles.firstOrNull { it.id == uiState.selectedProfileId }

    val filteredProfiles: List<Profile>
        get() {
            val minAge = uiState.minAge.toIntOrNull()
            val maxAge = uiState.maxAge.toIntOrNull()
            val nationality = uiState.nationality.trim()
            val query = uiState.query.trim()

            return uiState.profiles.filter { profile ->
                val matchesName = query.isBlank() ||
                    profile.displayName.contains(query, ignoreCase = true)
                val matchesMinAge = minAge == null || (profile.age ?: -1) >= minAge
                val matchesMaxAge = maxAge == null || (profile.age ?: Int.MAX_VALUE) <= maxAge
                val matchesRating = profile.rating >= uiState.minRating
                val matchesNationality = nationality.isBlank() ||
                    profile.nationality.contains(nationality, ignoreCase = true)

                matchesName &&
                    matchesMinAge &&
                    matchesMaxAge &&
                    matchesRating &&
                    matchesNationality
            }
        }

    init {
        refreshProfiles()
    }

    fun setQuery(value: String) {
        uiState = uiState.copy(query = value)
    }

    fun setFiltersVisible(value: Boolean) {
        uiState = uiState.copy(filtersVisible = value)
    }

    fun setMinAge(value: String) {
        uiState = uiState.copy(minAge = value.filter { it.isDigit() }.take(3))
    }

    fun setMaxAge(value: String) {
        uiState = uiState.copy(maxAge = value.filter { it.isDigit() }.take(3))
    }

    fun setMinRating(value: Float) {
        uiState = uiState.copy(minRating = value.coerceIn(0f, 5f))
    }

    fun setNationality(value: String) {
        uiState = uiState.copy(nationality = value)
    }

    fun clearFilters() {
        uiState = uiState.copy(
            minAge = "",
            maxAge = "",
            minRating = 0f,
            nationality = ""
        )
    }

    fun createProfile() {
        repository.createEmptyProfile()
        refreshProfiles(selectedId = null)
        uiState = uiState.copy(editMode = false)
    }

    fun openProfile(profileId: Long) {
        uiState = uiState.copy(selectedProfileId = profileId, editMode = false)
    }

    fun closeProfile() {
        uiState = uiState.copy(selectedProfileId = null, editMode = false)
    }

    fun setEditMode(value: Boolean) {
        uiState = uiState.copy(editMode = value)
    }

    fun saveProfile(profile: Profile) {
        repository.updateProfile(profile)
        refreshProfiles(selectedId = profile.id)
        uiState = uiState.copy(editMode = false)
    }

    fun deleteProfile(profile: Profile) {
        repository.deleteProfile(profile)
        refreshProfiles(selectedId = null)
        uiState = uiState.copy(editMode = false)
    }

    fun addImages(profileId: Long, uris: List<Uri>) {
        viewModelScope.launch {
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    repository.addImages(profileId, uris)
                }
            }

            result.onSuccess {
                refreshProfiles(selectedId = profileId)
            }.onFailure { error ->
                uiState = uiState.copy(errorMessage = error.message ?: "A képek mentése sikertelen.")
            }
        }
    }

    fun deleteImage(image: ProfileImage) {
        repository.deleteImage(image)
        refreshProfiles(selectedId = image.profileId)
    }

    fun consumeError() {
        uiState = uiState.copy(errorMessage = null)
    }

    private fun refreshProfiles(selectedId: Long? = uiState.selectedProfileId) {
        uiState = uiState.copy(
            profiles = repository.getProfiles(),
            selectedProfileId = selectedId
        )
    }
}

data class HoekedexUiState(
    val profiles: List<Profile> = emptyList(),
    val query: String = "",
    val filtersVisible: Boolean = false,
    val minAge: String = "",
    val maxAge: String = "",
    val minRating: Float = 0f,
    val nationality: String = "",
    val selectedProfileId: Long? = null,
    val editMode: Boolean = false,
    val errorMessage: String? = null
)
