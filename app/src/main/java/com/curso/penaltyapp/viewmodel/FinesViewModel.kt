package com.curso.penaltyapp.viewmodel


import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.penaltyapp.R
import com.curso.penaltyapp.data.repository.FakeRepository
import com.curso.penaltyapp.data.model.Comment
import com.curso.penaltyapp.data.model.Fine
import com.curso.penaltyapp.data.model.FineCategory
import com.curso.penaltyapp.data.model.FineStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

data class FinesUiState(
    val fines: List<Fine> = emptyList(),
    val isLoading: Boolean = false,
    val filterStatus: FineStatus? = null,
    val errorMessage: String? = null,
    val successMessage: Int? = null
)

class FinesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FinesUiState(isLoading = true))
    val uiState: StateFlow<FinesUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _allFines = MutableStateFlow<List<Fine>>(emptyList())

    private val _team = MutableStateFlow<Team?>(null)
    val team: StateFlow<Team?> = _team.asStateFlow()
    val ranking: List<RankingEntry>
        get() = _users.value
            .sortedByDescending { it.totalFines }
            .mapIndexed { idx, user ->
                RankingEntry(
                    user = user,
                    position = idx + 1,
                    totalAmount = user.totalFines,
                    fineCount = _allFines.value.count { it.userId == user.id }
                )
            }

    val pendingFines: List<Fine>
        get() = _allFines.value.filter { it.status == FineStatus.PENDING }

    val myFines: List<Fine>
        get() = _allFines.value.filter { it.userId == currentUser.value?.id }

    val totalPot: Double
        get() = _allFines.value
            .filter { it.status == FineStatus.PAID }
            .sumOf { it.amount }

    init {
        loadCurrentUser()
        loadFines()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val uid = AuthRepository.currentFirebaseUser?.uid ?: return@launch
                val user = FirestoreRepository.getUserById(uid)
                _currentUser.value = user
                user?.teamId?.let { teamId ->
                    val teamData = FirestoreRepository.getTeamById(teamId)
                    _team.value = teamData
                    FirestoreRepository.getUsersFlow(teamId).collect { _users.value = it }
                }
            } catch (e: Exception) {
                _currentUser.value = null
            }
        }
    }

    private fun loadFines() {
        viewModelScope.launch {
            FirestoreRepository.getFinesFlow().collect { allFines ->
                _allFines.value = allFines
                _uiState.update { state ->
                    state.copy(
                        fines = filterFines(allFines, state.filterStatus),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun reload() {
        _currentUser.value = null
        _users.value = emptyList()
        _allFines.value = emptyList()
        _uiState.value = FinesUiState(isLoading = true)
        loadCurrentUser()
        loadFines()
    }

    fun getFineById(fineId: String): Fine? =
        _allFines.value.find { it.id == fineId }

    fun getCommentsFlow(fineId: String) =
        FirestoreRepository.getCommentsFlow(fineId)

    fun setFilter(status: FineStatus?) {
        _uiState.update { state ->
            state.copy(
                filterStatus = status,
                fines = filterFines(_allFines.value, status)
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
            val user = FirestoreRepository.getUserById(targetUserId) ?: return@launch
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
            _uiState.update { it.copy(successMessage = (R.string.multa_afegida))}
        }
    }

    fun markFineAsPaid(fineId: String, viaNfc: Boolean = false) {
        viewModelScope.launch {
            repo.markAsPaid(fineId)
            val msg = if (viaNfc) (R.string.NFC_validado) else (R.string.multa_pag_manual)
            _uiState.update { it.copy(successMessage = msg) }
        }
    }

    fun addComment(fineId: String, text: String) {
        viewModelScope.launch {
            if (text.isBlank()) return@launch
            val user = currentUser.value ?: return@launch
            val comment = Comment(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                userName = user.name,
                userInitials = user.photoInitials,
                text = text,
                date = LocalDateTime.now()
            )
            FirestoreRepository.addComment(fineId, comment)
        }
    }

    fun addReaction(fineId: String, emoji: String) {
        viewModelScope.launch {
            FirestoreRepository.addReaction(fineId, emoji)
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun filterFines(fines: List<Fine>, status: FineStatus?): List<Fine> =
        if (status == null) fines else fines.filter { it.status == status }

    fun updateInviteCode(newCode: String) {
        viewModelScope.launch {
            val teamId = currentUser.value?.teamId ?: return@launch
            FirestoreRepository.updateInviteCode(teamId, newCode)
            // Recarregar l'equip
            val updated = FirestoreRepository.getTeamById(teamId)
            _team.value = updated
        }
    }
}