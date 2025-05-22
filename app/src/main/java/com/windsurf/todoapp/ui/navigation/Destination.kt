package com.windsurf.todoapp.ui.navigation

/**
 * Navigation destinations for the app.
 */
sealed class Destination(val route: String) {
    // Main navigation screens
    object Home : Destination("home_screen")
    object Favorites : Destination("favorites_screen")
    object Completed : Destination("completed_screen")
    object Settings : Destination("settings_screen")
    
    // Task operations screens
    object TaskDetail : Destination("task_detail/{taskId}") {
        fun createRoute(taskId: Long) = "task_detail/$taskId"
    }
    object AddTask : Destination("add_task")
    object EditTask : Destination("edit_task/{taskId}") {
        fun createRoute(taskId: Long) = "edit_task/$taskId"
    }
    
    // Category and search screens
    object CategoryTasks : Destination("category_tasks/{categoryName}") {
        fun createRoute(categoryName: String) = "category_tasks/$categoryName"
    }
    object Search : Destination("search")
}
