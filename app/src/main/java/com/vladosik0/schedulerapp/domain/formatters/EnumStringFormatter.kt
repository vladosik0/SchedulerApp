package com.vladosik0.schedulerapp.domain.formatters

fun String.toPrettyFormat(): String {
    return this.lowercase()
        .split('_')
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
}