package com.nichetrendradar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nichetrendradar.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(viewModel: MainViewModel, onComplete: () -> Unit) {
    var niche by remember { mutableStateOf("") }
    var keywords by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Choose your niche", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = niche,
            onValueChange = { niche = it },
            label = { Text("Niche") },
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        )
        OutlinedTextField(
            value = keywords,
            onValueChange = { keywords = it },
            label = { Text("Keywords (comma separated)") },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )
        Button(
            onClick = {
                val ks = keywords.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                viewModel.createNiche(
                    niche.ifBlank { "General" },
                    ks,
                    listOf("YouTube", "Instagram", "LinkedIn")
                ) { onComplete() }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            Text("Start Radar")
        }
    }
}