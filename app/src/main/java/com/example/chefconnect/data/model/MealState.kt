package com.example.chefconnect.data.model

/**
 * Sealed class representing UI states for any screen that loads data.
 */
sealed class MealState<out T> {
    object Loading : MealState<Nothing>()
    data class Success<T>(val data: T) : MealState<T>()
    data class Error(val message: String) : MealState<Nothing>()
}
