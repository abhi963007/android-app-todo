package com.windsurf.todoapp.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.windsurf.todoapp.R
import com.windsurf.todoapp.data.preferences.ThemeMode
import com.windsurf.todoapp.ui.screens.add.TimePickerDialog

/**
 * Screen for managing app settings and preferences.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel
) {
    val userPreferences by viewModel.userPreferences.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    // Dialog states
    var showThemeDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Theme setting
            SettingsCategory(title = "Appearance")
            
            SettingsItem(
                title = stringResource(R.string.setting_theme),
                subtitle = when (userPreferences.themeMode) {
                    ThemeMode.LIGHT -> stringResource(R.string.setting_theme_light)
                    ThemeMode.DARK -> stringResource(R.string.setting_theme_dark)
                    ThemeMode.SYSTEM -> stringResource(R.string.setting_theme_system)
                },
                onClick = { showThemeDialog = true }
            )
            
            // Sorting setting
            SettingsItem(
                title = stringResource(R.string.setting_sorting),
                subtitle = if (userPreferences.sortByDueDate) 
                    stringResource(R.string.setting_sort_date) 
                else 
                    stringResource(R.string.setting_sort_priority),
                onClick = { showSortDialog = true }
            )
            
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            
            // Notifications settings
            SettingsCategory(title = "Notifications")
            
            SettingsToggleItem(
                title = stringResource(R.string.setting_notifications),
                isChecked = userPreferences.notificationsEnabled,
                onToggle = { viewModel.updateNotificationsEnabled(it) }
            )
            
            if (userPreferences.notificationsEnabled) {
                SettingsItem(
                    title = stringResource(R.string.setting_notification_time),
                    subtitle = viewModel.getFormattedNotificationTime(
                        userPreferences.notificationHour,
                        userPreferences.notificationMinute
                    ),
                    onClick = { showTimePickerDialog = true }
                )
            }
            
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            
            // About section
            SettingsCategory(title = "About")
            
            SettingsItem(
                title = "Version",
                subtitle = "1.0.0",
                onClick = { }
            )
        }
    }
    
    // Theme selection dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(text = stringResource(R.string.setting_theme)) },
            text = {
                Column(
                    modifier = Modifier.selectableGroup()
                ) {
                    ThemeMode.values().forEach { themeMode ->
                        val text = when (themeMode) {
                            ThemeMode.LIGHT -> stringResource(R.string.setting_theme_light)
                            ThemeMode.DARK -> stringResource(R.string.setting_theme_dark)
                            ThemeMode.SYSTEM -> stringResource(R.string.setting_theme_system)
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = userPreferences.themeMode == themeMode,
                                    onClick = {
                                        viewModel.updateThemeMode(themeMode)
                                        showThemeDialog = false
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = userPreferences.themeMode == themeMode,
                                onClick = null
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(text = text)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showThemeDialog = false }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
    
    // Sort order dialog
    if (showSortDialog) {
        AlertDialog(
            onDismissRequest = { showSortDialog = false },
            title = { Text(text = stringResource(R.string.setting_sorting)) },
            text = {
                Column(
                    modifier = Modifier.selectableGroup()
                ) {
                    // By due date option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = userPreferences.sortByDueDate,
                                onClick = {
                                    viewModel.updateSortOrder(true)
                                    showSortDialog = false
                                },
                                role = Role.RadioButton
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = userPreferences.sortByDueDate,
                            onClick = null
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(text = stringResource(R.string.setting_sort_date))
                    }
                    
                    // By priority option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = !userPreferences.sortByDueDate,
                                onClick = {
                                    viewModel.updateSortOrder(false)
                                    showSortDialog = false
                                },
                                role = Role.RadioButton
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !userPreferences.sortByDueDate,
                            onClick = null
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(text = stringResource(R.string.setting_sort_priority))
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showSortDialog = false }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
    
    // Time picker dialog
    if (showTimePickerDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = userPreferences.notificationHour,
            initialMinute = userPreferences.notificationMinute
        )
        
        TimePickerDialog(
            onDismissRequest = { showTimePickerDialog = false },
            onConfirm = {
                viewModel.updateNotificationTime(timePickerState.hour, timePickerState.minute)
                showTimePickerDialog = false
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

/**
 * Composable for displaying a settings category title.
 */
@Composable
fun SettingsCategory(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

/**
 * Composable for displaying a settings item with title and subtitle.
 */
@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Composable for displaying a settings item with a toggle switch.
 */
@Composable
fun SettingsToggleItem(
    title: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        
        Switch(
            checked = isChecked,
            onCheckedChange = onToggle
        )
    }
}
