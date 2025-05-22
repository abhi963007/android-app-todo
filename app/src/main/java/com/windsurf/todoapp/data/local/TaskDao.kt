package com.windsurf.todoapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.windsurf.todoapp.data.model.Priority
import com.windsurf.todoapp.data.model.Task
import com.windsurf.todoapp.data.model.TaskCategory
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for Task entity.
 * Provides methods to interact with the tasks table in the database.
 */
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY " +
            "CASE WHEN :sortByDueDate = 1 THEN dueDate END ASC, " +
            "CASE WHEN :sortByDueDate = 0 THEN " +
            "CASE priority " +
            "WHEN 'HIGH' THEN 0 " +
            "WHEN 'MEDIUM' THEN 1 " +
            "WHEN 'LOW' THEN 2 END " +
            "END ASC")
    fun getAllTasks(sortByDueDate: Boolean = true): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY " +
            "CASE WHEN :sortByDueDate = 1 THEN dueDate END ASC, " +
            "CASE WHEN :sortByDueDate = 0 THEN " +
            "CASE priority " +
            "WHEN 'HIGH' THEN 0 " +
            "WHEN 'MEDIUM' THEN 1 " +
            "WHEN 'LOW' THEN 2 END " +
            "END ASC")
    fun getActiveTasks(sortByDueDate: Boolean = true): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY modifiedDate DESC")
    fun getCompletedTasks(): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE isFavorite = 1 AND isCompleted = 0 ORDER BY " +
            "CASE WHEN :sortByDueDate = 1 THEN dueDate END ASC, " +
            "CASE WHEN :sortByDueDate = 0 THEN " +
            "CASE priority " +
            "WHEN 'HIGH' THEN 0 " +
            "WHEN 'MEDIUM' THEN 1 " +
            "WHEN 'LOW' THEN 2 END " +
            "END ASC")
    fun getFavoriteTasks(sortByDueDate: Boolean = true): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE category = :category AND isCompleted = 0")
    fun getTasksByCategory(category: TaskCategory): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') AND isCompleted = 0")
    fun searchTasks(query: String): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?
    
    @Query("SELECT * FROM tasks WHERE dueDate = :date AND isCompleted = 0")
    fun getTasksByDueDate(date: String): Flow<List<Task>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)
    
    @Update
    suspend fun updateTask(task: Task)
    
    @Delete
    suspend fun deleteTask(task: Task)
    
    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)
    
    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteAllCompletedTasks()
}
