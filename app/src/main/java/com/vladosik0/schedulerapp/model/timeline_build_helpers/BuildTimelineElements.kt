package com.vladosik0.schedulerapp.model.timeline_build_helpers

import com.vladosik0.schedulerapp.model.Task
import com.vladosik0.schedulerapp.model.enums.EventStatus
import com.vladosik0.schedulerapp.model.parsers.parseDateTimeStringToTime
import com.vladosik0.schedulerapp.model.parsers.parseTimeStringToTime
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun buildTimelineElements(tasks: List<Task>, now: LocalTime, selectedDate: LocalDate): List<TimelineElement> {
    val sortedTasks = tasks.sortedBy { parseDateTimeStringToTime(it.startAt) }
    val result = mutableListOf<TimelineElement>()
    var nowMarkerPlaced = false

    val dayStart = LocalTime.MIDNIGHT
    val dayEnd = LocalTime.of(23, 59)


    if (sortedTasks.isNotEmpty()) {
        val firstStart = parseDateTimeStringToTime(sortedTasks.first().startAt)
        if (dayStart < firstStart) {
            val eventStatus = getEventStatus(dayStart.toString(), firstStart.toString(), now, selectedDate)
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
            val eventStatus = getEventStatus(previousEndTime.toString(), taskStart.toString(), now, selectedDate)
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
fun getEventStatus(startAt: String, finishAt: String, now: LocalTime, selectedDate: LocalDate): EventStatus {

    val start = parseTimeStringToTime(startAt)
    val finish = parseTimeStringToTime(finishAt)

    return when {
        selectedDate.isBefore(LocalDate.now()) -> EventStatus.PAST
        selectedDate.isAfter(LocalDate.now()) -> EventStatus.FUTURE
        now.isBefore(start) -> EventStatus.FUTURE
        now.isAfter(finish) -> EventStatus.PAST
        else -> EventStatus.CURRENT
    }
}

// --- Get event status based on time
fun getEventStatus(startAt: String, finishAt: String): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val now = LocalTime.now()
    val startTime = LocalTime.parse(startAt, formatter)
    val finishTime = LocalTime.parse(finishAt, formatter)

    return when {
        now.isBefore(startTime) -> "Planned"
        now.isAfter(finishTime) -> "Completed"
        else -> "In Process"
    }
}

// --- Class for timeline elements ---
sealed class TimelineElement {
    data class TaskElement(val task: Task, val status: EventStatus) : TimelineElement()
    data class FreeSlot(val start: String, val finish: String, val status: EventStatus) : TimelineElement()
    object NowMarker : TimelineElement()
}