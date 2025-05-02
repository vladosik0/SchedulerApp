package com.vladosik0.schedulerapp.model.formatters

fun String.toPrettyFormat(): String {
    return this.lowercase()
        .split('_')
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
}