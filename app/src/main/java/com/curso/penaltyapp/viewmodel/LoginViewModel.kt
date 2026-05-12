package com.curso.penaltyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.curso.penaltyapp.ui.state.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChanged(value: String) {
        _uiState.update {
            it.copy(email = value)
        }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update {
            it.copy(password = value)
        }
    }


    fun isFormValid(): Boolean {
        val state = _uiState.value

        return state.email.isNotBlank() &&
                state.password.isNotBlank()
    }
}