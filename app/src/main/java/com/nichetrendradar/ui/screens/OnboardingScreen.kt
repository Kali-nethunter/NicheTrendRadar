package com.nichetrendradar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.ui.theme.*
import com.nichetrendradar.viewmodel.MainViewModel

@Composable
private fun StepBadge(number: String, label: String, active: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (active) Primary else SurfaceElevated)
                .border(1.dp, if (active) Primary else Border, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(number, color = TextPrimary, fontWeight = FontWeight.Bold)
        }
        Text(
            label,
            color = if (active) TextPrimary else TextSecondary,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(viewModel: MainViewModel, onComplete: () -> Unit) {
    var niche by remember { mutableStateOf("") }
    var keywords by remember { mutableStateOf("") }
    val createState by viewModel.createNicheState.collectAsState()
    val loading = createState is UiState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            StepBadge("1", "Your niche", active = true)
            StepBadge("2", "Your radar", active = false)
        }

        Spacer(Modifier.height(34.dp))

        Text(
            "Build your
trend radar.",
            style = MaterialTheme.typography.displaySmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Tell us what you create or care about. We'll use it to personalize your trend feed.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            modifier = Modifier.padding(top = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Surface)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "Your focus",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = niche,
                    onValueChange = { niche = it; viewModel.clearCreateNicheState() },
                    label = { Text("Niche") },
                    placeholder = { Text("e.g. AI, Fitness, Finance") },
                    singleLine = true,
                    enabled = !loading,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp)
                )

                OutlinedTextField(
                    value = keywords,
                    onValueChange = { keywords = it; viewModel.clearCreateNicheState() },
                    label = { Text("Keywords") },
                    placeholder = { Text("e.g. AI tools, agents, automation") },
                    singleLine = true,
                    enabled = !loading,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )

                Text(
                    "Separate multiple keywords with commas.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 7.dp)
                )

                when (val result = createState) {
                    UiState.Idle -> Unit
                    UiState.Loading -> Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Primary,
                            strokeWidth = 2.dp
                        )
                        Text(
                            "Preparing your radar…",
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                    is UiState.Error -> Text(
                        "We couldn't connect to the radar. Please try again.",
                        modifier = Modifier.padding(top = 14.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    is UiState.Success -> Text(
                        "Radar ready. Opening your dashboard…",
                        modifier = Modifier.padding(top = 14.dp),
                        color = PrimaryBright,
                        style = MaterialTheme.typography.bodySmall
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
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(54.dp).padding(top = 10.dp)
                ) {
                    Text(
                        if (loading) "Preparing…" else "Start my radar",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            "You can change your niche later from your profile.",
            color = TextSecondary,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))
    }
}