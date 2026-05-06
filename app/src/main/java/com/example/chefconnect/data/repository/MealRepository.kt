package com.example.chefconnect.data.repository

import com.example.chefconnect.data.model.Category
import com.example.chefconnect.data.model.MealDetail
import com.example.chefconnect.data.model.MealSummary
import com.example.chefconnect.data.network.RetrofitClient

/**
 * Repository layer abstracting API calls from ViewModels.
 * All functions are suspend and should be called from viewModelScope.
 */
class MealRepository {

    private val api = RetrofitClient.apiService

    /** Fetch all meal categories */
    suspend fun getCategories(): List<Category> {
        return api.getCategories().categories
    }

    /** Fetch meals filtered by category name */
    suspend fun getMealsByCategory(category: String): List<MealSummary> {
        return api.getMealsByCategory(category).meals ?: emptyList()
    }

    /** Search meals by name query */
    suspend fun searchMeals(query: String): List<MealDetail> {
        return api.searchMeals(query).meals ?: emptyList()
    }

    /** Get full meal details by ID */
    suspend fun getMealById(id: String): MealDetail? {
        return api.getMealById(id).meals?.firstOrNull()
    }
}
