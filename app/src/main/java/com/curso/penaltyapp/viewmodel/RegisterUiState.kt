package com.curso.penaltyapp.viewmodel

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val inviteCode: String = "",
    val teamName: String = "",
    val selectedTab: Int = 0
)