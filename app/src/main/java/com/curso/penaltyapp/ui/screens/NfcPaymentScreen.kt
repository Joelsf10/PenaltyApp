package com.curso.penaltyapp.ui.screens


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    var screenState by remember { mutableStateOf(NfcScreenState.SCANNING) }

    // Pulse animation for the NFC icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Auto-complete after 3s for skeleton simulation
    LaunchedEffect(Unit) {
        delay(3000)
        screenState = NfcScreenState.SUCCESS
        finesViewModel.markFineAsPaid(fineId, viaNfc = true)
        delay(2000)
        onPaymentComplete()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pagament NFC") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Tornar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            fine?.let {
                // Fine info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Multa a pagar", style = MaterialTheme.typography.labelSmall, color = PenaltyGray)
                        Text(
                            text = it.formattedAmount(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PenaltyRed
                        )
                        Text(it.category.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(48.dp))
            }

            // NFC animation circle
            AnimatedContent(
                targetState = screenState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "nfc_state"
            ) { state ->
                when (state) {
                    NfcScreenState.SCANNING -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .scale(scale)
                                    .background(PenaltyGreen.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .background(PenaltyGreen.copy(alpha = 0.25f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Nfc,
                                        contentDescription = null,
                                        modifier = Modifier.size(60.dp),
                                        tint = PenaltyGreen
                                    )
                                }
                            }
                            Spacer(Modifier.height(32.dp))
                            Text(
                                text = "Apropa el dispositiu\nal lector NFC",
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Simulant escaneig NFC...",
                                color = PenaltyGray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(24.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                color = PenaltyGreen
                            )
                        }
                    }

                    NfcScreenState.SUCCESS -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .background(PenaltyGreen.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    tint = PenaltyGreen
                                )
                            }
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = "Pagament validat! ✅",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = PenaltyGreen
                            )
                            Text(
                                text = "La multa ha estat registrada com a pagada via NFC.",
                                textAlign = TextAlign.Center,
                                color = PenaltyGray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    NfcScreenState.NO_NFC -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Warning, null, modifier = Modifier.size(80.dp), tint = PenaltyYellow)
                            Spacer(Modifier.height(16.dp))
                            Text("NFC no disponible", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Aquest dispositiu no suporta NFC.", color = PenaltyGray, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    finesViewModel.markFineAsPaid(fineId)
                                    onPaymentComplete()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PenaltyGreen)
                            ) {
                                Text("Confirmar pagament manual")
                            }
                        }
                    }

                    NfcScreenState.ERROR -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Error, null, modifier = Modifier.size(80.dp), tint = PenaltyRed)
                            Spacer(Modifier.height(16.dp))
                            Text("Error en el pagament", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Button(
                                onClick = { screenState = NfcScreenState.SCANNING },
                                colors = ButtonDefaults.buttonColors(containerColor = PenaltyGreen)
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
        }
    }
}
