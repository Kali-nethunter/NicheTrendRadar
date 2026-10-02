package com.nichetrendradar.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.viewmodel.MainViewModel

@Composable
fun SavedIdeasScreen(navController: NavController, viewModel: MainViewModel) {
    LaunchedEffect(Unit) { viewModel.loadSavedIdeas() }
    val state by viewModel.savedState.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Saved Ideas", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        when (val s = state) {
            UiState.Idle, UiState.Loading ->
                CircularProgressIndicator(Modifier.padding(top = 20.dp))
            is UiState.Error ->
                Text("Error: " + s.message)
            is UiState.Success ->
                LazyColumn(Modifier.weight(1f)) {
                    items(s.data) { idea ->
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    viewModel.selectSavedIdea(idea)
                                    navController.navigate("saved_detail")
                                }
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(idea.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    idea.hook,
                                    modifier = Modifier.padding(top = 6.dp),
                                    maxLines = 3
                                )
                                Text(
                                    "Tap to view full idea →",
                                    modifier = Modifier.padding(top = 10.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
        }
        TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
    }
}