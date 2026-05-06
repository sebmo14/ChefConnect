package com.example.chefconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chefconnect.data.model.Category
import com.example.chefconnect.data.model.MealState
import com.example.chefconnect.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Categories screen.
 * Fetches categories from TheMealDB API on init.
 */
class CategoriesViewModel : ViewModel() {

    private val repository = MealRepository()

    private val _state = MutableStateFlow<MealState<List<Category>>>(MealState.Loading)
    val state: StateFlow<MealState<List<Category>>> = _state

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _state.value = MealState.Loading
            try {
                val categories = repository.getCategories()
                _state.value = MealState.Success(categories)
            } catch (e: Exception) {
                _state.value = MealState.Error(
                    e.localizedMessage ?: "Failed to load categories"
                )
            }
        }
    }
}
