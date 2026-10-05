package com.nichetrendradar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nichetrendradar.ui.screens.DashboardScreen
import com.nichetrendradar.ui.screens.IdeaScreen
import com.nichetrendradar.ui.screens.LegalDocumentScreen
import com.nichetrendradar.ui.screens.LoginScreen
import com.nichetrendradar.ui.screens.OnboardingScreen
import com.nichetrendradar.ui.screens.ProfileScreen
import com.nichetrendradar.ui.screens.SavedIdeasScreen
import com.nichetrendradar.ui.screens.SavedIdeaDetailScreen
import com.nichetrendradar.ui.screens.SettingsScreen
import com.nichetrendradar.viewmodel.MainViewModel

@Composable
fun NavGraph(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val hasSavedNiche = viewModel.currentNiche?.id != null
    NavHost(navController = navController, startDestination = if (viewModel.isLoggedIn) { if (hasSavedNiche) "dashboard" else "onboarding" } else "login") {
        composable("login") { LoginScreen(viewModel) { navController.navigate("onboarding") { popUpTo("login") { inclusive = true } } } }
        composable("onboarding") {
            OnboardingScreen(viewModel, navController) {
                navController.navigate("dashboard") {
                    popUpTo("onboarding") { inclusive = true }
                }
            }
        }
        composable("dashboard") { DashboardScreen(navController, viewModel) }
        composable("ideas") { IdeaScreen(navController, viewModel) }
        composable("saved") { SavedIdeasScreen(navController, viewModel) }
        composable("saved_detail") { SavedIdeaDetailScreen(navController, viewModel) }
        composable("profile") { ProfileScreen(navController, viewModel) }
        composable("security_privacy") { SettingsScreen(navController, "security", viewModel) }
        composable("account_privacy") { SettingsScreen(navController, "account", viewModel) }
        composable("help_support") { SettingsScreen(navController, "help", viewModel) }
        composable("privacy_policy") { LegalDocumentScreen(navController, LegalDocument.PRIVACY) }
        composable("terms_of_service") { LegalDocumentScreen(navController, LegalDocument.TERMS) }
        composable("data_usage_policy") { LegalDocumentScreen(navController, LegalDocument.DATA_USAGE) }
    }
}
