package com.example.whatsfordinner.data.source.local.userpreferences

import android.util.Log
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toUpperCase
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val TAG = "UserPreferencesRepository"

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val preferredTheme: Flow<UserPreferredTheme> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            when (val themeString = preferences[PREFERRED_THEME]) {
                null -> UserPreferredTheme.SYSTEM
                else -> try {
                    UserPreferredTheme.valueOf(themeString)
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid theme string in preferences: $themeString")
                    UserPreferredTheme.SYSTEM
                }
            }
        }

    private companion object {
        val PREFERRED_THEME = stringPreferencesKey("preferred_theme")
    }

    suspend fun saveThemePreference(preferredTheme: UserPreferredTheme) {
        dataStore.edit { preferences ->
            preferences[PREFERRED_THEME] = preferredTheme.name
        }
    }
}