package com.windsurf.todoapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Enum class representing the theme mode options.
 */
enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

/**
 * Data class representing the user preferences for the app.
 */
data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val sortByDueDate: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val notificationHour: Int = 8,
    val notificationMinute: Int = 0
)

/**
 * Manager for handling user preferences using DataStore.
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_preferences")
        
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val SORT_BY_DUE_DATE = booleanPreferencesKey("sort_by_due_date")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        private val NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")
    }
    
    /**
     * Flow of user preferences.
     */
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        val themeMode = when (preferences[THEME_MODE]) {
            ThemeMode.LIGHT.name -> ThemeMode.LIGHT
            ThemeMode.DARK.name -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
        
        UserPreferences(
            themeMode = themeMode,
            sortByDueDate = preferences[SORT_BY_DUE_DATE] ?: true,
            notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: true,
            notificationHour = preferences[NOTIFICATION_HOUR] ?: 8,
            notificationMinute = preferences[NOTIFICATION_MINUTE] ?: 0
        )
    }
    
    /**
     * Updates the theme mode preference.
     */
    suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = themeMode.name
        }
    }
    
    /**
     * Updates the sort order preference.
     */
    suspend fun updateSortOrder(sortByDueDate: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SORT_BY_DUE_DATE] = sortByDueDate
        }
    }
    
    /**
     * Updates the notifications enabled preference.
     */
    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    /**
     * Updates the notification time preference.
     */
    suspend fun updateNotificationTime(hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_HOUR] = hour
            preferences[NOTIFICATION_MINUTE] = minute
        }
    }
}
