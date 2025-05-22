package com.windsurf.todoapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.windsurf.todoapp.data.model.TaskCategory
import com.windsurf.todoapp.ui.screens.add.AddEditTaskScreen
import com.windsurf.todoapp.ui.screens.completed.CompletedTasksScreen
import com.windsurf.todoapp.ui.screens.favorites.FavoriteTasksScreen
import com.windsurf.todoapp.ui.screens.home.HomeScreen
import com.windsurf.todoapp.ui.screens.search.SearchScreen
import com.windsurf.todoapp.ui.screens.settings.SettingsScreen
import com.windsurf.todoapp.ui.screens.task.TaskDetailScreen

/**
 * Navigation graph for the application.
 * Defines all navigation routes and their corresponding composable screens.
 */
@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Destination.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Main Screens
        composable(Destination.Home.route) {
            HomeScreen(
                onTaskClick = { taskId ->
                    navController.navigate(Destination.TaskDetail.createRoute(taskId))
                },
                onAddClick = {
                    navController.navigate(Destination.AddTask.route)
                },
                onSearchClick = {
                    navController.navigate(Destination.Search.route)
                },
                onSettingsClick = {
                    navController.navigate(Destination.Settings.route)
                },
                onNavigateToFavorites = {
                    navController.navigate(Destination.Favorites.route)
                },
                onNavigateToCompleted = {
                    navController.navigate(Destination.Completed.route)
                },
                onCategoryClick = { category ->
                    navController.navigate(Destination.CategoryTasks.createRoute(category.name))
                },
                viewModel = hiltViewModel()
            )
        }
        
        composable(Destination.Favorites.route) {
            FavoriteTasksScreen(
                onTaskClick = { taskId ->
                    navController.navigate(Destination.TaskDetail.createRoute(taskId))
                },
                onBackClick = {
                    navController.navigateUp()
                },
                viewModel = hiltViewModel()
            )
        }
        
        composable(Destination.Completed.route) {
            CompletedTasksScreen(
                onTaskClick = { taskId ->
                    navController.navigate(Destination.TaskDetail.createRoute(taskId))
                },
                onBackClick = {
                    navController.navigateUp()
                },
                viewModel = hiltViewModel()
            )
        }
        
        composable(Destination.Settings.route) {
            SettingsScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                viewModel = hiltViewModel()
            )
        }
        
        // Task Detail Screen
        composable(
            route = Destination.TaskDetail.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
            TaskDetailScreen(
                taskId = taskId,
                onBackClick = {
                    navController.navigateUp()
                },
                onEditClick = { id ->
                    navController.navigate(Destination.EditTask.createRoute(id))
                },
                viewModel = hiltViewModel()
            )
        }
        
        // Add and Edit Task Screens
        composable(Destination.AddTask.route) {
            AddEditTaskScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onSaveClick = {
                    navController.navigateUp()
                },
                viewModel = hiltViewModel()
            )
        }
        
        composable(
            route = Destination.EditTask.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
            AddEditTaskScreen(
                taskId = taskId,
                onBackClick = {
                    navController.navigateUp()
                },
                onSaveClick = {
                    navController.navigateUp()
                },
                viewModel = hiltViewModel()
            )
        }
        
        // Category Tasks Screen
        composable(
            route = Destination.CategoryTasks.route,
            arguments = listOf(
                navArgument("categoryName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            val category = try {
                TaskCategory.valueOf(categoryName)
            } catch (e: IllegalArgumentException) {
                TaskCategory.OTHER
            }
            
            // Using HomeScreen with category filter
            HomeScreen(
                onTaskClick = { taskId ->
                    navController.navigate(Destination.TaskDetail.createRoute(taskId))
                },
                onAddClick = {
                    navController.navigate(Destination.AddTask.route)
                },
                onSearchClick = {
                    navController.navigate(Destination.Search.route)
                },
                onSettingsClick = {
                    navController.navigate(Destination.Settings.route)
                },
                onNavigateToFavorites = {
                    navController.navigate(Destination.Favorites.route)
                },
                onNavigateToCompleted = {
                    navController.navigate(Destination.Completed.route)
                },
                onCategoryClick = { categoryClicked ->
                    navController.navigate(Destination.CategoryTasks.createRoute(categoryClicked.name))
                },
                onBackClick = {
                    navController.navigateUp()
                },
                filterCategory = category,
                viewModel = hiltViewModel()
            )
        }
        
        // Search Screen
        composable(Destination.Search.route) {
            SearchScreen(
                onTaskClick = { taskId ->
                    navController.navigate(Destination.TaskDetail.createRoute(taskId))
                },
                onBackClick = {
                    navController.navigateUp()
                },
                viewModel = hiltViewModel()
            )
        }
    }
}
