package com.vladosik0.schedulerapp.data.local.repositories

import com.vladosik0.schedulerapp.data.local.Task
import kotlinx.coroutines.flow.Flow

interface TasksRepository {

    fun getAllTasksStream(): Flow<List<Task>>

    fun getTaskStream(id: Int): Flow<Task?>

    fun getTasksByDate(date: String): Flow<List<Task>>

    suspend fun insertTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

}