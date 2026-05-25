package com.curso.penaltyapp.data.repository

import com.curso.penaltyapp.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

object FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    // ─── FINES ───────────────────────────────────────────────────────────────

    fun getFinesFlow(teamId: String): Flow<List<Fine>> = callbackFlow {
        val listener: ListenerRegistration = db.collection("fines").whereEqualTo("teamId", teamId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val fines = snapshot.documents.mapNotNull { doc ->
                    try {
                        Fine(
                            id = doc.id,
                            userId = doc.getString("userId") ?: "",
                            userName = doc.getString("userName") ?: "",
                            userInitials = doc.getString("userInitials") ?: "",
                            teamId = doc.getString("teamId") ?: "",
                            category = FineCategory.valueOf(doc.getString("category") ?: "CUSTOM"),
                            amount = doc.getDouble("amount") ?: 0.0,
                            reason = doc.getString("reason") ?: "",
                            date = doc.getTimestamp("date")?.toLocalDateTime() ?: LocalDateTime.now(),
                            status = FineStatus.valueOf(doc.getString("status") ?: "PENDING"),
                            reactions = (doc.get("reactions") as? Map<String, Long>)
                                ?.mapValues { it.value.toInt() } ?: emptyMap()
                        )
                    } catch (e: Exception) { null }
                }.sortedByDescending { it.date }
                trySend(fines)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addFine(fine: Fine) {
        val data = hashMapOf(
            "userId" to fine.userId,
            "userName" to fine.userName,
            "userInitials" to fine.userInitials,
            "teamId" to fine.teamId,
            "category" to fine.category.name,
            "amount" to fine.amount,
            "reason" to fine.reason,
            "date" to fine.date.toTimestamp(),
            "status" to fine.status.name,
            "reactions" to fine.reactions
        )
        db.collection("fines").add(data).await()
    }

    suspend fun markAsPaid(fineId: String) {
        // Obtenir les dades de la multa abans de marcar-la com a pagada
        val fineDoc = db.collection("fines").document(fineId).get().await()
        val userId = fineDoc.getString("userId") ?: return
        val amount = fineDoc.getDouble("amount") ?: 0.0

        // Marcar la multa com a pagada
        db.collection("fines").document(fineId)
            .update("status", FineStatus.PAID.name).await()

        // Actualitzar pendingFines de l'usuari (utilitzem set amb merge per evitar errors si el doc no existeix)
        db.collection("users").document(userId)
            .set(
                mapOf("pendingFines" to FieldValue.increment(-amount)),
                SetOptions.merge()
            ).await()
    }

    suspend fun addReaction(fineId: String, emoji: String) {
        try {
            val doc = db.collection("fines").document(fineId).get().await()
            val reactions = (doc.get("reactions") as? Map<String, Long>)
                ?.mapValues { it.value.toInt() }?.toMutableMap() ?: mutableMapOf()
            reactions[emoji] = (reactions[emoji] ?: 0) + 1
            db.collection("fines").document(fineId)
                .update("reactions", reactions).await()
        } catch (e: Exception) {
            // Ignorar si no hay conexión
        }
    }

    // ─── COMMENTS ────────────────────────────────────────────────────────────

    fun getCommentsFlow(fineId: String): Flow<List<Comment>> = callbackFlow {
        val listener = db.collection("fines").document(fineId)
            .collection("comments")
            .orderBy("date")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val comments = snapshot.documents.mapNotNull { doc ->
                    try {
                        Comment(
                            id = doc.id,
                            userId = doc.getString("userId") ?: "",
                            userName = doc.getString("userName") ?: "",
                            userInitials = doc.getString("userInitials") ?: "",
                            text = doc.getString("text") ?: "",
                            date = doc.getTimestamp("date")?.toLocalDateTime() ?: LocalDateTime.now()
                        )
                    } catch (e: Exception) { null }
                }
                trySend(comments)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addComment(fineId: String, comment: Comment) {
        val data = hashMapOf(
            "userId" to comment.userId,
            "userName" to comment.userName,
            "userInitials" to comment.userInitials,
            "text" to comment.text,
            "date" to comment.date.toTimestamp()
        )
        db.collection("fines").document(fineId)
            .collection("comments").add(data).await()
    }

    // ─── USERS ───────────────────────────────────────────────────────────────

    suspend fun saveUser(user: User) {
        val data = hashMapOf(
            "name" to user.name,
            "photoInitials" to user.photoInitials,
            "teamId" to user.teamId,
            "role" to user.role.name,
            "totalFines" to user.totalFines,
            "pendingFines" to user.pendingFines
        )
        // Utilitzem merge per no sobreescriure altres camps com fcmToken
        db.collection("users").document(user.id).set(data, SetOptions.merge()).await()
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            val doc = db.collection("users").document(userId).get().await()
            if (!doc.exists()) return null
            User(
                id = doc.id,
                name = doc.getString("name") ?: "",
                photoInitials = doc.getString("photoInitials") ?: "",
                teamId = doc.getString("teamId") ?: "",
                role = UserRole.valueOf(doc.getString("role") ?: "PLAYER"),
                totalFines = doc.getDouble("totalFines") ?: 0.0,
                pendingFines = doc.getDouble("pendingFines") ?: 0.0
            )
        } catch (e: Exception) {
            null
        }
    }

    fun getUsersFlow(teamId: String): Flow<List<User>> = callbackFlow {
        val listener = db.collection("users")
            .whereEqualTo("teamId", teamId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val users = snapshot.documents.mapNotNull { doc ->
                    try {
                        User(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            photoInitials = doc.getString("photoInitials") ?: "",
                            teamId = doc.getString("teamId") ?: "",
                            role = UserRole.valueOf(doc.getString("role") ?: "PLAYER"),
                            totalFines = doc.getDouble("totalFines") ?: 0.0,
                            pendingFines = doc.getDouble("pendingFines") ?: 0.0
                        )
                    } catch (e: Exception) { null }
                }
                trySend(users)
            }
        awaitClose { listener.remove() }
    }

    // ─── TEAMS ───────────────────────────────────────────────────────────────────

    suspend fun getTeamByInviteCode(code: String): Team? {
        return try {
            val snapshot = db.collection("teams")
                .whereEqualTo("inviteCode", code.uppercase())
                .get().await()
            if (snapshot.isEmpty) return null
            val doc = snapshot.documents.first()
            Team(
                id = doc.id,
                name = doc.getString("name") ?: "",
                sport = doc.getString("sport") ?: "",
                totalPot = doc.getDouble("totalPot") ?: 0.0,
                inviteCode = doc.getString("inviteCode") ?: ""
            )
        } catch (e: Exception) { null }
    }

    suspend fun createTeam(name: String, adminId: String): Team {
        val inviteCode = generateInviteCode()
        val data = hashMapOf(
            "name" to name,
            "sport" to "Futbol",
            "totalPot" to 0.0,
            "inviteCode" to inviteCode,
            "adminId" to adminId
        )
        val ref = db.collection("teams").add(data).await()
        return Team(
            id = ref.id,
            name = name,
            sport = "Futbol",
            totalPot = 0.0,
            inviteCode = inviteCode
        )
    }

    private fun generateInviteCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return "PEN-" + (1..4).map { chars.random() }.joinToString("")
    }

    suspend fun getTeamById(teamId: String): Team? {
        return try {
            val doc = db.collection("teams").document(teamId).get().await()
            if (!doc.exists()) return null
            Team(
                id = doc.id,
                name = doc.getString("name") ?: "",
                sport = doc.getString("sport") ?: "",
                totalPot = doc.getDouble("totalPot") ?: 0.0,
                inviteCode = doc.getString("inviteCode") ?: ""
            )
        } catch (e: Exception) { null }
    }

    suspend fun updateInviteCode(teamId: String, newCode: String) {
        db.collection("teams").document(teamId)
            .update("inviteCode", newCode.uppercase()).await()
    }

    // ─── NOTIFICATIONS ───────────────────────────────────────────────────────────────────

    suspend fun saveFcmToken(userId: String, token: String) {
        try {
            val data = mapOf("fcmToken" to token)
            db.collection("users").document(userId)
                .set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun clearFcmToken(userId: String) {
        try {
            val data = mapOf("fcmToken" to null)
            db.collection("users").document(userId)
                .set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}