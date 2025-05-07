package com.vladosik0.schedulerapp.domain.formatters

import com.vladosik0.schedulerapp.domain.parsers.parseDateTimeStringToTime
import java.time.Duration

fun formatDuration(startTimeString: String, finishTimeString: String): String {
    if(startTimeString == "" || finishTimeString == "") {
        return ""
    }
    val minutes = getDuration(startTimeString, finishTimeString)
    val hours = minutes / 60
    val remainingMinutes = minutes % 60

    return buildString {
        if(hours > 0) append("${hours}h ")
        if(remainingMinutes > 0) append ("${remainingMinutes}min")
    }
}

fun getDuration(startTimeString: String, finishTimeString: String): Int {
    val startTime = parseDateTimeStringToTime(startTimeString)
    val finishTime = parseDateTimeStringToTime(finishTimeString)
    val minutes = Duration.between(startTime, finishTime).toMinutes().toInt()
    return minutes
}