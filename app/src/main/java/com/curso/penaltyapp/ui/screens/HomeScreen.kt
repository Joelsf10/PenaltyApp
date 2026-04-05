package com.curso.penaltyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val currentUser by finesViewModel.currentUser.collectAsStateWithLifecycle()
    val isAdmin = currentUser.role.name == "ADMIN"
    val team = finesViewModel.team
    val pendingFines = finesViewModel.pendingFines
    val recentFines = uiState.fines.take(3)

    Scaffold(
        containerColor = Color(0xFF0F1210) // Negro profundo coherente con el Login
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ─── HEADER: POT TOTAL ────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    PenaltyGreen.copy(alpha = 0.12f),
                                    Color.Transparent
                                ),
                                center = Offset(x = 540f, y = 450f),
                                radius = 1000f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Badge del nom de l'equip
                        Surface(
                            color = Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier.padding(bottom = 24.dp)
                        ) {
                            Text(
                                text = team.name.uppercase(),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = PenaltyGreenLight,
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "EL POT TOTAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.4f),
                            letterSpacing = 4.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        // Import del pot
                        Text(
                            text = "${String.format("%.2f", team.totalPot)} €",
                            fontSize = 62.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = (-2).sp
                        )

                        Spacer(Modifier.height(16.dp))

                        // Indicador amb el comptador de multes pendents actives
                        Surface(
                            color = PenaltyGreen.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PenaltyGreen.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(PenaltyGreen, RoundedCornerShape(3.dp))
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = "${pendingFines.size} MULTES ACTIVES",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PenaltyGreen,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }

            // ─── ACCIONS RÀPIDES ──────────────────────────────────────────────
            // El botó "MULTAR" només apareix si l'usuari és ADMIN.
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val actionModifier = Modifier.weight(1f).height(100.dp)

                    if (isAdmin) {
                        QuickActionButton(
                            label = "MULTAR",
                            icon = Icons.Rounded.Add,
                            color = PenaltyRed,
                            onClick = onNavigateToAddFine,
                            modifier = actionModifier
                        )
                    }
                    QuickActionButton(
                        label = "HISTORIAL",
                        icon = Icons.Rounded.FormatListBulleted,
                        color = Color.White,
                        onClick = onNavigateToFines,
                        modifier = actionModifier
                    )
                    QuickActionButton(
                        label = "RÀNQUING",
                        icon = Icons.Rounded.EmojiEvents,
                        color = PenaltyYellow,
                        onClick = onNavigateToRanking,
                        modifier = actionModifier
                    )
                }
            }

            // ─── RESUM PERSONAL ───────────────────────────────────────────────
            // Dues targetes que mostren l'import pendent i el total acumulat
            // de l'usuari amb sessió activa
            item {
                Spacer(Modifier.height(48.dp))
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = "RESUM PERSONAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.3f),
                        letterSpacing = 3.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatsCard(
                            title = "PENDENT",
                            value = "${String.format("%.2f", currentUser.pendingFines)}€",
                            icon = Icons.Rounded.Warning,
                            color = PenaltyRed,
                            modifier = Modifier.weight(1f)
                        )
                        StatsCard(
                            title = "TOTAL",
                            value = "${String.format("%.2f", currentUser.totalFines)}€",
                            icon = Icons.Rounded.Receipt,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ─── MULTES RECENTS ───────────────────────────────────────────────
            // Capçalera amb el títol i l'enllaç "VEURE TOTES"
            item {
                Spacer(Modifier.height(48.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "MULTES RECENTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.3f),
                        letterSpacing = 3.sp
                    )
                    TextButton(onClick = onNavigateToFines) {
                        Text(
                            "VEURE TOTES",
                            color = PenaltyGreen,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            items(recentFines, key = { it.id }) { fine ->
                FineCard(
                    fine = fine,
                    onClick = { onNavigateToFineDetail(fine.id) },
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }
        }
    }
}

// ─── COMPONENTS PRIVATS DE LA HOME ───────────────────────────────────────────

@Composable
fun QuickActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.03f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(26.dp))
            Spacer(Modifier.height(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.03f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Icon(icon, null, tint = color.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.3f),
                letterSpacing = 1.sp
            )
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = color,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}