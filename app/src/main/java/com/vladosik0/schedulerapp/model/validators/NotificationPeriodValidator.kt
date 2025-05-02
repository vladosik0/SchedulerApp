package com.vladosik0.schedulerapp.model.validators

import java.time.Duration
import java.time.LocalDateTime

fun isPeriodLogical(value: Int, unit: String, startAt: LocalDateTime, finishAt: LocalDateTime): Boolean {
    val periodDuration = when (unit) {
        "Minutes" -> Duration.ofMinutes(value.toLong())
        "Hours" -> Duration.ofHours(value.toLong())
        "Days" -> Duration.ofDays(value.toLong())
        "Weeks" -> Duration.ofDays(value.toLong() * 7)
        else -> Duration.ZERO
    }

    val taskDuration = Duration.between(startAt, finishAt)

    return !taskDuration.isNegative && periodDuration <= taskDuration
}