package com.vladosik0.schedulerapp.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vladosik0.schedulerapp.ui.theme.SchedulerAppTheme

@Composable
fun AuthScreen(
    buttonLabel: String,
    onButtonClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            value = "",
            onValueChange = {},
            placeholder = {
                Text(text = "Login")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = "",
            onValueChange = {},
            placeholder = {
                Text(text = "Password")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { onButtonClick() }) {
            Text(text = buttonLabel)
        }
    }
}

@Preview
@Composable
fun AuthScreenPreview() {
    SchedulerAppTheme {
        AuthScreen(buttonLabel = "Sign In")
    }
}