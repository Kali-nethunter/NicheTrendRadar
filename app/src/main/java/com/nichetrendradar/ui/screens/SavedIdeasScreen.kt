package com.nichetrendradar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.ui.theme.*
import com.nichetrendradar.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedIdeasScreen(navController: NavController, viewModel: MainViewModel) {
    LaunchedEffect(Unit) { viewModel.loadSavedIdeas() }
    val state by viewModel.savedState.collectAsState()

    Scaffold(containerColor = Background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 82.dp)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { navController.popBackStack() },
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp)
                ) {
                    Text("Back", color = PrimaryBright, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.width(18.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Saved Ideas",
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Your content library",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            when (val s = state) {
                UiState.Idle, UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is UiState.Error -> {
                    Box(
                        Modifier.fillMaxSize().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Could not load saved ideas.\n" + s.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is UiState.Success -> {
                    if (s.data.isEmpty()) {
                        Box(
                            Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Your library is empty",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Save an AI content idea from a trend and it will appear here.",
                                    color = TextSecondary
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(
                                start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(s.data) { idea ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.selectSavedIdea(idea)
                                            navController.navigate("saved_detail")
                                        },
                                    colors = CardDefaults.cardColors(containerColor = Surface)
                                ) {
                                    Column(Modifier.padding(18.dp)) {
                                        Text(
                                            idea.title.replace(
                                                Regex("\\bAI\\b", RegexOption.IGNORE_CASE),
                                                "AI"
                                            ),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            idea.hook.replace(
                                                Regex("\\bai\\b", RegexOption.IGNORE_CASE),
                                                "AI"
                                            ),
                                            modifier = Modifier.padding(top = 8.dp),
                                            maxLines = 3,
                                            color = TextSecondary,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            "Tap to view full idea →",
                                            modifier = Modifier.padding(top = 10.dp),
                                            color = PrimaryBright,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
