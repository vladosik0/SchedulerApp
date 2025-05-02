package com.vladosik0.schedulerapp.model.parsers

import java.time.LocalDateTime
import java.time.LocalTime

fun parseDateTimeStringToTime(time: String): LocalTime = LocalDateTime.parse(time).toLocalTime()