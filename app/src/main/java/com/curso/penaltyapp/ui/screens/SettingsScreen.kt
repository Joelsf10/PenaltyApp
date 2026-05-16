package com.curso.penaltyapp.ui.screens

import android.annotation.SuppressLint
import com.curso.penaltyapp.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Nfc
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.curso.penaltyapp.ui.theme.PenaltyGreen
import com.curso.penaltyapp.ui.theme.PenaltyRed
import com.curso.penaltyapp.viewmodel.SettingsViewModel

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val settings by settingsViewModel.uiState.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val activity = LocalContext.current as android.app.Activity



    // ─── DIÀLEG DE CONFIRMACIÓ DE LOGOUT ─────────────────────────────────────
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = Color(0xFF1A1D1B),
            title = {
                Text(
                    stringResource(R.string.logout),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    stringResource(R.string.logout_confirm),
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        settingsViewModel.logout()
                        onLogout()
                    }
                ) {
                    Text(stringResource(R.string.exit), color = PenaltyRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.cancelar), color = Color.White.copy(alpha = 0.5f))
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFF0F1210),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(
                        stringResource(R.string.config),
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew,
                            stringResource(R.string.back),
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 40.dp, top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // ─── APARENÇA ───────────────────────────────────────────────────
            item { SettingsSectionHeader(stringResource(R.string.apariencia),) }
            item {
                var showThemeDialog by remember { mutableStateOf(false) }
                SettingsItem(
                    title = stringResource(R.string.theme),
                    subtitle = when (settings.theme) {
                        "dark" -> stringResource(R.string.dark)
                        "light" -> stringResource(R.string.light)
                        else -> stringResource(R.string.follow_system)
                    },
                    icon = Icons.Rounded.DarkMode,
                    onClick = { showThemeDialog = true }
                )

                if (showThemeDialog) {
                    SettingsSelectionDialog(
                        title = stringResource(R.string.select_theme),
                        currentValue = settings.theme,
                        options = listOf(
                            "system" to stringResource(R.string.follow_system),
                            "light" to stringResource(R.string.light),
                            "dark" to stringResource(R.string.dark)
                        ),
                        onSelect = { settingsViewModel.setTheme(it) },
                        onDismiss = { showThemeDialog = false }
                    )
                }
            }

            // ─── NOTIFICACIONS ──────────────────────────────────────────────
            item { SettingsSectionHeader(stringResource(R.string.xx_i_not)) }
            item {
                SettingsSwitchItem(
                    title = stringResource(R.string.notificacions),
                    subtitle = stringResource(R.string.noti_sub),
                    icon = Icons.Rounded.Notifications,
                    checked = settings.notificationsEnabled,
                    onCheckedChange = { settingsViewModel.setNotificationsEnabled(it) }
                )
            }
            item {
                SettingsSwitchItem(
                    title = stringResource(R.string.mostrar_multes),
                    subtitle = stringResource(R.string.inclou_hist),
                    icon = Icons.Rounded.CheckCircle,
                    checked = settings.showPaidFines,
                    onCheckedChange = { settingsViewModel.setShowPaidFines(it) }
                )
            }

            // ─── PAGAMENTS ────────────────────────────────────────────────────────
            item { SettingsSectionHeader(stringResource(R.string.pagos)) }
            item {
                SettingsSwitchItem(
                    title = stringResource(R.string.NFC_validation),
                    subtitle = stringResource(R.string.NFC_check),
                    icon = Icons.Rounded.Nfc,
                    checked = settings.nfcEnabled,
                    onCheckedChange = { settingsViewModel.setNfcEnabled(it) }
                )
            }

            // ─── IDIOMA ───────────────────────────────────────────────────────
            item { SettingsSectionHeader(stringResource(R.string.idioma)) }
            item {
                var showLangDialog by remember { mutableStateOf(false) }
                SettingsItem(
                    title = stringResource(R.string.app_idioma),
                    subtitle = when (settings.language) {
                        "ca" -> "Català"
                        "es" -> "Castellano"
                        "en" -> "English"
                        else -> "Català"
                    },
                    icon = Icons.Rounded.Language,
                    onClick = { showLangDialog = true }
                )

                if (showLangDialog) {
                    SettingsSelectionDialog(
                        title = stringResource(R.string.idioma_select),
                        currentValue = settings.language,
                        options = listOf("ca" to "Català", "es" to "Castellano", "en" to "English"),
                        onSelect = { settingsViewModel.setLanguage(it, activity) },
                        onDismiss = { showLangDialog = false }
                    )
                }
            }

            // ─── SESSIÓ ───────────────────────────────────────────────────────
            item { SettingsSectionHeader(stringResource(R.string.session)) }
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLogoutDialog = true },
                    color = Color.White.copy(alpha = 0.03f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, PenaltyRed.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Logout,
                            contentDescription = null,
                            tint = PenaltyRed,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                stringResource(R.string.logout),
                                color = PenaltyRed,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                stringResource(R.string.logout_check),
                                color = PenaltyRed.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Icon(
                            Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            tint = PenaltyRed.copy(alpha = 0.4f)
                        )
                    }
                }
            }

            // ─── INFO ─────────────────────────────────────────────────────────
            item { SettingsSectionHeader(stringResource(R.string.info)) }
            item {
                SettingsItem(
                    title = stringResource(R.string.version),
                    subtitle = stringResource(R.string.version_subtitle),
                    icon = Icons.Rounded.Info,
                    showChevron = false
                )
            }
            item {
                SettingsItem(
                    title = stringResource(R.string.desenvolupadors),
                    subtitle = "Jon Stegherr · Joel Sambola",
                    icon = Icons.Rounded.Code,
                    showChevron = false
                )
            }
        }
    }
}

// ─── COMPONENTS PRIVATS DEL SETTINGS ─────────────────────────────────────────

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = Color.White.copy(alpha = 0.3f),
        letterSpacing = 2.sp,
        modifier = Modifier.padding(start = 8.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    showChevron: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        color = Color.White.copy(alpha = 0.03f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = PenaltyGreen, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    subtitle,
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (showChevron) {
                Icon(Icons.Rounded.ChevronRight, null, tint = Color.White.copy(alpha = 0.3f))
            }
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.03f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = PenaltyGreen, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    subtitle,
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PenaltyGreen,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.White.copy(alpha = 0.1f),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun SettingsSelectionDialog(
    title: String,
    currentValue: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1D1B),
        title = { Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(value); onDismiss() }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentValue == value,
                            onClick = { onSelect(value); onDismiss() },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = PenaltyGreen,
                                unselectedColor = Color.White.copy(alpha = 0.4f)
                            )
                        )
                        Text(label, color = Color.White, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {}
    )
}