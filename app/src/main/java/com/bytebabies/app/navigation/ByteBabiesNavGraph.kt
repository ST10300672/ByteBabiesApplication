package com.bytebabies.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bytebabies.app.ui.screens.*

@Composable
fun ByteBabiesNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Login.r
    ) {

        // ---------- Authentication ----------
        composable(Route.Login.r) { LoginScreen(navController) }
        composable(Route.ParentRegister.r) { ParentRegistrationScreen(navController) }

        // ---------- Admin ----------
        composable(Route.AdminHome.r) { AdminDashboardScreen(navController) }
        composable(Route.AdminTeachers.r) { AdminTeachersScreen(navController) }
        composable(Route.AdminAddChild.r) { AddChildScreen(navController) }
        composable(Route.AdminViewChildren.r) { ViewChildrenScreen(navController) } // NEW
        composable(Route.AdminViewParents.r) { ViewParentsScreen(navController) }

        // ---------- Attendance Screen ----------
        composable(Route.AdminAttendance.r) { AttendanceDashboardScreen(navController) }

        // ---------- Events & Notifications Screen ----------
        composable(Route.AdminEvents.r) { EventNotificationsScreen(navController) }

        // ---------- Admin: Messages & Announcements ----------
        composable(Route.AdminAnnouncements.r) { AnnouncementsScreen(navController) }
        composable(Route.AdminMessages.r) { AdminMessagesScreen(navController) }



        // ---------- Parent ----------
        composable(Route.ParentHome.r) { ParentDashboardScreen(navController) }
        composable(Route.ParentChildren.r) { ParentChildrenScreen(navController) }
        composable(Route.ParentAttendance.r) { ParentAttendanceScreen(navController) }
        composable(Route.ParentAnnouncements.r) { ParentAnnouncementsScreen(navController) }

    }
}
