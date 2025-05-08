package com.vladosik0.schedulerapp.domain.schedule_build_helpers

import com.vladosik0.schedulerapp.data.local.Task
import com.vladosik0.schedulerapp.domain.enums.Priority
import com.vladosik0.schedulerapp.domain.formatters.getDuration
import com.vladosik0.schedulerapp.presentation.ui_state_converters.BuildScheduleScreenUiState
import com.vladosik0.schedulerapp.presentation.ui_state_converters.TaskUiStateElement
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.Duration

fun getDateWorkloads(
    tasksDateRangeLists: List<List<Task>>,
    startDate: LocalDate,
    finishDate: LocalDate
): Map<LocalDate, Int> {

    val dates = (0..ChronoUnit.DAYS.between(startDate, finishDate)).map {startDate.plusDays(it)}

    val dateRangeWorkLoad: Map<LocalDate, Int> = dates.zip(tasksDateRangeLists.map { dateTasks ->
        dateTasks.sumOf { task ->
            val taskDuration = getDuration(task.startAt.toString(), task.finishAt.toString())
            task.priority * task.difficulty * taskDuration
        }
    }).toMap()

    return dateRangeWorkLoad.toList().sortedBy {it.second}.toMap()
}

fun <K, V : Comparable<V>> getNextKeyBySortedValue(map: Map<K, V>, currentKey: K): K {
    val sortedKeys = map.entries
        .sortedBy { it.value }
        .map { it.key }

    val currentIndex = sortedKeys.indexOf(currentKey)
    return if (currentIndex != -1 && currentIndex + 1 < sortedKeys.size) {
        sortedKeys[currentIndex + 1]
    } else currentKey
}

fun <K, V : Comparable<V>> getPreviousKeyBySortedValue(map: Map<K, V>, currentKey: K): K {
    val sortedKeys = map.entries
        .sortedBy { it.value }
        .map { it.key }

    val currentIndex = sortedKeys.indexOf(currentKey)
    return if (currentIndex > 0) {
        sortedKeys[currentIndex - 1]
    } else currentKey
}

