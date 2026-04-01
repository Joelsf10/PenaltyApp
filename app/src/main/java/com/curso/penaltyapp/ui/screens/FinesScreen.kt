package com.curso.penaltyapp.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.curso.penaltyapp.model.FineStatus
import com.curso.penaltyapp.model.UserRole
import com.curso.penaltyapp.ui.components.*
import com.curso.penaltyapp.ui.theme.*
import com.curso.penaltyapp.viewmodel.FinesViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinesScreen(
    finesViewModel: FinesViewModel,
    onNavigateToFineDetail: (String) -> Unit,
    onNavigateToAddFine: () -> Unit
) {
    val uiState by finesViewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by finesViewModel.currentUser.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFF0F1210),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                title = {
                    Text("REGISTRE GENERAL",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 4.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            )
        },
        floatingActionButton = {
            if (currentUser.role == UserRole.ADMIN) {
                FloatingActionButton(
                    onClick = onNavigateToAddFine,
                    containerColor = PenaltyGreen,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(30.dp))
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // ─── FILTROS ESTILO TAB ──────────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val statuses = listOf(null, FineStatus.PENDING, FineStatus.PAID, FineStatus.DISPUTED)
                val labels = listOf("TOTES", "PENDENTS", "PAGADES", "DISPUTA")

                items(statuses.size) { index ->
                    val isSelected = uiState.filterStatus == statuses[index]
                    Surface(
                        onClick = { finesViewModel.setFilter(statuses[index]) },
                        color = if (isSelected) PenaltyGreen else Color.White.copy(0.05f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                text = labels[index],
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.Black else Color.White.copy(0.6f)
                            )
                        }
                    }
                }
            }

            // ─── LISTADO DE MULTAS ───────────────────────────────────────────
            if (uiState.isLoading) {
                PenaltyLoadingIndicator()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (uiState.fines.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 100.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🛡️", fontSize = 40.sp)
                                    Text("HISTORIAL NET", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.3f), letterSpacing = 2.sp)
                                }
                            }
                        }
                    } else {
                        items(uiState.fines, key = { it.id }) { fine ->
                            FineCard(fine = fine, onClick = { onNavigateToFineDetail(fine.id) })
                        }
                    }
                }
            }
        }
    }
}