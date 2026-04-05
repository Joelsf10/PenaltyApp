package com.curso.penaltyapp.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.curso.penaltyapp.data.repository.UserPreferencesRepository
import com.curso.penaltyapp.data.repository.userPreferencesDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val theme: String = "system",
    val notificationsEnabled: Boolean = true,
    val nfcEnabled: Boolean = true,
    val language: String = "ca",
    val showPaidFines: Boolean = true
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = UserPreferencesRepository(
        application.userPreferencesDataStore
    )

    init {
        viewModelScope.launch {
            prefsRepo.setLoggedIn(false)
        }
    }

    val uiState: StateFlow<SettingsUiState> = combine(
        prefsRepo.theme,
        prefsRepo.notificationsEnabled,
        prefsRepo.nfcEnabled,
        prefsRepo.language,
        prefsRepo.showPaidFines
    ) { theme, notifications, nfc, language, showPaid ->
        SettingsUiState(
            theme = theme,
            notificationsEnabled = notifications,
            nfcEnabled = nfc,
            language = language,
            showPaidFines = showPaid
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    val theme: Flow<String> = prefsRepo.theme
    val isLoggedIn: Flow<Boolean> = prefsRepo.isLoggedIn

    // ─── ACTUALITZACIÓ DE PREFERÈNCIES ───────────────────────────────────────
    fun setTheme(theme: String) {
        viewModelScope.launch { prefsRepo.setTheme(theme) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setNotificationsEnabled(enabled) }
    }

    fun setNfcEnabled(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setNfcEnabled(enabled) }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch { prefsRepo.setLanguage(language) }
    }

    fun setShowPaidFines(show: Boolean) {
        viewModelScope.launch { prefsRepo.setShowPaidFines(show) }
    }

    // ─── GESTIÓ DE LA SESSIÓ ──────────────────────────────────────────────────
    fun login(userId: String) {
        viewModelScope.launch { prefsRepo.setLoggedIn(true, userId) }
    }

    fun logout() {
        viewModelScope.launch { prefsRepo.setLoggedIn(false) }
    }
}
