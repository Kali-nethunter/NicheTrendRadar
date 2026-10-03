package com.nichetrendradar.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.nichetrendradar.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedIdeaDetailScreen(navController: NavController, viewModel: MainViewModel) {
    val idea by viewModel.selectedSavedIdea.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("Content Idea") }, navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text("Back") } }) }) { padding ->
        if (idea == null) Text("Saved idea not found.", modifier = androidx.compose.ui.Modifier.padding(padding))
        else Text(idea!!.title, modifier = androidx.compose.ui.Modifier.padding(padding))
    }
}