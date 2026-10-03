package com.nichetrendradar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.ui.components.IdeaCard
import com.nichetrendradar.ui.theme.Background
import com.nichetrendradar.ui.theme.PrimaryBright
import com.nichetrendradar.ui.theme.TextSecondary
import com.nichetrendradar.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeaScreen(navController: NavController, viewModel: MainViewModel) {
    val state by viewModel.ideasState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val savedTitles = remember { mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AI Content Ideas", fontWeight = FontWeight.Bold)
                        Text("Turn a trend into publishable content", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Back", color = PrimaryBright, fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding)
        ) {
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

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(modifier = Modifier.weight(1f), onClick = { navController.navigate("saved") }) {
                    Text("Saved Ideas")
                }
                Button(modifier = Modifier.weight(1f), onClick = { navController.popBackStack() }) {
                    Text("Back to Trends")
                }
            }
        }
    }
}