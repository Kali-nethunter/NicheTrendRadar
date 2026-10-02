package com.nichetrendradar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nichetrendradar.ui.screens.DashboardScreen
import com.nichetrendradar.ui.screens.IdeaScreen
import com.nichetrendradar.ui.screens.LoginScreen
import com.nichetrendradar.ui.screens.OnboardingScreen
import com.nichetrendradar.ui.screens.ProfileScreen
import com.nichetrendradar.ui.screens.SavedIdeasScreen
import com.nichetrendradar.ui.screens.SavedIdeaDetailScreen
import com.nichetrendradar.viewmodel.MainViewModel

@Composable
fun NavGraph(viewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen { navController.navigate("onboarding") } }
        composable("onboarding") {
            OnboardingScreen(viewModel) {
                navController.navigate("dashboard") {
                    popUpTo("onboarding") { inclusive = true }
                }
            }
        }
        composable("dashboard") { DashboardScreen(navController, viewModel) }
        composable("ideas") { IdeaScreen(navController, viewModel) }
        composable("saved") { SavedIdeasScreen(navController, viewModel) }
        composable("saved_detail") { SavedIdeaDetailScreen(navController, viewModel) }
        composable("profile") { ProfileScreen(navController) }
    }
}
