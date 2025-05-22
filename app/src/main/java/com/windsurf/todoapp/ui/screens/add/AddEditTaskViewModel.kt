package com.windsurf.todoapp.ui.screens.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windsurf.todoapp.data.model.Priority
import com.windsurf.todoapp.data.model.Task
import com.windsurf.todoapp.data.model.TaskCategory
import com.windsurf.todoapp.domain.usecase.TaskUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * ViewModel for the Add/Edit Task screen.
 * Manages the state and business logic for creating or updating a task.
 */
@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val taskId: Long = savedStateHandle.get<Long>("taskId") ?: -1L
    private val isEditMode: Boolean = taskId != -1L
    
    // Task form state
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()
    
    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()
    
    private val _dueDate = MutableStateFlow(LocalDate.now())
    val dueDate: StateFlow<LocalDate> = _dueDate.asStateFlow()
    
    private val _dueTime = MutableStateFlow<LocalTime?>(null)
    val dueTime: StateFlow<LocalTime?> = _dueTime.asStateFlow()
    
    private val _priority = MutableStateFlow(Priority.MEDIUM)
    val priority: StateFlow<Priority> = _priority.asStateFlow()
    
    private val _category = MutableStateFlow(TaskCategory.PERSONAL)
    val category: StateFlow<TaskCategory> = _category.asStateFlow()
    
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    
    // Form validation state
    private val _titleError = MutableStateFlow<String?>(null)
    val titleError: StateFlow<String?> = _titleError.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Initialize ViewModel with existing task if in edit mode
    init {
        if (isEditMode) {
            loadTask()
        }
    }
    
    /**
     * Loads the task from the repository when in edit mode.
     */
    private fun loadTask() {
        viewModelScope.launch {
            _isLoading.value = true
            val task = taskUseCases.getTaskById(taskId)
            task?.let {
                _title.value = it.title
                _description.value = it.description
                _dueDate.value = LocalDate.parse(it.dueDate)
                _dueTime.value = it.dueTime?.let { time -> LocalTime.parse(time) }
                _priority.value = it.priority
                _category.value = it.category
                _isFavorite.value = it.isFavorite
            }
            _isLoading.value = false
        }
    }
    
    /**
     * Updates the title and validates it.
     */
    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
        validateTitle()
    }
    
    /**
     * Updates the description.
     */
    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
    }
    
    /**
     * Updates the due date.
     */
    fun onDueDateChange(newDueDate: LocalDate) {
        _dueDate.value = newDueDate
    }
    
    /**
     * Updates the due time.
     */
    fun onDueTimeChange(newDueTime: LocalTime?) {
        _dueTime.value = newDueTime
    }
    
    /**
     * Updates the priority.
     */
    fun onPriorityChange(newPriority: Priority) {
        _priority.value = newPriority
    }
    
    /**
     * Updates the category.
     */
    fun onCategoryChange(newCategory: TaskCategory) {
        _category.value = newCategory
    }
    
    /**
     * Toggles the favorite status.
     */
    fun onFavoriteToggle() {
        _isFavorite.value = !_isFavorite.value
    }
    
    /**
     * Validates the title field.
     */
    private fun validateTitle(): Boolean {
        return if (title.value.isBlank()) {
            _titleError.value = "Title cannot be empty"
            false
        } else {
            _titleError.value = null
            true
        }
    }
    
    /**
     * Validates all form fields and returns whether the form is valid.
     */
    private fun validateForm(): Boolean {
        return validateTitle()
    }
    
    /**
     * Saves the task based on the current form state.
     * Returns true if save was successful, false otherwise.
     */
    fun saveTask(): Boolean {
        if (!validateForm()) {
            return false
        }
        
        val currentDate = LocalDate.now().toString()
        
        val task = if (isEditMode) {
            Task(
                id = taskId,
                title = title.value.trim(),
                description = description.value.trim(),
                dueDate = dueDate.value.toString(),
                dueTime = dueTime.value?.toString(),
                priority = priority.value,
                category = category.value,
                isFavorite = isFavorite.value,
                createdDate = currentDate, // Will be overwritten by existing value
                modifiedDate = currentDate
            )
        } else {
            Task(
                title = title.value.trim(),
                description = description.value.trim(),
                dueDate = dueDate.value.toString(),
                dueTime = dueTime.value?.toString(),
                priority = priority.value,
                category = category.value,
                isFavorite = isFavorite.value,
                createdDate = currentDate,
                modifiedDate = currentDate
            )
        }
        
        viewModelScope.launch {
            if (isEditMode) {
                taskUseCases.updateTask(task)
            } else {
                taskUseCases.addTask(task)
            }
        }
        
        return true
    }
    
    /**
     * Returns a formatted string representation of the due date.
     */
    fun getFormattedDate(): String {
        return dueDate.value.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    }
    
    /**
     * Returns a formatted string representation of the due time.
     */
    fun getFormattedTime(): String? {
        return dueTime.value?.let {
            val hour = it.hour
            val minute = it.minute
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
