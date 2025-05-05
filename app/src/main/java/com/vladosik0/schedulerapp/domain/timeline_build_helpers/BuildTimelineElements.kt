package com.vladosik0.schedulerapp.domain.timeline_build_helpers

import com.vladosik0.schedulerapp.domain.enums.EventStatus
import com.vladosik0.schedulerapp.domain.parsers.parseDateTimeStringToTime
import com.vladosik0.schedulerapp.presentation.converters.TaskUiStateElement
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun buildTimelineElements(tasks: List<TaskUiStateElement>, now: LocalDateTime, selectedDate: LocalDate): List<TimelineElement> {
    val sortedTasks = tasks.sortedBy { parseDateTimeStringToTime(it.startAt) }
    val result = mutableListOf<TimelineElement>()
    var nowMarkerPlaced = false

    val dayStart = selectedDate.atTime(LocalTime.MIDNIGHT)
    val dayEnd = selectedDate.atTime(LocalTime.of(23, 59))

    if(sortedTasks.isEmpty()) {
        val eventStatus = getEventStatus(dayStart, dayEnd, now)
        result.add(
            TimelineElement.FreeSlot(
                start = dayStart.toString(),
                finish = dayEnd.toString(),
                status = eventStatus
            )
        )
        result.add(TimelineElement.NowMarker)
        return result
    }


    val firstStart = LocalDateTime.parse(sortedTasks.first().startAt)
    if (dayStart < firstStart) {
        val eventStatus = getEventStatus(dayStart, firstStart, now)
        result.add(
            TimelineElement.FreeSlot(
                start = dayStart.toString(),
                finish = firstStart.toString(),
                status = eventStatus
            )
        )
    }

    var previousEndTime: LocalDateTime? = null

    for (task in sortedTasks) {
        val taskStart = LocalDateTime.parse(task.startAt)
        val taskEnd = LocalDateTime.parse(task.finishAt)

        if (previousEndTime != null && previousEndTime < taskStart) {
            val eventStatus = getEventStatus(previousEndTime, taskStart, now)
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
            LocalDateTime.parse(task.startAt),
            LocalDateTime.parse(task.finishAt),
            now
        )
        result.add(TimelineElement.TaskElement(task, status))

        if (status == EventStatus.CURRENT && !nowMarkerPlaced) {
            result.add(TimelineElement.NowMarker)
            nowMarkerPlaced = true
        }

        previousEndTime = maxOf(previousEndTime ?: taskEnd, taskEnd)
    }

    if (previousEndTime != null && previousEndTime < dayEnd) {
        val eventStatus = getEventStatus(previousEndTime, dayEnd, now)
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
fun getEventStatus(startAt: LocalDateTime, finishAt: LocalDateTime, now: LocalDateTime): EventStatus {

    return when {
        now.isBefore(startAt) -> EventStatus.FUTURE
        now.isAfter(finishAt) -> EventStatus.PAST
        else -> EventStatus.CURRENT
    }
}

// --- Get event status based on time
fun getEventStatus(startAt: String, finishAt: String): String {
    val now = LocalDateTime.now()
    val startTime = LocalDateTime.parse(startAt)
    val finishTime = LocalDateTime.parse(finishAt)

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