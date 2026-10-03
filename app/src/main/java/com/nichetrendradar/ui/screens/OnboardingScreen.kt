package com.nichetrendradar.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.navigation.NavController
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.ui.theme.*
import com.nichetrendradar.viewmodel.MainViewModel

@Composable
private fun RadarPreview() {
    Box(
        modifier = Modifier
            .size(142.dp)
            .clip(CircleShape)
            .background(Surface)
            .border(1.dp, Border, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize().padding(18.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f
            listOf(0.38f, 0.66f, 0.94f).forEach {
                drawCircle(
                    color = Border,
                    radius = radius * it,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                )
            }
            drawLine(
                color = Primary,
                start = center,
                end = Offset(center.x + radius * 0.82f, center.y - radius * 0.52f),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(PrimaryBright, radius = 5.dp.toPx(), center = center)
        }
    }
}

@Composable
private fun ProgressStep(number: String, title: String, active: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (active) Primary else SurfaceElevated)
                .border(1.dp, if (active) Primary else Border, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (active) {
                Icon(Icons.Default.Check, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(17.dp))
            } else {
                Text(number, color = TextSecondary, fontWeight = FontWeight.Bold)
            }
        }
        Text(
            title,
            color = if (active) TextPrimary else TextSecondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(viewModel: MainViewModel, navController: NavController, onComplete: () -> Unit) {
    var niche by remember { mutableStateOf("") }
    var keywords by remember { mutableStateOf("") }
    val createState by viewModel.createNicheState.collectAsState()
    val loading = createState is UiState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Niche Trend Radar",
                color = TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { navController.navigate("profile") }) {
                Text("Profile", color = PrimaryBright, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(6.dp))
            Text(
                "1 of 2",
                color = TextSecondary,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(Modifier.height(18.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ProgressStep("1", "Your niche", active = true)
            ProgressStep("2", "Your radar", active = false)
        }

        Spacer(Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Build your\ntrend radar.",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Start with one clear niche. We'll turn it into personalized trend signals and content opportunities.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            RadarPreview()
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Surface)
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✦", color = PrimaryBright, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.padding(start = 12.dp)) {
                        Text(
                            "Your focus",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "What do you want to track?",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }
                }

                OutlinedTextField(
                    value = niche,
                    onValueChange = { niche = it; viewModel.clearCreateNicheState() },
                    label = { Text("Niche") },
                    placeholder = { Text("AI, fitness, finance…") },
                    singleLine = true,
                    enabled = !loading,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 18.dp)
                )

                OutlinedTextField(
                    value = keywords,
                    onValueChange = { keywords = it; viewModel.clearCreateNicheState() },
                    label = { Text("Keywords") },
                    placeholder = { Text("AI tools, agents, automation…") },
                    singleLine = true,
                    enabled = !loading,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )

                Text(
                    "Use commas to add multiple keywords.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )

                when (createState) {
                    UiState.Idle -> Unit
                    UiState.Loading -> Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Primary,
                            strokeWidth = 2.dp
                        )
                        Text("Building your radar…", color = TextSecondary, modifier = Modifier.padding(start = 10.dp))
                    }
                    is UiState.Error -> Text(
                        "We couldn't connect to the radar. Check your connection and try again.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 14.dp)
                    )
                    is UiState.Success -> Text(
                        "Radar ready. Opening your dashboard…",
                        color = PrimaryBright,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 14.dp)
                    )
                }

                Button(
                    onClick = {
                        val ks = keywords.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        viewModel.createNiche(
                            niche.ifBlank { "General" },
                            ks,
                            listOf("YouTube", "Instagram", "LinkedIn")
                        ) { onComplete() }
                    },
                    enabled = !loading,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 12.dp)
                ) {
                    Text(
                        if (loading) "Preparing…" else "Create my radar",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(
            "You can change your niche later from your profile.",
            color = TextSecondary,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(28.dp))
    }
}
