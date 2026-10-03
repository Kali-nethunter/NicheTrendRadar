package com.nichetrendradar.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedIdeasScreen(navController: NavController, viewModel: MainViewModel) {
    LaunchedEffect(Unit) { viewModel.loadSavedIdeas() }
    val state by viewModel.savedState.collectAsState()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                "Saved Ideas",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Your saved content ideas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            when (val s = state) {
                UiState.Idle, UiState.Loading -> {
                    Box(
                        Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is UiState.Error -> {
                    Box(
                        Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Could not load saved ideas.\nundefined",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is UiState.Success -> {
                    if (s.data.isEmpty()) {
                        Box(
                            Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "No saved ideas yet",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Save an idea and it will appear here.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(s.data) { idea ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.selectSavedIdea(idea)
                                            navController.navigate("saved_detail")
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Column(Modifier.padding(18.dp)) {
                                        Text(
                                            idea.title.replace(
                                                Regex("\\bAI\\b", RegexOption.IGNORE_CASE),
                                                "AI"
                                            ),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            idea.hook.replace(
                                                Regex("\\bai\\b", RegexOption.IGNORE_CASE),
                                                "AI"
                                            ),
                                            modifier = Modifier.padding(top = 8.dp),
                                            maxLines = 3,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            "Tap to view full idea →",
                                            modifier = Modifier.padding(top = 10.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Ideas")
            }
        }
    }
}
