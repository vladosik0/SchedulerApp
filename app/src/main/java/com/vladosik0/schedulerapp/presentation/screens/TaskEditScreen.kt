package com.vladosik0.schedulerapp.presentation.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import com.vladosik0.schedulerapp.R
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vladosik0.schedulerapp.domain.enums.Difficulty
import com.vladosik0.schedulerapp.domain.enums.Priority
import com.vladosik0.schedulerapp.domain.formatters.toPrettyFormat
import com.vladosik0.schedulerapp.domain.validators.isPeriodLogical
import com.vladosik0.schedulerapp.presentation.AppViewModelProvider
import com.vladosik0.schedulerapp.presentation.view_models.TaskEditScreenViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    onCancel: () -> Unit = {},
    viewModel: TaskEditScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current

    val initialTask by viewModel.taskEditScreenUiState.collectAsState()

    val topAppBarTitle = viewModel.topAppBarTitle

    var notificationsExpanded by rememberSaveable { mutableStateOf(false) }

    val startTimeErrorMessage by viewModel.startTimeErrorMessage.collectAsState()
    val finishTimeErrorMessage by viewModel.finishTimeErrorMessage.collectAsState()
    val saveTaskErrorMessage by viewModel.saveTaskErrorMessage.collectAsState()

    val isTaskValid by viewModel.isTaskValid.collectAsState()

    if(initialTask.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(36.dp))
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(topAppBarTitle) }, navigationIcon = {
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
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                    .verticalScroll(rememberScrollState()).padding(paddingValues).padding(16.dp)
            ) {
                OutlinedTextField(
                    value = initialTask.title,
                                  onValueChange = { viewModel.updateTitle(it) },
                                  label = { Text("Title") },
                                  singleLine = true,
                                  modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Max symbol limit: 50",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = initialTask.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
                Text(
                    text = "Max symbol limit: 250",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))


                OutlinedTextField(
                    value = initialTask.category,
                                  onValueChange = { viewModel.updateCategory(it) },
                                  label = { Text("Category") },
                                  singleLine = true,
                                  modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Max symbol limit: 50",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                DatePickerField(initialTask.date) { viewModel.updateDate(it) }

                Spacer(modifier = Modifier.height(12.dp))

                Log.d("UI_DEBUG_START", "${initialTask.startTime}")
                Log.d("UI_DEBUG_FINISH", "${initialTask.finishTime}")

                TimePickerField(
                    "Start", initialTask.startTime, startTimeErrorMessage
                ) { viewModel.updateStartTime(it) }

                Spacer(modifier = Modifier.height(12.dp))

                TimePickerField(
                    "Finish", initialTask.finishTime, finishTimeErrorMessage
                ) { viewModel.updateFinishTime(it) }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Difficulty:")
                    Difficulty.entries.forEach {
                        FilterChip(
                            selected = initialTask.difficulty == it,
                            onClick = { viewModel.updateDifficulty(it) },
                            label = { Text(it.name.toPrettyFormat()) })
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Priority:")
                    Priority.entries.forEach {
                        FilterChip(
                            selected = initialTask.priority == it,
                            onClick = { viewModel.updatePriority(it) },
                            label = { Text(it.name.toPrettyFormat()) })
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                        .clickable { notificationsExpanded = !notificationsExpanded }) {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Image(
                        painter = painterResource(R.drawable.notification),
                        contentDescription = if (notificationsExpanded) "Collapse Notifications" else "Expand Notifications",
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(
                    visible = notificationsExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    NotificationSettings()
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (saveTaskErrorMessage != "") {
                    Text(
                        text = saveTaskErrorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    OutlinedButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                    Button(
                        enabled = isTaskValid, onClick = {
                            viewModel.saveTask()
                            Toast.makeText(context, "Task saved successfully", Toast.LENGTH_SHORT)
                                .show()
                            onCancel()
                        }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun DatePickerField(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    var text by remember { mutableStateOf(selectedDate.format(formatter)) }

    OutlinedTextField(
        value = text, onValueChange = {
            text = it
            runCatching {
                val parsed = LocalDate.parse(it, formatter)
                onDateSelected(parsed)
            }
        },
        label = { Text("Date") },
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.calendar),
                contentDescription = "Pick date",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        val datePicker = DatePickerDialog(
                            context, { _, year, month, dayOfMonth ->
                                val newDate = LocalDate.of(year, month + 1, dayOfMonth)
                                text = newDate.format(formatter)
                                onDateSelected(newDate)
                            }, selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth
                        )
                        datePicker.show()
                    })
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        readOnly = true
    )
}

@Composable
fun TimePickerField(
    label: String,
    selectedTime: LocalTime,
    errorMessage: String,
    onTimeSelected: (LocalTime) -> Unit
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    var text by rememberSaveable { mutableStateOf(selectedTime.format(formatter)) }
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            runCatching {
                val parsed = LocalTime.parse(it, formatter)
                onTimeSelected(parsed)
            }
        },
        label = { Text("$label Time") },
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.clock_create),
                contentDescription = "Pick time",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        val timePicker = TimePickerDialog(
                            context, { _, hour, minute ->
                                val newTime = LocalTime.of(hour, minute)
                                text = newTime.format(formatter)
                                onTimeSelected(newTime)
                            }, selectedTime.hour, selectedTime.minute, true
                        )
                        timePicker.show()
                    })
        },
        singleLine = true,
        isError = errorMessage != "",
        modifier = Modifier.fillMaxWidth(),
        readOnly = true
    )

    if(errorMessage != "") {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}



@Composable
fun NotificationSettings(
    startAt: LocalDateTime = LocalDateTime.now(),
    finishAt: LocalDateTime = LocalDateTime.now(),
    onPeriodicChange: (Int?) -> Unit = {},
    customTimes: List<LocalDateTime> = listOf<LocalDateTime>(),
    onAddCustomTime: (LocalDateTime) -> Unit = {},
    onRemoveCustomTime: (LocalDateTime) -> Unit = {}
) {
    val units = listOf("Minutes", "Hours", "Days", "Weeks")
    var selectedUnit by remember { mutableStateOf("Hours") }
    var customValue by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var customNotificationsExpanded by remember { mutableStateOf(false) }
    var periodicNotificationsExpanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterLabel(
            text = "Periodic Notifications",
            expanded = periodicNotificationsExpanded
        ) { periodicNotificationsExpanded = !periodicNotificationsExpanded}

        AnimatedVisibility(visible = periodicNotificationsExpanded) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customValue,
                        onValueChange = { customValue = it },
                        label = { Text("Value") },
                        singleLine = true,
                        modifier = Modifier.width(80.dp),
                        isError = errorMessage != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    DropdownMenuBox(
                        options = units,
                        selected = selectedUnit,
                        onSelected = { selectedUnit = it })
                }

                if (errorMessage != null) {
                    Text(
                        errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(onClick = {
                    val value = customValue.toIntOrNull()
                    if (value == null || value <= 0) {
                        errorMessage = "Enter a valid number"
                    } else if (!isPeriodLogical(value, selectedUnit, startAt, finishAt)) {
                        errorMessage = "Period exceeds task duration"
                    } else {
                        errorMessage = null
                        val totalMinutes = when (selectedUnit) {
                            "Minutes" -> value
                            "Hours" -> value * 60
                            "Days" -> value * 60 * 24
                            "Weeks" -> value * 60 * 24 * 7
                            else -> value
                        }
                        onPeriodicChange(totalMinutes)
                    }
                }) {
                    Text("Set Period")
                }
            }
        }

        FilterLabel(
            text = "Custom Notifications",
            expanded = customNotificationsExpanded
        ) { customNotificationsExpanded = !customNotificationsExpanded}

        AnimatedVisibility(visible = customNotificationsExpanded) {
            Column {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    customTimes.forEach { dateTime ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(dateTime.toString())
                            IconButton(onClick = { onRemoveCustomTime(dateTime) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove")
                            }
                        }
                    }
                }
                DatePickerField(
                    selectedDate = selectedDate ?: LocalDate.now(),
                    onDateSelected = { selectedDate = it })

                Spacer(modifier = Modifier.height(8.dp))

//                TimePickerField(
//                    label = "Notification",
//                    selectedTime = selectedTime ?: LocalTime.now(),
//                    onTimeSelected = { selectedTime = it })

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            if (selectedDate != null && selectedTime != null) {
                                onAddCustomTime(LocalDateTime.of(selectedDate!!, selectedTime!!))
                                selectedDate = null
                                selectedTime = null
                            }
                        }, enabled = selectedDate != null && selectedTime != null
                    ) {
                        Text("Add Custom Time")
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuBox(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
