package com.curso.penaltyapp.data.model

import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.ui.res.stringResource
import com.curso.penaltyapp.R

// ─── USER ─────────────────────────────────────────────────────────────────────

data class User(
    val id: String,
    val name: String,
    val photoInitials: String,
    val teamId: String,
    val role: UserRole = UserRole.PLAYER,
    val totalFines: Double = 0.0,
    val pendingFines: Double = 0.0
)

enum class UserRole {
    ADMIN,   // Capità / tresorer: pot crear multes i confirmar pagaments manualment
    PLAYER   // Jugador normal: pot veure multes, comentar, reaccionar i pagar les seves
}


// ─── TEAM ─────────────────────────────────────────────────────────────────────

data class Team(
    val id: String,
    val name: String,
    val sport: String,
    val totalPot: Double,
    val inviteCode: String,
    val members: List<User> = emptyList()
)

// ─── FINE ─────────────────────────────────────────────────────────────────────

data class Fine(
    val id: String,
    val userId: String,
    val userName: String,
    val userInitials: String,
    val category: FineCategory,
    val amount: Double,
    val reason: String,
    val date: LocalDateTime,
    val status: FineStatus,
    val comments: List<Comment> = emptyList(),
    val reactions: Map<String, Int> = emptyMap()
) {
    fun formattedDate(): String = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    fun formattedAmount(): String = String.format("%.2f €", amount)
}

enum class FineStatus {
    PENDING,   // Pendent de pagament: es mostra en vermell
    PAID,      // Pagada i validada: es mostra en verd, compta al pot
    DISPUTED   // En disputa: el jugador ha protestat la multa
}

enum class FineCategory(val label: Int, val defaultAmount: Double) {
    LATE_TRAINING(R.string.late_at_training, 2.0),
    LATE_MATCH(R.string.late_at_match, 5.0),
    MISSING_TRAINING(R.string.no_training, 10.0),
    MISSING_MATCH(R.string.no_match, 20.0),
    BAD_ATTITUDE(R.string.bad_attitude, 5.0),
    PHONE_IN_TRAINING(R.string.mobile_training, 3.0),
    YELLOW_CARD(R.string.yellow_card, 5.0),
    RED_CARD(R.string.red_card, 15.0),
    EQUIPMENT(R.string.dress_code, 2.0),
    CUSTOM(R.string.custom, 0.0)
}

// ─── COMMENT ──────────────────────────────────────────────────────────────────

data class Comment(
    val id: String,
    val userId: String,
    val userName: String,
    val userInitials: String,
    val text: String,
    val date: LocalDateTime
) {
    fun formattedDate(): String = date.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
}

// ─── NFC PAYMENT EVENT ────────────────────────────────────────────────────────

data class NfcPaymentEvent(
    val fineId: String,
    val amount: Double,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

// ─── RANKING ENTRY ────────────────────────────────────────────────────────────

data class RankingEntry(
    val user: User,
    val position: Int,
    val totalAmount: Double,
    val fineCount: Int
)

// ─── FIREBASE TIMESTAMP CONVERTERS ───────────────────────────────────────────

fun LocalDateTime.toTimestamp(): Timestamp =
    Timestamp(this.atZone(ZoneId.systemDefault()).toEpochSecond(), 0)

fun Timestamp.toLocalDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochSecond(seconds), ZoneId.systemDefault())