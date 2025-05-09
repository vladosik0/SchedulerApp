package com.vladosik0.schedulerapp.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladosik0.schedulerapp.data.local.repositories.TasksRepository
import com.vladosik0.schedulerapp.domain.enums.Difficulty
import com.vladosik0.schedulerapp.domain.enums.Priority
import com.vladosik0.schedulerapp.domain.formatters.getFormattedDateFromString
import com.vladosik0.schedulerapp.domain.parsers.parseDateTimeStringToTime
import com.vladosik0.schedulerapp.domain.schedule_build_helpers.getDateWorkloads
import com.vladosik0.schedulerapp.domain.schedule_build_helpers.getNextKeyBySortedValue
import com.vladosik0.schedulerapp.domain.schedule_build_helpers.getPreviousKeyBySortedValue
import com.vladosik0.schedulerapp.domain.schedule_build_helpers.sortTasksAlgorithm
import com.vladosik0.schedulerapp.presentation.ui_state_converters.BuildScheduleScreenUiState
import com.vladosik0.schedulerapp.presentation.ui_state_converters.TaskUiStateElement
import com.vladosik0.schedulerapp.presentation.ui_state_converters.toTaskUiStateElement
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SharedScheduleScreensViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _buildScheduleScreenUiState = MutableStateFlow(BuildScheduleScreenUiState())
    val buildScheduleScreenUiState: StateFlow<BuildScheduleScreenUiState> =
        _buildScheduleScreenUiState


    private val _startDateErrorMessage = MutableStateFlow("")
    val startDateErrorMessage: StateFlow<String> = _startDateErrorMessage

    private val _finishDateErrorMessage = MutableStateFlow("")
    val finishDateErrorMessage: StateFlow<String> = _finishDateErrorMessage

    private val _dateOutOfRangeErrorMessage = MutableStateFlow("")
    val dateOutOfRangeErrorMessage: StateFlow<String> = _dateOutOfRangeErrorMessage

    private val _startActivityPeriodErrorMessage = MutableStateFlow("")
    val startActivityPeriodErrorMessage: StateFlow<String> = _startActivityPeriodErrorMessage

    private val _finishActivityPeriodErrorMessage = MutableStateFlow("")
    val finishActivityPeriodErrorMessage: StateFlow<String> = _finishActivityPeriodErrorMessage

    private val _startDesirablePeriodErrorMessage = MutableStateFlow("")
    val startDesirablePeriodErrorMessage: StateFlow<String> = _startDesirablePeriodErrorMessage

    private val _finishDesirablePeriodErrorMessage = MutableStateFlow("")
    val finishDesirablePeriodErrorMessage: StateFlow<String> = _finishDesirablePeriodErrorMessage

    private val _noFreeTimeForNewTaskErrorMessage = MutableStateFlow("")
    val noFreeTimeForNewTaskErrorMessage: StateFlow<String> = _noFreeTimeForNewTaskErrorMessage

    private val dateWorkLoads = mutableMapOf<LocalDate, Int>()

    private val allTasksForRecommendedDate = mutableListOf<TaskUiStateElement>()

    fun updateInitialBuildScheduleScreenUiState(
        id: Int,
        title: String,
        description: String,
        category: String,
        priority: Priority,
        difficulty: Difficulty,
    ) {
        _buildScheduleScreenUiState.update { it.copy(
            newTaskId = id,
            newTaskTitle = title,
            newTaskDescription = if(description == "") "No description" else description,
            newTaskCategory = category,
            newTaskPriority = priority,
            newTaskDifficulty = difficulty
        ) }
    }
    fun updateStartDate(startDate: LocalDate) {
        if (startDate.isAfter(_buildScheduleScreenUiState.value.finishDate)) {
            _startDateErrorMessage.value = "Start Date must be before Finish Date!"
        } else {
            _buildScheduleScreenUiState.update { it.copy(startDate = startDate) }
            _startDateErrorMessage.value = ""
        }
    }

    fun updateFinishDate(finishDate: LocalDate) {
        if (finishDate.isBefore(_buildScheduleScreenUiState.value.startDate)) {
            _finishDateErrorMessage.value = "Finish Date must be After Start Date!"
        } else {
            _buildScheduleScreenUiState.update { it.copy(finishDate = finishDate) }
            _startDateErrorMessage.value = ""
        }
    }

    fun getRecommendedDate() {
        _buildScheduleScreenUiState.update { it.copy(
            isRecommendedDateLoading = true
        ) }
        viewModelScope.launch {
            delay(500)
            val startDate = _buildScheduleScreenUiState.value.startDate
            val finishDate = _buildScheduleScreenUiState.value.finishDate
            val days = ChronoUnit.DAYS.between(startDate, finishDate)

            val tasksDateRangeLists = coroutineScope {
                (0..days).map { offset ->
                    val date = startDate.plusDays(offset).toString()
                    async {
                        tasksRepository.getTasksByDate(date).first()
                    }
                }.awaitAll()
            }

            getDateWorkloads(tasksDateRangeLists, startDate, finishDate).forEach { dateWorkLoad ->
                dateWorkLoads[dateWorkLoad.key] = dateWorkLoad.value
            }

            if (_buildScheduleScreenUiState.value.newTaskPriority == Priority.HIGH &&
                    _buildScheduleScreenUiState.value.newTaskDifficulty == Difficulty.NORMAL) {
                _buildScheduleScreenUiState.update { it.copy(
                    recommendedDate = startDate,
                    isRecommendedDateLoading = false
                ) }
            } else {
                _buildScheduleScreenUiState.update { it.copy(
                    recommendedDate = dateWorkLoads.entries.first().key,
                    isRecommendedDateLoading = false
                ) }
            }
        }
    }

    fun updateRecommendedDate(recommendedDate: LocalDate) {
        _buildScheduleScreenUiState.update {
            it.copy(
                recommendedDate = recommendedDate,
                temporaryTasks = mutableListOf<TaskUiStateElement>()
            )
        }
    }

    fun isTextFieldEnabled(): Boolean {
        return dateWorkLoads.isNotEmpty()
    }

    fun getNextRecommendedDate() {
        val nextDate = getNextKeyBySortedValue(
            dateWorkLoads,
            _buildScheduleScreenUiState.value.recommendedDate
        )
        if (nextDate == _buildScheduleScreenUiState.value.recommendedDate) {
            _dateOutOfRangeErrorMessage.value = "You've reached the date range boundary!"
        } else {
            updateRecommendedDate(nextDate)
            _dateOutOfRangeErrorMessage.value = ""
        }
    }

    fun getPreviousRecommendedDate() {
        val previousDate = getPreviousKeyBySortedValue(
            dateWorkLoads,
            _buildScheduleScreenUiState.value.recommendedDate
        )
        if (previousDate == _buildScheduleScreenUiState.value.recommendedDate) {
            _dateOutOfRangeErrorMessage.value = "You've reached the date range boundary!"
        } else {
            updateRecommendedDate(previousDate)
            _dateOutOfRangeErrorMessage.value = ""
        }
    }

    fun updateStartActivityPeriodTime(startActivityTime: LocalTime) {
        if (startActivityTime.isAfter(_buildScheduleScreenUiState.value.activityPeriodFinish)) {
            _startActivityPeriodErrorMessage.value = "Start Time must be before Finish Time!"
        } else {
            _buildScheduleScreenUiState.update { it.copy(
                activityPeriodStart = startActivityTime,
                desirableExecutionPeriodStart = startActivityTime
            ) }
            _startActivityPeriodErrorMessage.value = ""
            _buildScheduleScreenUiState.value = _buildScheduleScreenUiState.value.copy(
                temporaryTasks = mutableListOf<TaskUiStateElement>()
            )
        }
    }

    fun updateStartDesirablePeriodTime(startDesirableTime: LocalTime) {
        if (startDesirableTime.isAfter(_buildScheduleScreenUiState.value.desirableExecutionPeriodFinish)) {
            _startDesirablePeriodErrorMessage.value = "Start Desirable Time must be before Finish Desirable Time!"
        } else if (isDesirableIntervalWithinActivityInterval()) {
            _buildScheduleScreenUiState.update { it.copy(desirableExecutionPeriodStart = startDesirableTime) }
            _startDesirablePeriodErrorMessage.value = ""
            _finishDesirablePeriodErrorMessage.value = ""
        }
    }

    private fun isDesirableIntervalWithinActivityInterval(
        activityPeriodStart: LocalTime = _buildScheduleScreenUiState.value.activityPeriodStart,
        activityPeriodFinish: LocalTime = _buildScheduleScreenUiState.value.activityPeriodFinish
    ): Boolean {
        return if(_buildScheduleScreenUiState.value.considerDesirableExecutionPeriod) {
            if(_buildScheduleScreenUiState.value.desirableExecutionPeriodStart < activityPeriodStart){
                _startDesirablePeriodErrorMessage.value = "Start Desirable Time must be within activity period!"
                false
            } else if (_buildScheduleScreenUiState.value.desirableExecutionPeriodFinish > activityPeriodFinish) {
                _finishDesirablePeriodErrorMessage.value = "Finish Desirable Time must be within activity period!"
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    fun updateFinishActivityPeriodTime(finishActivityTime: LocalTime) {
        if (finishActivityTime.isBefore(_buildScheduleScreenUiState.value.activityPeriodStart)) {
            _finishActivityPeriodErrorMessage.value = "Finish Time must be after Start Time!"
        } else {
            _buildScheduleScreenUiState.update { it.copy(
                activityPeriodFinish = finishActivityTime,
                desirableExecutionPeriodFinish = finishActivityTime
            ) }
            _finishActivityPeriodErrorMessage.value = ""
            _buildScheduleScreenUiState.value = _buildScheduleScreenUiState.value.copy(
                temporaryTasks = mutableListOf<TaskUiStateElement>()
            )
        }
    }

    fun updateFinishDesirablePeriodTime(finishDesirableTime: LocalTime) {
        if (finishDesirableTime.isBefore(_buildScheduleScreenUiState.value.desirableExecutionPeriodStart)) {
            _startDesirablePeriodErrorMessage.value = "Finish Desirable Time must be after Start Desirable Time!"
        } else if (isDesirableIntervalWithinActivityInterval()) {
            _buildScheduleScreenUiState.update { it.copy(desirableExecutionPeriodFinish = finishDesirableTime) }
            _startDesirablePeriodErrorMessage.value = ""
            _finishDesirablePeriodErrorMessage.value = ""
        }
    }

    fun changeFixedStatus(task: TaskUiStateElement) {
        val currentState = _buildScheduleScreenUiState.value
        val taskIndex = currentState.temporaryTasks.indexOfFirst { it.id == task.id }
        val updatedTask =
            if (task.isFixed) currentState.temporaryTasks[taskIndex].copy(isFixed = false)
            else currentState.temporaryTasks[taskIndex].copy(isFixed = true)
        val updatedTasks = currentState.temporaryTasks.toMutableList()
        updatedTasks[taskIndex] = updatedTask
        _buildScheduleScreenUiState.value = currentState.copy(temporaryTasks = updatedTasks)
    }

    fun getTasksByDateInActivityPeriod() {
        val currentState = _buildScheduleScreenUiState.value
        allTasksForRecommendedDate.clear()
        viewModelScope.launch {
            tasksRepository.getTasksByDate(currentState.recommendedDate.toString()).first().forEach {
                allTasksForRecommendedDate.add(it.toTaskUiStateElement())
            }

            val overlappingTasks = allTasksForRecommendedDate.filter { task ->
                val start = parseDateTimeStringToTime(task.startAt)
                val end = parseDateTimeStringToTime(task.finishAt)
                end > currentState.activityPeriodStart && start < currentState.activityPeriodFinish
            }

            val updatedActivityPeriodStart = overlappingTasks.minOfOrNull { parseDateTimeStringToTime(it.startAt) }
                ?.let { minStart -> minOf(minStart, currentState.activityPeriodStart) }
                ?: currentState.activityPeriodStart

            val updatedActivityPeriodFinish = overlappingTasks.maxOfOrNull { parseDateTimeStringToTime(it.finishAt) }
                ?.let { maxEnd -> maxOf(maxEnd, currentState.activityPeriodFinish) }
                ?: currentState.activityPeriodFinish

            val temporaryTasksList: MutableList<TaskUiStateElement> = overlappingTasks.map {
                it
            }.toMutableList()

            _buildScheduleScreenUiState.value = currentState.copy(
                temporaryTasks = temporaryTasksList,
                activityPeriodStart = updatedActivityPeriodStart,
                activityPeriodFinish = updatedActivityPeriodFinish,
                desirableExecutionPeriodStart = updatedActivityPeriodStart,
                desirableExecutionPeriodFinish = updatedActivityPeriodFinish
            )
        }
    }

    private fun onChangeNewTaskDuration(minutes: Int) {
        _buildScheduleScreenUiState.update { it.copy(newTaskDurationInMinutes = minutes) }
    }

    fun validateDurationMinutes(minutes: Int): Boolean {
        val desirablePeriodDuration = Duration.between(
            _buildScheduleScreenUiState.value.desirableExecutionPeriodStart,
            _buildScheduleScreenUiState.value.desirableExecutionPeriodFinish
        ).toMinutes().toInt()
        val activityPeriodDuration = Duration.between(
            _buildScheduleScreenUiState.value.activityPeriodStart,
            _buildScheduleScreenUiState.value.activityPeriodFinish
        ).toMinutes().toInt()
        if (minutes > desirablePeriodDuration && _buildScheduleScreenUiState.value.considerDesirableExecutionPeriod) {
            _noFreeTimeForNewTaskErrorMessage.value = "Duration of the task is bigger than desirable period!"
            return false
        } else if(minutes >= activityPeriodDuration){
            _noFreeTimeForNewTaskErrorMessage.value = "Duration of the task is bigger than activity period!"
            return false
        } else {
            onChangeNewTaskDuration(minutes)
            _noFreeTimeForNewTaskErrorMessage.value = ""
            return true
        }
    }

    fun isBuildScheduleButtonAvailable(): Boolean {
        return isTextFieldEnabled() &&
                _buildScheduleScreenUiState.value.temporaryTasks != BuildScheduleScreenUiState().temporaryTasks &&
                validateDurationMinutes(_buildScheduleScreenUiState.value.newTaskDurationInMinutes) &&
                isDesirableIntervalWithinActivityInterval()
    }

    fun changeDesiredPeriodUsageStatus() {
        _buildScheduleScreenUiState.update {
            it.copy(
                considerDesirableExecutionPeriod = !_buildScheduleScreenUiState.value.considerDesirableExecutionPeriod
            ) }
        isDesirableIntervalWithinActivityInterval()
    }

    private val _newScheduleScreenUiState = MutableStateFlow<NewScheduleScreenUiState>(
        NewScheduleScreenUiState.Loading
    )
    val newScheduleScreenUiState: StateFlow<NewScheduleScreenUiState> = _newScheduleScreenUiState

    fun buildSchedule() {
        viewModelScope.launch {
            val newSchedule = sortTasksAlgorithm(_buildScheduleScreenUiState.value)
            for (i in allTasksForRecommendedDate.indices) {
                val newTask = newSchedule.find {
                    it.id == allTasksForRecommendedDate[i].id
                }
                if(newTask != null) {
                    allTasksForRecommendedDate[i] = newTask
                }
            }
            allTasksForRecommendedDate.add(newSchedule.find { it.id == 0 }!!)
            delay(1000)
            if(newSchedule == _buildScheduleScreenUiState.value.temporaryTasks) {
                _newScheduleScreenUiState.value = NewScheduleScreenUiState.Failure(
                    tasks = allTasksForRecommendedDate,
                    message = "There is no place for new task in current schedule! Please change" +
                            " parameters for schedule build or change existing tasks properties"
                )
            } else {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
                allTasksForRecommendedDate.sortBy { LocalDateTime.parse(it.startAt, formatter) }
                _newScheduleScreenUiState.value = NewScheduleScreenUiState.Success(
                    tasks = allTasksForRecommendedDate
                )
            }
        }
    }

    fun getDateForNewScheduleScreen() : String {
        return getFormattedDateFromString(_buildScheduleScreenUiState.value.recommendedDate.atStartOfDay().toString())
    }

    fun cleanUiStates() {
        dateWorkLoads.clear()
        allTasksForRecommendedDate.clear()
        _buildScheduleScreenUiState.update { it.copy(
            recommendedDate = LocalDate.now(),
            temporaryTasks = mutableListOf()
        ) }
    }

    fun updateNewScheduleScreenUiState() {
        _newScheduleScreenUiState.value = NewScheduleScreenUiState.Loading
    }

    fun onSaveSchedule() {
        viewModelScope.launch {

        }
    }



}

sealed class NewScheduleScreenUiState {
    object Loading : NewScheduleScreenUiState()
    data class Success(val tasks: List<TaskUiStateElement>) : NewScheduleScreenUiState()
    data class Failure(val message: String, val tasks: List<TaskUiStateElement>) : NewScheduleScreenUiState()
}