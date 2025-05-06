package com.vladosik0.schedulerapp.presentation.view_models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.vladosik0.schedulerapp.data.local.repositories.TasksRepository
import com.vladosik0.schedulerapp.domain.enums.Difficulty
import com.vladosik0.schedulerapp.domain.enums.Priority

class BuildScheduleScreenViewModel(
    tasksRepository: TasksRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: Int? = savedStateHandle["taskId"]

    private val title: String? = savedStateHandle["title"]

    private val description: String? = savedStateHandle["description"]

    private val category: String? = savedStateHandle["category"]

    private val priority: Int? = savedStateHandle["priority"]

    private val difficulty: Int? = savedStateHandle["difficulty"]

    fun getTitle() : String = title.toString()

    fun getDescription() : String = description.toString()

    fun getCategory(): String = category.toString()

    fun getPriority(): Priority = if(priority == 1) Priority.LOW else Priority.HIGH

    fun getDifficulty(): Difficulty = if(difficulty == 1) Difficulty.NORMAL else Difficulty.HIGH





}