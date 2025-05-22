package com.windsurf.todoapp.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.windsurf.todoapp.data.model.Task
import com.windsurf.todoapp.data.preferences.PreferencesManager
import com.windsurf.todoapp.data.preferences.UserPreferences
import com.windsurf.todoapp.domain.usecase.GetActiveTasksUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service responsible for scheduling task reminders using AlarmManager.
 */
@Singleton
class TaskScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getActiveTasksUseCase: GetActiveTasksUseCase,
    private val preferencesManager: PreferencesManager
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedules a reminder for a task.
     */
    fun scheduleTaskReminder(task: Task) {
        // Only schedule if the task is not completed and has a future due date
        if (task.isCompleted || LocalDate.parse(task.dueDate).isBefore(LocalDate.now())) {
            return
        }

        // Create the intent for the alarm
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TASK_ID, task.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Calculate the notification time based on user preferences
        val notificationTime = calculateNotificationTime(task)
        val notificationTimeMillis = notificationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        // Schedule the alarm
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notificationTimeMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                notificationTimeMillis,
                pendingIntent
            )
        }
    }

    /**
     * Cancels a scheduled reminder for a task.
     */
    fun cancelTaskReminder(task: Task) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        
        // If there's a pending intent, cancel it
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    /**
     * Schedules reminders for all active tasks.
     */
    suspend fun scheduleAllTaskReminders() {
        val preferences = preferencesManager.userPreferencesFlow.first()
        
        // Only proceed if notifications are enabled
        if (!preferences.notificationsEnabled) {
            return
        }
        
        // Get all active tasks and schedule reminders for each
        val tasks = getActiveTasksUseCase(true).first()
        for (task in tasks) {
            scheduleTaskReminder(task)
        }
    }

    /**
     * Calculates the time when a notification should be shown for a task.
     */
    private suspend fun calculateNotificationTime(task: Task): LocalDateTime {
        val preferences = preferencesManager.userPreferencesFlow.first()
        
        // Get the task's due date
        val dueDate = LocalDate.parse(task.dueDate)
        
        // If the task has a specific time, use that
        if (task.dueTime != null) {
            val dueTime = LocalTime.parse(task.dueTime)
            // Notify 1 hour before the task is due
            val notificationTime = LocalDateTime.of(dueDate, dueTime).minusHours(1)
            
            // If the notification time is in the past, notify immediately
            if (notificationTime.isBefore(LocalDateTime.now())) {
                return LocalDateTime.now().plusMinutes(1)
            }
            
            return notificationTime
        }
        
        // Otherwise, use the default notification time from preferences
        val notificationTime = LocalTime.of(preferences.notificationHour, preferences.notificationMinute)
        
        // If the due date is today and notification time has already passed, notify immediately
        val today = LocalDate.now()
        if (dueDate.isEqual(today) && LocalTime.now().isAfter(notificationTime)) {
            return LocalDateTime.now().plusMinutes(1)
        }
        
        // If the due date is tomorrow or later, notify at the preferred time on the due date
        return LocalDateTime.of(dueDate, notificationTime)
    }
}
