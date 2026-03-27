package com.curso.penaltyapp.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Posar multa", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, "Tancar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // ─── Select member ────────────────────────────────────────────────
            item {
                Text(
                    "Membre sancionat",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = userExpanded,
                    onExpandedChange = { userExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedUser?.name ?: "Selecciona un membre",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = userExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PenaltyGreen),
                        leadingIcon = { Icon(Icons.Default.Person, null) }
                    )
                    ExposedDropdownMenu(
                        expanded = userExpanded,
                        onDismissRequest = { userExpanded = false }
                    ) {
                        team.members.filter { it.id != finesViewModel.currentUser.id }
                            .forEach { user ->
                                DropdownMenuItem(
                                    text = { Text(user.name) },
                                    onClick = {
                                        selectedUserId = user.id
                                        userExpanded = false
                                    }
                                )
                            }
                    }
                }
            }

            // ─── Select category ──────────────────────────────────────────────
            item {
                Text(
                    "Categoria de la multa",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = "${selectedCategory.label} (${selectedCategory.defaultAmount}€)",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PenaltyGreen),
                        leadingIcon = { Icon(Icons.Default.Category, null) }
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        FineCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text("${cat.label} — ${cat.defaultAmount}€") },
                                onClick = {
                                    selectedCategory = cat
                                    categoryExpanded = false
                                    if (cat != FineCategory.CUSTOM) customAmount = ""
                                }
                            )
                        }
                    }
                }
            }

            // ─── Custom amount ────────────────────────────────────────────────
            item {
                Text(
                    "Import (€)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = if (customAmount.isEmpty()) selectedCategory.defaultAmount.toString() else customAmount,
                    onValueChange = { customAmount = it },
                    label = { Text("Import en euros") },
                    leadingIcon = { Icon(Icons.Default.Euro, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PenaltyGreen)
                )
            }

            // ─── Reason ───────────────────────────────────────────────────────
            item {
                Text(
                    "Motiu",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Descriu la infracció...") },
                    leadingIcon = { Icon(Icons.Default.Description, null) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PenaltyGreen)
                )
            }

            // ─── Preview ──────────────────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PenaltyRed.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Resum de la multa",
                            style = MaterialTheme.typography.titleSmall,
                            color = PenaltyRed
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "👤 ${selectedUser?.name ?: "-"}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "📋 ${selectedCategory.label}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "💰 ${String.format("%.2f", amount)} €",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = PenaltyRed
                        )
                        if (reason.isNotBlank()) {
                            Text("📝 $reason", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // ─── Submit ───────────────────────────────────────────────────────
            item {
                Button(
                    onClick = {
                        if (selectedUserId.isNotEmpty() && reason.isNotBlank()) {
                            finesViewModel.addFine(
                                targetUserId = selectedUserId,
                                category = selectedCategory,
                                reason = reason,
                                customAmount = customAmount.toDoubleOrNull()
                            )
                            onFineAdded()
                        }
                    },
                    enabled = selectedUserId.isNotEmpty() && reason.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PenaltyRed)
                ) {
                    Icon(Icons.Default.Warning, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Posar multa", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
