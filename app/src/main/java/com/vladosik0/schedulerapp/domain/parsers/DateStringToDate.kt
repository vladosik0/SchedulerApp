package com.vladosik0.schedulerapp.domain.parsers

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun parseDateStringToDate(dateString: String): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(dateString, formatter)

}