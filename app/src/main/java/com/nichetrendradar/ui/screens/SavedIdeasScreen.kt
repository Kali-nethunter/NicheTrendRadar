package com.nichetrendradar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

    Scaffold(containerColor = Background, topBar = {
        TopAppBar(title = {
            Column {
                Text("Saved Library", color = TextPrimary, fontWeight = FontWeight.Bold)
                Text("Your ideas, ready when you are.", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
            }
        }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.padding(start = 8.dp).size(44.dp)) {
                Surface(shape = RoundedCornerShape(14.dp), color = SurfaceElevated) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryBright, modifier = Modifier.padding(10.dp))
                }
            }
        })
    }) { padding ->
        when (val s = state) {
            UiState.Idle, UiState.Loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            is UiState.Error -> Box(Modifier.fillMaxSize().padding(padding).padding(24.dp), contentAlignment = Alignment.Center) {
                Card(colors = CardDefaults.cardColors(containerColor = Surface), shape = RoundedCornerShape(24.dp)) {
                    Text("Could not load saved ideas.\n" + s.message, color = TextSecondary, modifier = Modifier.padding(22.dp))
                }
            }
            is UiState.Success -> {
                if (s.data.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(padding).padding(24.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Your library is empty", color = TextPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("Save an AI content idea from the Content Studio.", color = TextSecondary, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                } else {
                    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        item {
                            Text(s.data.size.toString() + " saved ideas", color = PrimaryBright, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        }
                        items(s.data) { idea ->
                            Card(modifier = Modifier.fillMaxWidth().clickable {
                                viewModel.selectSavedIdea(idea)
                                navController.navigate("saved_detail")
                            }, shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = SurfaceElevated)) {
                                Column(Modifier.padding(20.dp)) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Surface(shape = RoundedCornerShape(10.dp), color = Primary.copy(alpha = .18f)) {
                                            Text((idea.platform ?: "GENERAL").uppercase(), color = PrimaryBright, style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                                        }
                                        Text("OPEN →", color = PrimaryBright, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                    Text(idea.title.replace(Regex("\\bAI\\b", RegexOption.IGNORE_CASE), "AI"),
                                        color = TextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp))
                                    Text(idea.hook.replace(Regex("\\bai\\b", RegexOption.IGNORE_CASE), "AI"),
                                        color = TextSecondary, style = MaterialTheme.typography.bodyMedium, maxLines = 3, modifier = Modifier.padding(top = 7.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
