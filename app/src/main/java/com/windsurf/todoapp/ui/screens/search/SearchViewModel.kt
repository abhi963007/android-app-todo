package com.windsurf.todoapp.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windsurf.todoapp.data.model.Task
import com.windsurf.todoapp.domain.usecase.TaskUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Search screen.
 * Manages search functionality for tasks.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()
    
    /**
     * Search results based on the current query.
     * Uses debounce to avoid excessive database queries while typing.
     */
    @OptIn(FlowPreview::class)
    val searchResults: StateFlow<List<Task>> = _searchQuery
        .debounce(300L) // Wait for 300ms of inactivity before performing search
        .filter { it.isNotBlank() }
        .flatMapLatest { query ->
            _isSearching.value = true
            taskUseCases.searchTasks(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    /**
     * Updates the search query.
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        
        if (query.isBlank()) {
            _isSearching.value = false
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
