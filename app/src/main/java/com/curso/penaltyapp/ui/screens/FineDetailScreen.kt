package com.curso.penaltyapp.ui.screens


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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.curso.penaltyapp.model.FineStatus
import com.curso.penaltyapp.model.UserRole
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
    var commentText by remember { mutableStateOf("") }

    val currentUser by finesViewModel.currentUser.collectAsStateWithLifecycle()
    val isAdmin = currentUser.role.name == "ADMIN"
    val canConfirmPayment = currentUser.role == UserRole.ADMIN
    // Reload fine on state changes
    val currentFine = finesViewModel.getFineById(fineId)

    if (currentFine == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Multa no trobada")
        }
        return
    }

    val availableEmojis = listOf("😂", "👎", "🔥", "😡", "🤣", "👏", "😬", "😤")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detall de la multa") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Tornar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─── Fine info card ───────────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            UserAvatar(initials = currentFine.userInitials, size = 56)
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = currentFine.userName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = currentFine.category.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(4.dp))
                                FineStatusBadge(currentFine.status)
                            }
                            Text(
                                text = currentFine.formattedAmount(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (currentFine.status == FineStatus.PAID) PenaltyGreen else PenaltyRed
                            )
                        }

                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Motiu",
                            style = MaterialTheme.typography.labelSmall,
                            color = PenaltyGray,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = currentFine.reason,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "📅 ${currentFine.formattedDate()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ─── Reactions ────────────────────────────────────────────────────
            item {
                Text("Reaccions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                // Existing reactions
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    currentFine.reactions.forEach { (emoji, count) ->
                        EmojiReactionChip(
                            emoji = emoji,
                            count = count,
                            onClick = { finesViewModel.addReaction(fineId, emoji) }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                // Add reactions
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    availableEmojis.filter { it !in currentFine.reactions.keys }.forEach { emoji ->
                        OutlinedButton(
                            onClick = { finesViewModel.addReaction(fineId, emoji) },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(emoji, fontSize = 18.sp)
                        }
                    }
                }
            }

            // ─── NFC Payment button (if PENDING) ──────────────────────────────
            if (currentFine.status == FineStatus.PENDING) {
                item {
                    Button(
                        onClick = { onNavigateToNfcPayment(fineId) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PenaltyGreen)
                    ) {
                        Icon(Icons.Default.Nfc, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Pagar amb NFC", fontWeight = FontWeight.Bold)
                    }
                }

                // Admin can also mark as paid manually
                if (isAdmin) {
                    item {
                        OutlinedButton(
                            onClick = { finesViewModel.markFineAsPaid(fineId) },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PenaltyGreen)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Marcar com a pagada (Admin)")
                        }
                    }
                }
            }

            // ─── Comments section ─────────────────────────────────────────────
            item {
                SectionHeader(title = "Comentaris (${currentFine.comments.size})")
            }

            if (currentFine.comments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sigues el primer a comentar! 💬", color = PenaltyGray)
                    }
                }
            } else {
                items(currentFine.comments, key = { it.id }) { comment ->
                    CommentItem(comment = comment)
                }
            }

            // ─── Add comment ─────────────────────────────────────────────────
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    UserAvatar(
                        initials = finesViewModel.currentUser.value.photoInitials,
                        size = 36
                    )
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Afegir comentari...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PenaltyGreen),
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                finesViewModel.addComment(fineId, commentText)
                                commentText = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar", tint = PenaltyGreen)
                    }
                }
            }
        }
    }

    // Snackbar messages
    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            finesViewModel.clearMessage()
        }
    }
}
