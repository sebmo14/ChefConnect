package com.example.chefconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chefconnect.data.model.MealDetail
import com.example.chefconnect.data.model.MealState
import com.example.chefconnect.data.repository.MealRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the Search screen.
 * Implements debounced search using Flow operators.
 */
@OptIn(FlowPreview::class)
class SearchViewModel : ViewModel() {

    private val repository = MealRepository()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _state = MutableStateFlow<MealState<List<MealDetail>>>(MealState.Success(emptyList()))
    val state: StateFlow<MealState<List<MealDetail>>> = _state

    init {
        // Debounce search: wait 500ms after user stops typing
        viewModelScope.launch {
            _query
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { searchQuery ->
                    if (searchQuery.isBlank()) {
                        _state.value = MealState.Success(emptyList())
                        return@collectLatest
                    }
                    _state.value = MealState.Loading
                    try {
                        val results = repository.searchMeals(searchQuery)
                        _state.value = MealState.Success(results)
                    } catch (e: Exception) {
                        _state.value = MealState.Error(
                            e.localizedMessage ?: "Search failed"
                        )
                    }
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }
}
