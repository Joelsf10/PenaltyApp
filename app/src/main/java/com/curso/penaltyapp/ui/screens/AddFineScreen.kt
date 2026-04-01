package com.curso.penaltyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curso.penaltyapp.data.model.FineCategory
import com.curso.penaltyapp.ui.theme.*
import com.curso.penaltyapp.viewmodel.FinesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFineScreen(
    finesViewModel: FinesViewModel,
    onFineAdded: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val team = finesViewModel.team
    var selectedUserId by remember { mutableStateOf(team.members.firstOrNull()?.id ?: "") }
    var selectedCategory by remember { mutableStateOf(FineCategory.LATE_TRAINING) }
    var reason by remember { mutableStateOf("") }
    var customAmount by remember { mutableStateOf("") }
    var userExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val selectedUser = team.members.find { it.id == selectedUserId }
    val amount = customAmount.toDoubleOrNull() ?: selectedCategory.defaultAmount

    // Definimos un estilo de colores común para todos los inputs para asegurar visibilidad
    val premiumFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedPlaceholderColor = Color.Gray,
        unfocusedPlaceholderColor = Color.Gray,
        unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
        focusedContainerColor = Color.White.copy(alpha = 0.06f),
        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
        focusedBorderColor = PenaltyGreen,
        focusedLeadingIconColor = PenaltyGreen,
        unfocusedLeadingIconColor = PenaltyGreen.copy(alpha = 0.5f),
        focusedTrailingIconColor = Color.White,
        unfocusedTrailingIconColor = Color.Gray
    )

    Scaffold(
        containerColor = Color(0xFF0F1210), // Fondo Negro Stealth
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(
                        "NÒMINA DE SANCIONS",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.Close, "Tancar", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            // ─── SELECCIÓN DE MIEMBRO ────────────────────────────────────────
            item {
                Column {
                    Text(
                        "MEMBRE SANCIONAT",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(0.4f),
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    ExposedDropdownMenuBox(
                        expanded = userExpanded,
                        onExpandedChange = { userExpanded = it }) {
                        OutlinedTextField(
                            value = selectedUser?.name ?: "Selecciona un membre",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = userExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(16.dp),
                            colors = premiumFieldColors,
                            leadingIcon = { Icon(Icons.Rounded.Person, null) }
                        )
                        ExposedDropdownMenu(
                            modifier = Modifier.background(Color(0xFF1A1D1B)),
                            expanded = userExpanded,
                            onDismissRequest = { userExpanded = false }
                        ) {
                            team.members.filter { it.id != finesViewModel.currentUser.value.id }
                                .forEach { user ->
                                    DropdownMenuItem(
                                        text = { Text(user.name, color = Color.White) },
                                        onClick = { selectedUserId = user.id; userExpanded = false }
                                    )
                                }
                        }
                    }
                }
            }

            // ─── CATEGORÍA Y MOTIVO ───────────────────────────────────────────
            item {
                Column {
                    Text(
                        "CATEGORIA I MOTIU",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(0.4f),
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it }) {
                        OutlinedTextField(
                            value = selectedCategory.label,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(16.dp),
                            colors = premiumFieldColors,
                            leadingIcon = { Icon(Icons.Rounded.Category, null) }
                        )
                        ExposedDropdownMenu(
                            modifier = Modifier.background(Color(0xFF1A1D1B)),
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            FineCategory.values().forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "${cat.label} (${cat.defaultAmount}€)",
                                            color = Color.White
                                        )
                                    },
                                    onClick = { selectedCategory = cat; categoryExpanded = false }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        placeholder = { Text("Breu descripció dels fets...", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = premiumFieldColors,
                        leadingIcon = { Icon(Icons.Rounded.Description, null) }
                    )
                }
            }

            // ─── RESUMEN (Glassmorphism) ──────────────────────────────────────
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = PenaltyRed.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        PenaltyRed.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "IMPORT DE LA SANCIÓ",
                                style = MaterialTheme.typography.labelSmall,
                                color = PenaltyRed.copy(0.6f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${String.format("%.2f", amount)} €",
                                fontSize = 32.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                        Icon(
                            Icons.Rounded.Gavel,
                            null,
                            tint = PenaltyRed,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            // ─── BOTÓN SUBMIT ─────────────────────────────────────────────────
            item {
                Button(
                    onClick = {
                        if (selectedUserId.isNotEmpty() && reason.isNotBlank()) {
                            finesViewModel.addFine(
                                selectedUserId,
                                selectedCategory,
                                reason,
                                customAmount.toDoubleOrNull()
                            )
                            onFineAdded()
                        }
                    },
                    enabled = selectedUserId.isNotEmpty() && reason.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PenaltyRed,
                        disabledContainerColor = PenaltyRed.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        "MULTAR A ${selectedUser?.name}",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )

                }
            }
        }
    }
}