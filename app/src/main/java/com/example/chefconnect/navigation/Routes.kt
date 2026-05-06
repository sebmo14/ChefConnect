package com.example.chefconnect.navigation

/** Route constants for NavHost. */
object Routes {
    const val SPLASH = "splash"
    const val CATEGORIES = "categories"
    const val MEALS = "meals/{category}"
    const val DETAIL = "detail/{idMeal}"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"

    fun mealsRoute(category: String) = "meals/$category"
    fun detailRoute(idMeal: String) = "detail/$idMeal"
}
