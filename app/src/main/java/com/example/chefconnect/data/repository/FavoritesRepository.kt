package com.example.chefconnect.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** DataStore instance scoped to the application context. */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favorites")

/**
 * Manages favorite meal IDs using DataStore Preferences for persistence.
 * Stores as a comma-separated string since stringSetKey may not be available in all versions.
 */
class FavoritesRepository(private val context: Context) {

    companion object {
        private val FAVORITES_KEY = stringPreferencesKey("favorite_meal_ids")
    }

    /** Flow emitting the current set of favorite meal IDs. */
    val favoriteIds: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        val raw = prefs[FAVORITES_KEY] ?: ""
        if (raw.isBlank()) emptySet() else raw.split(",").toSet()
    }

    /** Add a meal ID to favorites. */
    suspend fun addFavorite(mealId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[FAVORITES_KEY] ?: ""
            val set = if (current.isBlank()) mutableSetOf() else current.split(",").toMutableSet()
            set.add(mealId)
            prefs[FAVORITES_KEY] = set.joinToString(",")
        }
    }

    /** Remove a meal ID from favorites. */
    suspend fun removeFavorite(mealId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[FAVORITES_KEY] ?: ""
            val set = if (current.isBlank()) mutableSetOf() else current.split(",").toMutableSet()
            set.remove(mealId)
            prefs[FAVORITES_KEY] = set.joinToString(",")
        }
    }

    /** Toggle a meal ID in favorites. Returns true if added, false if removed. */
    suspend fun toggleFavorite(mealId: String): Boolean {
        var added = false
        context.dataStore.edit { prefs ->
            val current = prefs[FAVORITES_KEY] ?: ""
            val set = if (current.isBlank()) mutableSetOf() else current.split(",").toMutableSet()
            if (mealId in set) {
                set.remove(mealId)
                added = false
            } else {
                set.add(mealId)
                added = true
            }
            prefs[FAVORITES_KEY] = set.joinToString(",")
        }
        return added
    }
}
