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
import com.vladosik0.schedulerapp.domain.enums.Difficulty
import com.vladosik0.schedulerapp.domain.enums.Priority
import com.vladosik0.schedulerapp.presentation.AppViewModelProvider
import com.vladosik0.schedulerapp.presentation.navigation.NavigationRoutes
import com.vladosik0.schedulerapp.presentation.screens.BuildScheduleScreen
import com.vladosik0.schedulerapp.presentation.screens.DateScreen
import com.vladosik0.schedulerapp.presentation.screens.NewScheduleScreen
import com.vladosik0.schedulerapp.presentation.screens.TaskDetailsScreen
import com.vladosik0.schedulerapp.presentation.screens.TaskEditScreen
import com.vladosik0.schedulerapp.presentation.view_models.SharedScheduleScreensViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SchedulerAppNavigation() {
    val navController = rememberAnimatedNavController()
    val sharedScheduleScreensViewModel: SharedScheduleScreensViewModel = viewModel(
        factory = AppViewModelProvider.Factory
    )

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
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(600)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(600)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(600)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(600)) }
        ) {
            TaskDetailsScreen(
                onBackIconClick = { navController.popBackStack() },
                onEditIconClick = { taskId ->
                    navController.navigate(NavigationRoutes.TaskEditScreen.createRoute(taskId))
                }
            )
        }

        composable(
            route = NavigationRoutes.TaskEditScreen.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType }),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(600)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(600)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(600)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(600)) }
        ) {
            TaskEditScreen(
                onCancel = {navController.popBackStack()},
                onScheduleBuildButtonClick = { uiState ->
                    navController.navigate(NavigationRoutes.BuildScheduleScreen.createRoute(
                        taskId = uiState.id,
                        title = uiState.title,
                        description = if(uiState.description == "") "No Description" else uiState.description,
                        category = uiState.category,
                        difficulty = if(uiState.difficulty == Difficulty.HIGH) 2 else 1,
                        priority = if(uiState.priority == Priority.HIGH) 2 else 1
                    )
                    )
                }
            )
        }

        composable(
            route = NavigationRoutes.TaskCreateScreen.route,
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) {
            TaskEditScreen(
                onCancel = { navController.popBackStack() },
                onScheduleBuildButtonClick = { uiState ->
                    navController.navigate(NavigationRoutes.BuildScheduleScreen.createRoute(
                        taskId = uiState.id,
                        title = uiState.title,
                        description = if(uiState.description == "") "No Description" else uiState.description,
                        category = uiState.category,
                        difficulty = if(uiState.difficulty == Difficulty.HIGH) 2 else 1,
                        priority = if(uiState.priority == Priority.HIGH) 2 else 1
                    )
                    )
                }
            )
        }

        composable(
            route = NavigationRoutes.TaskCreateForFreeSlotScreen.route,
            arguments = listOf(navArgument("slot") { type = NavType.StringType })
        ) {
            TaskEditScreen(
                onCancel = { navController.popBackStack() },
                onScheduleBuildButtonClick = { uiState ->
                    navController.navigate(NavigationRoutes.BuildScheduleScreen.createRoute(
                        taskId = uiState.id,
                        title = uiState.title,
                        description = if(uiState.description == "") "No Description" else uiState.description,
                        category = uiState.category,
                        difficulty = if(uiState.difficulty == Difficulty.HIGH) 2 else 1,
                        priority = if(uiState.priority == Priority.HIGH) 2 else 1
                    )
                    )
                }
            )
        }

        composable(
            route = NavigationRoutes.BuildScheduleScreen.route,
            arguments = listOf(
                navArgument("taskId") { type = NavType.IntType },
                navArgument("title") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType },
                navArgument("difficulty") { type = NavType.IntType },
                navArgument("priority") { type = NavType.IntType },
                )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""
            val category = backStackEntry.arguments?.getString("category") ?: ""
            val difficulty = backStackEntry.arguments?.getInt("difficulty") ?: 0
            val priority = backStackEntry.arguments?.getInt("priority") ?: 0
            sharedScheduleScreensViewModel.updateInitialBuildScheduleScreenUiState(
                id = taskId,
                title = title,
                description = description,
                category = category,
                difficulty = if(difficulty == 1) Difficulty.NORMAL else Difficulty.HIGH,
                priority = if(priority == 1) Priority.LOW else Priority.HIGH
            )
            BuildScheduleScreen(
                viewModel = sharedScheduleScreensViewModel,
                onCancel = { navController.popBackStack() },
                onBuildNewSchedule = {
                    navController.navigate(NavigationRoutes.NewScheduleScreen.route)
                }
            )
        }

        composable(route = NavigationRoutes.NewScheduleScreen.route) {
            NewScheduleScreen(
                viewModel = sharedScheduleScreensViewModel,
                onCancel = {navController.popBackStack()},
                onSave = {
                    navController.navigate(NavigationRoutes.DateScreen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                onTaskClick = { taskId ->
                    navController.navigate(NavigationRoutes.TaskEditScreen.createRoute(taskId))
                }
            )
        }
    }
}