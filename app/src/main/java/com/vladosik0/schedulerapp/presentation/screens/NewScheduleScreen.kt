package com.vladosik0.schedulerapp.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vladosik0.schedulerapp.presentation.view_models.NewScheduleScreenUiState
import com.vladosik0.schedulerapp.presentation.view_models.SharedScheduleScreensViewModel

@Composable
fun NewScheduleScreen(
    viewModel: SharedScheduleScreensViewModel,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {

    when(val state = viewModel.newScheduleScreenUiState.collectAsState().value) {
        is NewScheduleScreenUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(36.dp))
            }
        }
        is NewScheduleScreenUiState.Success -> {

        }
    }

}