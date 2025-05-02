package com.vladosik0.schedulerapp.domain

import com.vladosik0.schedulerapp.domain.enums.Difficulty
import com.vladosik0.schedulerapp.domain.enums.Priority

val sampleTasks = listOf(
    Task(1, "2025-05-02T19:00", "2025-05-02T20:00", "Meeting with Team", "Discuss project updates", "Work", 60, Difficulty.NORMAL, Priority.LOW, isDone = true),
    Task(2, "2025-05-02T11:30", "2025-05-02T13:00", "Call with Client", "New requirements discussion", "Call", 30, Difficulty.HIGH, Priority.HIGH),
    Task(3, "2025-05-02T14:00", "2025-05-02T15:30", "Development", "Work on new feature", "Coding", 90, Difficulty.NORMAL, Priority.HIGH),
    Task(4, "2025-05-02T16:34", "2025-05-02T17:20", "Code Review", "Review PRs", "fdfsf", 60, Difficulty.HIGH, Priority.LOW, isNotified = true),
    Task(5, "2025-05-02T18:00", "2025-05-02T19:00", "Code Review", "Review PRs", "Revifdsfew", 60, Difficulty.HIGH, Priority.LOW),
    Task(6, "2025-05-02T20:00", "2025-05-02T21:00", "Code Review", "Review PRs", "Revaadaiew", 60, Difficulty.HIGH, Priority.LOW),
    Task(7, "2025-05-02T20:00", "2025-05-02T21:00", "Code Review", "Review PRs", "Revaadaiew", 60, Difficulty.HIGH, Priority.LOW),
    Task(8, "2025-05-02T20:00", "2025-05-02T21:00", "Code Review", "Review PRs", "Revaadaiew", 60, Difficulty.HIGH, Priority.LOW)
)