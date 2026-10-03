package com.nichetrendradar.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nichetrendradar.data.models.Trend
import com.nichetrendradar.ui.theme.*

@Composable
fun TrendCard(trend: Trend, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
        Column(Modifier.padding(18.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(shape = RoundedCornerShape(10.dp), color = Primary.copy(alpha = .18f)) {
                    Text(trend.growth_label.uppercase(), color = PrimaryBright, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                }
                Text("SCORE " + trend.score, color = TextPrimary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
            Text(trend.title, color = TextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 13.dp))
            Text(trend.source_summary, color = TextSecondary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 7.dp))
            Text("Generate content ideas  →", color = PrimaryBright, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 14.dp))
        }
    }
}
