package com.curso.penaltyapp.viewmodel

import android.app.Application
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.curso.penaltyapp.data.model.User
import com.curso.penaltyapp.data.model.UserRole
import com.curso.penaltyapp.data.repository.AuthRepository
import com.curso.penaltyapp.data.repository.FirestoreRepository
import com.curso.penaltyapp.data.repository.UserPreferencesRepository
import com.curso.penaltyapp.data.repository.userPreferencesDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.curso.penaltyapp.R

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

    private val _authError = MutableStateFlow<Int?>(null)
    val authError: StateFlow<Int?> = _authError.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess.asStateFlow()

    private val _logoutComplete = MutableStateFlow(false)
    val logoutComplete: StateFlow<Boolean> = _logoutComplete.asStateFlow()

    init {
        viewModelScope.launch {
            if (AuthRepository.isLoggedIn) {
                prefsRepo.setLoggedIn(true, AuthRepository.currentFirebaseUser?.uid ?: "")
            } else {
                prefsRepo.setLoggedIn(false)
            }
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

    // ─── AUTH ─────────────────────────────────────────────────────────────────

    private fun saveFcmToken(uid: String) {
        com.google.firebase.messaging.FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    viewModelScope.launch {
                        FirestoreRepository.saveFcmToken(uid, token)
                    }
                }
            }
    }
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val result = AuthRepository.login(email, password)
            result.onSuccess { firebaseUser ->
                prefsRepo.setLoggedIn(true, firebaseUser.uid)
                saveFcmToken(firebaseUser.uid)
            }
            result.onFailure { error ->
                _authError.value = mapFirebaseError(error.message)
            }
            _isAuthLoading.value = false
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val result = AuthRepository.register(email, password)
            result.onSuccess { firebaseUser ->
                prefsRepo.setLoggedIn(true, firebaseUser.uid)
                saveFcmToken(firebaseUser.uid)
                _registerSuccess.value = true
            }
            result.onFailure { error ->
                _authError.value = mapFirebaseError(error.message)
            }
            _isAuthLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            // Esborra el token FCM de l'usuari actual abans de fer logout
            val uid = AuthRepository.currentFirebaseUser?.uid
            if (uid != null) {
                FirestoreRepository.clearFcmToken(uid)
            }
            AuthRepository.logout()
            prefsRepo.setLoggedIn(false, "")
            _registerSuccess.value = false
            _authError.value = null
        }
    }

    fun clearLogoutComplete() {
        _logoutComplete.value = false
    }

    fun clearAuthError() {
        _authError.value = null
    }

    fun clearRegisterSuccess() {
        _registerSuccess.value = false
    }

    private fun mapFirebaseError(message: String?): Int {
        return when {
            message == null -> R.string.FBError
            "no user record" in message -> R.string.FB1
            "password is invalid" in message -> R.string.FB2
            "email address is already in use" in message -> R.string.FB3
            "badly formatted" in message -> R.string.FB4
            "password should be at least" in message -> R.string.FB5
            "network error" in message.lowercase() -> R.string.FB6
            else -> R.string.FB7
        }
    }

    // ─── PREFERÈNCIES ────────────────────────────────────────────────────────

    fun setTheme(theme: String) {
        viewModelScope.launch { prefsRepo.setTheme(theme) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setNotificationsEnabled(enabled) }
    }

    fun setNfcEnabled(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setNfcEnabled(enabled) }
    }

    fun setLanguage(language: String, activity: android.app.Activity) {
        viewModelScope.launch {
            prefsRepo.setLanguage(language)
            // Guardar a SharedPreferences per attachBaseContext
            val prefs = activity.getSharedPreferences("penalty_lang", android.content.Context.MODE_PRIVATE)
            prefs.edit().putString("language", language).apply()
            // Reiniciar l'activitat per aplicar el canvi
            activity.recreate()
        }
    }

    fun setShowPaidFines(show: Boolean) {
        viewModelScope.launch { prefsRepo.setShowPaidFines(show) }
    }
}