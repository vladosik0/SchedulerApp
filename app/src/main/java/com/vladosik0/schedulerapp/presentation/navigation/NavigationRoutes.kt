package com.vladosik0.schedulerapp.presentation.navigation

import java.time.LocalDate

sealed class NavigationRoutes(val route: String) {

    object DateScreen : NavigationRoutes("date_screen")

    object TaskDetailsScreen : NavigationRoutes("task_details_screen/{taskId}") {
        fun createRoute(taskId: Int) = "task_details_screen/$taskId"
    }

    object TaskEditScreen : NavigationRoutes("task_edit_screen/{taskId}") {
        fun createRoute(taskId: Int) = "task_edit_screen/$taskId"
    }

    object TaskCreateScreen : NavigationRoutes("task_create_screen/{date}") {
        fun createRoute(date: LocalDate) = "task_create_screen/${date}"
    }

    object TaskCreateForFreeSlotScreen : NavigationRoutes("task_create_free_screen?slot={slot}") {
        fun createRoute(slot: String?) = "task_create_free_screen?slot=${slot ?: ""}"
    }

    object BuildScheduleScreen : NavigationRoutes(
        "build_schedule_screen/{taskId}/{title}/{category}/{description}/{difficulty}/{priority}"
    ) {
        fun createRoute(
            taskId: Int,
            title: String,
            category: String,
            description: String,
            difficulty: Int,
            priority: Int
        ) = "build_schedule_screen/${taskId}/${title}/${category}/${description}/${difficulty}/${priority}"
    }

    object NewScheduleScreen : NavigationRoutes("new_schedule_screen")
}