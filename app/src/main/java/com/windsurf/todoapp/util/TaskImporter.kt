package com.windsurf.todoapp.util

import android.content.Context
import android.net.Uri
import com.windsurf.todoapp.data.model.Task
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject

/**
 * Utility class for importing tasks from a JSON file.
 */
class TaskImporter @Inject constructor(
    private val context: Context
) {
    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }
    
    /**
     * Imports tasks from a JSON file at the given URI.
     *
     * @param uri URI of the JSON file
     * @return List of imported tasks, or empty list if import failed
     */
    fun importTasksFromJson(uri: Uri): List<Task> {
        return try {
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().use { it.readText() }
            } ?: return emptyList()
            
            json.decodeFromString<List<Task>>(jsonString)
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
