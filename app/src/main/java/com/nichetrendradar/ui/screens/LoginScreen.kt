package com.nichetrendradar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: MainViewModel, onSuccess: () -> Unit) {
    var createAccount by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Niche Trend Radar", style = MaterialTheme.typography.headlineLarge)
        Text(
            if (createAccount) "Create your account" else "Sign in to continue",
            modifier = Modifier.padding(top = 8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; viewModel.clearAuthState() },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            enabled = authState !is UiState.Loading
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; viewModel.clearAuthState() },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            enabled = authState !is UiState.Loading
        )

        if (authState is UiState.Error) {
            Text(
                (authState as UiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Button(
            onClick = { viewModel.authenticate(email.trim(), password, createAccount, onSuccess) },
            enabled = email.isNotBlank() && password.isNotBlank() && authState !is UiState.Loading,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            Text(if (authState is UiState.Loading) "Please wait…" else if (createAccount) "Create Account" else "Login")
        }

        TextButton(
            onClick = { createAccount = !createAccount; viewModel.clearAuthState() },
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
        ) {
            Text(if (createAccount) "Already have an account? Login" else "New here? Create an account")
        }
    }
}