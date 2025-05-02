package com.vladosik0.schedulerapp.domain.parsers

import java.time.LocalDateTime
import java.time.LocalTime

fun parseDateTimeStringToTime(time: String): LocalTime = LocalDateTime.parse(time).toLocalTime()