package com.windsurf.todoapp.data.repository

import com.windsurf.todoapp.data.local.TaskDao
import com.windsurf.todoapp.data.model.Task
import com.windsurf.todoapp.data.model.TaskCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of TaskRepository that uses Room DAO for database operations.
 */
@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {
    
    override fun getAllTasks(sortByDueDate: Boolean): Flow<List<Task>> = 
        taskDao.getAllTasks(sortByDueDate)
    
    override fun getActiveTasks(sortByDueDate: Boolean): Flow<List<Task>> = 
        taskDao.getActiveTasks(sortByDueDate)
    
    override fun getCompletedTasks(): Flow<List<Task>> = 
        taskDao.getCompletedTasks()
    
    override fun getFavoriteTasks(sortByDueDate: Boolean): Flow<List<Task>> = 
        taskDao.getFavoriteTasks(sortByDueDate)
    
    override fun getTasksByCategory(category: TaskCategory): Flow<List<Task>> = 
        taskDao.getTasksByCategory(category)
    
    override fun searchTasks(query: String): Flow<List<Task>> = 
        taskDao.searchTasks(query)
    
    override fun getTasksByDueDate(date: String): Flow<List<Task>> = 
        taskDao.getTasksByDueDate(date)
    
    override suspend fun getTaskById(id: Long): Task? = 
        taskDao.getTaskById(id)
    
    override suspend fun insertTask(task: Task): Long = 
        taskDao.insertTask(task)
    
    override suspend fun insertTasks(tasks: List<Task>) = 
        taskDao.insertTasks(tasks)
    
    override suspend fun updateTask(task: Task) = 
        taskDao.updateTask(task)
    
    override suspend fun deleteTask(task: Task) = 
        taskDao.deleteTask(task)
    
    override suspend fun deleteTaskById(id: Long) = 
        taskDao.deleteTaskById(id)
    
    override suspend fun deleteAllCompletedTasks() = 
        taskDao.deleteAllCompletedTasks()
}
