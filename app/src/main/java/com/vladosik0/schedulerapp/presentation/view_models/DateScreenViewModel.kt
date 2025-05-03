package com.vladosik0.schedulerapp.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladosik0.schedulerapp.data.local.repositories.TasksRepository
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


data class DateScreenUiState(val tasks: List<TaskUiStateElement> = emptyList())

class DateScreenViewModel(
    private val tasksRepository: TasksRepository
): ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    fun updateSelectedDate(newDate: LocalDate) {
        _selectedDate.value = newDate
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val dateScreenUiState: StateFlow<DateScreenUiState> = _selectedDate
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


}