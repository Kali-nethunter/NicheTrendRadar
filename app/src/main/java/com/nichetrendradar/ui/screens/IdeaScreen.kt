package com.nichetrendradar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.ui.components.IdeaCard
import com.nichetrendradar.ui.theme.Background
import com.nichetrendradar.ui.theme.PrimaryBright
import com.nichetrendradar.ui.theme.TextPrimary
import com.nichetrendradar.ui.theme.TextSecondary
import com.nichetrendradar.viewmodel.MainViewModel

@Composable
fun IdeaScreen(navController: NavController, viewModel: MainViewModel) {
    val state by viewModel.ideasState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val savedTitles = remember { mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background)
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
                        "AI Content Ideas",
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Turn a trend into publishable content",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            when (val s = state) {
                UiState.Idle -> {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Select a trend to generate ideas.", color = TextSecondary)
                    }
                }

                UiState.Loading -> {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is UiState.Error -> {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Could not generate ideas.\n" + s.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = PaddingValues(
                            start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
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
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("saved") }
                ) {
                    Text("Saved Ideas")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { navController.popBackStack() }
                ) {
                    Text("Back to Trends")
                }
            }
        }
    }
}
