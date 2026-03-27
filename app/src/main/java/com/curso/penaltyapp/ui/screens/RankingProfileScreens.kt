package com.curso.penaltyapp.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curso.penaltyapp.data.repository.FakeRepository
import com.curso.penaltyapp.ui.components.*
import com.curso.penaltyapp.ui.theme.*
import com.curso.penaltyapp.viewmodel.FinesViewModel

// ─── Ranking Screen ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(finesViewModel: FinesViewModel) {
    val ranking = finesViewModel.ranking

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("🏆 Rànquing", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Qui ha fallat més al vestidor?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PenaltyGray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            itemsIndexed(ranking) { _, entry ->
                val podiumColor = when (entry.position) {
                    1 -> Color(0xFFFFD700)  // Gold
                    2 -> Color(0xFFC0C0C0)  // Silver
                    3 -> Color(0xFFCD7F32)  // Bronze
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                val medal = when (entry.position) {
                    1 -> "🥇"; 2 -> "🥈"; 3 -> "🥉"; else -> "  ${entry.position}."
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (entry.position <= 3)
                            podiumColor.copy(alpha = 0.08f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(if (entry.position == 1) 4.dp else 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = medal,
                            fontSize = if (entry.position <= 3) 28.sp else 18.sp,
                            modifier = Modifier.width(48.dp)
                        )
                        UserAvatar(
                            initials = entry.user.photoInitials,
                            size = 44,
                            color = podiumColor.takeIf { entry.position <= 3 } ?: PenaltyGreen
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = entry.user.name,
                                fontWeight = if (entry.position <= 3) FontWeight.ExtraBold else FontWeight.Medium,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${entry.fineCount} multes · ${String.format("%.2f", entry.user.pendingFines)}€ pendents",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${String.format("%.2f", entry.totalAmount)}€",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (entry.position <= 3) podiumColor else PenaltyRed
                        )
                    }
                }
            }
        }
    }
}

// ─── Profile Screen ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    finesViewModel: FinesViewModel,
    onNavigateToSettings: () -> Unit
) {
    val currentUser = finesViewModel.currentUser
    val myFines = finesViewModel.myFines
    val pendingCount = myFines.count { it.status.name == "PENDING" }
    val paidCount = myFines.count { it.status.name == "PAID" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UserAvatar(initials = currentUser.photoInitials, size = 80)
                    Spacer(Modifier.height(16.dp))
                    Text(currentUser.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        color = if (currentUser.role.name == "ADMIN") PenaltyGreen.copy(0.15f) else PenaltyGray.copy(0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (currentUser.role.name == "ADMIN") "⚽ Capità" else "Jugador",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = if (currentUser.role.name == "ADMIN") PenaltyGreen else PenaltyGray,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(FakeRepository.team.name, color = PenaltyGray)
                }
            }
        }

        // Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard("MULTES PENDENTS", "${pendingCount}", icon = Icons.Default.Warning, color = PenaltyRed, modifier = Modifier.weight(1f))
                StatsCard("MULTES PAGADES", "${paidCount}", icon = Icons.Default.CheckCircle, color = PenaltyGreen, modifier = Modifier.weight(1f))
                StatsCard("DEUTE TOTAL", "${String.format("%.0f", currentUser.totalFines)}€", icon = Icons.Default.Receipt, color = PenaltyGray, modifier = Modifier.weight(1f))
            }
        }

        // Settings button
        item {
            ListItem(
                headlineContent = { Text("Configuració") },
                supportingContent = { Text("Tema, notificacions, NFC...") },
                leadingContent = { Icon(Icons.Default.Settings, null, tint = PenaltyGreen) },
                trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                modifier = Modifier.padding(horizontal = 16.dp)
                    .then(Modifier)
                    .also { /* clickable via Modifier */ },
            )
            // Workaround: button below
            OutlinedButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PenaltyGreen)
            ) {
                Icon(Icons.Default.Settings, null)
                Spacer(Modifier.width(8.dp))
                Text("Obrir Configuració")
            }
        }
    }
}

// ─── Team Members Screen ──────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamMembersScreen(onNavigateBack: () -> Unit) {
    val team = FakeRepository.team

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Membres de l'equip") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Tornar") } }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp)) {
            item {
                Text(
                    "${team.name} · ${team.members.size} membres",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PenaltyGray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            itemsIndexed(team.members) { _, member ->
                ListItem(
                    headlineContent = { Text(member.name, fontWeight = FontWeight.Medium) },
                    supportingContent = { Text(if (member.role.name == "ADMIN") "Capità" else "Jugador") },
                    leadingContent = { UserAvatar(initials = member.photoInitials, size = 44) },
                    trailingContent = {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${String.format("%.2f", member.pendingFines)}€", color = if (member.pendingFines > 0) PenaltyRed else PenaltyGreen, fontWeight = FontWeight.Bold)
                            Text("pendent", style = MaterialTheme.typography.labelSmall, color = PenaltyGray)
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}
