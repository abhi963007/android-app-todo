package com.windsurf.todoapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

/**
 * Enum class representing the priority of a task.
 */
enum class Priority {
    LOW, MEDIUM, HIGH
}

/**
 * Enum class representing the category of a task.
 */
enum class TaskCategory {
    PERSONAL, WORK, SHOPPING, HEALTH, OTHER
}

/**
 * Entity class representing a task in the application.
 */
@Entity(tableName = "tasks")
@Serializable
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: String, // ISO formatted date: yyyy-MM-dd
    val dueTime: String? = null, // ISO formatted time: HH:mm
    val priority: Priority,
    val category: TaskCategory,
    val isCompleted: Boolean = false,
    val isFavorite: Boolean = false,
    val createdDate: String = LocalDate.now().toString(), // ISO formatted date: yyyy-MM-dd
    val modifiedDate: String = LocalDate.now().toString() // ISO formatted date: yyyy-MM-dd
) {
    fun isDue(): Boolean {
        val today = LocalDate.now()
        val taskDueDate = LocalDate.parse(dueDate)
        return !isCompleted && (taskDueDate.isBefore(today) || taskDueDate.isEqual(today))
    }
    
    fun isDueToday(): Boolean {
        val today = LocalDate.now()
        val taskDueDate = LocalDate.parse(dueDate)
        return !isCompleted && taskDueDate.isEqual(today)
    }
    
    fun getDisplayTime(): String? {
        return dueTime?.let {
            val time = LocalTime.parse(it)
            val hour = time.hour
            val minute = time.minute
            val amPm = if (hour < 12) "AM" else "PM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            String.format("%d:%02d %s", displayHour, minute, amPm)
        }
    }
}
