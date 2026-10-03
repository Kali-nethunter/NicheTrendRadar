package com.nichetrendradar.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.json.JSONObject
import com.nichetrendradar.data.models.UiState
import com.nichetrendradar.ui.theme.*
import com.nichetrendradar.viewmodel.MainViewModel

@Composable
private fun RadarMark() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceElevated)
            .border(1.dp, Border, RoundedCornerShape(22.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(52.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension * .38f
            drawCircle(
                color = Primary,
                radius = radius,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = Primary.copy(alpha = .45f),
                radius = radius * .62f,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
            )
            drawLine(
                color = PrimaryBright,
                start = center,
                end = Offset(center.x + radius * .72f, center.y - radius * .55f),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(color = TextPrimary, radius = 3.dp.toPx(), center = center)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: MainViewModel, onSuccess: () -> Unit) {
    var createAccount by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    val authState by viewModel.authState.collectAsState()
    val loading = authState is UiState.Loading
    val passwordsMatch = !createAccount || password == confirmPassword
    val passwordValid = !createAccount || password.length >= 8
    val canSubmit = email.isNotBlank() && password.isNotBlank() && passwordsMatch && passwordValid && !loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        RadarMark()

        Text(
            "Niche Trend Radar",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(top = 20.dp)
        )
        Text(
            if (createAccount) "Create your account and start discovering opportunities."
            else "Discover trends. Find opportunities. Create smarter content.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier
                .padding(top = 8.dp)
                .widthIn(max = 340.dp),
            textAlign = TextAlign.Center
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Surface)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    if (createAccount) "Create account" else "Welcome back",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Text(
                    if (createAccount) "Set up your radar in a few seconds."
                    else "Sign in to continue to your trend radar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; viewModel.clearAuthState() },
                    label = { Text("Email address") },
                    placeholder = { Text("you@example.com") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = !loading,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 18.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; viewModel.clearAuthState() },
                    label = { Text("Password") },
                    singleLine = true,
                    enabled = !loading,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(if (passwordVisible) "Hide" else "Show")
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )

                if (createAccount) {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; viewModel.clearAuthState() },
                        label = { Text("Confirm password") },
                        singleLine = true,
                        enabled = !loading,
                        visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { confirmVisible = !confirmVisible }) {
                                Text(if (confirmVisible) "Hide" else "Show")
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                    )

                    if (password.isNotEmpty() && password.length < 8) {
                        Text(
                            "Use at least 8 characters.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    } else if (confirmPassword.isNotEmpty() && !passwordsMatch) {
                        Text(
                            "Passwords do not match.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }

                if (authState is UiState.Error) {
                    Text(
                        run {
                            val raw = (authState as UiState.Error).message
                            try {
                                JSONObject(raw).optString("detail").ifBlank { raw }
                            } catch (_: Exception) {
                                raw
                            }
                        },
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Button(
                    onClick = { viewModel.authenticate(email.trim(), password, createAccount, onSuccess) },
                    enabled = canSubmit,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp).padding(top = 8.dp)
                ) {
                    Text(
                        when {
                            loading -> "Please wait…"
                            createAccount -> "Create account"
                            else -> "Sign in"
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }

                TextButton(
                    onClick = {
                        createAccount = !createAccount
                        confirmPassword = ""
                        viewModel.clearAuthState()
                    },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                ) {
                    Text(
                        if (createAccount) "Already have an account? Sign in"
                        else "New to Niche Trend Radar? Create account"
                    )
                }
            }
        }

        Text(
            "Your account keeps your niches and saved ideas private.",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(Modifier.height(28.dp))
    }
}