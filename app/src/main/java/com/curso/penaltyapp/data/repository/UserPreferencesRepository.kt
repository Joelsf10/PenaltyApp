package com.curso.penaltyapp.data.repository


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences

// Extension property to create a single DataStore instance
val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "penalty_user_preferences"
)

/**
 * UserPreferencesRepository — manages user preferences with Jetpack DataStore.
 * Covers: theme (dark/light/system), notifications, language, NFC enabled.
 */
class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        val KEY_THEME = stringPreferencesKey("theme")               // "light" | "dark" | "system"
        val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications")
        val KEY_NFC_ENABLED = booleanPreferencesKey("nfc_enabled")
        val KEY_LANGUAGE = stringPreferencesKey("language")          // "ca" | "es" | "en"
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val KEY_USER_ID = stringPreferencesKey("user_id")
        val KEY_SHOW_PAID_FINES = booleanPreferencesKey("show_paid_fines")
    }

    // ─── Flows ────────────────────────────────────────────────────────────────

    val theme: Flow<String> = dataStore.data.map { prefs: Preferences ->
        prefs[KEY_THEME] ?: "system"
    }

    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { prefs: Preferences ->
        prefs[KEY_NOTIFICATIONS] ?: true
    }

    val nfcEnabled: Flow<Boolean> = dataStore.data.map { prefs: Preferences ->
        prefs[KEY_NFC_ENABLED] ?: true
    }

    val language: Flow<String> = dataStore.data.map { prefs: Preferences ->
        prefs[KEY_LANGUAGE] ?: "ca"
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { prefs: Preferences ->
        prefs[KEY_IS_LOGGED_IN] ?: false
    }

    val userId: Flow<String> = dataStore.data.map { prefs: Preferences ->
        prefs[KEY_USER_ID] ?: ""
    }

    val showPaidFines: Flow<Boolean> = dataStore.data.map { prefs: Preferences ->
        prefs[KEY_SHOW_PAID_FINES] ?: true
    }

    // ─── Setters ──────────────────────────────────────────────────────────────

    suspend fun setTheme(theme: String) {
        dataStore.edit { prefs -> prefs[KEY_THEME] = theme }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_NOTIFICATIONS] = enabled }
    }

    suspend fun setNfcEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_NFC_ENABLED] = enabled }
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { prefs -> prefs[KEY_LANGUAGE] = language }
    }

    suspend fun setLoggedIn(loggedIn: Boolean, userId: String = "") {
        dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = loggedIn
            prefs[KEY_USER_ID] = userId
        }
    }

    suspend fun setShowPaidFines(show: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_SHOW_PAID_FINES] = show }
    }
}