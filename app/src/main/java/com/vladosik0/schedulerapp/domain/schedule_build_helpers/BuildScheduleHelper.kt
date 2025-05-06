package com.vladosik0.schedulerapp.domain.schedule_build_helpers

import com.vladosik0.schedulerapp.data.local.Task
import com.vladosik0.schedulerapp.domain.formatters.getDuration
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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