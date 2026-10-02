package com.nichetrendradar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.ui.components.IdeaCard
import com.nichetrendradar.viewmodel.MainViewModel

@Composable
fun IdeaScreen(navController: NavController, viewModel: MainViewModel) {
    val state by viewModel.ideasState.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("AI Content Ideas", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))

        when (val s = state) {
            UiState.Idle -> Text("Select a trend to generate ideas.")
            UiState.Loading -> CircularProgressIndicator()
            is UiState.Error -> Text("Error: " + s.message)
            is UiState.Success -> LazyColumn {
                items(s.data) { idea ->
                    IdeaCard(idea) { viewModel.saveIdea(idea) }
                }
            }
        }

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Back to Trends")
        }
    }
}