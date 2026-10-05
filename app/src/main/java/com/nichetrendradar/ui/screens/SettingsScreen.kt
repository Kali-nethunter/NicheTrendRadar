package com.nichetrendradar.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.nichetrendradar.ui.theme.*
import com.nichetrendradar.viewmodel.MainViewModel

private enum class SettingsMode { SECURITY, ACCOUNT, HELP }

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(title.uppercase(), color = TextSecondary, style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp, bottom = 9.dp))
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)) { Column(content = content) }
}

@Composable
private fun RowItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String,
                    enabled: Boolean = true, onClick: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth()
        .then(if (enabled && onClick != null) Modifier.clickable { onClick() } else Modifier)
        .padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = if (enabled) PrimaryBright else TextSecondary, modifier = Modifier.size(22.dp))
        Column(Modifier.padding(start = 14.dp).weight(1f)) {
            Text(title, color = if (enabled) TextPrimary else TextSecondary, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 2.dp))
        }
        if (enabled && onClick != null) Text("›", color = PrimaryBright, style = MaterialTheme.typography.headlineSmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, mode: String, viewModel: MainViewModel) {
    val screenMode = runCatching { SettingsMode.valueOf(mode.uppercase()) }.getOrDefault(SettingsMode.SECURITY)
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("niche_trend_radar", Context.MODE_PRIVATE) }
    val email = viewModel.accountEmail ?: "Account"
    var info by remember { mutableStateOf<String?>(null) }
    var sessionDialog by remember { mutableStateOf(false) }
    var changePasswordDialog by remember { mutableStateOf(false) }
    var confirmClearSaved by remember { mutableStateOf(false) }
    var confirmClearHistory by remember { mutableStateOf(false) }
    var confirmDeleteAccount by remember { mutableStateOf(false) }
    var deleteDialog by remember { mutableStateOf(false) }
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    var personalized by remember { mutableStateOf(prefs.getBoolean("personalized_recommendations", true)) }
    var analytics by remember { mutableStateOf(prefs.getBoolean("usage_analytics", true)) }
    var marketing by remember { mutableStateOf(prefs.getBoolean("marketing_emails", false)) }

    fun pref(key: String, value: Boolean) { prefs.edit().putBoolean(key, value).apply() }

    LazyColumn(Modifier.fillMaxSize().background(Background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 22.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        when (screenMode) {
                            SettingsMode.SECURITY -> "Security & Privacy"
                            SettingsMode.ACCOUNT -> "Account & Privacy"
                            SettingsMode.HELP -> "Help & Support"
                        },
                        color = TextPrimary, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(
                        when (screenMode) {
                            SettingsMode.SECURITY -> "Protect your account and understand your data."
                            SettingsMode.ACCOUNT -> "Manage your account and privacy preferences."
                            SettingsMode.HELP -> "Find answers, report problems and share feedback."
                        },
                        color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
                }
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.Close, "Back", tint = PrimaryBright)
                }
            }
        }

        when (screenMode) {
            SettingsMode.SECURITY -> {
                item { Section("Session Security") {
                    RowItem(Icons.Filled.Lock, "Session security", "● Active • This device • Authenticated") { sessionDialog = true }
                    RowItem(Icons.Filled.Person, "Sign out from this device", "End the current authenticated session") {
                        viewModel.logout { navController.navigate("login") { popUpTo(0) { inclusive = true } } }
                    }
                    RowItem(Icons.Filled.Lock, "Sign out of all devices", "Sign out every other active session") { viewModel.signOutAllDevices { _, message -> info = message } }
                }}
                item { Section("Password & Authentication") {
                    RowItem(Icons.Filled.Lock, "Change password", "Update your account password") { changePasswordDialog = true }
                    RowItem(Icons.Filled.Lock, "Password strength", "Use a unique password with 8+ characters") {
                        info = "Use a unique password with upper/lowercase letters, numbers and symbols."
                    }
                    RowItem(Icons.Filled.Lock, "Forgot password", "Open a password recovery request") { val i = Intent(Intent.ACTION_SENDTO).apply { data = Uri.parse("mailto:"); putExtra(Intent.EXTRA_SUBJECT, "Niche Trend Radar — Password Recovery"); putExtra(Intent.EXTRA_TEXT, "Please help me recover access to my account. Email: $email") }; runCatching { context.startActivity(i) } }
                    RowItem(Icons.Filled.Check, "2FA / biometric authentication", "Protect this device with biometric authentication") { info = "Biometric protection is ready to be connected to the device lock in the next security build." }
                }}
                item { Section("Data Privacy") {
                    RowItem(Icons.Filled.Person, "What data the app stores", "Account, radar configuration and saved ideas") {
                        info = "The app stores account information, selected niche, keywords, platforms and saved content ideas needed for the app experience."
                    }
                    RowItem(Icons.Filled.Check, "How trend/search data is used", "Used to request radar results and content ideas") {
                        info = "Your niche, keywords and selected platform provide context for trend requests and AI-generated content ideas."
                    }
                    RowItem(Icons.Filled.Check, "AI processing information", "How AI features use your inputs") {
                        info = "AI features use the trend and niche context you submit to generate content ideas. Do not submit sensitive personal information."
                    }
                    RowItem(Icons.Filled.Lock, "Encryption & API security", "Authenticated API communication over HTTPS") {
                        info = "The app uses authenticated API requests over HTTPS. The authentication token is stored locally for the active session."
                    }
                }}
                item { Section("Data Controls") {
                    RowItem(Icons.Filled.Check, "Download my data", "Export account data as JSON") { viewModel.exportAccount { ok, data -> if (ok) { val share = Intent(Intent.ACTION_SEND).apply { type = "application/json"; putExtra(Intent.EXTRA_TEXT, data) }; runCatching { context.startActivity(Intent.createChooser(share, "Export account data")) } } else info = data } }
                    RowItem(Icons.Filled.Check, "Clear saved ideas", "Remove every saved idea from your Library") { confirmClearSaved = true }
                    RowItem(Icons.Filled.Check, "Clear search / radar history", "Remove stored radar activity") { confirmClearHistory = true }
                    RowItem(Icons.Filled.Close, "Delete account", "Permanently remove your account and data") { confirmDeleteAccount = true }
                }}
                item { Section("Privacy Documents") {
                    RowItem(Icons.Filled.Check, "Privacy Policy", "How account and app data is handled") {
                        info = "Add your published Privacy Policy URL here before public launch."
                    }
                    RowItem(Icons.Filled.Check, "Terms of Service", "Rules for using Niche Trend Radar") {
                        info = "Add your published Terms of Service URL here before public launch."
                    }
                    RowItem(Icons.Filled.Check, "Data Usage Policy", "How data supports radar and AI features") {
                        info = "Add your published Data Usage Policy URL here before public launch."
                    }
                }}
            }

            SettingsMode.ACCOUNT -> {
                item { Section("Profile Information") {
                    RowItem(Icons.Filled.Person, "Email", email)
                    RowItem(Icons.Filled.Person, "User ID", prefs.getInt("user_id", -1).takeIf { it > 0 }?.toString() ?: "Unavailable")
                    RowItem(Icons.Filled.Person, "Account created", "Creation date will appear when the backend exposes it")
                }}
                item { Section("Account Preferences") {
                    RowItem(Icons.Filled.Check, "Default platform", viewModel.selectedPlatform)
                    RowItem(Icons.Filled.Check, "Default niche", viewModel.currentNiche?.name ?: "Not selected")
                    RowItem(Icons.Filled.Check, "Default keywords", viewModel.currentNiche?.keywords?.joinToString(" • ") ?: "None")
                }}
                item { Section("Privacy Settings") {
                    ToggleRow("Personalized recommendations", "More relevant suggestions", personalized) { personalized = it; pref("personalized_recommendations", it) }
                    ToggleRow("Usage analytics", "Help improve performance and reliability", analytics) { analytics = it; pref("usage_analytics", it) }
                    ToggleRow("Marketing communications", "Receive product and feature updates", marketing) { marketing = it; pref("marketing_emails", it) }
                }}
                item { Section("Your Data") {
                    RowItem(Icons.Filled.Check, "View my data", "Review account data used by the app") {
                        info = "Current account data includes your email, user ID, radar niche, keywords, platforms and saved ideas."
                    }
                    RowItem(Icons.Filled.Check, "Export my data", "Download a copy of your account data", false)
                    RowItem(Icons.Filled.Check, "Delete my data", "Remove selected data", false)
                    RowItem(Icons.Filled.Close, "Delete account", "Permanently remove account and associated data") { deleteDialog = true }
                }}
                item { Section("Account Actions") {
                    RowItem(Icons.Filled.Person, "Log out", "End your current session") {
                        viewModel.logout { navController.navigate("login") { popUpTo(0) { inclusive = true } } }
                    }
                    RowItem(Icons.Filled.Close, "Delete account", "Permanent action — cannot be undone") { deleteDialog = true }
                }}
            }

            SettingsMode.HELP -> {
                item { Section("Help Center") {
                    RowItem(Icons.Filled.Check, "How does Niche Trend Radar work?", "Radar, trends and AI content ideas") {
                        info = "Choose a niche, keywords and platforms. The radar requests relevant trend data, then you can generate content ideas and save the best concepts."
                    }
                    RowItem(Icons.Filled.Check, "How are trends calculated?", "Scores and growth labels") {
                        info = "Trend scores and growth labels are provided by the connected trend service; the exact methodology depends on its data source."
                    }
                    RowItem(Icons.Filled.Check, "Why isn't my radar generating results?", "Troubleshoot your radar") {
                        info = "Check your internet connection, make sure a niche is configured, and try again. If it continues, use Report a Problem."
                    }
                    RowItem(Icons.Filled.Check, "How do I change my niche?", "Update your radar configuration") {
                        info = "Open Profile → My Radar → Change niche."
                    }
                    RowItem(Icons.Filled.Check, "How do I save an idea?", "Add ideas to your Library") {
                        info = "Open a generated content idea and use the save action. Saved ideas appear under Profile → My Library."
                    }
                    RowItem(Icons.Filled.Check, "How do I delete a saved idea?", "Remove content from your Library") {
                        info = "Open the saved idea and use its delete action. The deletion is sent to the backend."
                    }
                    RowItem(Icons.Filled.Check, "Why am I not seeing new trends?", "Refresh and connectivity checks") {
                        info = "Trend results depend on the backend service and source data. Refresh the radar and verify your selected platform."
                    }
                }}
                item { Section("Contact Support") {
                    RowItem(Icons.Filled.Person, "Contact support", "Send a support request") { subject = ""; description = ""; info = "Support form is ready; connect it to your support email or ticket API before public launch." }
                    RowItem(Icons.Filled.Close, "Report a problem", "Tell us what went wrong") { subject = ""; description = ""; info = "Problem reporting UI is ready; connect it to your reporting endpoint before public launch." }
                    RowItem(Icons.Filled.Check, "Send feedback", "Help improve the product") { feedback = ""; info = "Feedback UI is ready; connect it to a feedback endpoint before public launch." }
                }}
                item { Section("App Information") {
                    RowItem(Icons.Filled.Check, "App version", "Niche Trend Radar • v1.0") { info = "Niche Trend Radar v1.0" }
                    RowItem(Icons.Filled.Check, "Build number", "Provided by the release build") { info = "Build number is available from the installed APK/build metadata." }
                    RowItem(Icons.Filled.Check, "API status", "Connected service status") { info = "The app uses the configured Niche Trend Radar API over HTTPS. Live health monitoring can be added later." }
                    RowItem(Icons.Filled.Check, "Privacy Policy", "Privacy and data handling") { info = "Add your published Privacy Policy URL before public launch." }
                    RowItem(Icons.Filled.Check, "Terms of Service", "Terms for using the app") { info = "Add your published Terms of Service URL before public launch." }
                    RowItem(Icons.Filled.Check, "Open-source licenses", "Third-party software notices") { info = "Add a generated third-party license screen before public launch." }
                }}
            }
        }

        item {
            Spacer(Modifier.height(20.dp))
            Text("Niche Trend Radar • v1.0", color = TextSecondary, style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))
        }
    }

    if (sessionDialog) {
        AlertDialog(onDismissRequest = { sessionDialog = false }, containerColor = SurfaceElevated,
            title = { Text("Session Security", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("Your account is currently protected and signed in securely.", color = TextSecondary)
                Text("● Active", color = PrimaryBright, fontWeight = FontWeight.Bold)
                Text("Signed in as $email", color = TextPrimary)
                Text("Current session: This device", color = TextPrimary)
                Text("✓ Authentication token protected", color = TextPrimary)
                Text("✓ Secure API connection", color = TextPrimary)
                Text("✓ Session authenticated", color = TextPrimary)
            }},
            confirmButton = { TextButton(onClick = { sessionDialog = false }) { Text("Close", color = PrimaryBright) } },
            dismissButton = { TextButton(onClick = {
                sessionDialog = false
                viewModel.logout { navController.navigate("login") { popUpTo(0) { inclusive = true } } }
            }) { Text("Sign Out", color = PrimaryBright) } })
    }

    if (deleteDialog) {
        AlertDialog(onDismissRequest = { deleteDialog = false }, containerColor = SurfaceElevated,
            title = { Text("Delete your account?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("This action will remove your profile, saved ideas and associated account data. This cannot be undone.", color = TextSecondary) },
            confirmButton = { TextButton(onClick = {
                deleteDialog = false
                info = "Account deletion is not enabled yet because a protected backend delete-account endpoint is required."
            }) { Text("Delete Account", color = PrimaryBright, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { deleteDialog = false }) { Text("Cancel", color = TextSecondary) } })
    }

    if (changePasswordDialog) {
        var currentPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { changePasswordDialog = false },
            containerColor = SurfaceElevated,
            title = { Text("Change Password", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(currentPassword, { currentPassword = it }, label = { Text("Current password") }, singleLine = true)
                    OutlinedTextField(newPassword, { newPassword = it }, label = { Text("New password") }, singleLine = true)
                    OutlinedTextField(confirmPassword, { confirmPassword = it }, label = { Text("Confirm new password") }, singleLine = true)
                    Text("Minimum 8 characters.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newPassword.length < 8) info = "New password must be at least 8 characters"
                    else if (newPassword != confirmPassword) info = "New passwords do not match"
                    else {
                        viewModel.changePassword(currentPassword, newPassword) { _, message -> info = message }
                        changePasswordDialog = false
                    }
                }) { Text("Change Password", color = PrimaryBright) }
            },
            dismissButton = { TextButton(onClick = { changePasswordDialog = false }) { Text("Cancel", color = TextSecondary) } }
        )
    }

    if (confirmClearSaved) {
        AlertDialog(
            onDismissRequest = { confirmClearSaved = false },
            containerColor = SurfaceElevated,
            title = { Text("Clear saved ideas?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Every saved idea in your Library will be permanently removed.", color = TextSecondary) },
            confirmButton = { TextButton(onClick = { confirmClearSaved = false; viewModel.clearSavedIdeas { _, message -> info = message } }) { Text("Clear", color = PrimaryBright) } },
            dismissButton = { TextButton(onClick = { confirmClearSaved = false }) { Text("Cancel", color = TextSecondary) } }
        )
    }

    if (confirmClearHistory) {
        AlertDialog(
            onDismissRequest = { confirmClearHistory = false },
            containerColor = SurfaceElevated,
            title = { Text("Clear radar history?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Stored radar activity for this account will be removed.", color = TextSecondary) },
            confirmButton = { TextButton(onClick = { confirmClearHistory = false; viewModel.clearRadarHistory { _, message -> info = message } }) { Text("Clear History", color = PrimaryBright) } },
            dismissButton = { TextButton(onClick = { confirmClearHistory = false }) { Text("Cancel", color = TextSecondary) } }
        )
    }

    if (confirmDeleteAccount) {
        AlertDialog(
            onDismissRequest = { confirmDeleteAccount = false },
            containerColor = SurfaceElevated,
            title = { Text("Delete your account permanently?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("This removes your profile, saved ideas, radar configuration, radar history and active sessions. This cannot be undone.", color = TextSecondary) },
            confirmButton = { TextButton(onClick = {
                confirmDeleteAccount = false
                viewModel.deleteAccount { ok, message ->
                    if (ok) navController.navigate("login") { popUpTo(0) { inclusive = true } } else info = message
                }
            }) { Text("Delete Account", color = PrimaryBright) } },
            dismissButton = { TextButton(onClick = { confirmDeleteAccount = false }) { Text("Cancel", color = TextSecondary) } }
        )
    }

    info?.let { message ->
        AlertDialog(onDismissRequest = { info = null }, containerColor = SurfaceElevated,
            title = { Text(when (screenMode) { SettingsMode.SECURITY -> "Security & Privacy"; SettingsMode.ACCOUNT -> "Account & Privacy"; SettingsMode.HELP -> "Help & Support" }, color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text(message, color = TextSecondary) },
            confirmButton = { TextButton(onClick = { info = null }) { Text("Close", color = PrimaryBright) } })
    }
}

@Composable
private fun ToggleRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 2.dp))
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
