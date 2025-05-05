package com.vladosik0.schedulerapp

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.vladosik0.schedulerapp.presentation.AppViewModelProvider
import com.vladosik0.schedulerapp.presentation.navigation.NavigationRoutes
import com.vladosik0.schedulerapp.presentation.screens.DateScreen
import com.vladosik0.schedulerapp.presentation.screens.TaskDetailsScreen
import com.vladosik0.schedulerapp.presentation.screens.TaskEditScreen
import com.vladosik0.schedulerapp.presentation.view_models.TaskDetailsScreenViewModel
import com.vladosik0.schedulerapp.presentation.view_models.TaskEditScreenViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SchedulerAppNavigation() {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        navController = navController,
        startDestination = NavigationRoutes.DateScreen.route,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(400)) }
    ) {
        composable(route = NavigationRoutes.DateScreen.route) { DateScreen(navController) }

        composable(
            route = NavigationRoutes.TaskDetailsScreen.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType }),
        ) {
            val taskDetailsScreenViewModel: TaskDetailsScreenViewModel = viewModel(
                factory = AppViewModelProvider.Factory
            )
            TaskDetailsScreen(
                viewModel = taskDetailsScreenViewModel,
                onBackIconClick = { navController.popBackStack() },
                onEditIconClick = { taskId ->
                    navController.navigate(NavigationRoutes.TaskEditScreen.createRoute(taskId))
                }
            )
        }

        composable(
            route = NavigationRoutes.TaskEditScreen.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) {
            val taskEditScreenViewModel: TaskEditScreenViewModel = viewModel(
                factory = AppViewModelProvider.Factory
            )
            TaskEditScreen(
                viewModel = taskEditScreenViewModel,
                onCancel = {navController.popBackStack()}
            )
        }

        composable(
            route = NavigationRoutes.TaskCreateScreen.route,
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) {
            val taskEditScreenViewModel: TaskEditScreenViewModel = viewModel(
                factory = AppViewModelProvider.Factory
            )
            TaskEditScreen(
                viewModel = taskEditScreenViewModel,
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            route = NavigationRoutes.TaskCreateForFreeSlotScreen.route,
            arguments = listOf(navArgument("slot") {
                type = NavType.StringType
            })
        ) {
            val taskEditScreenViewModel: TaskEditScreenViewModel = viewModel(
                factory = AppViewModelProvider.Factory
            )
            TaskEditScreen(
                viewModel = taskEditScreenViewModel,
                onCancel = { navController.popBackStack() }
            )
        }
    }
}