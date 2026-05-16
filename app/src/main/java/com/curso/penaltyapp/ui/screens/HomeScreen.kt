package com.curso.penaltyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.curso.penaltyapp.ui.components.FineCard
import com.curso.penaltyapp.ui.theme.PenaltyGreen
import com.curso.penaltyapp.ui.theme.PenaltyGreenLight
import com.curso.penaltyapp.ui.theme.PenaltyRed
import com.curso.penaltyapp.ui.theme.PenaltyYellow
import com.curso.penaltyapp.viewmodel.FinesViewModel
import com.curso.penaltyapp.R


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
    val isAdmin = currentUser?.role?.name == "ADMIN"
    val pendingFines = finesViewModel.pendingFines
    val recentFines = uiState.fines.take(3)
    val totalPot = finesViewModel.totalPot

    Scaffold(
        containerColor = Color(0xFF0F1210)
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
                        Surface(
                            color = Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier.padding(bottom = 24.dp)
                        ) {
                            Text(
                                text = "FC PENALTY",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = PenaltyGreenLight,
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = stringResource(R.string.bote),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.4f),
                            letterSpacing = 4.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "${String.format("%.2f", totalPot)} €",
                            fontSize = 62.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = (-2).sp
                        )

                        Spacer(Modifier.height(16.dp))

                        Surface(
                            color = PenaltyGreen.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                PenaltyGreen.copy(alpha = 0.2f)
                            )
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
                                    text = "${pendingFines.size} {${stringResource(R.string.multas_activas)}}",
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
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val actionModifier = Modifier
                        .weight(1f)
                        .height(100.dp)

                    if (isAdmin) {
                        QuickActionButton(
                            label = stringResource(R.string.multar),
                            icon = Icons.Rounded.Add,
                            color = PenaltyRed,
                            onClick = onNavigateToAddFine,
                            modifier = actionModifier
                        )
                    }
                    QuickActionButton(
                        label = stringResource(R.string.historial),
                        icon = Icons.Rounded.FormatListBulleted,
                        color = Color.White,
                        onClick = onNavigateToFines,
                        modifier = actionModifier
                    )
                    QuickActionButton(
                        label = stringResource(R.string.ranking),
                        icon = Icons.Rounded.EmojiEvents,
                        color = PenaltyYellow,
                        onClick = onNavigateToRanking,
                        modifier = actionModifier
                    )
                }
            }

            // ─── RESUM PERSONAL ───────────────────────────────────────────────
            item {
                Spacer(Modifier.height(48.dp))
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = stringResource(R.string.personal_summary),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.3f),
                        letterSpacing = 3.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatsCard(
                            title = stringResource(R.string.personal_pendiente),
                            value = "${String.format("%.2f", currentUser?.pendingFines ?: 0.0)}€",
                            icon = Icons.Rounded.Warning,
                            color = PenaltyRed,
                            modifier = Modifier.weight(1f)
                        )
                        StatsCard(
                            title = stringResource(R.string.personal_total),
                            value = "${String.format("%.2f", currentUser?.totalFines ?: 0.0)}€",
                            icon = Icons.Rounded.Receipt,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ─── MULTES RECENTS ───────────────────────────────────────────────
            item {
                Spacer(Modifier.height(48.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.multas_recientes),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.3f),
                        letterSpacing = 3.sp
                    )
                    TextButton(onClick = onNavigateToFines) {
                        Text(
                            stringResource(R.string.ver_multas),
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