package com.curso.penaltyapp.data.repository


import com.curso.penaltyapp.data.model.Comment
import com.curso.penaltyapp.data.model.Fine
import com.curso.penaltyapp.data.model.FineCategory
import com.curso.penaltyapp.data.model.FineStatus
import com.curso.penaltyapp.data.model.RankingEntry
import com.curso.penaltyapp.data.model.Team
import com.curso.penaltyapp.data.model.User
import com.curso.penaltyapp.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

/**x
 * FakeRepository — simulates a backend (Firebase) with hardcoded data.
 * In the next delivery this will be replaced by real Firebase calls.
 */
object FakeRepository {

    // ─── Hardcoded users ──────────────────────────────────────────────────────

    private val users = listOf(
        User("u1", "Jon Stegherr", "JS", "team1", UserRole.ADMIN, 42.0, 12.0),
        User("u2", "Joel Sambola", "JO", "team1", UserRole.PLAYER, 28.0, 5.0),
        User("u3", "Marc Puig", "MP", "team1", UserRole.PLAYER, 55.0, 20.0),
        User("u4", "Pau Ferrer", "PF", "team1", UserRole.PLAYER, 15.0, 0.0),
        User("u5", "Laia Torres", "LT", "team1", UserRole.PLAYER, 33.0, 8.0),
        User("u6", "Àlex Vidal", "AV", "team1", UserRole.PLAYER, 60.0, 15.0),
        User("u7", "Bernat Mas", "BM", "team1", UserRole.PLAYER, 10.0, 0.0),
        User("u8", "Sara Núñez", "SN", "team1", UserRole.PLAYER, 22.0, 7.0)
    )

    private val _currentUser = MutableStateFlow(users.first { it.role == UserRole.ADMIN })
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // ─── Hardcoded team ───────────────────────────────────────────────────────

    val team = Team(
        id = "team1",
        name = "FC Penalty",
        sport = "Futbol",
        totalPot = 265.0,
        inviteCode = "PEN-2026",
        members = users
    )

    // ─── Hardcoded fines ──────────────────────────────────────────────────────

