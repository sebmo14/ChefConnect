package com.example.chefconnect.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.chefconnect.ui.screens.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToCategories = {
                    navController.navigate(Routes.CATEGORIES) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CATEGORIES) {
            CategoriesScreen(
                onCategoryClick = { category ->
                    navController.navigate(Routes.mealsRoute(category))
                }
            )
        }

        composable(
            route = Routes.MEALS,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            MealsGridScreen(
                category = category,
                onMealClick = { id -> navController.navigate(Routes.detailRoute(id)) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("idMeal") { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "chefconnect://detail/{idMeal}" }
            )
        ) { backStackEntry ->
            val idMeal = backStackEntry.arguments?.getString("idMeal") ?: ""
            MealDetailScreen(
                mealId = idMeal,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                onMealClick = { id -> navController.navigate(Routes.detailRoute(id)) }
            )
        }

        composable(Routes.FAVORITES) {
            FavoritesScreen(
                onMealClick = { id -> navController.navigate(Routes.detailRoute(id)) }
            )
        }
    }
}
