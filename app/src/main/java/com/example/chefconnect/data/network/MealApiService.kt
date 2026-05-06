package com.example.chefconnect.data.network

import com.example.chefconnect.data.model.CategoriesResponse
import com.example.chefconnect.data.model.MealDetailResponse
import com.example.chefconnect.data.model.MealsFilterResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for TheMealDB API.
 * Base URL: https://www.themealdb.com/api/json/v1/1/
 */
interface MealApiService {

    /** Get all meal categories */
    @GET("categories.php")
    suspend fun getCategories(): CategoriesResponse

    /** Filter meals by category name */
    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): MealsFilterResponse

    /** Search meals by name */
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealDetailResponse

    /** Lookup full meal details by ID */
    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id: String): MealDetailResponse
}
