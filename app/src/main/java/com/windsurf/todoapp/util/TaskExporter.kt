package com.windsurf.todoapp.util

import android.content.Context
import android.net.Uri
import com.windsurf.todoapp.data.model.Task
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

/**
 * Utility class for exporting tasks to a JSON file.
 */
class TaskExporter @Inject constructor(
    private val context: Context
) {
    private val json = Json { 
        prettyPrint = true 
        ignoreUnknownKeys = true
    }
    
    /**
     * Exports a list of tasks to a JSON file at the given URI.
     *
     * @param tasks List of tasks to export
     * @param uri URI where to save the file
     * @return true if export was successful, false otherwise
     */
    fun exportTasksToJson(tasks: List<Task>, uri: Uri): Boolean {
        return try {
            val jsonString = json.encodeToString(tasks)
            context.contentResolver.openFileDescriptor(uri, "w")?.use { pfd ->
                FileOutputStream(pfd.fileDescriptor).use { outputStream ->
                    outputStream.write(jsonString.toByteArray())
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
