package com.vladosik0.schedulerapp.presentation.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vladosik0.schedulerapp.ui.theme.SchedulerAppTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    initialTask: Task? = null,
    date: LocalDate = LocalDate.now(),
    startAt: String? = null,
    areNotificationsExpanded: Boolean = true,
    onSave: (Task) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val now = remember { LocalDateTime.now() }

    val topAppBarTitle = if (initialTask == null) "Create Task" else "Edit Task"

    var title by rememberSaveable { mutableStateOf(initialTask?.title ?: "") }
    var description by rememberSaveable { mutableStateOf(initialTask?.description ?: "") }
    var category by rememberSaveable { mutableStateOf(initialTask?.category ?: "") }

    var date by rememberSaveable {
        mutableStateOf(initialTask?.let { LocalDate.parse(it.startAt.substring(0, 10)) } ?: date)
    }
    var notificationsExpanded by remember { mutableStateOf(areNotificationsExpanded) }

    var startTime by rememberSaveable {
        mutableStateOf(
            when {
                initialTask != null -> LocalTime.parse(initialTask.startAt.substring(11))
                startAt != null -> LocalTime.parse(startAt)
                else -> now.toLocalTime()
            }
        )
    }
    var finishTime by rememberSaveable {
        mutableStateOf(initialTask?.let { LocalTime.parse(it.finishAt.substring(11)) } ?: now.toLocalTime().plusMinutes(30))
    }

    var difficulty by rememberSaveable { mutableStateOf(initialTask?.difficulty ?: Difficulty.NORMAL) }
    var priority by rememberSaveable { mutableStateOf(initialTask?.priority ?: Priority.LOW) }
    var duration by rememberSaveable { mutableStateOf(initialTask?.duration?.toString() ?: "30") }

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
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { if (it.length <= 50) title = it },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { if (it.length <= 500) description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )

            OutlinedTextField(
                value = category,
                onValueChange = { if (it.length <= 50) category = it },
                label = { Text("Category") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            DatePickerField(date) { date = it }
            TimePickerField("Start", startTime) { startTime = it }
            TimePickerField("Finish", finishTime) { finishTime = it }

            OutlinedTextField(
                value = duration,
                onValueChange = { if (it.all(Char::isDigit)) duration = it },
                label = { Text("Duration (min)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Difficulty:")
                Difficulty.entries.forEach {
                    FilterChip(selected = difficulty == it, onClick = { difficulty = it }, label = {
                        Text(
                            it.name.lowercase().replaceFirstChar(Char::uppercase)
                        )
                    })
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Priority:")
                Priority.entries.forEach {
                    FilterChip(selected = priority == it, onClick = { priority = it }, label = {
                        Text(
                            it.name.lowercase().replaceFirstChar(Char::uppercase)
                        )
                    })
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { notificationsExpanded = !notificationsExpanded }
            ) {
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
            AnimatedVisibility(
                visible = notificationsExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                NotificationSettings()
            }

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = onCancel) {
                    Text("Cancel")
                }
                Button(onClick = {
                    val startAt = "${date}T${startTime}"
                    val finishAt = "${date}T${finishTime}"
                    onSave(
                        Task(
                            id = initialTask?.id ?: 0,
                            startAt = startAt,
                            finishAt = finishAt,
                            title = title,
                            description = description.takeIf { it.isNotBlank() },
                            category = category,
                            duration = duration.toIntOrNull() ?: 0,
                            difficulty = difficulty,
                            priority = priority
                        )
                    )
                }) {
                    Text("Save")
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
        label = { Text("Date (dd.MM.yyyy)") },
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
    onTimeSelected: (LocalTime) -> Unit
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    var text by remember { mutableStateOf(selectedTime.format(formatter)) }
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            runCatching {
                val parsed = LocalTime.parse(it, formatter)
                onTimeSelected(parsed)
            }
        },
        label = { Text("$label Time (HH:mm)") },
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
        modifier = Modifier.fillMaxWidth(),
        readOnly = true
    )
}



@Composable
fun NotificationSettings(
    periodicOptions: List<Int> = listOf(12, 24, 48),
    selectedPeriodic: Int? = null,
    onPeriodicChange: (Int?) -> Unit = {},
    customTimes: List<LocalDateTime> = listOf<LocalDateTime>(),
    onAddCustomTime: (LocalDateTime) -> Unit = {},
    onRemoveCustomTime: (LocalDateTime) -> Unit = {}
) {
    var periodicDropdownExpanded by remember { mutableStateOf(false) }
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Select Period:", modifier = Modifier.padding(start = 16.dp, end = 8.dp))
                Box {
                    OutlinedButton(onClick = { periodicDropdownExpanded = true }) {
                        Text(selectedPeriodic?.let { "Every $it hours" } ?: "None")
                    }
                    DropdownMenu(expanded = periodicDropdownExpanded, onDismissRequest = { periodicDropdownExpanded = false }) {
                        periodicOptions.forEach { hours ->
                            DropdownMenuItem(
                                text = { Text("Every $hours hours") },
                                onClick = {
                                    onPeriodicChange(hours)
                                    periodicDropdownExpanded = false
                                })
                        }
                    }
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

                TimePickerField(
                    label = "Notification",
                    selectedTime = selectedTime ?: LocalTime.now(),
                    onTimeSelected = { selectedTime = it })

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

@Preview(showBackground = true)
@Composable
fun EditScreenPreview() {
    SchedulerAppTheme {
        TaskEditScreen { }
    }
}