package com.windsurf.todoapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windsurf.todoapp.data.preferences.PreferencesManager
import com.windsurf.todoapp.data.preferences.ThemeMode
import com.windsurf.todoapp.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

/**
 * ViewModel for the Settings screen.
 * Manages user preferences and settings.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    /**
     * Current user preferences.
     */
    val userPreferences: StateFlow<UserPreferences> = preferencesManager.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )
    
    /**
     * Updates the theme mode.
     */
    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            preferencesManager.updateThemeMode(themeMode)
        }
    }
    
    /**
     * Updates the sort order preference.
     */
    fun updateSortOrder(sortByDueDate: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateSortOrder(sortByDueDate)
        }
    }
    
    /**
     * Updates the notifications enabled setting.
     */
    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateNotificationsEnabled(enabled)
        }
    }
    
    /**
     * Updates the notification time.
     */
    fun updateNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            preferencesManager.updateNotificationTime(hour, minute)
        }
    }
    
    /**
     * Gets the notification time as a formatted string.
     */
    fun getFormattedNotificationTime(hour: Int, minute: Int): String {
        val time = LocalTime.of(hour, minute)
        val hourDisplay = when {
            time.hour == 0 -> 12
            time.hour > 12 -> time.hour - 12
            else -> time.hour
        }
        val amPm = if (time.hour < 12) "AM" else "PM"
        return String.format("%d:%02d %s", hourDisplay, time.minute, amPm)
    }
}
