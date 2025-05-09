package com.vladosik0.schedulerapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.key
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.vladosik0.schedulerapp.R
import com.vladosik0.schedulerapp.domain.enums.Difficulty
import com.vladosik0.schedulerapp.domain.enums.Priority
import com.vladosik0.schedulerapp.domain.formatters.getFormattedTime
import com.vladosik0.schedulerapp.domain.formatters.toPrettyFormat
import com.vladosik0.schedulerapp.presentation.ui_state_converters.BuildScheduleScreenUiState
import com.vladosik0.schedulerapp.presentation.ui_state_converters.TaskUiStateElement
import com.vladosik0.schedulerapp.presentation.view_models.SharedScheduleScreensViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildScheduleScreen(
    viewModel: SharedScheduleScreensViewModel,
    onCancel: () -> Unit,
    onBuildNewSchedule: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val buildScheduleScreenUiState by viewModel.buildScheduleScreenUiState.collectAsState()

    val startDateErrorMessage by viewModel.startDateErrorMessage.collectAsState()
    val finishDateErrorMessage by viewModel.finishDateErrorMessage.collectAsState()
    val dateOutOfRangeErrorMessage by viewModel.dateOutOfRangeErrorMessage.collectAsState()
    val startActivityPeriodErrorMessage by viewModel.startActivityPeriodErrorMessage.collectAsState()
    val finishActivityPeriodErrorMessage by viewModel.finishActivityPeriodErrorMessage.collectAsState()
    val startDesirablePeriodErrorMessage by viewModel.startDesirablePeriodErrorMessage.collectAsState()
    val finishDesirablePeriodErrorMessage by viewModel.finishDesirablePeriodErrorMessage.collectAsState()
    val durationErrorMessage by viewModel.noFreeTimeForNewTaskErrorMessage.collectAsState()

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
                text = "Task Title: " + buildScheduleScreenUiState.newTaskTitle,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Task Description: " + buildScheduleScreenUiState.newTaskDescription,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Task Category: " + buildScheduleScreenUiState.newTaskCategory,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Task Priority: " + buildScheduleScreenUiState.newTaskPriority.name.toPrettyFormat(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Task Difficulty: " + buildScheduleScreenUiState.newTaskDifficulty.name.toPrettyFormat(),
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
                buildScheduleScreenUiState = buildScheduleScreenUiState,
                isEnabled = viewModel.isTextFieldEnabled()
            ) {
                viewModel.updateRecommendedDate(it)
            }

            if (dateOutOfRangeErrorMessage != "") {
                Text(
                    text = dateOutOfRangeErrorMessage,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                    IconButton(
                        onClick = { viewModel.getPreviousRecommendedDate() },
                        enabled = viewModel.isTextFieldEnabled()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Get previous recommended date"
                        )
                    }
                    IconButton(
                        onClick = { viewModel.getNextRecommendedDate() },
                        enabled = viewModel.isTextFieldEnabled()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Get next recommended date"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if(buildScheduleScreenUiState.isRecommendedDateLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    enabled = startDateErrorMessage == "" && finishDateErrorMessage == "",
                    onClick = { viewModel.getRecommendedDate() }
                ) {
                    Text(text = "Get recommended date")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Get Schedule for Recommended Date", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            TimePickerField(
                label = "Activity Period Start",
                selectedTime = buildScheduleScreenUiState.activityPeriodStart,
                errorMessage = startActivityPeriodErrorMessage,
            ) {
                viewModel.updateStartActivityPeriodTime(it)
                viewModel.updateStartDesirablePeriodTime(it)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TimePickerField(
                label = "Activity Period Finish",
                selectedTime = buildScheduleScreenUiState.activityPeriodFinish,
                errorMessage = finishActivityPeriodErrorMessage,
            ) {
                viewModel.updateFinishActivityPeriodTime(it)
                viewModel.updateFinishDesirablePeriodTime(it)
            }

            Spacer(modifier = Modifier.height(8.dp))


            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Button(
                    onClick = { viewModel.getTasksByDateInActivityPeriod() },
                    enabled = viewModel.isTextFieldEnabled() &&
                            finishActivityPeriodErrorMessage == "" &&
                            startActivityPeriodErrorMessage == ""
                ) {
                    Text(text = "Get Tasks")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Tasks", style = MaterialTheme.typography.titleSmall)

            TaskTable(buildScheduleScreenUiState.temporaryTasks) {
                viewModel.changeFixedStatus(it)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TimePickerField(
                label = "Desired Task Period Start",
                selectedTime = buildScheduleScreenUiState.desirableExecutionPeriodStart,
                errorMessage = startDesirablePeriodErrorMessage,
            ) { viewModel.updateStartDesirablePeriodTime(it) }

            Spacer(modifier = Modifier.height(8.dp))

            TimePickerField(
                label = "Desired Task Period Finish",
                selectedTime = buildScheduleScreenUiState.desirableExecutionPeriodFinish,
                errorMessage = finishDesirablePeriodErrorMessage
            ) { viewModel.updateFinishDesirablePeriodTime(it) }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Consider this period for new schedule",
                    style = MaterialTheme.typography.titleSmall
                )

                Switch(
                    checked = buildScheduleScreenUiState.considerDesirableExecutionPeriod,
                    onCheckedChange = { viewModel.changeDesiredPeriodUsageStatus() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            InputField(
                onValueChange = { viewModel.validateDurationMinutes(it.toString()) },
                errorMessage = durationErrorMessage,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                OutlinedButton(onClick = onCancel) {
                    Text("Cancel")
                }
                Button(
                    enabled = viewModel.isBuildScheduleButtonAvailable(),
                    onClick = {
                        viewModel.buildSchedule()
                        onBuildNewSchedule()
                    }) {
                    Text("Build new schedule")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithIconField(
    buildScheduleScreenUiState: BuildScheduleScreenUiState,
    isEnabled: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val startDate = buildScheduleScreenUiState.startDate
    val finishDate = buildScheduleScreenUiState.finishDate
    val recommendedDate = buildScheduleScreenUiState.recommendedDate

    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    key(startDate, finishDate, recommendedDate) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = buildScheduleScreenUiState.recommendedDate.atStartOfDay(
                ZoneId.systemDefault()
            )?.toInstant()?.toEpochMilli(), selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val date = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    return !date.isBefore(buildScheduleScreenUiState.startDate) && !date.isAfter(
                        buildScheduleScreenUiState.finishDate
                    )
                }
            })

        if (showDialog) {
            DatePickerDialog(onDismissRequest = { showDialog = false }, confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val pickedDate =
                                Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            onDateSelected(pickedDate)
                        }
                        showDialog = false
                    }) {
                    Text("OK")
                }
            }, dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }) {
                DatePicker(state = datePickerState)
            }
        }

        OutlinedTextField(
            value = buildScheduleScreenUiState.recommendedDate.format(dateFormatter) ?: "",
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
}

