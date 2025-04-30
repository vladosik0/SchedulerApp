package com.vladosik0.schedulerapp.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.vladosik0.schedulerapp.R
import com.vladosik0.schedulerapp.presentation.navigation.NavigationRoutes
import com.vladosik0.schedulerapp.ui.theme.SchedulerAppTheme
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DateScreen(navController: NavController) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val distinctCategories = sampleTasks.map { it.category }.distinct()

    var selectedPriorities by remember { mutableStateOf(setOf<Priority>()) }
    var selectedDifficulties by remember { mutableStateOf(setOf<Difficulty>()) }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var selectedTimelineEvents by remember { mutableStateOf(setOf<TimelineEvents>()) }


    var filtersExpanded by remember { mutableStateOf(false) }

    val filteredTasks = sampleTasks.filter { task ->
        (selectedPriorities.isEmpty() || task.priority in selectedPriorities) &&
                (selectedDifficulties.isEmpty() || task.difficulty in selectedDifficulties) &&
                (selectedCategories.isEmpty() || task.category in selectedCategories)
    }

    Scaffold(
        topBar = {
            TopBarWithDatePicker(
                scrollBehavior,
                selectedDate
            ) { newSelectedDate -> selectedDate = newSelectedDate }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { filtersExpanded = !filtersExpanded }
                    .padding(16.dp)
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    painter = painterResource(R.drawable.filter),
                    contentDescription = if (filtersExpanded) "Collapse Filters" else "Expand Filters",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(14.dp)
                )
            }
            AnimatedVisibility(
                visible = filtersExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                TaskFiltersRow(
                    selectedPriorities = selectedPriorities,
                    selectedDifficulties = selectedDifficulties,
                    selectedCategories = selectedCategories,
                    selectedTimelineEvents = selectedTimelineEvents,
                    categories = distinctCategories,
                    onPrioritySelected = { priority ->
                        selectedPriorities = if (selectedPriorities.contains(priority)) {
                            selectedPriorities - priority
                        } else {
                            selectedPriorities + priority
                        }
                    },
                    onDifficultySelected = { difficulty ->
                        selectedDifficulties = if (selectedDifficulties.contains(difficulty)) {
                            selectedDifficulties - difficulty
                        } else {
                            selectedDifficulties + difficulty
                        }
                    },
                    onCategorySelected = { category ->
                        selectedCategories = if (selectedCategories.contains(category)) {
                            selectedCategories - category
                        } else {
                            selectedCategories + category
                        }
                    },
                    onTimelineEventSelected = { timelineEvent ->
                        selectedTimelineEvents = if (selectedTimelineEvents.contains(timelineEvent)) {
                            selectedTimelineEvents - timelineEvent
                        } else {
                            selectedTimelineEvents + timelineEvent
                        }
                    },
                )
            }
            Text(
                text = "Schedule",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp, top = 8.dp)
            )
            AnimatedContent(
                targetState = filteredTasks,
                transitionSpec = {
                    (fadeIn(tween(300)) + slideInVertically { it }) togetherWith
                            (fadeOut(tween(300)) + slideOutVertically { it })
                },
                label = "Tasks Animation"
            ) { tasks ->
                TimelineListView(
                    tasks = tasks,
                    selectedDate = selectedDate,
                    filter = selectedTimelineEvents,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    onTaskClick = { task ->
                        navController.navigate(NavigationRoutes.TaskDetailsScreen.createRoute(task.id))
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithDatePicker(
    scrollBehavior: TopAppBarScrollBehavior,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    TopAppBar(
        title = { Text(text = getFormattedDate(selectedDate)) },
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    painter = painterResource(R.drawable.calendar),
                    contentDescription = "Select Date",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        )
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateChange(localDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun getFormattedDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
    return date.format(formatter)
}

// --- Enums for difficulty and priority ---
enum class Difficulty(val value: Int) {
    NORMAL(1),
    HIGH(2)
}

enum class Priority(val value: Int) {
    LOW(1),
    HIGH(2)
}

// --- Task data class ---
data class Task(
    val id: Int,
    val startAt: String,
    val finishAt: String,
    val title: String,
    val description: String?,
    val category: String,
    val duration: Int,
    val difficulty: Difficulty,
    val priority: Priority,
    val isNotified: Boolean = false,
    val isDone: Boolean = false
)

// --- Helper to parse time ---
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

private fun parseTime(time: String): LocalTime = LocalTime.parse(time, timeFormatter)

// --- Check task status ---
private fun getEventStatus(startAt: String, finishAt: String, now: LocalTime, selectedDate: LocalDate): EventStatus {
    val start = parseTime(startAt)
    val end = parseTime(finishAt)

    return when {
        selectedDate.isBefore(LocalDate.now()) -> EventStatus.PAST
        selectedDate.isAfter(LocalDate.now()) -> EventStatus.FUTURE
        now.isBefore(start) -> EventStatus.FUTURE
        now.isAfter(end) -> EventStatus.PAST
        else -> EventStatus.CURRENT
    }
}

// --- Format enum names ---
private fun String.toPrettyFormat(): String {
    return this.lowercase()
        .split('_')
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
}

// --- Enum for current status of tasks ---
enum class EventStatus { PAST, CURRENT, FUTURE }

// --- Enum for Timeline filter ---
enum class TimelineEvents() {
    TASKS, FREE_SLOTS
}

// --- Class for timeline elements ---
sealed class TimelineElement {
    data class TaskElement(val task: Task, val status: EventStatus) : TimelineElement()
    data class FreeSlot(val start: String, val end: String, val status: EventStatus) : TimelineElement()
    object NowMarker : TimelineElement()
}

// --- Timeline List View ---
@Composable
fun TimelineListView(
    tasks: List<Task>,
    selectedDate: LocalDate,
    filter: Set<TimelineEvents>,
    modifier: Modifier = Modifier,
    onTaskClick: (Task) -> Unit
) {
    val currentTime = remember { mutableStateOf(LocalTime.now()) }
    val today = remember { LocalDate.now() }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = LocalTime.now()
            delay(30_000)
        }
    }

    val timelineElements = remember(currentTime.value, tasks, selectedDate) {
        buildTimelineElements(tasks, currentTime.value, selectedDate)
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(timelineElements.filter {
            when {
                TimelineEvents.TASKS in filter && TimelineEvents.FREE_SLOTS !in filter -> it is TimelineElement.TaskElement || it is TimelineElement.NowMarker
                TimelineEvents.FREE_SLOTS in filter && TimelineEvents.TASKS !in filter -> it is TimelineElement.FreeSlot || it is TimelineElement.NowMarker
                else -> true
            }
        }) { element ->
            when (element) {
                is TimelineElement.TaskElement -> TaskItem(
                    element.task,
                    element.status,
                    onClick = { onTaskClick(element.task) }
                )
                is TimelineElement.FreeSlot -> FreeSlotItem(
                    startAt = element.start,
                    finishAt = element.end,
                    status = element.status,
                    onClick = {}
                )
                TimelineElement.NowMarker -> if (today == selectedDate) NowMarker()
            }
        }
    }
}

@Composable
fun FreeSlotItem(
    startAt: String,
    finishAt: String,
    status: EventStatus,
    onClick: () -> Unit
) {
    val color = when (status) {
        EventStatus.PAST -> MaterialTheme.colorScheme.primaryContainer
        EventStatus.CURRENT -> MaterialTheme.colorScheme.error
        EventStatus.FUTURE -> MaterialTheme.colorScheme.primary
    }

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.clickable { onClick() }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(60.dp)
                    .background(isPast(status))
            )
        }
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "$startAt - $finishAt",
                fontSize = 12.sp,
                color = isPast(status)
            )
            Text(
                "Free time slot available",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = isPast(status)
            )
        }
    }
}

