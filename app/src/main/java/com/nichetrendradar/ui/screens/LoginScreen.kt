package com.nichetrendradar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(onContinue: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Niche Trend Radar", style = MaterialTheme.typography.headlineLarge)
        Text("Discover what your audience is talking about.", modifier = Modifier.padding(top = 8.dp))
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth().padding(top = 28.dp)) {
            Text("Get Started")
        }
    }
}