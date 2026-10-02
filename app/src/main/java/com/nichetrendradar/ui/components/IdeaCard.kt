package com.nichetrendradar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nichetrendradar.data.models.ContentIdea

@Composable
fun IdeaCard(idea: ContentIdea, onSave: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(vertical = 8.dp), shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(18.dp)) {
            Text(idea.title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Text("Hook: " + idea.hook, modifier = Modifier.padding(top = 8.dp))
            idea.outline.forEach { Text("• " + it, modifier = Modifier.padding(top = 5.dp)) }
            Text("CTA: " + idea.cta, modifier = Modifier.padding(top = 8.dp))
            Button(onClick = onSave, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) { Text("Save to Library") }
        }
    }
}
