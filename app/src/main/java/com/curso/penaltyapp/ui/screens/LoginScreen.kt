package com.curso.penaltyapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.curso.penaltyapp.R
import com.curso.penaltyapp.data.repository.FakeRepository
import com.curso.penaltyapp.ui.theme.PenaltyGreen
import com.curso.penaltyapp.ui.theme.PenaltyGreenLight
import com.curso.penaltyapp.viewmodel.LoginViewModel
import com.curso.penaltyapp.viewmodel.SettingsViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    settingsViewModel: SettingsViewModel,
    loginViewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val authError by settingsViewModel.authError.collectAsStateWithLifecycle()
    val isLoading by settingsViewModel.isAuthLoading.collectAsStateWithLifecycle()
    val isLoggedIn by settingsViewModel.isLoggedIn.collectAsStateWithLifecycle(false)
    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) onLoginSuccess("")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E2923),
                        Color(0xFF0F1210)
                    ),
                    center = Offset(x = 500f, y = 400f),
                    radius = 1500f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ─── MARCA ─────────────────────────────────────────────────────
            // Icona de l'app amb un halo verd molt subtil al darrere
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = "Penalty logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "PENALTY",
                style = MaterialTheme.typography.headlineLarge.copy(
                    letterSpacing = 6.sp,
                    fontFamily = FontFamily.SansSerif
                ),
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp
            )

            // Badge de subtítol amb fons verd semi-transparent
            Surface(
                color = PenaltyGreen.copy(alpha = 0.1f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.subtitle),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = PenaltyGreenLight,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(Modifier.height(64.dp))

            // ─── INPUTS ───────────────────────────────────────────────────────
            Text(
                text = stringResource(R.string.identificate),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = loginViewModel::onPasswordChanged,
                placeholder = {
                    Text(
                        text = stringResource(R.string.correu),
                        color = Color.Gray
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    focusedBorderColor = PenaltyGreen,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = loginViewModel::onPasswordChanged,
                placeholder = {
                    Text(
                        text = stringResource(R.string.contrasenya),
                        color = Color.Gray
                    )
                },                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    focusedBorderColor = PenaltyGreen,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            AnimatedVisibility(visible = authError != null) {
                authError?.let {
                    Text(
                        text = stringResource(it),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ─── BOTONS D'ACCIÓ ───────────────────────────────────────────────

            // Botó principal: valida que els camps no estiguin buits abans de fer login
            Button(
                onClick = {
                    when {
                        uiState.email.isBlank() || uiState.password.isBlank() ->
                            settingsViewModel.clearAuthError()
                        else -> settingsViewModel.login(uiState.email, uiState.password)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PenaltyGreen),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        text = stringResource(R.string.login_title),
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }

            AnimatedVisibility(visible = authError != null) {
                authError?.let {
                    Text(
                        text = stringResource(it),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            TextButton(onClick = onNavigateToRegister) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.sensecompte), color = Color.Gray, fontSize = 13.sp)
                    Text(
                        text = stringResource(R.string.register_title),
                        color = PenaltyGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}