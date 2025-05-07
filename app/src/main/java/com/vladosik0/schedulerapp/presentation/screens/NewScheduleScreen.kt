package com.vladosik0.schedulerapp.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vladosik0.schedulerapp.presentation.view_models.NewScheduleScreenUiState
import com.vladosik0.schedulerapp.presentation.view_models.SharedScheduleScreensViewModel

@Composable
fun NewScheduleScreen(
    viewModel: SharedScheduleScreensViewModel,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current

    when(val state = viewModel.newScheduleScreenUiState.collectAsState().value) {
        is NewScheduleScreenUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(36.dp))
            }
        }
        is NewScheduleScreenUiState.Success -> {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                OutlinedButton(onClick = onCancel) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        // viewModel.saveSchedule()
                        Toast.makeText(context, "Schedule saved successfully", Toast.LENGTH_SHORT)
                            .show()
                        onSave()
                    }) {
                    Text("Save Schedule")
                }
            }
        }
        is NewScheduleScreenUiState.Failure -> {

        }
    }

}