@Composable
fun TaskFiltersRow(
    selectedPriorities: Set<Priority>,
    selectedDifficulties: Set<Difficulty>,
    selectedCategories: Set<String>,
    selectedTimelineEvents: Set<TimelineEvents>,
    categories: List<String>,
    onPrioritySelected: (Priority) -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onCategorySelected: (String) -> Unit,
    onTimelineEventSelected:(TimelineEvents) -> Unit,
    modifier: Modifier = Modifier
) {
    val priorities = Priority.entries.toTypedArray()
    val difficulties = Difficulty.entries.toTypedArray()
    val timelineEvents = TimelineEvents.entries.toTypedArray()

    var showPriorityFilters by remember { mutableStateOf(false) }
    var showDifficultyFilters by remember { mutableStateOf(false) }
    var showCategoryFilters by remember { mutableStateOf(false) }
    var showTimelineEventFilters by remember { mutableStateOf(false) }


    Spacer(modifier = Modifier.height(8.dp))

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)) {
        FilterLabel(
            text = "Priority",
            expanded = showPriorityFilters,
            onClick = { showPriorityFilters = !showPriorityFilters }
        )
        AnimatedVisibility(visible = showPriorityFilters) {
            FilterContainer {
                FilterChipRow(
                    items = priorities.map { it.name },
                    selectedItems = selectedPriorities.map { it.name }.toSet(),
                    onItemSelected = { name ->
                        val priority = Priority.valueOf(name)
                        onPrioritySelected(priority)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        FilterLabel(
            text = "Difficulty",
            expanded = showDifficultyFilters,
            onClick = { showDifficultyFilters = !showDifficultyFilters }
        )
        AnimatedVisibility(visible = showDifficultyFilters) {
            FilterContainer {
                FilterChipRow(
                    items = difficulties.map { it.name },
                    selectedItems = selectedDifficulties.map { it.name }.toSet(),
                    onItemSelected = { name ->
                        val difficulty = Difficulty.valueOf(name)
                        onDifficultySelected(difficulty)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        FilterLabel(
            text = "Category",
            expanded = showCategoryFilters,
            onClick = { showCategoryFilters = !showCategoryFilters }
        )
        AnimatedVisibility(visible = showCategoryFilters) {
            FilterContainer {
                FilterChipRow(
                    items = categories,
                    selectedItems = selectedCategories,
                    onItemSelected = { category ->
                        onCategorySelected(category)
                    },
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        FilterLabel(
            text = "Timeline Events",
            expanded = showTimelineEventFilters,
            onClick = { showTimelineEventFilters = !showTimelineEventFilters }
        )
        AnimatedVisibility(visible = showTimelineEventFilters) {
            FilterContainer {
                FilterChipRow(
                    items = timelineEvents.map{ it.name },
                    selectedItems = selectedTimelineEvents.map{ it.name }.toSet(),
                    onItemSelected = { name ->
                        val timelineEvent = TimelineEvents.valueOf(name)
                        onTimelineEventSelected(timelineEvent)
                    }
                )
            }
        }
    }
}

@Composable
fun FilterChipRow(
    items: List<String>,
    selectedItems: Set<String>,
    onItemSelected: (String) -> Unit,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceAround
) {
    LazyRow(
        horizontalArrangement = horizontalArrangement,
        modifier = Modifier.fillMaxWidth()
    ) {
        items(items) { item ->
            val isSelected = item in selectedItems

            FilterChip(
                selected = isSelected,
                onClick = { onItemSelected(item) },
                label = { Text(item.toPrettyFormat()) },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                    selectedLabelColor = MaterialTheme.colorScheme.onTertiary,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    labelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ),
                modifier = Modifier.height(36.dp)
            )
        }
    }
}

@Composable
fun FilterLabel(
    text: String,
    expanded: Boolean,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "Arrow Rotation"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = if (expanded) "Collapse" else "Expand",
            modifier = Modifier.rotate(rotation),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FilterContainer(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp, end = 16.dp)
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            content()
        }
    }
}

fun buildTimelineElements(tasks: List<Task>, now: LocalTime, selectedDate: LocalDate): List<TimelineElement> {
    val sortedTasks = tasks.sortedBy { parseTime(it.startAt) }
    val result = mutableListOf<TimelineElement>()
    var nowMarkerPlaced = false

    val dayStart = LocalTime.MIDNIGHT
    val dayEnd = LocalTime.of(23, 59)


    if (sortedTasks.isNotEmpty()) {
        val firstStart = parseTime(sortedTasks.first().startAt)
        if (dayStart < firstStart) {
            val eventStatus = getEventStatus(dayStart.toString(), firstStart.toString(), now, selectedDate)
            result.add(
                TimelineElement.FreeSlot(
                    start = dayStart.toString(),
                    end = firstStart.toString(),
                    status = eventStatus
                )
            )
        }
    }

    var previousEndTime: LocalTime? = null

    for (task in sortedTasks) {
        val taskStart = parseTime(task.startAt)
        val taskEnd = parseTime(task.finishAt)

        if (previousEndTime != null && previousEndTime < taskStart) {
            val eventStatus = getEventStatus(previousEndTime.toString(), taskStart.toString(), now, selectedDate)
            result.add(
                TimelineElement.FreeSlot(
                    start = previousEndTime.toString(),
                    end = taskStart.toString(),
                    status = eventStatus
                )
            )
        }

        if (!nowMarkerPlaced && now.isBefore(taskStart)) {
            result.add(TimelineElement.NowMarker)
            nowMarkerPlaced = true
        }

        val status = getEventStatus(task.startAt, task.finishAt, now, selectedDate)
        result.add(TimelineElement.TaskElement(task, status))

        if (status == EventStatus.CURRENT && !nowMarkerPlaced) {
            result.add(TimelineElement.NowMarker)
            nowMarkerPlaced = true
        }

        previousEndTime = maxOf(previousEndTime ?: taskEnd, taskEnd)
    }

    if (previousEndTime != null && previousEndTime < dayEnd) {
        val eventStatus = getEventStatus(previousEndTime.toString(), dayEnd.toString(), now, selectedDate)
        result.add(
            TimelineElement.FreeSlot(
                start = previousEndTime.toString(),
                end = dayEnd.toString(),
                status = eventStatus
            )
        )
    }

    if (!nowMarkerPlaced) {
        result.add(TimelineElement.NowMarker)
    }

    return result
}

@Composable
fun TaskItem(
    task: Task,
    status: EventStatus,
    onClick: () -> Unit
) {
    val color = when (status) {
        EventStatus.PAST -> MaterialTheme.colorScheme.primaryContainer
        EventStatus.CURRENT -> MaterialTheme.colorScheme.error
        EventStatus.FUTURE -> MaterialTheme.colorScheme.primary
    }

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.clickable { onClick() }
    ) {
        // Timeline visual
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(60.dp)
                    .background(isPast(status))
            )
        }
        Spacer(modifier = Modifier.width(12.dp))

        // Task details
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "${task.startAt} - ${task.finishAt}",
                    fontSize = 12.sp,
                    color = isPast(status)
                )
                if (task.isNotified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Image(
                        painter = painterResource(R.drawable.notification),
                        contentDescription = "Notification",
                        modifier = Modifier.size(14.dp)
                    )
                }
                if (task.priority == Priority.HIGH) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Image(
                        painter = painterResource(R.drawable.priority),
                        contentDescription = "High priority",
                        modifier = Modifier.size(14.dp)
                    )
                }
                if (task.difficulty == Difficulty.HIGH) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Image(
                        painter = painterResource(R.drawable.difficulty),
                        contentDescription = "High difficulty",
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Text(
                task.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = isPast(status)
            )
            Text(
                task.category,
                fontSize = 12.sp,
                color = isPast(status)
            )
            Text(
                task.description ?: "(No description)",
                fontSize = 14.sp,
                color = isPast(status),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun isPast(status: EventStatus): Color{
    return if (status != EventStatus.PAST) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
}

@Composable
fun NowMarker() {
    val infiniteTransition = rememberInfiniteTransition()

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.error.copy(alpha = alpha))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Now",
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )
    }
}

val sampleTasks = listOf(
    Task(1, "19:00", "20:00", "Meeting with Team", "Discuss project updates", "Work", 60, Difficulty.NORMAL, Priority.LOW, isDone = true),
    Task(2, "11:30", "13:00", "Call with Client", "New requirements discussion", "Call", 30, Difficulty.HIGH, Priority.HIGH),
    Task(3, "14:00", "15:30", "Development", "Work on new feature", "Coding", 90, Difficulty.NORMAL, Priority.HIGH),
    Task(4, "16:34", "17:20", "Code Review", "Review PRs", "fdfsf", 60, Difficulty.HIGH, Priority.LOW, isNotified = true),
    Task(5, "18:00", "19:00", "Code Review", "Review PRs", "Revifdsfew", 60, Difficulty.HIGH, Priority.LOW),
    Task(6, "20:00", "21:00", "Code Review", "Review PRs", "Revaadaiew", 60, Difficulty.HIGH, Priority.LOW),
    Task(7, "20:00", "21:00", "Code Review", "Review PRs", "Revaadaiew", 60, Difficulty.HIGH, Priority.LOW),
    Task(8, "20:00", "21:00", "Code Review", "Review PRs", "Revaadaiew", 60, Difficulty.HIGH, Priority.LOW)
)

@Preview(showBackground = true)
@Composable
fun DateScreenPreview() {
    SchedulerAppTheme {
        val navController = rememberNavController()
        TaskDetailsScreen(1) { navController.popBackStack() }
    }
}