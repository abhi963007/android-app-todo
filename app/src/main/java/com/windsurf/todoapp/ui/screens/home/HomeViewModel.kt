package com.windsurf.todoapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windsurf.todoapp.data.model.Task
import com.windsurf.todoapp.data.model.TaskCategory
import com.windsurf.todoapp.domain.usecase.TaskUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Home screen.
 * Manages the UI state and business logic for displaying and interacting with tasks.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases
) : ViewModel() {
    
    private val _sortByDueDate = MutableStateFlow(true)
    private val _filterCategory = MutableStateFlow<TaskCategory?>(null)
    
    /**
     * Current tasks filtered based on the selected category and sort order.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<Task>> = combine(
        _sortByDueDate,
        _filterCategory
    ) { sortByDueDate, filterCategory ->
        Pair(sortByDueDate, filterCategory)
    }.flatMapLatest { (sortByDueDate, filterCategory) ->
        if (filterCategory != null) {
            taskUseCases.getTasksByCategory(filterCategory)
        } else {
            taskUseCases.getActiveTasks(sortByDueDate)
        }
    }.stateIn(
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
     * Sets the category filter for tasks.
     */
    fun setFilterCategory(category: TaskCategory?) {
        _filterCategory.value = category
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
