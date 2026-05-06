package com.example.chefconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chefconnect.data.model.MealState
import com.example.chefconnect.data.model.MealSummary
import com.example.chefconnect.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Meals grid screen.
 * Loads meals filtered by category.
 */
class MealsViewModel : ViewModel() {

    private val repository = MealRepository()

    private val _state = MutableStateFlow<MealState<List<MealSummary>>>(MealState.Loading)
    val state: StateFlow<MealState<List<MealSummary>>> = _state

    fun loadMealsByCategory(category: String) {
        viewModelScope.launch {
            _state.value = MealState.Loading
            try {
                val meals = repository.getMealsByCategory(category)
                _state.value = MealState.Success(meals)
            } catch (e: Exception) {
                _state.value = MealState.Error(
                    e.localizedMessage ?: "Failed to load meals"
                )
            }
        }
    }
}
