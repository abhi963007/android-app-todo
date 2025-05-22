package com.windsurf.todoapp.di

import com.windsurf.todoapp.data.repository.TaskRepository
import com.windsurf.todoapp.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing use cases.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    @Singleton
    @Provides
    fun provideTaskUseCases(
        repository: TaskRepository
    ): TaskUseCases {
        val getTaskById = GetTaskByIdUseCase(repository)
        
        return TaskUseCases(
            getAllTasks = GetAllTasksUseCase(repository),
            getActiveTasks = GetActiveTasksUseCase(repository),
            getCompletedTasks = GetCompletedTasksUseCase(repository),
            getFavoriteTasks = GetFavoriteTasksUseCase(repository),
            getTasksByCategory = GetTasksByCategoryUseCase(repository),
            searchTasks = SearchTasksUseCase(repository),
            getTaskById = getTaskById,
            getTasksByDueDate = GetTasksByDueDateUseCase(repository),
            addTask = AddTaskUseCase(repository),
            updateTask = UpdateTaskUseCase(repository),
            deleteTask = DeleteTaskUseCase(repository),
            deleteTaskById = DeleteTaskByIdUseCase(repository),
            toggleTaskCompletion = ToggleTaskCompletionUseCase(repository, getTaskById),
            toggleTaskFavorite = ToggleTaskFavoriteUseCase(repository, getTaskById),
            deleteAllCompletedTasks = DeleteAllCompletedTasksUseCase(repository)
        )
    }
}
