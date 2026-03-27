package com.curso.penaltyapp.ui.navigation


/**
 * Sealed class defining all navigation destinations in the app.
 * Follows Jetpack Navigation Compose best practices.
 */
sealed class Screen(val route: String) {

    // ─── Auth ─────────────────────────────────────────────────────────────────
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object TeamSetup : Screen("team_setup")

    // ─── Main ─────────────────────────────────────────────────────────────────
    data object Home : Screen("home")
    data object Fines : Screen("fines")
    data object Ranking : Screen("ranking")
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")

    // ─── Detail screens ───────────────────────────────────────────────────────
    data object FineDetail : Screen("fine_detail/{fineId}") {
        fun createRoute(fineId: String) = "fine_detail/$fineId"
    }
    data object AddFine : Screen("add_fine")
    data object NfcPayment : Screen("nfc_payment/{fineId}") {
        fun createRoute(fineId: String) = "nfc_payment/$fineId"
    }
    data object TeamMembers : Screen("team_members")
}

/**
 * Bottom navigation items shown in the main scaffold
 */
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val iconRes: androidx.compose.ui.graphics.vector.ImageVector
)
