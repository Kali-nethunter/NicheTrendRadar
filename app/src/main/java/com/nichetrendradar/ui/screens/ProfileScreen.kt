package com.nichetrendradar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController) {
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Profile & Settings", style = MaterialTheme.typography.headlineMedium)
        Text("Niche Trend Radar", modifier = Modifier.padding(top = 16.dp))
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text("Back")
        }
    }
}