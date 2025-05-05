package com.vladosik0.schedulerapp.presentation.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vladosik0.schedulerapp.presentation.AppViewModelProvider
import com.vladosik0.schedulerapp.presentation.view_models.BuildScheduleScreenViewModel


@Composable
fun BuildScheduleScreen(
    onCancel: () -> Unit,
    onSave: () -> Unit,
    viewModel: BuildScheduleScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

}