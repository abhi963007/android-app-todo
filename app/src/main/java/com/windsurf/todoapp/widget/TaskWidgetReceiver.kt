package com.windsurf.todoapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.windsurf.todoapp.R
import com.windsurf.todoapp.data.model.Task
import com.windsurf.todoapp.domain.usecase.GetTasksByDueDateUseCase
import com.windsurf.todoapp.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * Widget provider for displaying today's tasks on the home screen.
 */
@AndroidEntryPoint
class TaskWidgetReceiver : AppWidgetProvider() {
    
    @Inject
    lateinit var getTasksByDueDateUseCase: GetTasksByDueDateUseCase
    
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    /**
     * Updates a single widget instance.
     */
    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        // Create a CoroutineScope to perform asynchronous operations
        CoroutineScope(Dispatchers.IO).launch {
            // Get today's tasks
            val today = LocalDate.now().toString()
            val tasks = getTasksByDueDateUseCase(LocalDate.now()).first()
            
            // Update the widget UI on the main thread
            updateWidgetUI(context, appWidgetManager, appWidgetId, tasks)
        }
    }
    
    /**
     * Updates the widget UI with the provided tasks.
     */
    private fun updateWidgetUI(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        tasks: List<Task>
    ) {
        // Create RemoteViews for the widget layout
        val views = RemoteViews(context.packageName, R.layout.widget_task_list)
        
        // Set up click intent for the widget
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.widget_title, pendingIntent)
        
        // If there are no tasks, show the empty view
        if (tasks.isEmpty()) {
            views.setTextViewText(R.id.widget_title, context.getString(R.string.widget_title) + " (0)")
            views.setViewVisibility(R.id.widget_list_view, android.view.View.GONE)
            views.setViewVisibility(R.id.widget_empty_view, android.view.View.VISIBLE)
        } else {
            views.setTextViewText(
                R.id.widget_title,
                context.getString(R.string.widget_title) + " (${tasks.size})"
            )
            views.setViewVisibility(R.id.widget_list_view, android.view.View.VISIBLE)
            views.setViewVisibility(R.id.widget_empty_view, android.view.View.GONE)
            
            // Set up the widget's ListView
            val adapter = TaskWidgetService()
            views.setRemoteAdapter(R.id.widget_list_view, Intent(context, TaskWidgetService::class.java))
        }
        
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
