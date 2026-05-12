package com.curso.penaltyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.curso.penaltyapp.data.model.FineCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddFineViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AddFineUiState())
    val uiState = _uiState.asStateFlow()

    fun onUserSelected(id: String) {
        _uiState.update {
            it.copy(selectedUserId = id)
        }
    }

    fun onCategorySelected(category: FineCategory) {
        _uiState.update {
            it.copy(selectedCategoryName = category.name)
        }
    }

    fun onReasonChanged(value: String) {
        _uiState.update {
            it.copy(reason = value)
        }
    }

    fun onCustomAmountChanged(value: String) {
        _uiState.update {
            it.copy(customAmount = value)
        }
    }
}