package com.curso.penaltyapp.ui.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.curso.penaltyapp.ui.screens.*
import com.curso.penaltyapp.viewmodel.*

@Composable
fun PenaltyNavHost(
    navController: NavHostController,
    isLoggedIn: Boolean,
    finesViewModel: FinesViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
    val currentUser by finesViewModel.currentUser.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        // ─── Auth flow ────────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { userId ->
                    settingsViewModel.login(userId)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.TeamSetup.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TeamSetup.route) {
            TeamSetupScreen(
                onTeamReady = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.TeamSetup.route) { inclusive = true }
                    }
                }
            )
        }

        // ─── Main screens ─────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            HomeScreen(
                finesViewModel = finesViewModel,
                onNavigateToFines = { navController.navigate(Screen.Fines.route) },
                onNavigateToFineDetail = { fineId ->
                    navController.navigate(Screen.FineDetail.createRoute(fineId))
                },
                onNavigateToAddFine = { navController.navigate(Screen.AddFine.route) },
                onNavigateToRanking = { navController.navigate(Screen.Ranking.route) }
            )
        }

        composable(Screen.Fines.route) {
            FinesScreen(
                finesViewModel = finesViewModel,
                onNavigateToFineDetail = { fineId ->
                    navController.navigate(Screen.FineDetail.createRoute(fineId))
                },
                onNavigateToAddFine = { navController.navigate(Screen.AddFine.route) }
            )
        }

        composable(Screen.Ranking.route) {
            RankingScreen(
                finesViewModel = finesViewModel
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                finesViewModel = finesViewModel,
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ─── Detail screens ───────────────────────────────────────────────────
        composable(
            route = Screen.FineDetail.route,
            arguments = listOf(navArgument("fineId") { type = NavType.StringType })
        ) { backStackEntry ->
            val fineId = backStackEntry.arguments?.getString("fineId") ?: ""
            FineDetailScreen(
                fineId = fineId,
                finesViewModel = finesViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNfcPayment = { id ->
                    navController.navigate(Screen.NfcPayment.createRoute(id))
                }
            )
        }

        composable(Screen.AddFine.route) {
            if (currentUser.role.name == "ADMIN") {
                AddFineScreen(
                    finesViewModel = finesViewModel,
                    onFineAdded = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = Screen.NfcPayment.route,
            arguments = listOf(navArgument("fineId") { type = NavType.StringType })
        ) { backStackEntry ->
            val fineId = backStackEntry.arguments?.getString("fineId") ?: ""
            NfcPaymentScreen(
                fineId = fineId,
                finesViewModel = finesViewModel,
                onPaymentComplete = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }


        composable(Screen.TeamMembers.route) {
            TeamMembersScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}