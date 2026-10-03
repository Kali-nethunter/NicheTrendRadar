package com.nichetrendradar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nichetrendradar.data.models.ContentIdea
import com.nichetrendradar.ui.theme.*

private fun normalizeAi(text: String): String = text.replace(Regex("\\bai\\b", RegexOption.IGNORE_CASE), "AI")

@Composable
fun IdeaCard(idea: ContentIdea, saved: Boolean = false, onSave: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = SurfaceElevated)) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(shape = RoundedCornerShape(10.dp), color = Primary.copy(alpha = .18f)) {
                    Text("CONTENT IDEA", color = PrimaryBright, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                }
                if (idea.platform != null) Text(idea.platform!!, color = TextSecondary, style = MaterialTheme.typography.labelSmall)
            }
            Text(normalizeAi(idea.title), color = TextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp))
            Text("HOOK", color = PrimaryBright, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            Text(normalizeAi(idea.hook), color = TextSecondary, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 5.dp))
            if (idea.outline.isNotEmpty()) {
                Text("CONTENT OUTLINE", color = PrimaryBright, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp))
                idea.outline.forEachIndexed { index, item ->
                    Row(Modifier.fillMaxWidth().padding(top = 9.dp)) {
                        Surface(shape = RoundedCornerShape(8.dp), color = Primary.copy(alpha = .16f)) {
                            Text((index + 1).toString(), color = PrimaryBright, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                        Text(normalizeAi(item), color = TextSecondary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 10.dp).weight(1f))
                    }
                }
            }
            Text("CTA", color = PrimaryBright, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp))
            Text(normalizeAi(idea.cta), color = TextSecondary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 5.dp))
            Button(onClick = onSave, enabled = !saved, modifier = Modifier.fillMaxWidth().padding(top = 18.dp)) {
                Text(if (saved) "Saved to Library ✓" else "Save to Library")
            }
        }
    }
}
