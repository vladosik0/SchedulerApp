package com.vladosik0.schedulerapp.domain.schedule_build_helpers

import com.vladosik0.schedulerapp.data.local.Task
import com.vladosik0.schedulerapp.domain.enums.Priority
import com.vladosik0.schedulerapp.domain.formatters.getDuration
import com.vladosik0.schedulerapp.presentation.ui_state_converters.BuildScheduleScreenUiState
import com.vladosik0.schedulerapp.presentation.ui_state_converters.TaskUiStateElement
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.Duration
import java.time.format.DateTimeFormatter

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

fun sortTasksAlgorithm(buildScheduleScreenUiState: BuildScheduleScreenUiState): List<TaskUiStateElement> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    val date = buildScheduleScreenUiState.recommendedDate
    val dayStart = LocalDateTime.of(date, buildScheduleScreenUiState.activityPeriodStart)
    val dayEnd = LocalDateTime.of(date, buildScheduleScreenUiState.activityPeriodFinish)
    val duration = Duration.ofMinutes(buildScheduleScreenUiState.newTaskDurationInMinutes.toLong())

    val fixedTasks = buildScheduleScreenUiState.temporaryTasks.filter { it.isFixed }
        .sortedBy { LocalDateTime.parse(it.startAt, formatter) }

    val unfixedTasks = buildScheduleScreenUiState.temporaryTasks.filter { !it.isFixed }.toMutableList()
    val highPriorityUnfixedTasks = unfixedTasks.filter { it.priority == Priority.HIGH }.toMutableList()
    val lowPriorityUnfixedTasks = unfixedTasks.filter { it.priority == Priority.LOW }.toMutableList()

    val newTask = TaskUiStateElement(
        id = buildScheduleScreenUiState.newTaskId,
        title = buildScheduleScreenUiState.newTaskTitle,
        category = buildScheduleScreenUiState.newTaskCategory,
        description = if(buildScheduleScreenUiState.newTaskDescription == "") "" else buildScheduleScreenUiState.newTaskDescription,
        difficulty = buildScheduleScreenUiState.newTaskDifficulty,
        priority = buildScheduleScreenUiState.newTaskPriority,
        startAt = "",
        finishAt = ""
    )

    // Add new task at the end of the corresponding priority list
    if (newTask.priority == Priority.HIGH) {
        highPriorityUnfixedTasks.add(newTask)
    } else {
        lowPriorityUnfixedTasks.add(newTask)
    }

    val occupiedIntervals = fixedTasks.map {
        LocalDateTime.parse(it.startAt, formatter) to LocalDateTime.parse(it.finishAt, formatter)
    }.sortedBy { it.first }

    // Calculate free intervals between occupied ones
    fun buildFreeIntervals(occupied: List<Pair<LocalDateTime, LocalDateTime>>): MutableList<Pair<LocalDateTime, LocalDateTime>> {
        val free = mutableListOf<Pair<LocalDateTime, LocalDateTime>>()
        var pointer = dayStart
        for ((start, end) in occupied) {
            if (pointer.isBefore(start)) free.add(pointer to start)
            if (end.isAfter(pointer)) pointer = end
        }
        if (pointer.isBefore(dayEnd)) free.add(pointer to dayEnd)
        return free
    }

    // Assign start and finish time for each task to simplify further processing
    fun assignDummyDurations(tasks: MutableList<TaskUiStateElement>) {
        tasks.replaceAll {
            val dur = if (it.id == newTask.id) duration else Duration.between(
                LocalDateTime.parse(it.startAt, formatter),
                LocalDateTime.parse(it.finishAt, formatter)
            )
            it.copy(
                startAt = dayStart.format(formatter),
                finishAt = dayStart.plus(dur).format(formatter)
            )
        }
    }

    // Place tasks into available free slots
    fun tryPlaceTasks(
        tasks: MutableList<TaskUiStateElement>,
        freeSlots: MutableList<Pair<LocalDateTime, LocalDateTime>>
    ): List<TaskUiStateElement> {
        val placed = mutableListOf<TaskUiStateElement>()
        val toPlace = tasks.toMutableList()

        for ((index, slot) in freeSlots.withIndex()) {
            var current = slot.first
            while (current.plusMinutes(1) <= slot.second && toPlace.isNotEmpty()) {
                val task = toPlace.firstOrNull()
                val taskDuration = if (task?.id == newTask.id) duration else Duration.between(
                    LocalDateTime.parse(task!!.startAt, formatter),
                    LocalDateTime.parse(task.finishAt, formatter)
                )

                if (Duration.between(current, slot.second) >= taskDuration) {
                    placed.add(
                        task.copy(
                            startAt = current.format(formatter),
                            finishAt = current.plus(taskDuration).format(formatter)
                        )
                    )
                    toPlace.remove(task)
                    current = current.plus(taskDuration)
                    freeSlots[index] = current to slot.second
                } else {
                    break
                }
            }
        }
        return placed
    }

    assignDummyDurations(highPriorityUnfixedTasks)
    assignDummyDurations(lowPriorityUnfixedTasks)

    val freeIntervals = buildFreeIntervals(occupiedIntervals)

    val placedHigh = tryPlaceTasks(highPriorityUnfixedTasks, freeIntervals)
    val placedLow = tryPlaceTasks(lowPriorityUnfixedTasks, freeIntervals)

    val totalUnfixedCount = highPriorityUnfixedTasks.size + lowPriorityUnfixedTasks.size
    val totalPlacedCount = placedHigh.size + placedLow.size

    if (totalPlacedCount < totalUnfixedCount) {
        return buildScheduleScreenUiState.temporaryTasks
    }

    val allTasks = (fixedTasks + placedHigh + placedLow).sortedBy {
        LocalDateTime.parse(it.startAt, formatter)
    }.toMutableList()

    // Swap tasks if one can exactly fit into another's slot to reduce fragmentation
    fun advancedOptimize(tasks: MutableList<TaskUiStateElement>) {
        var i = 0
        while (i < tasks.size - 1) {
            val current = tasks[i]

            // Skip fixed tasks
            if (current.isFixed) {
                i++
                continue
            }

            val next = tasks[i + 1]

            val currentEnd = LocalDateTime.parse(current.finishAt, formatter)
            val nextStart = LocalDateTime.parse(next.startAt, formatter)
            val gap = Duration.between(currentEnd, nextStart)

            // Find task to place within current time + gap
            for (j in i + 1 until tasks.size) {
                val candidate = tasks[j]

                // Skip fixed task
                if (candidate.isFixed) continue

                val candidateStart = LocalDateTime.parse(candidate.startAt, formatter)
                val candidateFinish = LocalDateTime.parse(candidate.finishAt, formatter)
                val candidateDuration = Duration.between(candidateStart, candidateFinish)
                val currentDuration = Duration.between(
                    LocalDateTime.parse(current.startAt, formatter),
                    LocalDateTime.parse(current.finishAt, formatter)
                )

                if (candidateDuration == currentDuration.plus(gap)) {
                    // Check overlay
                    val candidateOverlap = tasks.subList(i + 1, j).any {
                        val taskStart = LocalDateTime.parse(it.startAt, formatter)
                        val taskFinish = LocalDateTime.parse(it.finishAt, formatter)
                        candidateStart.isBefore(taskFinish) && taskStart.isBefore(candidateFinish)
                    }

                    if (candidateOverlap) {
                        continue // Skip if overlay
                    }

                    // Changing positions for current and candidate
                    tasks[i] = candidate.copy(
                        startAt = LocalDateTime.parse(current.startAt, formatter).format(formatter),
                        finishAt = LocalDateTime.parse(current.startAt, formatter).plus(candidateDuration).format(formatter)
                    )
                    tasks[j] = current.copy(
                        startAt = candidate.startAt,
                        finishAt = candidateStart.plus(currentDuration).format(formatter)
                    )

                    // Compress all tasks if possible
                    for (k in j + 1 until tasks.size) {
                        val prevEnd = LocalDateTime.parse(tasks[k - 1].finishAt, formatter)
                        val task = tasks[k]
                        val duration = Duration.between(
                            LocalDateTime.parse(task.startAt, formatter),
                            LocalDateTime.parse(task.finishAt, formatter)
                        )
                        tasks[k] = task.copy(
                            startAt = prevEnd.format(formatter),
                            finishAt = prevEnd.plus(duration).format(formatter)
                        )
                    }
                    break
                }
            }
            i++
        }
    }


    // Reorder tasks to reduce internal gaps in a group
    fun optimizeGroup(tasks: MutableList<TaskUiStateElement>) {
        tasks.sortBy { LocalDateTime.parse(it.startAt, formatter) }

        var i = 0
        while (i < tasks.size - 1) {
            val current = tasks[i]
            val next = tasks[i + 1]
            val currentEnd = LocalDateTime.parse(current.finishAt, formatter)
            val nextStart = LocalDateTime.parse(next.startAt, formatter)
            val gap = Duration.between(currentEnd, nextStart)

            if (gap.toMinutes() > 0) {
                for (j in i + 2 until tasks.size) {
                    val candidate = tasks[j]
                    val candidateDuration = Duration.between(
                        LocalDateTime.parse(candidate.startAt, formatter),
                        LocalDateTime.parse(candidate.finishAt, formatter)
                    )
                    if (candidateDuration <= gap) {
                        val temp = tasks[i + 1]
                        tasks[i + 1] = candidate
                        tasks[j] = temp
                        break
                    }
                }
            }
            i++
        }
    }

    val groupedByPriority = allTasks.groupBy { it.priority }

    val finalSchedule = mutableListOf<TaskUiStateElement>()

    // HIGH priority: first fill gaps with suitable tasks, then compact
    finalSchedule += groupedByPriority[Priority.HIGH]?.toMutableList()?.also {
        advancedOptimize(it)
        optimizeGroup(it)
    } ?: listOf()

    // LOW priority: same process
    finalSchedule += groupedByPriority[Priority.LOW]?.toMutableList()?.also {
        advancedOptimize(it)
        optimizeGroup(it)
    } ?: listOf()

    fun reduceGapBetweenHighAndLow(finalSchedule: MutableList<TaskUiStateElement>) {
        val highTasks = finalSchedule.filter { it.priority == Priority.HIGH }
        val lowTasks = finalSchedule.filter { it.priority == Priority.LOW }

        if (highTasks.isEmpty() || lowTasks.isEmpty()) return

        val lastHigh = highTasks.maxBy { LocalDateTime.parse(it.finishAt, formatter) }
        val firstLow = lowTasks.minBy { LocalDateTime.parse(it.startAt, formatter) }

        val lastHighFinish = LocalDateTime.parse(lastHigh.finishAt, formatter)
        val firstLowStart = LocalDateTime.parse(firstLow.startAt, formatter)

        val shiftGap = Duration.between(lastHighFinish, firstLowStart)
        if (shiftGap.isNegative || shiftGap.isZero) return

        // Shift all LOW priority tasks earlier
        for (i in finalSchedule.indices) {
            val task = finalSchedule[i]
            if (task.priority == Priority.LOW && !task.isFixed) {
                val start = LocalDateTime.parse(task.startAt, formatter).minus(shiftGap)
                val finish = LocalDateTime.parse(task.finishAt, formatter).minus(shiftGap)

                // Ensure no overlap with last high-priority task
                if (start >= lastHighFinish) {
                    finalSchedule[i] = task.copy(
                        startAt = start.format(formatter), finishAt = finish.format(formatter)
                    )
                }
            }
        }
    }

    reduceGapBetweenHighAndLow(finalSchedule)

    return finalSchedule.sortedBy { LocalDateTime.parse(it.startAt, formatter) }
}