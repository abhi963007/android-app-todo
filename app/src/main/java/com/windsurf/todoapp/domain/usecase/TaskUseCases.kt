package com.windsurf.todoapp.domain.usecase

import com.windsurf.todoapp.data.model.Task
import com.windsurf.todoapp.data.model.TaskCategory
import com.windsurf.todoapp.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for getting all tasks
 */
class GetAllTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(sortByDueDate: Boolean): Flow<List<Task>> = 
        repository.getAllTasks(sortByDueDate)
}

/**
 * Use case for getting active (not completed) tasks
 */
class GetActiveTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(sortByDueDate: Boolean): Flow<List<Task>> = 
        repository.getActiveTasks(sortByDueDate)
}

/**
 * Use case for getting completed tasks
 */
class GetCompletedTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> = repository.getCompletedTasks()
}

/**
 * Use case for getting favorite tasks
 */
class GetFavoriteTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(sortByDueDate: Boolean): Flow<List<Task>> = 
        repository.getFavoriteTasks(sortByDueDate)
}

/**
 * Use case for getting tasks by category
 */
class GetTasksByCategoryUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(category: TaskCategory): Flow<List<Task>> = 
        repository.getTasksByCategory(category)
}

/**
 * Use case for searching tasks
 */
class SearchTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(query: String): Flow<List<Task>> = 
        repository.searchTasks(query)
}

/**
 * Use case for getting a task by its ID
 */
class GetTaskByIdUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(id: Long): Task? = repository.getTaskById(id)
}

/**
 * Use case for getting tasks due on a specific date
 */
class GetTasksByDueDateUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(date: LocalDate): Flow<List<Task>> = 
        repository.getTasksByDueDate(date.toString())
}

/**
 * Use case for adding a new task
 */
class AddTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Long = repository.insertTask(task)
}

/**
 * Use case for updating an existing task
 */
class UpdateTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) = repository.updateTask(task)
}

/**
 * Use case for deleting a task
 */
class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) = repository.deleteTask(task)
}

/**
 * Use case for deleting a task by its ID
 */
class DeleteTaskByIdUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(id: Long) = repository.deleteTaskById(id)
}

/**
 * Use case for toggling the completion status of a task
 */
class ToggleTaskCompletionUseCase @Inject constructor(
    private val repository: TaskRepository,
    private val getTaskById: GetTaskByIdUseCase
) {
    suspend operator fun invoke(taskId: Long) {
        val task = getTaskById(taskId) ?: return
        repository.updateTask(task.copy(
            isCompleted = !task.isCompleted,
            modifiedDate = LocalDate.now().toString()
        ))
    }
}

/**
 * Use case for toggling the favorite status of a task
 */
class ToggleTaskFavoriteUseCase @Inject constructor(
    private val repository: TaskRepository,
    private val getTaskById: GetTaskByIdUseCase
) {
    suspend operator fun invoke(taskId: Long) {
        val task = getTaskById(taskId) ?: return
        repository.updateTask(task.copy(
            isFavorite = !task.isFavorite,
            modifiedDate = LocalDate.now().toString()
        ))
    }
}

/**
 * Use case for cleaning up (deleting) all completed tasks
 */
class DeleteAllCompletedTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke() = repository.deleteAllCompletedTasks()
}

/**
 * Container class for all task-related use cases
 */
data class TaskUseCases(
    val getAllTasks: GetAllTasksUseCase,
    val getActiveTasks: GetActiveTasksUseCase,
    val getCompletedTasks: GetCompletedTasksUseCase,
    val getFavoriteTasks: GetFavoriteTasksUseCase,
    val getTasksByCategory: GetTasksByCategoryUseCase,
    val searchTasks: SearchTasksUseCase,
    val getTaskById: GetTaskByIdUseCase,
    val getTasksByDueDate: GetTasksByDueDateUseCase,
    val addTask: AddTaskUseCase,
    val updateTask: UpdateTaskUseCase,
    val deleteTask: DeleteTaskUseCase,
    val deleteTaskById: DeleteTaskByIdUseCase,
    val toggleTaskCompletion: ToggleTaskCompletionUseCase,
    val toggleTaskFavorite: ToggleTaskFavoriteUseCase,
    val deleteAllCompletedTasks: DeleteAllCompletedTasksUseCase
)
