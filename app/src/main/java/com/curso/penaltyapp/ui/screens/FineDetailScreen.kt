package com.curso.penaltyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Nfc
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.curso.penaltyapp.data.model.FineStatus
import com.curso.penaltyapp.ui.components.*
import com.curso.penaltyapp.ui.theme.*
import com.curso.penaltyapp.viewmodel.FinesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FineDetailScreen(
    fineId: String,
    finesViewModel: FinesViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNfcPayment: (String) -> Unit
) {
    val uiState by finesViewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by finesViewModel.currentUser.collectAsStateWithLifecycle()
    val isAdmin = currentUser.role.name == "ADMIN"

    var commentText by rememberSaveable { mutableStateOf("") }

    val currentFine = finesViewModel.getFineById(fineId)

    if (currentFine == null) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF0F1210)),
            contentAlignment = Alignment.Center
        ) {
            Text("Multa no trobada", color = Color.White)
        }
        return
    }

    val availableEmojis = listOf("😂", "👎", "🔥", "😡", "🤣", "👏", "😬", "😤")

    Scaffold(
        containerColor = Color(0xFF0F1210),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        "DETALL DE LA SANCIÓ",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 3.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew,
                            "Tornar",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ─── TARGETA PRINCIPAL ────────────────────────────────────────────
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White.copy(0.02f),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color.White.copy(0.05f)
                    )
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            UserAvatar(
                                initials = currentFine.userInitials,
                                size = 60,
                                color = PenaltyGreen
                            )
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    currentFine.userName,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 20.sp
                                )
                                Text(
                                    currentFine.category.label.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PenaltyGreen,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = currentFine.formattedAmount(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = if (currentFine.status == FineStatus.PAID)
                                    PenaltyGreen else PenaltyRed
                            )
                        }

                        Spacer(Modifier.height(20.dp))
                        HorizontalDivider(color = Color.White.copy(0.05f))
                        Spacer(Modifier.height(20.dp))

                        Text(
                            "MOTIU DE LA SANCIÓ",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(0.4f),
                            letterSpacing = 2.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            currentFine.reason,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )

                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Rounded.Event,
                                null,
                                tint = Color.White.copy(0.3f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                currentFine.formattedDate(),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(0.3f)
                            )
                            Spacer(Modifier.weight(1f))
                            FineStatusBadge(currentFine.status)
                        }
                    }
                }
            }

            // ─── REACCIONS ────────────────────────────────────────────────────
            item {
                Text(
                    "REACCIONS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(0.4f),
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(12.dp))

                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    currentFine.reactions.forEach { (emoji, count) ->
                        item {
                            Surface(
                                onClick = { finesViewModel.addReaction(fineId, emoji) },
                                color = PenaltyGreen.copy(0.05f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    PenaltyGreen.copy(0.2f)
                                )
                            ) {
                                Row(
                                    Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(emoji, fontSize = 16.sp)
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        "$count",
                                        color = PenaltyGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    items(availableEmojis.filter { it !in currentFine.reactions.keys }) { emoji ->
                        Surface(
                            onClick = { finesViewModel.addReaction(fineId, emoji) },
                            color = Color.White.copy(0.03f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color.White.copy(0.05f)
                            )
                        ) {
                            Text(
                                emoji,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // ─── ACCIONS ──────────────────────────────────────────────────────
            if (currentFine.status == FineStatus.PENDING) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { onNavigateToNfcPayment(fineId) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PenaltyGreen)
                        ) {
                            Icon(Icons.Rounded.Nfc, null, tint = Color.Black)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "PAGAR AMB NFC",
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }

                        if (isAdmin) {
                            OutlinedButton(
                                onClick = { finesViewModel.markFineAsPaid(fineId) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color.White.copy(0.1f)
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    Icons.Rounded.CheckCircle,
                                    null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "MARCAR PAGADA (ADMIN)",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

            // ─── COMENTARIS ───────────────────────────────────────────────────
            item {
                Text(
                    "COMENTARIS (${currentFine.comments.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(0.4f),
                    letterSpacing = 2.sp
                )
            }

            if (currentFine.comments.isEmpty()) {
                item {
                    Text(
                        "Sense comentaris encara. Sigues el primer! 💬",
                        color = Color.White.copy(0.2f),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(currentFine.comments, key = { it.id }) { comment ->
                    CommentItem(comment = comment)
                }
            }

            // ─── INPUT DE COMENTARI ───────────────────────────────────────────
            item {
                Surface(
                    color = Color.White.copy(0.03f),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color.White.copy(0.05f)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        UserAvatar(initials = currentUser.photoInitials, size = 32)
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = {
                                Text(
                                    "Escriu un comentari...",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank()) {
                                    finesViewModel.addComment(fineId, commentText)
                                    commentText = ""
                                }
                            }
                        ) {
                            Icon(Icons.Rounded.Send, "Enviar", tint = PenaltyGreen)
                        }
                    }
                }
            }
        }
    }
}