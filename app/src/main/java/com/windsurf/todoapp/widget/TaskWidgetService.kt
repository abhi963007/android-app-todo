package com.windsurf.todoapp.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.windsurf.todoapp.R
import com.windsurf.todoapp.data.model.Task
import com.windsurf.todoapp.domain.usecase.GetTasksByDueDateUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import javax.inject.Inject

/**
 * Service that provides data for the task widget's ListView.
 */
@AndroidEntryPoint
class TaskWidgetService : RemoteViewsService() {
    
    @Inject
    lateinit var getTasksByDueDateUseCase: GetTasksByDueDateUseCase
    
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return TaskWidgetItemFactory(applicationContext, getTasksByDueDateUseCase)
    }
    
    /**
     * Factory for creating the individual items in the widget's ListView.
     */
    class TaskWidgetItemFactory(
        private val context: Context,
        private val getTasksByDueDateUseCase: GetTasksByDueDateUseCase
    ) : RemoteViewsFactory {
        
        private var tasks: List<Task> = emptyList()
        
        override fun onCreate() {
            // No initialization needed
        }
        
        override fun onDataSetChanged() {
            // Load today's tasks
            tasks = runBlocking {
                getTasksByDueDateUseCase(LocalDate.now()).first()
            }
        }
        
        override fun onDestroy() {
            tasks = emptyList()
        }
        
        override fun getCount(): Int = tasks.size
        
        override fun getViewAt(position: Int): RemoteViews {
            if (position < 0 || position >= tasks.size) {
                return RemoteViews(context.packageName, R.layout.widget_task_item)
            }
            
            val task = tasks[position]
            val views = RemoteViews(context.packageName, R.layout.widget_task_item)
            
            // Set task title
            views.setTextViewText(R.id.widget_task_title, task.title)
            
            // Set task time if available
            if (task.dueTime != null) {
                views.setTextViewText(R.id.widget_task_time, task.getDisplayTime())
                views.setViewVisibility(R.id.widget_task_time, android.view.View.VISIBLE)
            } else {
                views.setViewVisibility(R.id.widget_task_time, android.view.View.GONE)
            }
            
            // Set priority indicator color
            when (task.priority) {
                com.windsurf.todoapp.data.model.Priority.HIGH -> 
                    views.setInt(R.id.widget_priority_indicator, "setBackgroundResource", R.color.priority_high)
                com.windsurf.todoapp.data.model.Priority.MEDIUM -> 
                    views.setInt(R.id.widget_priority_indicator, "setBackgroundResource", R.color.priority_medium)
                com.windsurf.todoapp.data.model.Priority.LOW -> 
                    views.setInt(R.id.widget_priority_indicator, "setBackgroundResource", R.color.priority_low)
            }
            
            // Set up fill-in intent for item click
            val fillInIntent = Intent().apply {
                putExtra("task_id", task.id)
            }
            views.setOnClickFillInIntent(R.id.widget_task_item, fillInIntent)
            
            return views
        }
        
        override fun getLoadingView(): RemoteViews? = null
        
        override fun getViewTypeCount(): Int = 1
        
        override fun getItemId(position: Int): Long = position.toLong()
        
        override fun hasStableIds(): Boolean = true
    }
}
