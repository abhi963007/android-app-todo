package com.windsurf.todoapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.windsurf.todoapp.R
import com.windsurf.todoapp.ui.navigation.Destination

/**
 * Bottom navigation bar for the app.
 * Shows navigation options for the three main screens: All Tasks, Favorites, and Completed Tasks.
 */
@Composable
fun BottomAppBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem(
            name = stringResource(R.string.nav_all_tasks),
            route = Destination.Home.route,
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            name = stringResource(R.string.nav_favorites),
            route = Destination.Favorites.route,
            icon = Icons.Default.Star
        ),
        BottomNavItem(
            name = stringResource(R.string.nav_completed),
            route = Destination.Completed.route,
            icon = Icons.Default.CheckCircle
        )
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.name) },
                label = { Text(text = item.name) },
                selected = selected,
                onClick = {
                    // Avoid unnecessary navigation to the current route
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Pop up to the Home destination when clicking Home to avoid
                            // building up a large stack of destinations
                            if (item.route == Destination.Home.route) {
                                popUpTo(Destination.Home.route) {
                                    inclusive = false
                                }
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when navigating back
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
