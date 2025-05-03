package com.vladosik0.schedulerapp.data.local.repositories

import com.vladosik0.schedulerapp.data.local.Task
import com.vladosik0.schedulerapp.data.local.TaskDao
import kotlinx.coroutines.flow.Flow

class OfflineTasksRepository(private val taskDao: TaskDao): TasksRepository {
    override fun getAllTasksStream(): Flow<List<Task>> = taskDao.getAllTasks()

    override fun getTaskStream(id: Int): Flow<Task?> = taskDao.getTask(id)

    override fun getTasksByDate(date: String): Flow<List<Task>> = taskDao.getTasksByDate(date)

    override suspend fun insertTask(task: Task) = taskDao.insert(task)

    override suspend fun updateTask(task: Task) = taskDao.update(task)

    override suspend fun deleteTask(task: Task) = taskDao.delete(task)

}