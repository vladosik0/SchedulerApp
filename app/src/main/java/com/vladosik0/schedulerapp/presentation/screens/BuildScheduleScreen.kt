package com.vladosik0.schedulerapp.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vladosik0.schedulerapp.R
import com.vladosik0.schedulerapp.domain.formatters.toPrettyFormat
import com.vladosik0.schedulerapp.presentation.AppViewModelProvider
import com.vladosik0.schedulerapp.presentation.view_models.BuildScheduleScreenViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


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
            }, scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
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
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(text = "New Task Info", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Task Title: " + viewModel.getTitle(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Task Description: " + viewModel.getDescription(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Task Category: " + viewModel.getCategory(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Task Priority: " + viewModel.getPriority().name.toPrettyFormat(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Task Difficulty: " + viewModel.getDifficulty().name.toPrettyFormat(),
                style = MaterialTheme.typography.bodyMedium
            )

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

            Spacer(modifier = Modifier.height(8.dp))

            DatePickerWithIconField(
                startDate = buildScheduleScreenUiState.startDate,
                finishDate = buildScheduleScreenUiState.finishDate,
                initialDate = buildScheduleScreenUiState.recommendedDate,
                isEnabled = viewModel.isTextFieldEnabled()
            ) {
                viewModel.updateRecommendedDate(it)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Button(
                    onClick = {
                        if (startDateErrorMessage == "" && finishDateErrorMessage == "") viewModel.getRecommendedDate()
                    }) {
                    Text(text = "Get recommended date")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithIconField(
    startDate: LocalDate,
    finishDate: LocalDate,
    initialDate: LocalDate,
    isEnabled: Boolean,
    onDateSelected: (LocalDate) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(initialDate) }

    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                return !date.isBefore(startDate) && !date.isAfter(finishDate)
            }
        }
    )

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val pickedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            selectedDate = pickedDate
                            onDateSelected(pickedDate)
                        }
                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    OutlinedTextField(
        value = selectedDate.format(dateFormatter) ?: "",
        onValueChange = {},
        label = { Text("Recommended Date") },
        readOnly = true,
        enabled = isEnabled,
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    painterResource(R.drawable.calendar),
                    contentDescription = "Pick date",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}