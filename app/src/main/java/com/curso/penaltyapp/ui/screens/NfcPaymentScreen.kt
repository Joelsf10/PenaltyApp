package com.curso.penaltyapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Nfc
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.curso.penaltyapp.ui.theme.*
import com.curso.penaltyapp.viewmodel.FinesViewModel

enum class NfcScreenState { SCANNING, SUCCESS, ERROR, NO_NFC }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NfcPaymentScreen(
    fineId: String,
    finesViewModel: FinesViewModel,
    onPaymentComplete: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val fine = finesViewModel.getFineById(fineId)
    var screenState by rememberSaveable { mutableStateOf(NfcScreenState.SCANNING) }

    // Animación de pulso para el Radar NFC
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val opacity by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "opacity"
    )

    // Simulación de lectura NFC
    LaunchedEffect(Unit) {
        delay(3000)
        screenState = NfcScreenState.SUCCESS
        finesViewModel.markFineAsPaid(fineId, viaNfc = true)
        delay(2000)
        onPaymentComplete()
    }

    Scaffold(
        containerColor = Color(0xFF0F1210),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                title = { Text("PAGAMENT NFC", style = MaterialTheme.typography.labelSmall, letterSpacing = 3.sp, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBackIosNew, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            fine?.let {
                // Tarjeta de información de la multa
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White.copy(alpha = 0.03f),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("IMPORT A PAGAR", style = MaterialTheme.typography.labelSmall, color = Color.Gray, letterSpacing = 2.sp)
                        Text(
                            text = it.formattedAmount(),
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Surface(
                            color = PenaltyGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                it.category.label.uppercase(),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = PenaltyGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(Modifier.height(60.dp))
            }

            AnimatedContent(
                targetState = screenState,
                transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(500)) },
                label = "nfc_state"
            ) { state ->
                when (state) {
                    NfcScreenState.SCANNING -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(contentAlignment = Alignment.Center) {
                                Box(
                                    modifier = Modifier
                                        .size(140.dp)
                                        .scale(scale)
                                        .background(PenaltyGreen.copy(alpha = opacity), CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .background(Color.White.copy(alpha = 0.05f), CircleShape)
                                        .border(2.dp, PenaltyGreen, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Rounded.Nfc,
                                        contentDescription = null,
                                        modifier = Modifier.size(45.dp),
                                        tint = PenaltyGreen
                                    )
                                }
                            }

                            Spacer(Modifier.height(48.dp))
                            Text(
                                text = "APROPA EL DISPOSITIU",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Escanejant sensor NFC...",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    NfcScreenState.SUCCESS -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(100.dp),
                                tint = PenaltyGreen
                            )
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = "PAGAMENT COMPLETAT",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "La multa s'ha marcat com a pagada.",
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    else -> {
                        val isNoNfc = state == NfcScreenState.NO_NFC
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (isNoNfc) Icons.Rounded.Warning else Icons.Rounded.ErrorOutline,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = if (isNoNfc) PenaltyYellow else PenaltyRed
                            )
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = if (isNoNfc) "NFC NO DISPONIBLE" else "ERROR DE LECTURA",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = if (isNoNfc)
                                    "Aquest dispositiu no suporta pagaments físics."
                                else
                                    "No s'ha pogut completar l'escaneig.",
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Spacer(Modifier.height(40.dp))

                            if (isNoNfc) {
                                Button(
                                    onClick = {
                                        finesViewModel.markFineAsPaid(fineId)
                                        onPaymentComplete()
                                    },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PenaltyGreen)
                                ) {
                                    Text("CONFIRMAR MANUALMENT", fontWeight = FontWeight.Black, color = Color.Black)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { screenState = NfcScreenState.SCANNING },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                                ) {
                                    Text("REINTENTAR ESCANEIG", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}