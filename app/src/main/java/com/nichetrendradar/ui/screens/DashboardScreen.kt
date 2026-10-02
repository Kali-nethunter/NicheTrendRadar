package com.nichetrendradar.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.ui.components.TrendCard
import com.nichetrendradar.viewmodel.MainViewModel

@Composable
fun DashboardScreen(navController: NavController, viewModel: MainViewModel) {
    LaunchedEffect(viewModel.currentNiche?.id) {
        viewModel.currentNiche?.id?.let { viewModel.fetchTrends(it) }
    }

    val trendsState by viewModel.trendsState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Trend Radar") }) }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Row(
                Modifier.horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                listOf("YouTube", "Instagram", "LinkedIn").forEach { platform ->
                    FilterChip(
                        selected = viewModel.selectedPlatform == platform,
                        onClick = { viewModel.setPlatform(platform) },
                        label = { Text(platform) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            when (val s = trendsState) {
                UiState.Idle -> Text("Scanning for trends...", modifier = Modifier.padding(16.dp))
                UiState.Loading -> CircularProgressIndicator(Modifier.padding(16.dp))
                is UiState.Error -> Text("Error: " + s.message, modifier = Modifier.padding(16.dp))
                is UiState.Success -> LazyColumn {
                    items(s.data) { trend ->
                        TrendCard(trend) {
                            viewModel.generateIdeas(trend.title)
                            navController.navigate("ideas")
                        }
                    }
                }
            }
        }
    }
}