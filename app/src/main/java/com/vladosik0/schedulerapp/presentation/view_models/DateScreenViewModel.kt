package com.vladosik0.schedulerapp.presentation.view_models

import kotlinx.coroutines.flow.combine
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladosik0.schedulerapp.data.local.repositories.TasksRepository
import com.vladosik0.schedulerapp.domain.enums.Difficulty
import com.vladosik0.schedulerapp.domain.enums.Priority
import com.vladosik0.schedulerapp.domain.enums.TimelineEvents
import com.vladosik0.schedulerapp.domain.validators.areOnlyTasksPicked
import com.vladosik0.schedulerapp.presentation.TaskUiStateElement
import com.vladosik0.schedulerapp.presentation.toTaskUiStateElement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate


data class DateScreenUiState(
    val tasks: List<TaskUiStateElement> = emptyList()
    //filteredTasks
)

class DateScreenViewModel(
    private val tasksRepository: TasksRepository
): ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    fun updateSelectedDate(newDate: LocalDate) {
        _selectedDate.value = newDate
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dateScreenUiState: StateFlow<DateScreenUiState> = _selectedDate
        .flatMapLatest { date ->
            tasksRepository.getTasksByDate(date.toString())
                .map { tasks ->
                    DateScreenUiState(tasks = tasks.map {it.toTaskUiStateElement()})
                }
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            DateScreenUiState()
        )

    val distinctCategories: StateFlow<List<String>> = dateScreenUiState.map { uiState ->
        uiState.tasks.map { it.category }.distinct()
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _selectedPriorities = MutableStateFlow(setOf<Priority>())
    val selectedPriorities: StateFlow<Set<Priority>> = _selectedPriorities

    private val _selectedDifficulties = MutableStateFlow(setOf<Difficulty>())
    val selectedDifficulties: StateFlow<Set<Difficulty>> = _selectedDifficulties

    private val _selectedCategories = MutableStateFlow(setOf<String>())
    val selectedCategories: StateFlow<Set<String>> = _selectedCategories

    private val _selectedTimelineEvents = MutableStateFlow(setOf<TimelineEvents>(TimelineEvents.TASKS))
    val selectedTimelineEvents: StateFlow<Set<TimelineEvents>> = _selectedTimelineEvents

    fun updateSelectedPriorities(priority: Priority) {
        if (!_selectedPriorities.value.contains(priority) && areOnlyTasksPicked(_selectedTimelineEvents.value)) {
            _selectedPriorities.value = _selectedPriorities.value + priority
        } else {
            _selectedPriorities.value = _selectedPriorities.value - priority
        }
    }

    fun updateSelectedDifficulties(difficulty: Difficulty) {
        if (!_selectedDifficulties.value.contains(difficulty) && areOnlyTasksPicked(_selectedTimelineEvents.value)) {
            _selectedDifficulties.value = _selectedDifficulties.value + difficulty
        } else {
            _selectedDifficulties.value = _selectedDifficulties.value - difficulty
        }
    }

    fun updateSelectedCategories(category: String) {
        if (!_selectedCategories.value.contains(category) && areOnlyTasksPicked(_selectedTimelineEvents.value)) {
            _selectedCategories.value = _selectedCategories.value + category
        } else {
            _selectedCategories.value = _selectedCategories.value - category
        }
    }

    fun updateSelectedTimelineEvents(timelineEvent: TimelineEvents) {
        if (!_selectedTimelineEvents.value.contains(timelineEvent)) {
            _selectedTimelineEvents.value = _selectedTimelineEvents.value + timelineEvent
        } else {
            _selectedTimelineEvents.value = _selectedTimelineEvents.value - timelineEvent
        }
    }

    val filteredTasks: StateFlow<List<TaskUiStateElement>> = combine(
        dateScreenUiState,
        selectedPriorities,
        selectedDifficulties,
        selectedCategories
    ) { uiState, priorities, difficulties, categories ->
        uiState.tasks.filter { task ->
            (priorities.isEmpty() || task.priority in priorities) &&
                    (difficulties.isEmpty() || task.difficulty in difficulties) &&
                    (categories.isEmpty() || task.category in categories)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        emptyList()
    )
}