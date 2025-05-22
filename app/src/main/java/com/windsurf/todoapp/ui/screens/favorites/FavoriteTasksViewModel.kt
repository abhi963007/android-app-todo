package com.windsurf.todoapp.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windsurf.todoapp.data.model.Task
import com.windsurf.todoapp.domain.usecase.TaskUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Favorite Tasks screen.
 */
@HiltViewModel
class FavoriteTasksViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases
) : ViewModel() {
    
    private val _sortByDueDate = MutableStateFlow(true)
    
    /**
     * Stream of favorite tasks.
     */
    val favoriteTasks: StateFlow<List<Task>> = taskUseCases.getFavoriteTasks(_sortByDueDate.value)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    /**
     * Sets the sort order for tasks.
     */
    fun setSortOrder(sortByDueDate: Boolean) {
        _sortByDueDate.value = sortByDueDate
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
     * Toggles the completion status of a task.
     */
    fun toggleCompletion(taskId: Long) {
        viewModelScope.launch {
            taskUseCases.toggleTaskCompletion(taskId)
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
}
