package com.curso.penaltyapp.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.curso.penaltyapp.ui.components.*
import com.curso.penaltyapp.ui.theme.*
import com.curso.penaltyapp.viewmodel.FinesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    finesViewModel: FinesViewModel,
    onNavigateToFines: () -> Unit,
    onNavigateToFineDetail: (String) -> Unit,
    onNavigateToAddFine: () -> Unit,
    onNavigateToRanking: () -> Unit
) {
    val uiState by finesViewModel.uiState.collectAsStateWithLifecycle()
    val currentUser = finesViewModel.currentUser
    val team = finesViewModel.team
    val pendingFines = finesViewModel.pendingFines
    val recentFines = uiState.fines.take(3)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // ─── Header with pot ─────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(PenaltyGreenDark, PenaltyGreen.copy(alpha = 0.8f))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Hola, ${currentUser.name.split(" ").first()} 👋",
                                style = MaterialTheme.typography.headlineSmall,
                                color = androidx.compose.ui.graphics.Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = team.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                            )
                        }
                        UserAvatar(
                            initials = currentUser.photoInitials,
                            size = 48,
                            color = PenaltyGreenDark
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Pot card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.15f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "💰 El Pot",
                                style = MaterialTheme.typography.labelSmall,
                                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f),
                                letterSpacing = 1.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${String.format("%.2f", team.totalPot)} €",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = androidx.compose.ui.graphics.Color.White
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "${pendingFines.size} multes pendents · ${team.members.size} membres",
                                style = MaterialTheme.typography.bodySmall,
                                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        // ─── Quick actions ────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    label = "Posar\nmulta",
                    icon = Icons.Default.AddCircle,
                    color = PenaltyRed,
                    onClick = onNavigateToAddFine,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    label = "Totes les\nmultes",
                    icon = Icons.Default.List,
                    color = PenaltyGreen,
                    onClick = onNavigateToFines,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    label = "Rànquing",
                    icon = Icons.Default.EmojiEvents,
                    color = PenaltyYellow,
                    onClick = onNavigateToRanking,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ─── My stats ─────────────────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(title = "Les meves estadístiques")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatsCard(
                        title = "DEUTE PENDENT",
                        value = "${String.format("%.2f", currentUser.pendingFines)}€",
                        icon = Icons.Default.Warning,
                        color = PenaltyRed,
                        modifier = Modifier.weight(1f)
                    )
                    StatsCard(
                        title = "TOTAL MULTES",
                        value = "${String.format("%.2f", currentUser.totalFines)}€",
                        icon = Icons.Default.Receipt,
                        color = PenaltyGray,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ─── Recent fines ─────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(8.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = "Multes recents",
                    actionLabel = "Veure-les totes",
                    onAction = onNavigateToFines
                )
            }
        }

        items(recentFines, key = { it.id }) { fine ->
            FineCard(
                fine = fine,
                onClick = { onNavigateToFineDetail(fine.id) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        if (recentFines.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cap multa recent 🎉", color = PenaltyGray)
                }
            }
        }
    }
}

// ─── Quick Action Button ──────────────────────────────────────────────────────

@Composable
fun QuickActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
