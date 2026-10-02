package com.nichetrendradar.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nichetrendradar.data.models.Trend

@Composable
fun TrendCard(trend: Trend, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable(onClick = onClick), shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(trend.growth_label, color = MaterialTheme.colorScheme.primary)
                Text("Score: " + trend.score, fontWeight = FontWeight.Bold)
            }
            Text(trend.title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp))
            Text(trend.source_summary, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp))
        }
    }
}
