package com.curso.penaltyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.curso.penaltyapp.ui.state.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun onInviteCodeChanged(value: String) {
        _uiState.update { it.copy(inviteCode = value) }
    }

    fun onTeamNameChanged(value: String) {
        _uiState.update { it.copy(teamName = value) }
    }

    fun onSelectedTabChanged(value: Int) {
        _uiState.update { it.copy(selectedTab = value) }
    }
}