    private val _fines = MutableStateFlow(
        listOf(
            Fine(
                id = "f1",
                userId = "u3",
                userName = "Marc Puig",
                userInitials = "MP",
                category = FineCategory.LATE_TRAINING,
                amount = 2.0,
                reason = "20 minuts de retard a l'entrenament del dimarts",
                date = LocalDateTime.now().minusDays(1),
                status = FineStatus.PENDING,
                comments = listOf(
                    Comment(
                        "c1",
                        "u2",
                        "Joel Sambola",
                        "JO",
                        "Marc sempre igual 😂",
                        LocalDateTime.now().minusHours(20)
                    ),
                    Comment(
                        "c2",
                        "u4",
                        "Pau Ferrer",
                        "PF",
                        "Clàssic del Marc!!",
                        LocalDateTime.now().minusHours(18)
                    ),
                    Comment(
                        "c3",
                        "u3",
                        "Marc Puig",
                        "MP",
                        "Va, va... tenia embús 😅",
                        LocalDateTime.now().minusHours(17)
                    )
                ),
                reactions = mapOf("😂" to 5, "👎" to 2, "🔥" to 1)
            ),
            Fine(
                id = "f2",
                userId = "u6",
                userName = "Àlex Vidal",
                userInitials = "AV",
                category = FineCategory.RED_CARD,
                amount = 15.0,
                reason = "Targeta vermella per doble grog al partit del dissabte",
                date = LocalDateTime.now().minusDays(3),
                status = FineStatus.PENDING,
                comments = listOf(
                    Comment(
                        "c4",
                        "u1",
                        "Jon Stegherr",
                        "JS",
                        "Àlex quin animal... 15€ van!",
                        LocalDateTime.now().minusDays(2)
                    ),
                    Comment(
                        "c5",
                        "u6",
                        "Àlex Vidal",
                        "AV",
                        "L'àrbitre era un desastre",
                        LocalDateTime.now().minusDays(2)
                    )
                ),
                reactions = mapOf("😡" to 3, "😂" to 4, "👀" to 2)
            ),
            Fine(
                id = "f3",
                userId = "u2",
                userName = "Joel Sambola",
                userInitials = "JO",
                category = FineCategory.PHONE_IN_TRAINING,
                amount = 3.0,
                reason = "Mirant el mòbil durant els exercicis",
                date = LocalDateTime.now().minusDays(5),
                status = FineStatus.PAID,
                comments = listOf(
                    Comment(
                        "c6",
                        "u5",
                        "Laia Torres",
                        "LT",
                        "Joel always connected 📱😂",
                        LocalDateTime.now().minusDays(4)
                    )
                ),
                reactions = mapOf("😂" to 6, "📱" to 3)
            ),
            Fine(
                id = "f4",
                userId = "u5",
                userName = "Laia Torres",
                userInitials = "LT",
                category = FineCategory.MISSING_TRAINING,
                amount = 10.0,
                reason = "No va venir sense avisar a l'entrenament del dijous",
                date = LocalDateTime.now().minusDays(7),
                status = FineStatus.PAID,
                comments = emptyList(),
                reactions = mapOf("😤" to 2)
            ),
            Fine(
                id = "f5",
                userId = "u1",
                userName = "Jon Stegherr",
                userInitials = "JS",
                category = FineCategory.LATE_MATCH,
                amount = 5.0,
                reason = "Retard de 10 min al partit de copa",
                date = LocalDateTime.now().minusDays(10),
                status = FineStatus.PENDING,
                comments = listOf(
                    Comment(
                        "c7",
                        "u2",
                        "Joel Sambola",
                        "JO",
                        "El capità fent-la! 😂",
                        LocalDateTime.now().minusDays(9)
                    )
                ),
                reactions = mapOf("😂" to 8, "🤣" to 4)
            ),
            Fine(
                id = "f6",
                userId = "u8",
                userName = "Sara Núñez",
                userInitials = "SN",
                category = FineCategory.BAD_ATTITUDE,
                amount = 5.0,
                reason = "Discussió amb l'entrenador durant el descans",
                date = LocalDateTime.now().minusDays(4),
                status = FineStatus.DISPUTED,
                comments = listOf(
                    Comment(
                        "c8",
                        "u8",
                        "Sara Núñez",
                        "SN",
                        "Protesto, l'entrenador va exagerar!",
                        LocalDateTime.now().minusDays(3)
                    )
                ),
                reactions = mapOf("😬" to 3, "🤔" to 2)
            )
        )
    )

    val fines: StateFlow<List<Fine>> = _fines.asStateFlow()

    // ─── Ranking ──────────────────────────────────────────────────────────────

    val ranking: List<RankingEntry> get() {
        return users
            .sortedByDescending { it.totalFines }
            .mapIndexed { idx, user ->
                RankingEntry(
                    user = user,
                    position = idx + 1,
                    totalAmount = user.totalFines,
                    fineCount = _fines.value.count { it.userId == user.id }
                )
            }
    }

    // ─── Operations (simulated) ────────────────────────────────────────────────

    fun addFine(fine: Fine) {
        _fines.value = listOf(fine) + _fines.value
    }

    fun markAsPaid(fineId: String) {
        _fines.value = _fines.value.map {
            if (it.id == fineId) it.copy(status = FineStatus.PAID) else it
        }
    }

    fun addComment(fineId: String, comment: Comment) {
        _fines.value = _fines.value.map { fine ->
            if (fine.id == fineId) fine.copy(comments = fine.comments + comment)
            else fine
        }
    }

    fun addReaction(fineId: String, emoji: String) {
        _fines.value = _fines.value.map { fine ->
            if (fine.id == fineId) {
                val updatedReactions = fine.reactions.toMutableMap()
                updatedReactions[emoji] = (updatedReactions[emoji] ?: 0) + 1
                fine.copy(reactions = updatedReactions)
            } else fine
        }
    }

    fun getUserById(userId: String): User? = users.find { it.id == userId }

    fun getPendingFinesForUser(userId: String): List<Fine> =
        _fines.value.filter { it.userId == userId && it.status == FineStatus.PENDING }

    fun loginAsAdmin() {
        _currentUser.value = users.first { it.role == UserRole.ADMIN }
    }

    fun loginAsPlayer() {
        _currentUser.value = users.first { it.role == UserRole.PLAYER }
    }
}