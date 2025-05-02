package com.vladosik0.schedulerapp.model.parsers

import java.time.LocalDate
import java.time.LocalDateTime

fun parseDateTimeStringToDate(date: String): LocalDate = LocalDateTime.parse(date).toLocalDate()