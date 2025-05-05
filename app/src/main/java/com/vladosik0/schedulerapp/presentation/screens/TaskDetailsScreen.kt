package com.vladosik0.schedulerapp.presentation.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vladosik0.schedulerapp.R
import com.vladosik0.schedulerapp.domain.formatters.formatDuration
import com.vladosik0.schedulerapp.domain.formatters.getFormattedDateFromString
import com.vladosik0.schedulerapp.domain.formatters.getFormattedTime
import com.vladosik0.schedulerapp.domain.formatters.toPrettyFormat
import com.vladosik0.schedulerapp.domain.timeline_build_helpers.getEventStatus
import com.vladosik0.schedulerapp.presentation.AppViewModelProvider
import com.vladosik0.schedulerapp.presentation.view_models.TaskDetailsScreenViewModel
import com.vladosik0.schedulerapp.presentation.view_models.TaskDetailsUiState
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(
    viewModel: TaskDetailsScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onBackIconClick: () -> Unit,
    onEditIconClick: (Int) -> Unit
) {
    when (val state = viewModel.taskDetailsUiState.collectAsState().value) {
        is TaskDetailsUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(36.dp))
            }
        }
        is TaskDetailsUiState.Success -> {
            val context = LocalContext.current
            var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
            var expanded by rememberSaveable { mutableStateOf(false) }
            val rotation by animateFloatAsState(
                targetValue = if (expanded) 180f else 0f,
                animationSpec = tween(durationMillis = 300),
                label = "icon_rotation"
            )
            val backgroundAlpha by animateFloatAsState(
                targetValue = if (expanded) 0.4f else 1f,
                label = "background_alpha"
            )

            Scaffold(topBar = {
                TopAppBar(
                    title = { Text("Task Details") }, navigationIcon = {
                    IconButton(onClick = onBackIconClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
                )
            }, floatingActionButton = {
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
                    ) {
                        AnimatedVisibility(
                            visible = expanded,
                            enter = fadeIn(tween(300)) + scaleIn(tween(300)),
                            exit = fadeOut(tween(300)) + scaleOut(tween(300))
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                SmallActionButton(
                                    icon = Icons.Default.Delete, label = "Delete"
                                ) {
                                    showDeleteDialog = true
                                }
                                SmallActionButton(
                                    icon = if(state.task.isDone) Icons.Default.Close else Icons.Default.Check,
                                    label = if(state.task.isDone) "Uncomplete" else "Complete"
                                ) {
                                    if(LocalDateTime.now().isAfter(LocalDateTime.parse(state.task.finishAt))) {
                                        Toast.makeText(context, "This task is already in the past", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Task's status is changed", Toast.LENGTH_SHORT).show()
                                        viewModel.updateTaskStatus()
                                    }
                                    expanded = !expanded
                                }
                                SmallActionButton(
                                    icon = Icons.Default.Edit, label = "Edit"
                                ) {
                                    onEditIconClick(state.task.id)
                                    expanded = !expanded
                                }
                            }
                        }
                        FloatingActionButton(
                            onClick = { expanded = !expanded },
                            containerColor = if (expanded) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Crossfade(
                                targetState = expanded,
                                label = "icon_crossfade"
                            ) { isExpanded ->
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.MoreVert,
                                    contentDescription = if (isExpanded) "Close" else "Actions",
                                    modifier = Modifier.rotate(rotation),
                                    tint = if (isExpanded) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }
            }) { paddingValues ->
                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Confirm Deletion") },
                        text = { Text("Are you sure you want to delete this task?") },
                        confirmButton = {
                            TextButton(onClick = {
                                showDeleteDialog = false
                                viewModel.deleteTask {
                                    Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show()
                                    onBackIconClick()
                                }
                            }) {
                                Text("Yes")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("No")
                            }
                        }
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .padding(16.dp)
                        .alpha(backgroundAlpha)
                ) {
                    Text(
                        text = "${state.task.title}, ${formatDuration(state.task.startAt, state.task.finishAt)}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = "Category: ${state.task.category}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            TaskDetailRow(
                                icon = painterResource(R.drawable.calendar_details),
                                label = "Date",
                                value = getFormattedDateFromString(state.task.startAt)
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            TaskDetailRow(
                                icon = painterResource(R.drawable.clock),
                                label = "Time",
                                value = "${getFormattedTime(state.task.startAt)} - ${getFormattedTime(state.task.finishAt)}"
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            TaskDetailRow(
                                icon = painterResource(R.drawable.status),
                                label = "Status",
                                value = if(state.task.isDone) "Completed" else getEventStatus(state.task.startAt, state.task.finishAt)
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            TaskDetailRow(
                                icon = painterResource(R.drawable.priority),
                                label = "Priority",
                                value = state.task.priority.name.toPrettyFormat()
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            TaskDetailRow(
                                icon = painterResource(R.drawable.difficulty),
                                label = "Difficulty",
                                value = state.task.difficulty.name.toPrettyFormat()
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            TaskDetailRow(
                                icon = painterResource(R.drawable.notification),
                                label = "Notification",
                                value = if (state.task.isNotified) "On" else "Off"
                            )
                        }
                    }
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                    )
                    Text(
                        text = if (state.task.description.isNullOrBlank()) "No description" else state.task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun TaskDetailRow(icon: Painter, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun SmallActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onTertiary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onTertiary,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}
