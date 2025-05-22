package com.windsurf.todoapp.ui.screens.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windsurf.todoapp.data.model.Task
import com.windsurf.todoapp.domain.usecase.TaskUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Task Detail screen.
 * Manages retrieving and manipulating a single task.
 */
@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val taskId: Long = savedStateHandle.get<Long>("taskId") ?: -1L
    
    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadTask()
    }
    
    /**
     * Loads the task from the repository.
     */
    private fun loadTask() {
        viewModelScope.launch {
            _isLoading.value = true
            _task.value = taskUseCases.getTaskById(taskId)
            _isLoading.value = false
        }
    }
    
    /**
     * Toggles the favorite status of the task.
     */
    fun toggleFavorite() {
        viewModelScope.launch {
            taskUseCases.toggleTaskFavorite(taskId)
            loadTask() // Reload the task to get updated data
        }
    }
    
    /**
     * Toggles the completion status of the task.
     */
    fun toggleComplete() {
        viewModelScope.launch {
            taskUseCases.toggleTaskCompletion(taskId)
            loadTask() // Reload the task to get updated data
        }
    }
    
    /**
     * Deletes the task.
     */
    fun deleteTask() {
        viewModelScope.launch {
            taskUseCases.deleteTaskById(taskId)
        }
    }
}
