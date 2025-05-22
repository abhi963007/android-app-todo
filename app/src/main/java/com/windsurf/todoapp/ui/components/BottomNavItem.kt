package com.windsurf.todoapp.ui.components

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class representing an item in the bottom navigation bar.
 */
data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector
)
