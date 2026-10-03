package com.nichetrendradar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nichetrendradar.data.models.Trend
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.ui.components.TrendCard
import com.nichetrendradar.ui.theme.*
import com.nichetrendradar.viewmodel.MainViewModel

@Composable
private fun MetricTile(label: String, value: String, caption: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(value, color = TextPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(label, color = PrimaryBright, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        Text(caption, color = TextSecondary, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun PlatformPill(name: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
            .background(if (selected) Primary.copy(alpha = 0.22f) else Surface)
            .border(1.dp, if (selected) PrimaryBright else Border, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Text(name, modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp),
            color = if (selected) TextPrimary else TextSecondary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DashboardHero(niche: String, email: String?, onChangeNiche: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
        .background(Brush.linearGradient(listOf(SurfaceElevated, Color(0xFF1D1740), Surface)))
        .border(1.dp, Border, RoundedCornerShape(28.dp)).padding(22.dp)) {
        Column {
            Text("TREND INTELLIGENCE", color = PrimaryBright, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("Your radar is live.", color = TextPrimary, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp))
            Text("Discover signals in " + niche + " and turn momentum into content before the feed gets crowded.",
                color = TextSecondary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 7.dp))
            Surface(
                onClick = onChangeNiche,
                shape = RoundedCornerShape(12.dp),
                color = Primary.copy(alpha = .12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = .25f)),
                modifier = Modifier.padding(top = 14.dp)
            ) {
                Text("Change niche  →", color = PrimaryBright, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
            }
            if (!email.isNullOrBlank()) Text(email, color = TextSecondary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 12.dp))
        }
    }
}

@Composable
private fun MetricsPanel(trends: List<Trend>) {
    val avg = if (trends.isEmpty()) 0 else trends.map { it.score }.average().toInt()
    val top = trends.maxOfOrNull { it.score } ?: 0
    val growing = trends.count { val l = it.growth_label.lowercase(); l.contains("grow") || l.contains("ris") }
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = SurfaceElevated)) {
        Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            MetricTile("Signals", trends.size.toString(), "detected", Modifier.weight(1f))
            MetricTile("Avg score", avg.toString(), "quality", Modifier.weight(1f))
            MetricTile("Top score", top.toString(), "highest", Modifier.weight(1f))
            MetricTile("Growing", growing.toString(), "momentum", Modifier.weight(1f))
        }
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
    val nicheName = viewModel.currentNiche?.name ?: "your niche"

    Scaffold(containerColor = Background, topBar = {
        TopAppBar(title = {
            Column {
                Text("Niche Trend Radar", color = TextPrimary, fontWeight = FontWeight.Bold)
                Text("Find trends. Create faster.", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
            }
        }, actions = {
            TextButton(onClick = { navController.navigate("onboarding") }) {
                Text("Change niche", color = PrimaryBright, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = { navController.navigate("profile") }) {
                Text("Profile", color = PrimaryBright, fontWeight = FontWeight.Bold)
            }
        })
    }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(Background).padding(padding),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)) {
            item { DashboardHero(nicheName, viewModel.accountEmail) { navController.navigate("onboarding") } }
            item {
                when (val s = trendsState) {
                    is UiState.Success -> MetricsPanel(s.data)
                    UiState.Loading -> Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = SurfaceElevated)) {
                        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(Modifier.size(20.dp), color = Primary)
                            Text("Scanning fresh signals…", color = TextSecondary, modifier = Modifier.padding(start = 12.dp))
                        }
                    }
                    else -> {}
                }
            }
            item {
                Column {
                    Text("Platforms", color = TextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Choose where you want to create.", color = TextSecondary, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 3.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState()).padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("YouTube", "Instagram", "LinkedIn").forEach { platform ->
                            PlatformPill(platform, selectedPlatform == platform) {
                                selectedPlatform = platform
                                viewModel.setPlatform(platform)
                            }
                        }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Trending signals", color = TextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Ranked by current momentum", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                    TextButton(onClick = { viewModel.currentNiche?.id?.let { viewModel.fetchTrends(it, selectedPlatform) } }) {
                        Text("Refresh", color = PrimaryBright, fontWeight = FontWeight.Bold)
                    }
                }
            }
            when (val s = trendsState) {
                UiState.Idle, UiState.Loading -> {}
                is UiState.Error -> item {
                    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
                        Column(Modifier.padding(20.dp)) {
                            Text("Radar unavailable", color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text(s.message, color = TextSecondary, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
                is UiState.Success -> {
                    if (s.data.isEmpty()) item { Text("No signals yet. Try another platform.", color = TextSecondary) }
                    else items(s.data, key = { it.trend_id }) { trend ->
                        TrendCard(trend) {
                            viewModel.generateIdeas(trend.title)
                            navController.navigate("ideas")
                        }
                    }
                }
            }
            item {
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF211A52))) {
                    Column(Modifier.padding(20.dp)) {
                        Text("TURN SIGNALS INTO CONTENT", color = PrimaryBright, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text("Your next post can start here.", color = TextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 5.dp))
                        Text("Open your saved library or tap a trend to generate fresh ideas.", color = TextSecondary, modifier = Modifier.padding(top = 5.dp))
                        Button(onClick = { navController.navigate("saved") }, modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
                            Text("Open Saved Ideas")
                        }
                    }
                }
            }
        }
    }
}
