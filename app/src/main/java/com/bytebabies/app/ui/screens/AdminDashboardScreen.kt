package com.bytebabies.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bytebabies.app.navigation.Route
import com.bytebabies.app.ui.components.FeatureCard
import com.bytebabies.app.ui.components.TopBar
import com.bytebabies.app.ui.components.bbGradient

data class AdminFeature(
    val title: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val route: String
)

@Composable
fun AdminDashboardScreen(nav: NavHostController) {
    val features = listOf(

        // --- NEW: Manage Teachers wired properly ---
        AdminFeature(
            title = "Manage Teachers",
            description = "Add and remove teachers",
            primaryColor = MaterialTheme.colorScheme.primary,
            secondaryColor = MaterialTheme.colorScheme.primaryContainer,
            route = Route.AdminTeachers.r
        ),

        // Future features — these remain but point to real routes later
        AdminFeature(
            title = "Manage Parents & Children",
            description = "View and edit parent & child profiles",
            primaryColor = MaterialTheme.colorScheme.secondary,
            secondaryColor = MaterialTheme.colorScheme.secondaryContainer,
            route = Route.AdminHome.r   // placeholder until feature is implemented
        ),

        AdminFeature(
            title = "Attendance",
            description = "Mark and review attendance",
            primaryColor = MaterialTheme.colorScheme.tertiary,
            secondaryColor = MaterialTheme.colorScheme.tertiaryContainer,
            route = Route.AdminHome.r   // placeholder
        ),

        AdminFeature(
            title = "Events",
            description = "Create and manage events",
            primaryColor = MaterialTheme.colorScheme.primary,
            secondaryColor = MaterialTheme.colorScheme.primaryContainer,
            route = Route.AdminHome.r   // placeholder
        ),

        AdminFeature(
            title = "Meals & Orders",
            description = "Manage meals and view orders",
            primaryColor = MaterialTheme.colorScheme.secondary,
            secondaryColor = MaterialTheme.colorScheme.secondaryContainer,
            route = Route.AdminHome.r   // placeholder
        ),

        AdminFeature(
            title = "Messages & Announcements",
            description = "Send updates to parents",
            primaryColor = MaterialTheme.colorScheme.tertiary,
            secondaryColor = MaterialTheme.colorScheme.tertiaryContainer,
            route = Route.AdminHome.r   // placeholder
        ),

        AdminFeature(
            title = "Media Uploads",
            description = "Share photos & videos with consent",
            primaryColor = MaterialTheme.colorScheme.primary,
            secondaryColor = MaterialTheme.colorScheme.primaryContainer,
            route = Route.AdminHome.r   // placeholder
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(title = "Admin Dashboard")

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
                    onClick = { nav.navigate(feature.route) }
                )
            }
        }
    }
}
