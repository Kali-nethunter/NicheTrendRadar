package com.nichetrendradar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nichetrendradar.data.models.Trend
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.ui.components.TrendCard
import com.nichetrendradar.ui.theme.*
import com.nichetrendradar.viewmodel.MainViewModel

@Composable
private fun MetricCard(label: String, value: String, supporting: String) {
    Card(
        modifier = Modifier.width(155.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, color = TextSecondary, style = MaterialTheme.typography.labelMedium)
            Text(
                value,
                color = TextPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                supporting,
                color = PrimaryBright,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            title,
            color = TextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        if (action != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(action, color = PrimaryBright, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun TrendRadarHeader(
    nicheName: String,
    email: String?,
    onProfile: () -> Unit,
    onSaved: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                "Your Trend Radar",
                color = TextPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                nicheName.ifBlank { "Personalized insights" },
                color = PrimaryBright,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (!email.isNullOrBlank()) {
                Text(
                    email,
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            TextButton(onClick = onProfile) {
                Text("Profile", color = PrimaryBright, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = onSaved) {
                Text("Saved", color = TextSecondary)
            }
        }
    }
}

@Composable
private fun RadarMetrics(trends: List<Trend>) {
    val average = if (trends.isEmpty()) 0 else trends.map { it.score }.average().toInt()
    val topScore = trends.maxOfOrNull { it.score } ?: 0
    val growing = trends.count {
        val label = it.growth_label.lowercase()
        label.contains("grow") || label.contains("ris")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MetricCard("Signals", trends.size.toString(), "Detected")
        MetricCard("Avg. score", average.toString(), "Across signals")
        MetricCard("Top score", topScore.toString(), "Highest signal")
        MetricCard("Growing", growing.toString(), "Momentum signals")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, viewModel: MainViewModel) {
    var selectedPlatform by remember { mutableStateOf(viewModel.selectedPlatform) }

    LaunchedEffect(viewModel.currentNiche?.id, selectedPlatform) {
        viewModel.currentNiche?.id?.let { viewModel.fetchTrends(it, selectedPlatform) }
    }

    val trendsState by viewModel.trendsState.collectAsState()
    val nicheName = viewModel.currentNiche?.name ?: "Your niche"

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Niche Trend Radar", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = { navController.navigate("profile") }) {
                        Text("Profile", color = PrimaryBright)
                    }
                },
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                TrendRadarHeader(
                    nicheName = nicheName,
                    email = viewModel.accountEmail,
                    onProfile = { navController.navigate("profile") },
                    onSaved = { navController.navigate("saved") }
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceElevated)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text(
                            "Radar overview",
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Fresh signals from your selected platform, scored for content opportunity.",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                        Spacer(Modifier.height(14.dp))
                        when (val s = trendsState) {
                            UiState.Idle -> Text("Ready to scan.", color = TextSecondary)
                            UiState.Loading -> Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Primary,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    "Scanning for fresh signals…",
                                    color = TextSecondary,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                            }
                            is UiState.Error -> Text(
                                "We couldn't refresh your radar. Pull again from the platform below.",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                            is UiState.Success -> RadarMetrics(s.data)
                        }
                    }
                }
            }

            item {
                SectionHeader("Platforms")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("YouTube", "Instagram", "LinkedIn").forEach { platform ->
                        FilterChip(
                            selected = selectedPlatform == platform,
                            onClick = {
                                selectedPlatform = platform
                                viewModel.setPlatform(platform)
                            },
                            label = {
                                Text(
                                    platform,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary.copy(alpha = 0.20f),
                                selectedLabelColor = TextPrimary,
                                containerColor = Surface,
                                labelColor = TextSecondary
                            ),
                            )
                    }
                }
            }

            item {
                SectionHeader(
                    title = "Trending signals",
                    action = "Refresh",
                    onAction = {
                        viewModel.currentNiche?.id?.let {
                            viewModel.fetchTrends(it, selectedPlatform)
                        }
                    }
                )
            }

            when (val s = trendsState) {
                UiState.Idle -> item {
                    Text("Your radar is ready.", color = TextSecondary)
                }
                UiState.Loading -> item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                is UiState.Error -> item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface)
                    ) {
                        Column(Modifier.padding(18.dp)) {
                            Text("Radar unavailable", color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text(
                                s.message,
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
                is UiState.Success -> {
                    if (s.data.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Surface)
                            ) {
                                Column(Modifier.padding(20.dp)) {
                                    Text("No signals yet", color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Text(
                                        "Try another platform or refresh your radar.",
                                        color = TextSecondary,
                                        modifier = Modifier.padding(top = 5.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        items(s.data, key = { it.trend_id }) { trend ->
                            TrendCard(trend) {
                                viewModel.generateIdeas(trend.title)
                                navController.navigate("ideas")
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Border, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Background)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text(
                            "Turn trends into content",
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Tap any signal to generate platform-specific content ideas.",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                        TextButton(
                            onClick = { navController.navigate("saved") },
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text("Open saved ideas →", color = PrimaryBright, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
