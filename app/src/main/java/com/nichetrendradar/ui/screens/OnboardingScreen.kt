package com.nichetrendradar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(viewModel: MainViewModel, onComplete: () -> Unit) {
    var niche by remember { mutableStateOf("") }
    var keywords by remember { mutableStateOf("") }
    val createState by viewModel.createNicheState.collectAsState()

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Choose your niche", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = niche,
            onValueChange = { niche = it; viewModel.clearCreateNicheState() },
            label = { Text("Niche") },
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            singleLine = true,
            enabled = createState !is UiState.Loading
        )

        OutlinedTextField(
            value = keywords,
            onValueChange = { keywords = it; viewModel.clearCreateNicheState() },
            label = { Text("Keywords (comma separated)") },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            singleLine = true,
            enabled = createState !is UiState.Loading
        )

        when (val result = createState) {
            UiState.Idle -> Unit
            UiState.Loading -> Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) { CircularProgressIndicator() }
            is UiState.Error -> Card(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Connection error\n" + result.message,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            is UiState.Success -> Text(
                text = "Connected. Starting Radar…",
                modifier = Modifier.padding(top = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Button(
            onClick = {
                val ks = keywords.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                viewModel.createNiche(
                    niche.ifBlank { "General" },
                    ks,
                    listOf("YouTube", "Instagram", "LinkedIn")
                ) { onComplete() }
            },
            enabled = createState !is UiState.Loading,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            Text(if (createState is UiState.Loading) "Connecting…" else "Start Radar")
        }
    }
}