package com.vladosik0.schedulerapp.presentation.view_models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladosik0.schedulerapp.data.local.repositories.TasksRepository
import com.vladosik0.schedulerapp.domain.enums.Difficulty
import com.vladosik0.schedulerapp.domain.enums.Priority
import com.vladosik0.schedulerapp.domain.schedule_build_helpers.getDateWorkloads
import com.vladosik0.schedulerapp.domain.schedule_build_helpers.getNextKeyBySortedValue
import com.vladosik0.schedulerapp.domain.schedule_build_helpers.getPreviousKeyBySortedValue
import com.vladosik0.schedulerapp.presentation.ui_state_converters.BuildScheduleScreenUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class BuildScheduleScreenViewModel(
    private val tasksRepository: TasksRepository,
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

    private val _dateOutOfRangeErrorMessage = MutableStateFlow("")
    val dateOutOfRangeErrorMessage: StateFlow<String> = _dateOutOfRangeErrorMessage

    private val dateWorkLoads = mutableMapOf<LocalDate, Int>()

    fun updateStartDate(startDate: LocalDate) {
        if(startDate.isAfter(_buildScheduleScreenUiState.value.finishDate)){
            _startDateErrorMessage.value = "Start Date must be before Finish Date!"
        } else {
            _buildScheduleScreenUiState.update{it.copy(startDate = startDate)}
            _startDateErrorMessage.value = ""
        }
    }

    fun updateFinishDate(finishDate: LocalDate) {
        if(finishDate.isBefore(_buildScheduleScreenUiState.value.startDate)){
            _finishDateErrorMessage.value = "Finish Date must be After Start Date!"
        } else {
            _buildScheduleScreenUiState.update{it.copy(finishDate = finishDate)}
            _startDateErrorMessage.value = ""
        }
    }

    fun getRecommendedDate() {
        viewModelScope.launch(Dispatchers.IO) {
            val startDate = _buildScheduleScreenUiState.value.startDate
            val finishDate = _buildScheduleScreenUiState.value.finishDate
            val days = ChronoUnit.DAYS.between(startDate, finishDate)

            val tasksDateRangeLists = coroutineScope {
                (0..days).map { offset ->
                    val date = startDate.plusDays(offset).toString()
                    async{
                        tasksRepository.getTasksByDate(date).first()
                    }
                }.awaitAll()
            }

            getDateWorkloads(tasksDateRangeLists, startDate, finishDate).forEach { dateWorkLoad ->
                dateWorkLoads[dateWorkLoad.key] = dateWorkLoad.value
            }

            if(getPriority() == Priority.HIGH && getDifficulty() == Difficulty.NORMAL) {
                _buildScheduleScreenUiState.update{it.copy(recommendedDate = startDate)}
            } else {
                _buildScheduleScreenUiState.update{it.copy(recommendedDate = dateWorkLoads.entries.first().key)}
            }

        }
    }

    fun updateRecommendedDate(recommendedDate: LocalDate) {
        _buildScheduleScreenUiState.update{it.copy(recommendedDate = recommendedDate)}
    }

    fun isTextFieldEnabled(): Boolean {
        return dateWorkLoads.isNotEmpty()
    }

    fun getNextRecommendedDate() {
        val nextDate = getNextKeyBySortedValue(dateWorkLoads, _buildScheduleScreenUiState.value.recommendedDate)
        if(nextDate == _buildScheduleScreenUiState.value.recommendedDate) {
            _dateOutOfRangeErrorMessage.value = "You've reached the date range boundary!"
        } else {
            _buildScheduleScreenUiState.update { it.copy(recommendedDate = nextDate) }
            _dateOutOfRangeErrorMessage.value = ""
        }
    }

    fun getPreviousRecommendedDate() {
        val previousDate = getPreviousKeyBySortedValue(dateWorkLoads, _buildScheduleScreenUiState.value.recommendedDate)
        _buildScheduleScreenUiState.update { it.copy(recommendedDate = previousDate) }
    }
}