fun sortTasksAlgorithm(
    buildScheduleScreenUiState: BuildScheduleScreenUiState
): List<TaskUiStateElement> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    val fixedTasks = buildScheduleScreenUiState.temporaryTasks.filter { it.isFixed }
    val unfixedTasks = buildScheduleScreenUiState.temporaryTasks.filter { !it.isFixed}
    val newTask = TaskUiStateElement(
        id = buildScheduleScreenUiState.newTaskId,
        title = buildScheduleScreenUiState.newTaskTitle,
        category = buildScheduleScreenUiState.newTaskCategory,
        description = buildScheduleScreenUiState.newTaskDescription,
        difficulty = buildScheduleScreenUiState.newTaskDifficulty,
        priority = buildScheduleScreenUiState.newTaskPriority
    )
    val date = buildScheduleScreenUiState.recommendedDate

    val occupiedIntervals = fixedTasks.map {
        LocalDateTime.parse(it.startAt, formatter) to LocalDateTime.parse(it.finishAt, formatter)
    }.sortedBy { it.first }

    val freeIntervals = mutableListOf<Pair<LocalDateTime, LocalDateTime>>()
    val dayStart = LocalDateTime.of(date, buildScheduleScreenUiState.activityPeriodStart)
    val dayEnd = LocalDateTime.of(date, buildScheduleScreenUiState.activityPeriodFinish)

    var current = dayStart
    for((start, end) in occupiedIntervals) {
        if(Duration.between(current, start).toMinutes() >= buildScheduleScreenUiState.newTaskDurationInMinutes) {
            freeIntervals.add(current to start)
        }
        if (end.isAfter(current)) current = end
    }
    if(Duration.between(current, dayEnd).toMinutes() >= buildScheduleScreenUiState.newTaskDurationInMinutes) {
        freeIntervals.add(current to dayEnd)
    }

    val highPriorityUnfixedTasks = unfixedTasks.filter { it.priority == Priority.HIGH }
    val earliestHighPriorityStart = highPriorityUnfixedTasks.minOfOrNull {
        LocalDateTime.parse(it.startAt, formatter)
    }


    val newScheduledTask: TaskUiStateElement? = if (buildScheduleScreenUiState.considerDesirableExecutionPeriod) {
        val desiredStart = LocalDateTime.of(date, buildScheduleScreenUiState.desirableExecutionPeriodStart)
        val desiredEnd = LocalDateTime.of(date, buildScheduleScreenUiState.desirableExecutionPeriodFinish)
        val duration = Duration.ofMinutes(buildScheduleScreenUiState.newTaskDurationInMinutes.toLong())

        if(Duration.between(desiredStart, desiredEnd) >= duration) {
            val candidateStart = desiredStart
            val candidateFinish = candidateStart.plus(duration)

            val violatesPriority = newTask.priority == Priority.LOW && earliestHighPriorityStart != null &&
                    candidateStart.isBefore(earliestHighPriorityStart)

            if(!violatesPriority) {
                newTask.copy(
                    startAt = candidateStart.format(formatter),
                    finishAt = candidateFinish.plus(duration).format(formatter),
                )
            } else null
        } else null
    } else {
        val slot = freeIntervals.firstOrNull {
            val candidateStart = it.first
            val violatesPriority = newTask.priority == Priority.LOW && earliestHighPriorityStart != null &&
                    candidateStart.isBefore(earliestHighPriorityStart)
            Duration.between(candidateStart, it.second).toMinutes() >= buildScheduleScreenUiState.newTaskDurationInMinutes &&
                    !violatesPriority
        }
        if(slot != null) {
            newTask.copy(
                startAt = slot.first.format(formatter),
                finishAt = slot.first.plusMinutes(
                    buildScheduleScreenUiState.newTaskDurationInMinutes.toLong()
                ).format(formatter)
            )
        } else null
    }

    if(newScheduledTask == null) {
        println("No suitable time slot found for new task")
        return buildScheduleScreenUiState.temporaryTasks.toList()
    }

    val allUnfixed = (unfixedTasks + newScheduledTask).groupBy {it.priority}.toMutableMap()
    val now = LocalDateTime.of(date, buildScheduleScreenUiState.activityPeriodStart)
    val finalScheduled = mutableListOf<TaskUiStateElement>()

    var currentStart = now
    fixedTasks.sortedBy { LocalDateTime.parse(it.startAt, formatter) }.forEach { task ->
        val fixedStart = LocalDateTime.parse(task.startAt, formatter)
        if (currentStart.isBefore(fixedStart)) {
            Priority.entries.forEach { priority ->
                val samePriorityTasks = allUnfixed[priority] ?: emptyList()
                for(t in samePriorityTasks.sortedBy { it.id }) {
                    val duration = Duration.between(
                        LocalDateTime.parse(t.startAt, formatter),
                        LocalDateTime.parse(t.finishAt, formatter)
                    )
                    if (Duration.between(currentStart, fixedStart) >= duration) {
                        finalScheduled.add (
                            t.copy(
                                startAt = currentStart.format(formatter),
                                finishAt = currentStart.plus(duration).format(formatter)
                            )
                        )
                        currentStart = currentStart.plus(duration)
                    }
                }
                allUnfixed -= priority
            }
        }
        finalScheduled.add(task)
        currentStart = LocalDateTime.parse(task.finishAt, formatter)
    }

    Priority.entries.forEach { priority ->
        val samePriorityTasks = allUnfixed[priority] ?: emptyList()
        for (t in samePriorityTasks.sortedBy { it.id }) {
            val duration = Duration.between(
                LocalDateTime.parse(t.startAt, formatter),
                LocalDateTime.parse(t.finishAt, formatter)
            )
            if(currentStart.plus(duration).isBefore(dayEnd)) {
                finalScheduled.add(
                    t.copy(
                        startAt = currentStart.format(formatter),
                        finishAt = currentStart.plus(duration).format(formatter)
                    )
                )
                currentStart = currentStart.plus(duration)
            }
        }
    }

    return finalScheduled
}