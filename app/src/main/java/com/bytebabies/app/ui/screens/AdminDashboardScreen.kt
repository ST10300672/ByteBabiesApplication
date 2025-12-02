package com.bytebabies.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bytebabies.app.navigation.Route
import com.bytebabies.app.ui.components.FeatureCard
import com.bytebabies.app.ui.components.bbGradient
import com.bytebabies.app.data.Repo

data class AdminFeature(
    val title: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavHostController) {
    val features = listOf(
        // Manage Teachers
        AdminFeature(
            title = "Manage Teachers",
            description = "Add and remove teachers",
            primaryColor = MaterialTheme.colorScheme.primary,
            secondaryColor = MaterialTheme.colorScheme.primaryContainer,
            route = Route.AdminTeachers.r
        ),

        // Add Child
        AdminFeature(
            title = "Add Child",
            description = "Register a child and assign parent + teacher",
            primaryColor = MaterialTheme.colorScheme.primary,
            secondaryColor = MaterialTheme.colorScheme.primaryContainer,
            route = Route.AdminAddChild.r
        ),

        // View Children
        AdminFeature(
            title = "View Children",
            description = "View all registered children, edit or delete",
            primaryColor = MaterialTheme.colorScheme.secondary,
            secondaryColor = MaterialTheme.colorScheme.secondaryContainer,
            route = Route.AdminViewChildren.r
        ),

        // Manage Parents
        AdminFeature(
            title = "Manage Parents",
            description = "Edit or remove parent profiles",
            primaryColor = MaterialTheme.colorScheme.secondary,
            secondaryColor = MaterialTheme.colorScheme.secondaryContainer,
            route = Route.AdminViewParents.r
        ),

        // Attendance
        AdminFeature(
            title = "Attendance",
            description = "Mark and review attendance",
            primaryColor = MaterialTheme.colorScheme.tertiary,
            secondaryColor = MaterialTheme.colorScheme.tertiaryContainer,
            route = Route.AdminAttendance.r
        ),

        // Events
        AdminFeature(
            title = "Events",
            description = "Create and manage events",
            primaryColor = MaterialTheme.colorScheme.primary,
            secondaryColor = MaterialTheme.colorScheme.primaryContainer,
            route = Route.AdminEvents.r
        ),

        // Messages & Announcements
        AdminFeature(
            title = "Announcements",
            description = "Send updates to parents",
            primaryColor = MaterialTheme.colorScheme.tertiary,
            secondaryColor = MaterialTheme.colorScheme.tertiaryContainer,
            route = Route.AdminAnnouncements.r
        ),

        AdminFeature(
            title = "Messages",
            description = "View messages from parents",
            primaryColor = MaterialTheme.colorScheme.secondary,
            secondaryColor = MaterialTheme.colorScheme.secondaryContainer,
            route = Route.AdminMessages.r
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Admin Dashboard") },
            actions = {
                IconButton(onClick = {
                    // Clear app session + firebase
                    try {
                        Repo.logout()
                    } catch (_: Exception) { /* safety */ }

                    // Ensure we remove current screens from backstack and navigate to login.
                    navController.popBackStack() // pop current
                    navController.navigate(Route.Login.r) {
                        launchSingleTop = true
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(features) { feature ->
                FeatureCard(
                    title = feature.title,
                    description = feature.description,
                    gradient = bbGradient(feature.primaryColor, feature.secondaryColor),
                    onClick = { navController.navigate(feature.route) }
                )
            }
        }
    }
}
