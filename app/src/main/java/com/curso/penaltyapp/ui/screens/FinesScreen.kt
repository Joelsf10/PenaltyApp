package com.curso.penaltyapp.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.curso.penaltyapp.model.FineStatus
import com.curso.penaltyapp.ui.components.*
import com.curso.penaltyapp.ui.theme.*
import com.curso.penaltyapp.viewmodel.FinesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinesScreen(
    finesViewModel: FinesViewModel,
    onNavigateToFineDetail: (String) -> Unit,
    onNavigateToAddFine: () -> Unit
) {
    val uiState by finesViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Multes", fontWeight = FontWeight.Bold) },
                actions = {
                    if (finesViewModel.currentUser.role.name == "ADMIN") {
                        IconButton(onClick = onNavigateToAddFine) {
                            Icon(Icons.Default.Add, contentDescription = "Afegir multa", tint = PenaltyGreen)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (finesViewModel.currentUser.role.name == "ADMIN") {
                FloatingActionButton(
                    onClick = onNavigateToAddFine,
                    containerColor = PenaltyGreen
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Posar multa", tint = androidx.compose.ui.graphics.Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.filterStatus == null,
                        onClick = { finesViewModel.setFilter(null) },
                        label = { Text("Totes (${finesViewModel.uiState.value.fines.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PenaltyGreen,
                            selectedLabelColor = androidx.compose.ui.graphics.Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.filterStatus == FineStatus.PENDING,
                        onClick = { finesViewModel.setFilter(FineStatus.PENDING) },
                        label = { Text("Pendents") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PenaltyRed,
                            selectedLabelColor = androidx.compose.ui.graphics.Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.filterStatus == FineStatus.PAID,
                        onClick = { finesViewModel.setFilter(FineStatus.PAID) },
                        label = { Text("Pagades") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PenaltyGreen,
                            selectedLabelColor = androidx.compose.ui.graphics.Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.filterStatus == FineStatus.DISPUTED,
                        onClick = { finesViewModel.setFilter(FineStatus.DISPUTED) },
                        label = { Text("En disputa") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PenaltyYellow,
                            selectedLabelColor = androidx.compose.ui.graphics.Color.White
                        )
                    )
                }
            }

            if (uiState.isLoading) {
                PenaltyLoadingIndicator()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (uiState.fines.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🎉", style = MaterialTheme.typography.headlineLarge)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Cap multa!", fontWeight = FontWeight.Bold)
                                    Text("El vestidor es porta bé 🙌", color = PenaltyGray)
                                }
                            }
                        }
                    } else {
                        items(uiState.fines, key = { it.id }) { fine ->
                            FineCard(
                                fine = fine,
                                onClick = { onNavigateToFineDetail(fine.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
