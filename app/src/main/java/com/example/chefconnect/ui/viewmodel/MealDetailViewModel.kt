package com.example.chefconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chefconnect.data.model.MealDetail
import com.example.chefconnect.data.model.MealState
import com.example.chefconnect.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Meal Detail screen.
 * Loads full meal details by ID.
 */
class MealDetailViewModel : ViewModel() {

    private val repository = MealRepository()

    private val _state = MutableStateFlow<MealState<MealDetail>>(MealState.Loading)
    val state: StateFlow<MealState<MealDetail>> = _state

    fun loadMeal(id: String) {
        viewModelScope.launch {
            _state.value = MealState.Loading
            try {
                val meal = repository.getMealById(id)
                if (meal != null) {
                    _state.value = MealState.Success(meal)
                } else {
                    _state.value = MealState.Error("Meal not found")
                }
            } catch (e: Exception) {
                _state.value = MealState.Error(
                    e.localizedMessage ?: "Failed to load meal details"
                )
            }
        }
    }
}
