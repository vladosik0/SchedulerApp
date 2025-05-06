package com.vladosik0.schedulerapp.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vladosik0.schedulerapp.domain.formatters.toPrettyFormat
import com.vladosik0.schedulerapp.presentation.AppViewModelProvider
import com.vladosik0.schedulerapp.presentation.view_models.BuildScheduleScreenViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildScheduleScreen(
    onCancel: () -> Unit,
    onSave: () -> Unit,
    viewModel: BuildScheduleScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val buildScheduleScreenUiState by viewModel.buildScheduleScreenUiState.collectAsState()

    val startDateErrorMessage by viewModel.startDateErrorMessage.collectAsState()
    val finishDateErrorMessage by viewModel.finishDateErrorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Build schedule") }, navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }, scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues).padding(16.dp)
        ) {
            Text(text = "New Task Info", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Task Title: " + viewModel.getTitle(), style = MaterialTheme.typography.bodyMedium)
            Text(text = "Task Description: " + viewModel.getDescription(), style = MaterialTheme.typography.bodyMedium)
            Text(text = "Task Category: " + viewModel.getCategory(), style = MaterialTheme.typography.bodyMedium)
            Text(text = "Task Priority: " + viewModel.getPriority().name.toPrettyFormat(), style = MaterialTheme.typography.bodyMedium)
            Text(text = "Task Difficulty: " + viewModel.getDifficulty().name.toPrettyFormat(), style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Get Recommended Date", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            DatePickerField(
                label = "Pick Start Range Date",
                selectedDate = buildScheduleScreenUiState.startDate,
                errorMessage = startDateErrorMessage
            ) { viewModel.updateStartDate(it) }

            Spacer(modifier = Modifier.height(8.dp))

            DatePickerField(
                label = "Pick Finish Range Date",
                selectedDate = buildScheduleScreenUiState.finishDate,
                errorMessage = finishDateErrorMessage
            ) { viewModel.updateFinishDate(it) }
        }
    }
}