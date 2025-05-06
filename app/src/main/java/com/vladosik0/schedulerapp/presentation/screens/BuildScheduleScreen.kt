package com.vladosik0.schedulerapp.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vladosik0.schedulerapp.domain.formatters.toPrettyFormat
import com.vladosik0.schedulerapp.presentation.AppViewModelProvider
import com.vladosik0.schedulerapp.presentation.view_models.BuildScheduleScreenViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildScheduleScreen(
    onCancel: () -> Unit,
    onSave: () -> Unit,
    viewModel: BuildScheduleScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Build schedule") }, navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }, scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues).padding(8.dp)
        ) {
            Text(text = viewModel.getTitle())
            Text(text = viewModel.getDescription())
            Text(text = viewModel.getCategory())
            Text(text = viewModel.getPriority().name.toPrettyFormat())
            Text(text = viewModel.getDifficulty().name.toPrettyFormat())
        }
    }
}