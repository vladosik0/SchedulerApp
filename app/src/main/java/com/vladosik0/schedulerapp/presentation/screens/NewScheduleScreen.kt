package com.vladosik0.schedulerapp.presentation.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.vladosik0.schedulerapp.domain.parsers.parseDateTimeStringToDate
import com.vladosik0.schedulerapp.presentation.view_models.NewScheduleScreenUiState
import com.vladosik0.schedulerapp.presentation.view_models.SharedScheduleScreensViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewScheduleScreen(
    viewModel: SharedScheduleScreensViewModel,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onTaskClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Schedule for ${viewModel.getDateForNewScheduleScreen()}") }, navigationIcon = {
                    IconButton(onClick = {
                        onCancel()
                        viewModel.updateNewScheduleScreenUiState()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }, scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when (val state = viewModel.newScheduleScreenUiState.collectAsState().value) {
            is NewScheduleScreenUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(36.dp))
                }
            }
            is NewScheduleScreenUiState.Success -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .padding(paddingValues = paddingValues)
                ) {
                    TimelineListView(
                        tasks = state.tasks,
                        selectedDate = parseDateTimeStringToDate(state.tasks.first().startAt),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(24.dp)
                            .zIndex(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        OutlinedButton(
                            onClick = {
                                onCancel()
                                viewModel.updateNewScheduleScreenUiState()
                            }
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                viewModel.updateNewScheduleScreenUiState()
                                viewModel.onSaveSchedule()
                                Toast.makeText(
                                    context, "Schedule saved successfully", Toast.LENGTH_SHORT
                                ).show()
                                onSave()
                            }
                        ) {
                            Text("Save Schedule")
                        }
                    }
                }
            }

            is NewScheduleScreenUiState.Failure -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .padding(paddingValues = paddingValues)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )

                    TimelineListView(
                        tasks = state.tasks,
                        selectedDate = parseDateTimeStringToDate(state.tasks.first().startAt),
                        modifier = Modifier.fillMaxWidth(),
                        onTaskClick = { task ->
                            onTaskClick(task.id)
                            viewModel.cleanUiStates()
                        }
                    )

                    OutlinedButton(
                        onClick = {
                            onCancel()
                            viewModel.updateNewScheduleScreenUiState()
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}