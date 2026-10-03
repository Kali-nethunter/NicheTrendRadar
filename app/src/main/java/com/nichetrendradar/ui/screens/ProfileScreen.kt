package com.nichetrendradar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nichetrendradar.viewmodel.MainViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: MainViewModel) {
    var loggingOut by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Profile & Settings", style = MaterialTheme.typography.headlineMedium)
        Text(
            viewModel.accountEmail ?: "Account",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
        Button(
            onClick = {
                loggingOut = true
                viewModel.logout {
                    loggingOut = false
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            },
            enabled = !loggingOut,
            modifier = Modifier.fillMaxWidth().padding(top = 28.dp)
        ) {
            Text(if (loggingOut) "Logging out…" else "Log out")
        }
        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        ) {
            Text("Back")
        }
    }
}