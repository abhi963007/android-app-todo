package com.windsurf.todoapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.windsurf.todoapp.data.model.Priority
import com.windsurf.todoapp.data.model.Task
import com.windsurf.todoapp.data.model.TaskCategory

/**
 * Room database class for the Todo application.
 * Defines the database configuration and serves as the main access point for the underlying connection.
 */
@Database(entities = [Task::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}

/**
 * Type converters for Room database.
 * Used to convert between Room database types and application data types.
 */
class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }
    
    @TypeConverter
    fun toPriority(value: String): Priority {
        return Priority.valueOf(value)
    }
    
    @TypeConverter
    fun fromTaskCategory(category: TaskCategory): String {
        return category.name
    }
    
    @TypeConverter
    fun toTaskCategory(value: String): TaskCategory {
        return TaskCategory.valueOf(value)
    }
}
