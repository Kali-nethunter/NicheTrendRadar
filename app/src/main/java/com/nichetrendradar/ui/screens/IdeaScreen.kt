package com.nichetrendradar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.ui.components.IdeaCard
import com.nichetrendradar.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeaScreen(navController: NavController, viewModel: MainViewModel) {
    val state by viewModel.ideasState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val savedTitles = remember { mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)
        ) {
            Text("AI Content Ideas", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))

            when (val s = state) {
                UiState.Idle -> Text("Select a trend to generate ideas.")
                UiState.Loading -> CircularProgressIndicator()
                is UiState.Error -> Text("Error: " + s.message)
                is UiState.Success -> LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(s.data) { idea ->
                        IdeaCard(
                            idea = idea,
                            saved = idea.title in savedTitles,
                            onSave = {
                                viewModel.saveIdea(idea) { success, message ->
                                    if (success && idea.title !in savedTitles) {
                                        savedTitles.add(idea.title)
                                    }
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (success) "Saved to Library" else "Save failed: $message"
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }

            TextButton(onClick = { navController.navigate("saved") }) {
                Text("View Saved Ideas")
            }
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Back to Trends")
            }
        }
    }
}