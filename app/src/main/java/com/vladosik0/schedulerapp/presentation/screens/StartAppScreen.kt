package com.vladosik0.schedulerapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vladosik0.schedulerapp.R
import com.vladosik0.schedulerapp.presentation.navigation.NavigationRoutes


@Composable
fun StartAppScreen(
    navController: NavController
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.planning),
                contentDescription = "App logo",
                modifier = Modifier.size(128.dp)
            )
            Button(onClick = {
                navController.navigate(NavigationRoutes.AuthScreen.createRoute("In"))
            }) {
                Text(text = "Sign In")
            }
            OutlinedButton(onClick = {
                navController.navigate(NavigationRoutes.AuthScreen.createRoute("Up"))
            }) {
                Text(text = "Sign Up")
            }

        }
    }
}