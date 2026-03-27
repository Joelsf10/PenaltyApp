package com.curso.penaltyapp.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.curso.penaltyapp.ui.theme.*
import com.curso.penaltyapp.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by settingsViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuració", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Tornar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {

            // ─── Appearance section ───────────────────────────────────────────
            item {
                SettingsSectionHeader("Aparença")
            }

            item {
                // Theme preference (DataStore)
                var showThemeDialog by remember { mutableStateOf(false) }

                ListItem(
                    headlineContent = { Text("Tema de l'app") },
                    supportingContent = {
                        Text(when (settings.theme) {
                            "dark" -> "Fosc"
                            "light" -> "Clar"
                            else -> "Seguir sistema"
                        })
                    },
                    leadingContent = { Icon(Icons.Default.DarkMode, null, tint = PenaltyGreen) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                    modifier = Modifier.clickableListItem { showThemeDialog = true }
                )

                if (showThemeDialog) {
                    AlertDialog(
                        onDismissRequest = { showThemeDialog = false },
                        title = { Text("Selecciona el tema") },
                        text = {
                            Column {
                                listOf(
                                    "system" to "Seguir sistema",
                                    "light" to "Clar",
                                    "dark" to "Fosc"
                                ).forEach { (value, label) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = settings.theme == value,
                                            onClick = {
                                                settingsViewModel.setTheme(value)
                                                showThemeDialog = false
                                            },
                                            colors = RadioButtonDefaults.colors(selectedColor = PenaltyGreen)
                                        )
                                        Text(label, modifier = Modifier.padding(start = 8.dp))
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showThemeDialog = false }) { Text("Tancar") }
                        }
                    )
                }
            }

            // ─── Network / Notifications section ─────────────────────────────
            item { SettingsSectionHeader("Xarxa i notificacions") }

            item {
                // Notifications toggle (DataStore)
                ListItem(
                    headlineContent = { Text("Notificacions de multes") },
                    supportingContent = { Text("Avisar quan et posin una multa nova") },
                    leadingContent = { Icon(Icons.Default.Notifications, null, tint = PenaltyGreen) },
                    trailingContent = {
                        Switch(
                            checked = settings.notificationsEnabled,
                            onCheckedChange = { settingsViewModel.setNotificationsEnabled(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = PenaltyGreen, checkedTrackColor = PenaltyGreenLight)
                        )
                    }
                )
            }

            item {
                // Show paid fines toggle
                ListItem(
                    headlineContent = { Text("Mostrar multes pagades") },
                    supportingContent = { Text("Inclou l'historial de multes ja pagades") },
                    leadingContent = { Icon(Icons.Default.CheckCircle, null, tint = PenaltyGreen) },
                    trailingContent = {
                        Switch(
                            checked = settings.showPaidFines,
                            onCheckedChange = { settingsViewModel.setShowPaidFines(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = PenaltyGreen, checkedTrackColor = PenaltyGreenLight)
                        )
                    }
                )
            }

            // ─── NFC section ──────────────────────────────────────────────────
            item { SettingsSectionHeader("Pagaments") }

            item {
                // NFC toggle (DataStore)
                ListItem(
                    headlineContent = { Text("Validació NFC") },
                    supportingContent = { Text("Usa NFC per confirmar pagaments físics") },
                    leadingContent = { Icon(Icons.Default.Nfc, null, tint = PenaltyGreen) },
                    trailingContent = {
                        Switch(
                            checked = settings.nfcEnabled,
                            onCheckedChange = { settingsViewModel.setNfcEnabled(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = PenaltyGreen, checkedTrackColor = PenaltyGreenLight)
                        )
                    }
                )
            }

            // ─── Language section ─────────────────────────────────────────────
            item { SettingsSectionHeader("Idioma") }

            item {
                var showLangDialog by remember { mutableStateOf(false) }

                ListItem(
                    headlineContent = { Text("Idioma de l'app") },
                    supportingContent = {
                        Text(when (settings.language) {
                            "ca" -> "Català"
                            "es" -> "Castellano"
                            "en" -> "English"
                            else -> "Català"
                        })
                    },
                    leadingContent = { Icon(Icons.Default.Language, null, tint = PenaltyGreen) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                    modifier = Modifier.clickableListItem { showLangDialog = true }
                )

                if (showLangDialog) {
                    AlertDialog(
                        onDismissRequest = { showLangDialog = false },
                        title = { Text("Selecciona l'idioma") },
                        text = {
                            Column {
                                listOf(
                                    "ca" to "Català",
                                    "es" to "Castellano 🇪🇸",
                                    "en" to "English 🇬🇧"
                                ).forEach { (code, label) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = settings.language == code,
                                            onClick = {
                                                settingsViewModel.setLanguage(code)
                                                showLangDialog = false
                                            },
                                            colors = RadioButtonDefaults.colors(selectedColor = PenaltyGreen)
                                        )
                                        Text(label, modifier = Modifier.padding(start = 8.dp))
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showLangDialog = false }) { Text("Tancar") }
                        }
                    )
                }
            }

            // ─── About ────────────────────────────────────────────────────────
            item { SettingsSectionHeader("Informació") }

            item {
                ListItem(
                    headlineContent = { Text("Versió de l'app") },
                    supportingContent = { Text("Penalty v1.0.0 · Fase Elaboració") },
                    leadingContent = { Icon(Icons.Default.Info, null, tint = PenaltyGray) }
                )
            }

            item {
                ListItem(
                    headlineContent = { Text("Desenvolupadors") },
                    supportingContent = { Text("Jon Stegherr · Joel Sambola — UdL 2025/2026") },
                    leadingContent = { Icon(Icons.Default.Code, null, tint = PenaltyGray) }
                )
            }
        }
    }
}

// ─── Helper composables ───────────────────────────────────────────────────────

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = PenaltyGreen,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 4.dp)
    )
    HorizontalDivider()
}

private fun Modifier.clickableListItem(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)

