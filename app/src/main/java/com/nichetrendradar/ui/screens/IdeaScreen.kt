package com.nichetrendradar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.ui.components.IdeaCard
import com.nichetrendradar.ui.theme.*
import com.nichetrendradar.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeaScreen(navController: NavController, viewModel: MainViewModel) {
    val state by viewModel.ideasState.collectAsState()
    val savedTitles = remember { mutableStateListOf<String>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(containerColor = Background, snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = {
                Column {
                    Text("AI Content Studio", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Turn a trend into publishable content", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                }
            }, navigationIcon = {
                TextButton(onClick = { navController.popBackStack() }) { Text("Back", color = PrimaryBright, fontWeight = FontWeight.Bold) }
            })
        }) { padding ->
        Column(Modifier.fillMaxSize().background(Background).padding(padding)) {
            when (val s = state) {
                UiState.Idle -> Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { Text("Select a trend to generate ideas.", color = TextSecondary) }
                UiState.Loading -> Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Primary)
                        Text("Creating content ideas…", color = TextSecondary, modifier = Modifier.padding(top = 12.dp))
                    }
                }
                is UiState.Error -> Box(Modifier.weight(1f).fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Card(colors = CardDefaults.cardColors(containerColor = Surface), shape = RoundedCornerShape(24.dp)) {
                        Column(Modifier.padding(22.dp)) {
                            Text("Generation failed", color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text(s.message, color = TextSecondary, modifier = Modifier.padding(top = 7.dp))
                        }
                    }
                }
                is UiState.Success -> LazyColumn(Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Surface), shape = RoundedCornerShape(22.dp),
                            modifier = Modifier.fillMaxWidth().border(1.dp, Border, RoundedCornerShape(22.dp))) {
                            Column(Modifier.padding(17.dp)) {
                                Text("AI-GENERATED", color = PrimaryBright, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Text("Copy individual sections, refine the idea, or save the complete concept to your library.",
                                    color = TextSecondary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 5.dp))
                            }
                        }
                    }
                    items(s.data) { idea ->
                        IdeaCard(idea = idea, saved = idea.title in savedTitles,
                            onCopied = { scope.launch { snackbarHostState.showSnackbar("Copied to clipboard") } },
                            onSave = {
                                viewModel.saveIdea(idea) { success, message ->
                                    if (success && idea.title !in savedTitles) savedTitles.add(idea.title)
                                    scope.launch { snackbarHostState.showSnackbar(if (success) "Saved to Library" else "Save failed: $message") }
                                }
                            })
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = { navController.navigate("saved") }, modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(18.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Border)) {
                    Text("Saved Ideas", color = PrimaryBright, fontWeight = FontWeight.Bold)
                }
                Button(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                    Text("Back to Trends", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
