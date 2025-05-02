package com.vladosik0.schedulerapp.presentation.screens

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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vladosik0.schedulerapp.R
import com.vladosik0.schedulerapp.model.Task
import com.vladosik0.schedulerapp.model.formatters.getFormattedTime
import com.vladosik0.schedulerapp.model.timeline_build_helpers.getEventStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(
    task: Task,
    onBackIconClick: () -> Unit,
    onEditIconClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "icon_rotation"
    )
    val backgroundAlpha by animateFloatAsState(targetValue = if (expanded) 0.4f else 1f, label = "background_alpha")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = onBackIconClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
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
                                icon = Icons.Default.Delete,
                                label = "Delete"
                            ) { /* TODO */ }
                            SmallActionButton(
                                icon = Icons.Default.Check,
                                label = "Complete"
                            ) { /* TODO */ }
                            SmallActionButton(
                                icon = Icons.Default.Edit,
                                label = "Edit"
                            ) {
                                onEditIconClick()
                            }
                        }
                    }
                    FloatingActionButton(
                        onClick = { expanded = !expanded },
                        containerColor = if (expanded) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Crossfade(targetState = expanded, label = "icon_crossfade") { isExpanded ->
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
                .alpha(backgroundAlpha)
        ) {
            Text(
                text = task.title + ", 10h ${task.duration}min",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Category: ${task.category}",
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
                        value = "28 Apr, 2025"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    TaskDetailRow(
                        icon = painterResource(R.drawable.clock),
                        label = "Time",
                        value = "${getFormattedTime(task.startAt)} - ${getFormattedTime(task.finishAt)}"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    TaskDetailRow(
                        icon = painterResource(R.drawable.status),
                        label = "Status",
                        value = getEventStatus(task.startAt, task.finishAt)
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    TaskDetailRow(
                        icon = painterResource(R.drawable.priority),
                        label = "Priority",
                        value = task.priority.name.lowercase().replaceFirstChar { it.titlecase() }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    TaskDetailRow(
                        icon = painterResource(R.drawable.difficulty),
                        label = "Difficulty",
                        value = task.difficulty.name.lowercase().replaceFirstChar { it.titlecase() }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    TaskDetailRow(
                        icon = painterResource(R.drawable.notification),
                        label = "Notification",
                        value = if(task.isNotified) "On" else "Off"
                    )
                }
            }
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )
                Text(
                    text = if(task.description.isNullOrBlank()) "No description" else "Staying focused on your goals each day builds the foundation for long-term success. Small consistent actions matter more than short bursts of inspiration. Trust the process, stay curious, and remember that persistence always beats perfection. Every great achievement starts with a decision to try. Challenges will come, but each obstacle is a chance to grow stronger. Keep learning, stay positive, and believe in your ability to shape the future you dream about. Small steps create big changes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
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
