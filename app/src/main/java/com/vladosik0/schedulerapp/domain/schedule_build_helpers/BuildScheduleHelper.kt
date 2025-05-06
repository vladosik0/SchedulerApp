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