@Composable
fun TaskTable(
    tasks: List<TaskUiStateElement>,
    onToggle: (TaskUiStateElement) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
        ) {
            Text("Title", Modifier.weight(2f))
            Text("Time", Modifier.weight(2f))
            Image(
                painter = painterResource(R.drawable.priority),
                contentDescription = "Priority",
                modifier = Modifier.size(24.dp).weight(1f)
            )
            Image(
                painter = painterResource(R.drawable.difficulty),
                contentDescription = "Difficulty",
                modifier = Modifier.size(24.dp).weight(1f)
            )
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = "Fixed",
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider(thickness = 2.dp)

        Column {
            if(tasks.isEmpty()) {
                Text(text = "No tasks")
            }
            tasks.forEach { task ->
                key(task.isFixed) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(task.title, Modifier.weight(2f))
                        Text(
                            "${getFormattedTime(task.startAt)}-${getFormattedTime(task.finishAt)}",
                            Modifier.weight(2f)
                        )
                        Icon(
                            imageVector = if(task.priority == Priority.LOW) Icons.Default.KeyboardArrowDown
                            else Icons.Default.KeyboardArrowUp,
                            contentDescription = "Priority",
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if(task.difficulty == Difficulty.NORMAL) Icons.Default.KeyboardArrowDown
                            else Icons.Default.KeyboardArrowUp,
                            contentDescription = "Difficulty",
                            modifier = Modifier.weight(1f)
                        )
                        IconToggleButton(
                            checked = task.isFixed,
                            onCheckedChange = { onToggle(task) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (task.isFixed) Icons.Filled.Lock else Icons.Outlined.Lock,
                                contentDescription = null
                            )
                        }
                    }
                    VerticalDivider(thickness = 4.dp)
                }
            }
        }
    }
}

@Composable
fun InputField(
    onValueChange: (String) -> Unit,
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    var durationInput by remember{mutableStateOf("30")}

    OutlinedTextField(
        value = durationInput,
        onValueChange = {
            durationInput = it
            onValueChange(it) },
        label = { Text("Approx. new task duration(min)") },
        isError = errorMessage != "",
        modifier = modifier,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        singleLine = true
    )

    if(errorMessage != "") {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}