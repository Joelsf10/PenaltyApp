package com.curso.penaltyapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.curso.penaltyapp.R
import com.curso.penaltyapp.ui.theme.*
import com.curso.penaltyapp.viewmodel.RegisterViewModel
import com.curso.penaltyapp.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel,
    registerViewModel: RegisterViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val authError by settingsViewModel.authError.collectAsStateWithLifecycle()
    val isLoading by settingsViewModel.isAuthLoading.collectAsStateWithLifecycle()
    val isLoggedIn by settingsViewModel.isLoggedIn.collectAsStateWithLifecycle(false)
    val uiState by registerViewModel.uiState.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) onRegisterSuccess()
    }

    val premiumFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        unfocusedContainerColor = Color.White.copy(0.03f),
        focusedContainerColor = Color.White.copy(0.06f),
        unfocusedBorderColor = Color.White.copy(0.1f),
        focusedBorderColor = PenaltyGreen,
        focusedLeadingIconColor = PenaltyGreen,
        unfocusedLeadingIconColor = PenaltyGreen.copy(0.5f)
    )

    Scaffold(
        containerColor = Color(0xFF0F1210),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        stringResource(R.string.register_title),
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 3.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.crearcompte),
                style = MaterialTheme.typography.labelSmall,
                color = PenaltyGreen,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = uiState.name,
                onValueChange = registerViewModel::onNameChanged,
                label = { Text(stringResource(R.string.name)) },
                leadingIcon = { Icon(Icons.Rounded.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = premiumFieldColors
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = registerViewModel::onEmailChanged,
                label = { Text(stringResource(R.string.correu)) },
                leadingIcon = { Icon(Icons.Rounded.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = premiumFieldColors
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = registerViewModel::onPasswordChanged,
                label = { Text("Contrasenya") },
                leadingIcon = { Icon(Icons.Rounded.Lock, null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Rounded.VisibilityOff
                            else Icons.Rounded.Visibility,
                            null,
                            tint = Color.Gray
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = premiumFieldColors
            )

            Spacer(Modifier.height(40.dp))

            AnimatedVisibility(visible = authError != null) {
                authError?.let {
                    Text(
                        text = stringResource(it),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            Button(
                onClick = {
                    when {
                        uiState.name.isBlank() || uiState.email.isBlank() || uiState.password.isBlank() ->
                            settingsViewModel.clearAuthError()
                        else -> settingsViewModel.register(uiState.email, uiState.password)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PenaltyGreen),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                } else {
                    Text(stringResource(R.string.continuar), fontWeight = FontWeight.Black, color = Color.Black)
                }
            }
        }
    }
}

// ─── CONFIGURACIÓ D'EQUIP ────────────────────────────────────────────────────────
@Composable
fun TeamSetupScreen(onTeamReady: () -> Unit) {
    var inviteCode by rememberSaveable { mutableStateOf("") }
    var teamName by rememberSaveable { mutableStateOf("") }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val premiumFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        unfocusedContainerColor = Color.White.copy(0.03f),
        focusedContainerColor = Color.White.copy(0.06f),
        unfocusedBorderColor = Color.White.copy(0.1f),
        focusedBorderColor = PenaltyGreen
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1210))
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("⚽", fontSize = 48.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(R.string.configura_equip),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            letterSpacing = 3.sp
        )

        Spacer(Modifier.height(32.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White.copy(0.05f),
            contentColor = PenaltyGreen,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PenaltyGreen
                )
            },
            divider = {}
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text(
                    stringResource(R.string.unir_equip),
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text(
                    stringResource(R.string.crear_equip),
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        if (selectedTab == 0) {
            OutlinedTextField(
                value = inviteCode,
                onValueChange = { inviteCode = it.uppercase() },
                label = { Text(stringResource(R.string.invitecode)) },
                placeholder = { Text(stringResource(R.string.codigo)) },
                leadingIcon = { Icon(Icons.Rounded.Key, null, tint = PenaltyGreen) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = premiumFieldColors
            )
        } else {
            OutlinedTextField(
                value = teamName,
                onValueChange = { teamName = it },
                label = { Text(stringResource(R.string.teamname)) },
                leadingIcon = { Icon(Icons.Rounded.Groups, null, tint = PenaltyGreen) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = premiumFieldColors
            )
        }

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = onTeamReady,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PenaltyGreen)
        ) {
            Text(
                if (selectedTab == 0) stringResource(R.string.unir_equip) else stringResource(R.string.crear_equip),
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
        }
    }
}