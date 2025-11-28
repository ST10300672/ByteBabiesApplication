package com.bytebabies.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bytebabies.app.ui.screens.AdminDashboardScreen
import com.bytebabies.app.ui.screens.LoginScreen
import com.bytebabies.app.ui.screens.ParentDashboardScreen

@Composable
fun ByteBabiesNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.Login.r) {

        // ---------- Auth ----------
        composable(Route.Login.r) { LoginScreen(navController) }

        // ---------- Admin ----------
        composable(Route.AdminHome.r) { AdminDashboardScreen(navController) }

        // ---------- Parent ----------
        composable(Route.ParentHome.r) { ParentDashboardScreen(navController) }
    }
}
