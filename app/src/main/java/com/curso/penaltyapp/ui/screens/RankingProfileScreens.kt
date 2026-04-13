package com.curso.penaltyapp.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        containerColor = Color(0xFF0F1210),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                title = { Text("RÀNQUING", style = MaterialTheme.typography.labelSmall, letterSpacing = 3.sp, color = Color.White, fontWeight = FontWeight.Black) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "QUI HA FALLAT MÉS AL VESTIDOR?",
                    style = MaterialTheme.typography.labelSmall,
                    color = PenaltyGreen,
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )
            }

            itemsIndexed(ranking) { _, entry ->
                val podiumColor = when (entry.position) {
                    1 -> Color(0xFFFFD700); 2 -> Color(0xFFC0C0C0); 3 -> Color(0xFFCD7F32)
                    else -> Color.White.copy(alpha = 0.4f)
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White.copy(alpha = 0.02f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (entry.position <= 3) podiumColor.copy(0.3f) else Color.White.copy(0.05f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Número de posició amb zero inicial per als tres primers (ex: "01", "02")
                        Text(
                            text = if (entry.position <= 3) "0${entry.position}" else "${entry.position}",
                            fontWeight = FontWeight.Black,
                            color = podiumColor,
                            modifier = Modifier.width(40.dp)
                        )
                        UserAvatar(
                            initials = entry.user.photoInitials,
                            size = 46,
                            color = if (entry.position <= 3) podiumColor else PenaltyGreen
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(entry.user.name, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("${entry.fineCount} multes", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        // Import total en color de podi per als tres primers
                        Text(
                            text = "${String.format("%.2f", entry.totalAmount)}€",
                            fontWeight = FontWeight.Black,
                            color = if (entry.position <= 3) podiumColor else Color.White
                        )
                    }
                }
            }
        }
    }
}

// ─── PROFILE SCREEN ───────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    finesViewModel: FinesViewModel,
    onNavigateToSettings: () -> Unit
) {
    val currentUser by finesViewModel.currentUser.collectAsStateWithLifecycle()
    val myFines = finesViewModel.myFines
    // Comptem les multes per estat per mostrar les estadístiques
    val pendingCount = myFines.count { it.status.name == "PENDING" }
    val paidCount = myFines.count { it.status.name == "PAID" }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0F1210)),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // ─── CAPÇALERA DE PERFIL ──────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UserAvatar(initials = currentUser.photoInitials, size = 100, color = PenaltyGreen)
                    Spacer(Modifier.height(16.dp))
                    Text(currentUser.name.uppercase(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 2.sp)
                    Spacer(Modifier.height(8.dp))
                    // Badge de rol: "CAPITÀ" per a ADMIN, "JUGADOR" per a PLAYER
                    Surface(
                        color = PenaltyGreen.copy(0.1f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PenaltyGreen.copy(0.2f))
                    ) {
                        Text(
                            text = if (currentUser.role.name == "ADMIN") "CAPITÀ" else "JUGADOR",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = PenaltyGreen,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // ─── ESTADÍSTIQUES PERSONALS ──────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatsCard("PENDENTS", "$pendingCount", icon = Icons.Default.Warning, color = PenaltyRed, modifier = Modifier.weight(1f))
                StatsCard("PAGADES", "$paidCount", icon = Icons.Default.CheckCircle, color = PenaltyGreen, modifier = Modifier.weight(1f))
            }
        }

        // ─── ACCÉS A LA CONFIGURACIÓ ──────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Surface(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clickable { onNavigateToSettings() },
                color = Color.White.copy(0.03f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.05f))
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, null, tint = PenaltyGreen)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("CONFIGURACIÓ", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("Tema, notificacions, NFC...", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.ChevronRight, null, tint = Color.White.copy(0.3f))
                }
            }
        }
    }
}

// ─── TEAM MEMBERS SCREEN ──────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamMembersScreen(onNavigateBack: () -> Unit) {
    val team = FakeRepository.team

    Scaffold(
        containerColor = Color(0xFF0F1210),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                title = { Text("MEMBRES", style = MaterialTheme.typography.labelSmall, letterSpacing = 3.sp, color = Color.White) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    "${team.name.uppercase()} · ${team.members.size} MEMBRES",
                    style = MaterialTheme.typography.labelSmall,
                    color = PenaltyGreen,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            itemsIndexed(team.members) { _, member ->
                Surface(
                    color = Color.White.copy(0.02f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.05f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(initials = member.photoInitials, size = 44)
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(member.name, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(if (member.role.name == "ADMIN") "Capità" else "Jugador", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            // Import en vermell si hi ha deute, verd si està al dia
                            Text("${String.format("%.2f", member.pendingFines)}€", color = if (member.pendingFines > 0) PenaltyRed else PenaltyGreen, fontWeight = FontWeight.Black)
                            Text("deute", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}