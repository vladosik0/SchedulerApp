package com.vladosik0.schedulerapp.presentation.navigation

sealed class NavigationRoutes(val route: String) {
    object DateScreen : NavigationRoutes("date_screen")
    object TaskDetailsScreen : NavigationRoutes("task_details_screen/{taskId}") {
        fun createRoute(taskId: Int) = "task_details_screen/$taskId"
    }
}