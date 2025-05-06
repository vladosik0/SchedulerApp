package com.vladosik0.schedulerapp.presentation.view_models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.vladosik0.schedulerapp.data.local.repositories.TasksRepository
import com.vladosik0.schedulerapp.domain.enums.Difficulty
import com.vladosik0.schedulerapp.domain.enums.Priority
import com.vladosik0.schedulerapp.presentation.ui_state_converters.BuildScheduleScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

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

    private val _buildScheduleScreenUiState = MutableStateFlow(BuildScheduleScreenUiState())
    val buildScheduleScreenUiState: StateFlow<BuildScheduleScreenUiState> = _buildScheduleScreenUiState

    private val _startDateErrorMessage = MutableStateFlow("")
    val startDateErrorMessage: StateFlow<String> = _startDateErrorMessage

    private val _finishDateErrorMessage = MutableStateFlow("")
    val finishDateErrorMessage: StateFlow<String> = _finishDateErrorMessage

    fun updateStartDate(startDate: LocalDate) {
        if(startDate.isAfter(_buildScheduleScreenUiState.value.finishDate)){
            _startDateErrorMessage.value = "Start Date must be before Finish Date"
        } else {
            _buildScheduleScreenUiState.value = _buildScheduleScreenUiState.value.copy(startDate = startDate)
        }
    }

    fun updateFinishDate(finishDate: LocalDate) {
        if(finishDate.isBefore(_buildScheduleScreenUiState.value.startDate)){
            _finishDateErrorMessage.value = "Finish Date must be After Start Date"
        } else {
            _buildScheduleScreenUiState.value = _buildScheduleScreenUiState.value.copy(finishDate = finishDate)
        }

    }





}