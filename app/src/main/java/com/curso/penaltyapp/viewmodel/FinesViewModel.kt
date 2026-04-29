package com.curso.penaltyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.penaltyapp.data.repository.FakeRepository
import com.curso.penaltyapp.data.model.Comment
import com.curso.penaltyapp.data.model.Fine
import com.curso.penaltyapp.data.model.FineCategory
import com.curso.penaltyapp.data.model.FineStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

// Estat complet de la UI relacionada amb les multes.
data class FinesUiState(
    val fines: List<Fine> = emptyList(),
    val myFines: List<Fine> = emptyList(),
    val pendingFines: List<Fine> = emptyList(),
    val isLoading: Boolean = false,
    val filterStatus: FineStatus? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

// Gestiona tota la lògica de negoci relacionada amb les multes.
class FinesViewModel : ViewModel() {

    private val repo = FakeRepository

    private val _uiState = MutableStateFlow(FinesUiState(isLoading = true))
    val uiState: StateFlow<FinesUiState> = _uiState.asStateFlow()

    val currentUser = repo.currentUser
    val team = repo.team
    val ranking = repo.ranking

    init {
        viewModelScope.launch {
            repo.fines.collect { allFines ->
                _uiState.update { state ->

                    val filtered = filterFines(allFines, state.filterStatus)
                    val my = allFines.filter { it.userId == currentUser.value.id }
                    val pending = allFines.filter { it.status == FineStatus.PENDING }

                    state.copy(
                        fines = filtered,
                        myFines = my,
                        pendingFines = pending,
                        isLoading = false
                    )
                }
            }
        }
    }

    // ─── PROPIETATS CALCULADES ────────────────────────────────────────────────

    val totalPot: Double get() = team.totalPot

    fun getFineById(fineId: String): Fine? =
        repo.fines.value.find { it.id == fineId }

    // ─── ACCIONS ──────────────────────────────────────────────────────────────

    fun setFilter(status: FineStatus?) {
        _uiState.update { state ->
            state.copy(
                filterStatus = status,
                fines = filterFines(repo.fines.value, status)
            )
        }
    }

    fun addFine(
        targetUserId: String,
        category: FineCategory,
        reason: String,
        customAmount: Double? = null
    ) {
        viewModelScope.launch {
            val user = repo.getUserById(targetUserId) ?: return@launch
            val amount = customAmount ?: category.defaultAmount

            val newFine = Fine(
                id = UUID.randomUUID().toString(),
                userId = targetUserId,
                userName = user.name,
                userInitials = user.photoInitials,
                category = category,
                amount = amount,
                reason = reason,
                date = LocalDateTime.now(),
                status = FineStatus.PENDING,
                comments = emptyList(),
                reactions = emptyMap()
            )
            repo.addFine(newFine)
            _uiState.update { it.copy(successMessage = "Multa afegida correctament!") }
        }
    }

    fun markFineAsPaid(fineId: String, viaNfc: Boolean = false) {
        viewModelScope.launch {
            repo.markAsPaid(fineId)
            val msg = if (viaNfc) "Pagament validat via NFC ✓" else "Multa marcada com a pagada"
            _uiState.update { it.copy(successMessage = msg) }
        }
    }

    fun addComment(fineId: String, text: String) {
        viewModelScope.launch {
            if (text.isBlank()) return@launch
            val comment = Comment(
                id = UUID.randomUUID().toString(),
                userId = currentUser.value.id,
                userName = currentUser.value.name,
                userInitials = currentUser.value.photoInitials,
                text = text,
                date = LocalDateTime.now()
            )
            repo.addComment(fineId, comment)
        }
    }

    fun addReaction(fineId: String, emoji: String) {
        viewModelScope.launch {
            repo.addReaction(fineId, emoji)
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    // ─── HELPERS PRIVATS ──────────────────────────────────────────────────────

    private fun filterFines(fines: List<Fine>, status: FineStatus?): List<Fine> =
        if (status == null) fines else fines.filter { it.status == status }
}