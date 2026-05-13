package com.curso.penaltyapp.data.repository

import com.curso.penaltyapp.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

object FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    // ─── FINES ───────────────────────────────────────────────────────────────

    fun getFinesFlow(): Flow<List<Fine>> = callbackFlow {
        val listener: ListenerRegistration = db.collection("fines")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val fines = snapshot.documents.mapNotNull { doc ->
                    try {
                        Fine(
                            id = doc.id,
                            userId = doc.getString("userId") ?: "",
                            userName = doc.getString("userName") ?: "",
                            userInitials = doc.getString("userInitials") ?: "",
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
        db.collection("fines").document(fineId)
            .update("status", FineStatus.PAID.name).await()
    }

    suspend fun addReaction(fineId: String, emoji: String) {
        val doc = db.collection("fines").document(fineId).get().await()
        val reactions = (doc.get("reactions") as? Map<String, Long>)
            ?.mapValues { it.value.toInt() }?.toMutableMap() ?: mutableMapOf()
        reactions[emoji] = (reactions[emoji] ?: 0) + 1
        db.collection("fines").document(fineId)
            .update("reactions", reactions).await()
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
        db.collection("users").document(user.id).set(data).await()
    }

    suspend fun getUserById(userId: String): User? {
        val doc = db.collection("users").document(userId).get().await()
        if (!doc.exists()) return null
        return User(
            id = doc.id,
            name = doc.getString("name") ?: "",
            photoInitials = doc.getString("photoInitials") ?: "",
            teamId = doc.getString("teamId") ?: "",
            role = UserRole.valueOf(doc.getString("role") ?: "PLAYER"),
            totalFines = doc.getDouble("totalFines") ?: 0.0,
            pendingFines = doc.getDouble("pendingFines") ?: 0.0
        )
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
}