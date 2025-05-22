package com.windsurf.todoapp.ui.screens.completed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.windsurf.todoapp.R
import com.windsurf.todoapp.ui.components.EmptyState
import com.windsurf.todoapp.ui.components.TaskItem
import kotlinx.coroutines.launch

/**
 * Screen that displays all completed tasks.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedTasksScreen(
    onTaskClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    viewModel: CompletedTasksViewModel
) {
    val completedTasks by viewModel.completedTasks.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showDeleteAllConfirmation by remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.nav_completed)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Only show delete all button if there are completed tasks
                    if (completedTasks.isNotEmpty()) {
                        IconButton(onClick = { showDeleteAllConfirmation = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Delete All"
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (completedTasks.isEmpty()) {
            EmptyState(
                message = "No completed tasks yet. Tasks marked as complete will appear here.",
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    items = completedTasks,
                    key = { task -> task.id }
                ) { task ->
                    // Using SwipeToDismissBox for swipe actions
                    val dismissState = remember {
                        SwipeToDismissBoxState(
                            initialValue = SwipeToDismissBoxValue.Settled,
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.deleteTask(task.id)
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Task deleted",
                                            actionLabel = "Undo"
                                        )
                                        // TODO: Implement undo functionality
                                    }
                                    true
                                } else {
                                    false
                                }
                            }
                        )
                    }
                    
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(initialOffsetY = { it * 2 }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it * 2 }) + fadeOut()
                    ) {
                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = true,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.onError
                                    )
                                }
                            }
                        ) {
                            TaskItem(
                                task = task,
                                onClick = { onTaskClick(task.id) },
                                onFavoriteClick = { viewModel.toggleFavorite(task.id) },
                                onCompleteClick = { viewModel.toggleCompletion(task.id) }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Delete all confirmation dialog
    if (showDeleteAllConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteAllConfirmation = false },
            title = { Text(text = "Delete All Completed Tasks") },
            text = { Text(text = "Are you sure you want to delete all completed tasks? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAllCompletedTasks()
                        showDeleteAllConfirmation = false
                        scope.launch {
                            snackbarHostState.showSnackbar("All completed tasks deleted")
                        }
                    }
                ) {
                    Text(text = "Delete All")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteAllConfirmation = false }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}
