package com.nichetrendradar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.ui.theme.*
import com.nichetrendradar.viewmodel.MainViewModel

@Composable
private fun ProfileSectionTitle(title: String) {
    Text(title.uppercase(), color = TextSecondary, style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 22.dp, bottom = 10.dp))
}

@Composable
private fun ProfileRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(Primary.copy(alpha = .12f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = PrimaryBright, modifier = Modifier.size(22.dp))
        }
        Column(Modifier.padding(start = 13.dp).weight(1f)) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall,
                maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
        }
        if (onClick != null) Text("›", color = PrimaryBright, style = MaterialTheme.typography.headlineSmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: MainViewModel) {
    var loggingOut by remember { mutableStateOf(false) }
    var showSecurityInfo by remember { mutableStateOf(false) }
    var showHelpInfo by remember { mutableStateOf(false) }
    val email = viewModel.accountEmail ?: "Account"
    val initial = email.take(1).uppercase()
    val niche = viewModel.currentNiche
    val savedState by viewModel.savedState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadSavedIdeas() }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Profile", color = TextPrimary, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text("Your radar account", color = TextSecondary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                }
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Filled.Close, contentDescription = "Close profile", tint = PrimaryBright)
                }
            }
        }

        item {
            Card(Modifier.fillMaxWidth().padding(top = 22.dp), shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated)) {
                Box(Modifier.fillMaxWidth().background(
                    Brush.linearGradient(listOf(Color(0xFF171D31), Color(0xFF251B50), Color(0xFF101827)))
                ).padding(22.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(76.dp).clip(CircleShape).background(Primary.copy(alpha = .18f))
                            .border(2.dp, PrimaryBright.copy(alpha = .55f), CircleShape), contentAlignment = Alignment.Center) {
                            Text(initial, color = PrimaryBright, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        }
                        Column(Modifier.padding(start = 16.dp).weight(1f)) {
                            Text("SIGNED IN", color = PrimaryBright, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Text("Welcome back", color = TextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 3.dp))
                            Text(email, color = TextSecondary, style = MaterialTheme.typography.bodyMedium, maxLines = 1,
                                overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 3.dp))
                        }
                    }
                }
            }
        }

        item {
            ProfileSectionTitle("My Radar")
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border)) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = PrimaryBright, modifier = Modifier.size(22.dp))
                        Text("Current radar", color = TextPrimary, style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 10.dp))
                        Spacer(Modifier.weight(1f))
                        Surface(shape = RoundedCornerShape(10.dp), color = Primary.copy(alpha = .14f)) {
                            Text("ACTIVE", color = PrimaryBright, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp))
                        }
                    }
                    Text(niche?.name ?: "No niche selected", color = TextPrimary, style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp))
                    Text(
                        if (niche?.keywords.isNullOrEmpty()) "No keywords added" else niche!!.keywords.joinToString(" • "),
                        color = TextSecondary, style = MaterialTheme.typography.bodySmall, maxLines = 2,
                        overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 5.dp)
                    )
                    if (!niche?.platforms.isNullOrEmpty()) {
                        Text("Platforms: " + niche!!.platforms.joinToString(" • "), color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 5.dp))
                    }
                    Button(onClick = { navController.navigate("onboarding") }, shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
                        Text("Change niche", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            ProfileSectionTitle("My Library")
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border)) {
                val countText = when (val saved = savedState) {
                    is UiState.Success -> "${saved.data.size} saved ideas"
                    else -> "Open your saved content library"
                }
                ProfileRow(Icons.Filled.Person, "Saved Ideas", countText) { navController.navigate("saved") }
            }
        }

        item {
            ProfileSectionTitle("Security & Privacy")
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border)) {
                ProfileRow(Icons.Filled.Lock, "Session security", "Authenticated and account-linked") { showSecurityInfo = true }
            }
        }

        item {
            ProfileSectionTitle("Account")
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border)) {
                ProfileRow(Icons.Filled.Person, "Account & privacy", "Your saved ideas and radar stay connected to this account")
            }
        }

        item {
            ProfileSectionTitle("Help & Support")
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border)) {
                ProfileRow(Icons.Filled.Check, "Help & support", "FAQ, feedback and app information") { showHelpInfo = true }
            }
        }

        item {
            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = {
                    loggingOut = true
                    viewModel.logout {
                        loggingOut = false
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    }
                },
                enabled = !loggingOut,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border)
            ) {
                Text(if (loggingOut) "Signing out…" else "Log out", color = PrimaryBright, fontWeight = FontWeight.Bold)
            }
            Button(onClick = { navController.popBackStack() }, shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().height(58.dp).padding(top = 2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                Text("Back to Radar", color = TextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Text("Niche Trend Radar • v1.0", color = TextSecondary, style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 8.dp))
        }
    }

    if (showSecurityInfo) {
        AlertDialog(onDismissRequest = { showSecurityInfo = false }, containerColor = SurfaceElevated,
            title = { Text("Session security", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Your session is active and authenticated.", color = TextSecondary)
                    Text("✓ Secure access token", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Text("✓ Account-linked saved data", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
            },
            confirmButton = { TextButton(onClick = { showSecurityInfo = false }) {
                Text("Done", color = PrimaryBright, fontWeight = FontWeight.Bold)
            } })
    }

    if (showHelpInfo) {
        AlertDialog(onDismissRequest = { showHelpInfo = false }, containerColor = SurfaceElevated,
            title = { Text("Help & support", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text("Use your radar to discover trends, generate content ideas and save the best concepts to your library. Support contact can be connected in a later update.", color = TextSecondary)
            },
            confirmButton = { TextButton(onClick = { showHelpInfo = false }) {
                Text("Done", color = PrimaryBright, fontWeight = FontWeight.Bold)
            } })
    }
}