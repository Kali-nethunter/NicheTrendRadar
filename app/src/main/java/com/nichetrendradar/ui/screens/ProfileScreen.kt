package com.nichetrendradar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.nichetrendradar.ui.theme.*
import com.nichetrendradar.viewmodel.MainViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: MainViewModel) {
    var loggingOut by remember { mutableStateOf(false) }
    val email = viewModel.accountEmail ?: "Account"
    val initial = email.take(1).uppercase()

    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(24.dp))

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Profile", color = TextPrimary, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text("Your radar account", color = TextSecondary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
            }
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Close", color = PrimaryBright, fontWeight = FontWeight.Bold)
            }
        }

        Card(
            Modifier.fillMaxWidth().padding(top = 22.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceElevated)
        ) {
            Box(
                Modifier.fillMaxWidth().background(
                    Brush.linearGradient(listOf(Color(0xFF171D31), Color(0xFF251B50), Color(0xFF101827)))
                ).padding(22.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(76.dp).clip(CircleShape)
                            .background(Primary.copy(alpha = .18f))
                            .border(2.dp, PrimaryBright.copy(alpha = .55f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initial, color = PrimaryBright, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.padding(start = 16.dp).weight(1f)) {
                        Text("SIGNED IN", color = PrimaryBright, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text("Welcome back", color = TextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 3.dp))
                        Text(email, color = TextSecondary, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 3.dp))
                    }
                }
            }
        }

        Text("ACCOUNT", color = TextSecondary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 26.dp, bottom = 10.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, Border)
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("Account & privacy", color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Your saved ideas and niches stay connected to this account.", color = TextSecondary, style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 5.dp))
                Divider(color = Border, modifier = Modifier.padding(vertical = 15.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Session", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Text("Authenticated", color = PrimaryBright, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 2.dp))
                    }
                    Surface(shape = RoundedCornerShape(10.dp), color = Primary.copy(alpha = .14f)) {
                        Text("SECURE", color = PrimaryBright, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp))
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

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

        Button(
            onClick = { navController.popBackStack() },
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth().height(58.dp).padding(top = 2.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Back to Radar", color = TextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(20.dp))
    }
}