package com.curso.penaltyapp.viewmodel

import com.curso.penaltyapp.data.model.FineCategory

data class AddFineUiState(
    val selectedUserId: String = "",
    val selectedCategoryName: String = FineCategory.LATE_TRAINING.name,
    val reason: String = "",
    val customAmount: String = ""
)