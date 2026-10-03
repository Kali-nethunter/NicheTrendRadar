package com.nichetrendradar.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nichetrendradar.data.models.ContentIdea
import com.nichetrendradar.ui.theme.*

private fun normalizeAi(text: String): String = text.replace(Regex("\\bai\\b", RegexOption.IGNORE_CASE), "AI")

@Composable
private fun CopyButton(text: String, onCopied: () -> Unit) {
    val context = LocalContext.current
    OutlinedButton(onClick = {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Content", text))
        onCopied()
    }, contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp), shape = RoundedCornerShape(12.dp)) {
        Text("Copy", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SectionCard(title: String, body: String, onCopied: () -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, color = PrimaryBright, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                CopyButton(body, onCopied)
            }
            Text(normalizeAi(body), color = TextSecondary, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 9.dp))
        }
    }
}

@Composable
fun IdeaCard(idea: ContentIdea, saved: Boolean = false, onSave: () -> Unit, onCopied: () -> Unit = {}) {
    val outlineText = idea.outline.mapIndexed { index, item -> (index + 1).toString() + ". " + normalizeAi(item) }.joinToString("\n")
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = SurfaceElevated)) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(shape = RoundedCornerShape(12.dp), color = Primary.copy(alpha = .18f)) {
                    Text("CONTENT IDEA", color = PrimaryBright, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp))
                }
                if (!idea.platform.isNullOrBlank()) Text(idea.platform!!, color = TextSecondary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            }
            Text(normalizeAi(idea.title), color = TextPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp))
            Text("READY TO PUBLISH", color = PrimaryBright, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 7.dp))
            SectionCard("HOOK", idea.hook, onCopied)
            if (idea.outline.isNotEmpty()) {
                Card(Modifier.fillMaxWidth().padding(top = 12.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("CONTENT OUTLINE", color = PrimaryBright, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            CopyButton(outlineText, onCopied)
                        }
                        idea.outline.forEachIndexed { index, item ->
                            Row(Modifier.fillMaxWidth().padding(top = 11.dp)) {
                                Surface(shape = RoundedCornerShape(9.dp), color = Primary.copy(alpha = .18f)) {
                                    Text((index + 1).toString(), color = PrimaryBright, fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp))
                                }
                                Text(normalizeAi(item), color = TextSecondary, style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 10.dp).weight(1f))
                            }
                        }
                    }
                }
            }
            SectionCard("CTA", idea.cta, onCopied)
            Box(Modifier.fillMaxWidth().padding(top = 16.dp).clip(RoundedCornerShape(18.dp))
                .background(Brush.horizontalGradient(listOf(Primary, PrimaryBright)))
                .border(1.dp, PrimaryBright.copy(alpha = .65f), RoundedCornerShape(18.dp))) {
                TextButton(onClick = onSave, enabled = !saved, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(vertical = 15.dp)) {
                    Text(if (saved) "Saved to Library ✓" else "Save to Library", color = TextPrimary,
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
