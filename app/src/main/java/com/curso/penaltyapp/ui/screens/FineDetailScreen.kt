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
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Nfc
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.curso.penaltyapp.data.model.FineStatus
import com.curso.penaltyapp.ui.components.CommentItem
import com.curso.penaltyapp.ui.components.FineStatusBadge
import com.curso.penaltyapp.ui.components.UserAvatar
import com.curso.penaltyapp.ui.theme.PenaltyGreen
import com.curso.penaltyapp.ui.theme.PenaltyRed
import com.curso.penaltyapp.viewmodel.FinesViewModel
import com.curso.penaltyapp.R

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
    val isAdmin = currentUser?.role?.name == "ADMIN"

    // Comentaris en temps real des de Firestore
    val comments by finesViewModel.getCommentsFlow(fineId)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    var commentText by rememberSaveable { mutableStateOf("") }

    val currentFine = finesViewModel.getFineById(fineId)

    if (currentFine == null) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF0F1210)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(R.string.error_multa), color = Color.White)
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
                        stringResource(R.string.multa_details),

                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 3.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew,
                            stringResource(R.string.back),
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
                                    stringResource(currentFine.category.label).uppercase(),
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
                            stringResource(R.string.multa_why),
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
                    stringResource(R.string.reactions),
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

            // ─── ACCIONS DE PAGAMENT ──────────────────────────────────────────
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
                                stringResource(R.string.NFC_pay),
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
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Icon(
                                    Icons.Rounded.CheckCircle,
                                    null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    stringResource(R.string.multa_pag_manual),
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
                    "{${stringResource(R.string.comentaris)} (${comments.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(0.4f),
                    letterSpacing = 2.sp
                )
            }

            if (comments.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.no_comentaris),
                        color = Color.White.copy(0.2f),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(comments, key = { it.id }) { comment ->
                    CommentItem(comment = comment)
                }
            }

            // ─── INPUT DE COMENTARI ───────────────────────────────────────────
            item {
                Surface(
                    color = Color.White.copy(0.03f),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.05f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        UserAvatar(initials = currentUser?.photoInitials ?: "?", size = 32)
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = {
                                Text(stringResource(R.string.escriu_comentaris), color = Color.Gray, fontSize = 14.sp)
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
                            Icon(Icons.Rounded.Send, stringResource(R.string.enviar), tint = PenaltyGreen)
                        }
                    }
                }
            }
        }
    }
}