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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.curso.penaltyapp.data.model.FineCategory
import com.curso.penaltyapp.ui.theme.*
import com.curso.penaltyapp.viewmodel.AddFineViewModel
import androidx.compose.ui.res.stringResource
import com.curso.penaltyapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFineScreen(
    finesViewModel: FinesViewModel,
    onFineAdded: () -> Unit,
    onNavigateBack: () -> Unit,
    addFineViewModel: AddFineViewModel = viewModel()
) {
    val users by finesViewModel.users.collectAsStateWithLifecycle()
    val currentUser by finesViewModel.currentUser.collectAsStateWithLifecycle()
    val uiState by addFineViewModel.uiState.collectAsStateWithLifecycle()
    var userExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val selectedCategory = FineCategory.valueOf(uiState.selectedCategoryName)
    val selectedUser = users.find { it.id == uiState.selectedUserId }
    val amount = uiState.customAmount.toDoubleOrNull() ?: selectedCategory.defaultAmount

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
        containerColor = Color(0xFF0F1210),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(
                        text = stringResource(R.string.nomina_sancions).uppercase(),
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
            // ─── SELECCIÓ DE MEMBRE ───────────────────────────────────────────
            item {
                Column {
                    Text(
                        text = stringResource(R.string.membre_sancionat),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(0.4f),
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    ExposedDropdownMenuBox(
                        expanded = userExpanded,
                        onExpandedChange = { userExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedUser?.name ?: stringResource(R.string.membre_seleccion),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = userExpanded)
                            },
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
                            users
                                .filter { it.id != currentUser?.id }
                                .forEach { user ->
                                    DropdownMenuItem(
                                        text = { Text(user.name, color = Color.White) },
                                        onClick = {
                                            addFineViewModel.onUserSelected(user.id)
                                            userExpanded = false
                                        }
                                    )
                                }
                        }
                    }
                }
            }

            // ─── CATEGORIA I MOTIU ────────────────────────────────────────────
            item {
                Column {
                    Text(
                        stringResource(R.string.categoria_i_motiu).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(0.4f),
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = stringResource(selectedCategory.label),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                            },
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
                                    onClick = {
                                        addFineViewModel.onCategorySelected(cat)
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.reason,
                        onValueChange = addFineViewModel::onReasonChanged,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.fine_description),
                                color = Color.Gray
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = premiumFieldColors,
                        leadingIcon = { Icon(Icons.Rounded.Description, null) }
                    )
                }
            }

            // ─── RESUM DE LA SANCIÓ ───────────────────────────────────────────
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = PenaltyRed.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PenaltyRed.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                stringResource(R.string.importe_sancion),
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
                        Icon(Icons.Rounded.Gavel, null, tint = PenaltyRed, modifier = Modifier.size(40.dp))
                    }
                }
            }

            // ─── BOTÓ DE CONFIRMACIÓ ──────────────────────────────────────────
            item {
                Button(
                    onClick = {
                        if (uiState.selectedUserId.isNotEmpty() && uiState.reason.isNotBlank()) {
                            finesViewModel.addFine(
                                uiState.selectedUserId,
                                selectedCategory,
                                uiState.reason,
                                uiState.customAmount.toDoubleOrNull()
                            )
                            onFineAdded()
                        }
                    },
                    enabled = uiState.selectedUserId.isNotEmpty() && uiState.reason.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PenaltyRed,
                        disabledContainerColor = PenaltyRed.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.multar) + " " + (selectedUser?.name ?: ""),
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}