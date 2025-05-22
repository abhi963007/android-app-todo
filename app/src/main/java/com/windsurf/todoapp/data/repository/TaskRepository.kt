package com.windsurf.todoapp.data.repository

import com.windsurf.todoapp.data.model.Priority
import com.windsurf.todoapp.data.model.Task
import com.windsurf.todoapp.data.model.TaskCategory
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Task operations.
 * Defines the contract for data operations related to Tasks.
 */
interface TaskRepository {
    fun getAllTasks(sortByDueDate: Boolean): Flow<List<Task>>
    fun getActiveTasks(sortByDueDate: Boolean): Flow<List<Task>>
    fun getCompletedTasks(): Flow<List<Task>>
    fun getFavoriteTasks(sortByDueDate: Boolean): Flow<List<Task>>
    fun getTasksByCategory(category: TaskCategory): Flow<List<Task>>
    fun searchTasks(query: String): Flow<List<Task>>
    fun getTasksByDueDate(date: String): Flow<List<Task>>
    suspend fun getTaskById(id: Long): Task?
    suspend fun insertTask(task: Task): Long
    suspend fun insertTasks(tasks: List<Task>)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun deleteTaskById(id: Long)
    suspend fun deleteAllCompletedTasks()
}
