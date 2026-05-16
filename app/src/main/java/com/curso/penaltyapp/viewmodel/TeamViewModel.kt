package com.curso.penaltyapp.viewmodel

import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.penaltyapp.data.model.User
import com.curso.penaltyapp.data.model.UserRole
import com.curso.penaltyapp.data.repository.AuthRepository
import com.curso.penaltyapp.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.curso.penaltyapp.R


data class TeamSetupUiState(
    val isLoading: Boolean = false,
    val error: Int? = null,
    val success: Boolean = false,
    val inviteCode: String? = null  // per mostrar el codi quan es crea equip
)

class TeamViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TeamSetupUiState())
    val uiState: StateFlow<TeamSetupUiState> = _uiState.asStateFlow()

    // Unir-se a un equip existent amb codi
    fun joinTeam(inviteCode: String, userName: String, userInitials: String) {
        viewModelScope.launch {
            _uiState.value = TeamSetupUiState(isLoading = true)
            val uid = AuthRepository.currentFirebaseUser?.uid ?: return@launch

            val team = FirestoreRepository.getTeamByInviteCode(inviteCode)
            if (team == null) {
                _uiState.value = TeamSetupUiState(
                    error = R.string.fake_codi
                )
                return@launch
            }

            val user = User(
                id = uid,
                name = userName,
                photoInitials = userInitials,
                teamId = team.id,
                role = UserRole.PLAYER,
                totalFines = 0.0,
                pendingFines = 0.0
            )
            FirestoreRepository.saveUser(user)
            _uiState.value = TeamSetupUiState(success = true)
        }
    }

    // Crear un equip nou (l'usuari es converteix en ADMIN)
    fun createTeam(teamName: String, userName: String, userInitials: String) {
        viewModelScope.launch {
            _uiState.value = TeamSetupUiState(isLoading = true)
            val uid = AuthRepository.currentFirebaseUser?.uid ?: return@launch

            if (teamName.isBlank()) {
                _uiState.value = TeamSetupUiState(error = R.string.teamname_error)
                return@launch
            }

            val team = FirestoreRepository.createTeam(teamName, uid)

            val user = User(
                id = uid,
                name = userName,
                photoInitials = userInitials,
                teamId = team.id,
                role = UserRole.ADMIN,
                totalFines = 0.0,
                pendingFines = 0.0
            )
            FirestoreRepository.saveUser(user)
            _uiState.value = TeamSetupUiState(
                success = true,
                inviteCode = team.inviteCode
            )
        }
    }
}