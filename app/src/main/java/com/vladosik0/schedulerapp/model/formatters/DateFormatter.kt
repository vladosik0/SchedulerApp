package com.vladosik0.schedulerapp.model.formatters

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun getFormattedDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
    return date.format(formatter)
}