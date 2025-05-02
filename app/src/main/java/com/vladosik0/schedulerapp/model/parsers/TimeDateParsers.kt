package com.vladosik0.schedulerapp.model.parsers

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun parseDateTimeStringToTime(time: String): LocalTime = LocalDateTime.parse(time).toLocalTime()

fun parseTimeStringToTime(time: String): LocalTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))