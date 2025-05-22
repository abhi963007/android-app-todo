package com.windsurf.todoapp.di

import android.content.Context
import androidx.room.Room
import com.windsurf.todoapp.data.local.TaskDao
import com.windsurf.todoapp.data.local.TodoDatabase
import com.windsurf.todoapp.data.repository.TaskRepository
import com.windsurf.todoapp.data.repository.TaskRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing application-level dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Provides the Room database instance.
     */
    @Singleton
    @Provides
    fun provideTodoDatabase(
        @ApplicationContext context: Context
    ): TodoDatabase {
        return Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todo_database"
        ).build()
    }
    
    /**
     * Provides the TaskDao.
     */
    @Singleton
    @Provides
    fun provideTaskDao(database: TodoDatabase): TaskDao {
        return database.taskDao()
    }
    
    /**
     * Provides the TaskRepository implementation.
     */
    @Singleton
    @Provides
    fun provideTaskRepository(
        taskDao: TaskDao
    ): TaskRepository {
        return TaskRepositoryImpl(taskDao)
    }
}
