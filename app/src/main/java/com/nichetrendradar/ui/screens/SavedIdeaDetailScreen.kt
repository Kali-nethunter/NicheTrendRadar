package com.nichetrendradar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nichetrendradar.viewmodel.MainViewModel

@Composable
fun SavedIdeaDetailScreen(navController: NavController, viewModel: MainViewModel) {
    val idea by viewModel.selectedSavedIdea.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextButton(onClick = { navController.popBackStack() }) {
            Text("← Back to Saved Ideas")
        }

        if (idea == null) {
            Text(
                "Saved idea not found.",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 20.dp)
            )
        } else {
            val item = idea!!
            Text(
                item.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(18.dp))
            Text("HOOK", style = MaterialTheme.typography.labelLarge)
            Text(item.hook, modifier = Modifier.padding(top = 6.dp))

            Spacer(Modifier.height(18.dp))
            Text("OUTLINE", style = MaterialTheme.typography.labelLarge)
            item.outline.forEach { point ->
                Text("• $point", modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(Modifier.height(18.dp))
            Text("CTA", style = MaterialTheme.typography.labelLarge)
            Text(item.cta, modifier = Modifier.padding(top = 6.dp))
        }
    }
}