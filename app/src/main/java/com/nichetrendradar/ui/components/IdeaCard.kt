package com.nichetrendradar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nichetrendradar.data.models.ContentIdea
import com.nichetrendradar.ui.theme.PrimaryBright
import com.nichetrendradar.ui.theme.TextSecondary

private fun normalizeAi(text: String): String =
    text.replace(Regex("\\bai\\b", RegexOption.IGNORE_CASE), "AI")

@Composable
fun IdeaCard(
    idea: ContentIdea,
    saved: Boolean = false,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(22.dp)) {
            Text(
                normalizeAi(idea.title),
                style = MaterialTheme.typography.headlineSmall,
                color = PrimaryBright,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(14.dp))
            Text("HOOK", color = PrimaryBright, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(normalizeAi(idea.hook), modifier = Modifier.padding(top = 5.dp), color = TextSecondary, style = MaterialTheme.typography.bodyLarge)

            if (idea.outline.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text("CONTENT OUTLINE", color = PrimaryBright, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                idea.outline.forEachIndexed { index, item ->
                    Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text((index + 1).toString(), color = PrimaryBright, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(10.dp))
                        Text(normalizeAi(item), color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("CTA", color = PrimaryBright, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(normalizeAi(idea.cta), modifier = Modifier.padding(top = 5.dp), color = TextSecondary, style = MaterialTheme.typography.bodyLarge)

            Button(
                onClick = onSave,
                enabled = !saved,
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp)
            ) {
                Text(if (saved) "Saved ✓" else "Save to Library")
            }
        }
    }
}
