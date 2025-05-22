package com.windsurf.todoapp.ui.screens.completed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windsurf.todoapp.data.model.Task
import com.windsurf.todoapp.domain.usecase.TaskUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Completed Tasks screen.
 */
@HiltViewModel
class CompletedTasksViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases
) : ViewModel() {
    
    /**
     * Stream of completed tasks.
     */
    val completedTasks: StateFlow<List<Task>> = taskUseCases.getCompletedTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    /**
     * Toggles the completion status of a task.
     */
    fun toggleCompletion(taskId: Long) {
        viewModelScope.launch {
            taskUseCases.toggleTaskCompletion(taskId)
        }
    }
    
    /**
     * Toggles the favorite status of a task.
     */
    fun toggleFavorite(taskId: Long) {
        viewModelScope.launch {
            taskUseCases.toggleTaskFavorite(taskId)
        }
    }
    
    /**
     * Deletes a task.
     */
    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            taskUseCases.deleteTaskById(taskId)
        }
    }
    
    /**
     * Deletes all completed tasks.
     */
    fun deleteAllCompletedTasks() {
        viewModelScope.launch {
            taskUseCases.deleteAllCompletedTasks()
        }
    }
}
