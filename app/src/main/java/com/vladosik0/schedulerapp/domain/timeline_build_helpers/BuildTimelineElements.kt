package com.vladosik0.schedulerapp.domain.timeline_build_helpers

import com.vladosik0.schedulerapp.domain.enums.EventStatus
import com.vladosik0.schedulerapp.domain.parsers.parseDateTimeStringToTime
import com.vladosik0.schedulerapp.presentation.TaskUiStateElement
import java.time.LocalDate
import java.time.LocalTime

fun buildTimelineElements(tasks: List<TaskUiStateElement>, now: LocalTime, selectedDate: LocalDate): List<TimelineElement> {
    val sortedTasks = tasks.sortedBy { parseDateTimeStringToTime(it.startAt) }
    val result = mutableListOf<TimelineElement>()
    var nowMarkerPlaced = false

    val dayStart = LocalTime.MIDNIGHT
    val dayEnd = LocalTime.of(23, 59)


    if (sortedTasks.isNotEmpty()) {
        val firstStart = parseDateTimeStringToTime(sortedTasks.first().startAt)
        if (dayStart < firstStart) {
            val eventStatus = getEventStatus(dayStart, firstStart, now, selectedDate)
            result.add(
                TimelineElement.FreeSlot(
                    start = dayStart.toString(),
                    finish = firstStart.toString(),
                    status = eventStatus
                )
            )
        }
    }

    var previousEndTime: LocalTime? = null

    for (task in sortedTasks) {
        val taskStart = parseDateTimeStringToTime(task.startAt)
        val taskEnd = parseDateTimeStringToTime(task.finishAt)

        if (previousEndTime != null && previousEndTime < taskStart) {
            val eventStatus = getEventStatus(previousEndTime, taskStart, now, selectedDate)
            result.add(
                TimelineElement.FreeSlot(
                    start = previousEndTime.toString(),
                    finish = taskStart.toString(),
                    status = eventStatus
                )
            )
        }

        if (!nowMarkerPlaced && now.isBefore(taskStart)) {
            result.add(TimelineElement.NowMarker)
            nowMarkerPlaced = true
        }

        val status = getEventStatus(
            parseDateTimeStringToTime(task.startAt),
            parseDateTimeStringToTime(task.finishAt),
            now,
            selectedDate
        )
        result.add(TimelineElement.TaskElement(task, status))

        if (status == EventStatus.CURRENT && !nowMarkerPlaced) {
            result.add(TimelineElement.NowMarker)
            nowMarkerPlaced = true
        }

        previousEndTime = maxOf(previousEndTime ?: taskEnd, taskEnd)
    }

    if (previousEndTime != null && previousEndTime < dayEnd) {
        val eventStatus = getEventStatus(previousEndTime, dayEnd, now, selectedDate)
        result.add(
            TimelineElement.FreeSlot(
                start = previousEndTime.toString(),
                finish = dayEnd.toString(),
                status = eventStatus
            )
        )
    }

    if (!nowMarkerPlaced) {
        result.add(TimelineElement.NowMarker)
    }

    return result
}

// --- Get event status based on current time and date ---
fun getEventStatus(startAt: LocalTime, finishAt: LocalTime, now: LocalTime, selectedDate: LocalDate): EventStatus {

    return when {
        selectedDate.isBefore(LocalDate.now()) -> EventStatus.PAST
        selectedDate.isAfter(LocalDate.now()) -> EventStatus.FUTURE
        now.isBefore(startAt) -> EventStatus.FUTURE
        now.isAfter(finishAt) -> EventStatus.PAST
        else -> EventStatus.CURRENT
    }
}

// --- Get event status based on time
fun getEventStatus(startAt: String, finishAt: String): String {
    val now = LocalTime.now()
    val startTime = parseDateTimeStringToTime(startAt)
    val finishTime = parseDateTimeStringToTime(finishAt)

    return when {
        now.isBefore(startTime) -> "Planned"
        now.isAfter(finishTime) -> "Completed"
        else -> "In Process"
    }
}

// --- Class for timeline elements ---
sealed class TimelineElement {
    data class TaskElement(val task: TaskUiStateElement, val status: EventStatus) : TimelineElement()
    data class FreeSlot(val start: String, val finish: String, val status: EventStatus) : TimelineElement()
    object NowMarker : TimelineElement()
}