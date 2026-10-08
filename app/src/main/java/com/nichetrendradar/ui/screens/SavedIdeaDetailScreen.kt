package com.nichetrendradar.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nichetrendradar.ui.theme.*
import com.nichetrendradar.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedIdeaDetailScreen(navController: NavController, viewModel: MainViewModel) {
    val idea by viewModel.selectedSavedIdea.collectAsState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(containerColor = Background, snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = {
                Column {
                    Text("Content Idea", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Saved to your library", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                }
            }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.padding(start = 8.dp).size(44.dp)) {
                    Surface(shape = RoundedCornerShape(14.dp), color = SurfaceElevated) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryBright, modifier = Modifier.padding(10.dp))
                    }
                }
            })
        }) { padding ->
        Column(Modifier.fillMaxSize().background(Background).verticalScroll(rememberScrollState()).padding(padding).padding(horizontal = 18.dp, vertical = 14.dp)) {
            if (idea == null) {
                Text("Saved idea not found.", color = TextSecondary)
            } else {
                val item = idea!!
                val displayTitle = item.title.replace(Regex("\\bAI\\b", RegexOption.IGNORE_CASE), "AI")
                val shareText = buildString {
                    appendLine(displayTitle); appendLine(); appendLine("HOOK"); appendLine(item.hook); appendLine()
                    appendLine("OUTLINE"); item.outline.forEachIndexed { index, point -> appendLine((index + 1).toString() + ". " + point) }
                    appendLine(); appendLine("CTA"); appendLine(item.cta)
                }
                Card(colors = CardDefaults.cardColors(containerColor = SurfaceElevated), shape = RoundedCornerShape(28.dp)) {
                    Column(Modifier.padding(22.dp)) {
                        Text("SAVED IDEA", color = PrimaryBright, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text(displayTitle, color = TextPrimary, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
                        Surface(shape = RoundedCornerShape(10.dp), color = Primary.copy(alpha = .18f), modifier = Modifier.padding(top = 12.dp)) {
                            Text((item.platform ?: "GENERAL").uppercase(), color = PrimaryBright, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp))
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Saved Idea", shareText))
                        scope.launch { snackbarHostState.showSnackbar("Copied to clipboard") }
                    }, modifier = Modifier.weight(1f)) { Text("Copy") }
                    Button(onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, shareText) }
                        context.startActivity(Intent.createChooser(intent, "Share idea"))
                    }, modifier = Modifier.weight(1f)) { Text("Share") }
                }
                OutlinedButton(onClick = { showDeleteDialog = true }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) { Text("Delete from Library") }
                DetailBlock("HOOK", item.hook)
                DetailBlock("OUTLINE", item.outline.mapIndexed { index, point -> (index + 1).toString() + ". " + point }.joinToString("\n"))
                DetailBlock("CTA", item.cta)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
    if (showDeleteDialog) {
        AlertDialog(onDismissRequest = { showDeleteDialog = false }, title = { Text("Delete saved idea?") },
            text = { Text("This will permanently remove this idea from your Library.") },
            confirmButton = { TextButton(onClick = {
                val selected = idea
                if (selected != null) viewModel.deleteSavedIdea(selected) { success, message ->
                    if (success) navController.popBackStack() else scope.launch { snackbarHostState.showSnackbar("Delete failed: " + message) }
                }
                showDeleteDialog = false
            }) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } })
    }
}

@Composable
private fun DetailBlock(title: String, body: String) {
    Card(Modifier.fillMaxWidth().padding(top = 14.dp), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
        Column(Modifier.padding(20.dp)) {
            Text(title, color = PrimaryBright, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(body.replace(Regex("\\bai\\b", RegexOption.IGNORE_CASE), "AI"), color = TextSecondary, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 9.dp))
        }
    }
}
