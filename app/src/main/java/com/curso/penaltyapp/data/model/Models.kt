package com.curso.penaltyapp.data.model


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

enum class FineCategory(val label: String, val defaultAmount: Double) {
    LATE_TRAINING("Retard entrenament", 2.0),
    LATE_MATCH("Retard partit", 5.0),
    MISSING_TRAINING("Falta entrenament", 10.0),
    MISSING_MATCH("Falta partit", 20.0),
    BAD_ATTITUDE("Mala actitud", 5.0),
    PHONE_IN_TRAINING("Mòbil a l'entrenament", 3.0),
    YELLOW_CARD("Targeta groga", 5.0),
    RED_CARD("Targeta vermella", 15.0),
    EQUIPMENT("Equipació incorrecta", 2.0),
    CUSTOM("Personalitzada", 0.0)
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
