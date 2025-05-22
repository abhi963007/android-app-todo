package com.windsurf.todoapp.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.windsurf.todoapp.R
import com.windsurf.todoapp.data.model.Task
import com.windsurf.todoapp.domain.usecase.GetTaskByIdUseCase
import com.windsurf.todoapp.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * BroadcastReceiver for handling task alarms.
 * Shows a notification when a task alarm is triggered.
 */
@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var getTaskByIdUseCase: GetTaskByIdUseCase
    
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1)
        if (taskId != -1L) {
            CoroutineScope(Dispatchers.IO).launch {
                val task = getTaskByIdUseCase(taskId)
                task?.let {
                    showTaskNotification(context, it)
                }
            }
        }
    }
    
    /**
     * Shows a notification for the given task.
     */
    private fun showTaskNotification(context: Context, task: Task) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create intent for opening the app when notification is tapped
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_TASK_ID, task.id)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            task.id.toInt(),
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Build the notification
        val notification = NotificationCompat.Builder(context, "task_reminders")
            .setSmallIcon(R.drawable.empty_tasks)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_content, task.title))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        // Show the notification
        notificationManager.notify(task.id.toInt(), notification)
    }
    
    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
    }
}
