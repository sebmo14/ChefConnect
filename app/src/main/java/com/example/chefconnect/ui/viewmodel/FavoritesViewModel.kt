package com.example.chefconnect.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chefconnect.data.model.MealDetail
import com.example.chefconnect.data.model.MealState
import com.example.chefconnect.data.repository.FavoritesRepository
import com.example.chefconnect.data.repository.MealRepository
import com.example.chefconnect.data.repository.NotificationHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the Favorites screen.
 * Observes favorite IDs from DataStore and loads full details from API.
 */
class FavoritesViewModel(
    private val context: Context
) : ViewModel() {

    private val mealRepository = MealRepository()
    val favoritesRepository = FavoritesRepository(context)

    private val _state = MutableStateFlow<MealState<List<MealDetail>>>(MealState.Loading)
    val state: StateFlow<MealState<List<MealDetail>>> = _state

    /** Flow of favorite meal IDs for checking if a specific meal is favorited. */
    val favoriteIds: StateFlow<Set<String>> = favoritesRepository.favoriteIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    init {
        // Reload favorites whenever the favorite IDs change
        viewModelScope.launch {
            favoritesRepository.favoriteIds.collectLatest { ids ->
                loadFavoriteDetails(ids)
            }
        }
    }

    private suspend fun loadFavoriteDetails(ids: Set<String>) {
        if (ids.isEmpty()) {
            _state.value = MealState.Success(emptyList())
            return
        }
        _state.value = MealState.Loading
        try {
            val meals = ids.mapNotNull { id ->
                mealRepository.getMealById(id)
            }
            _state.value = MealState.Success(meals)
        } catch (e: Exception) {
            _state.value = MealState.Error(
                e.localizedMessage ?: "Failed to load favorites"
            )
        }
    }

    /**
     * Toggle favorite status. Shows notification when added.
     */
    fun toggleFavorite(mealId: String, mealName: String) {
        viewModelScope.launch {
            val added = favoritesRepository.toggleFavorite(mealId)
            if (added) {
                NotificationHelper.showFavoriteNotification(context, mealId, mealName)
            }
        }
    }

    /** Factory to inject Context into the ViewModel. */
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavoritesViewModel(context.applicationContext) as T
        }
    }
}
