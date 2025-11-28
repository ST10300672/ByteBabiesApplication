package com.bytebabies.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bytebabies.app.ui.components.FeatureCard
import com.bytebabies.app.ui.components.TopBar
import com.bytebabies.app.ui.components.bbGradient
import androidx.compose.ui.graphics.Color

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
        AdminFeature(
            "Manage Parents & Children",
            "View and edit parent & child profiles",
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer,
            "manageParents"
        ),
        AdminFeature(
            "Attendance",
            "Mark and review attendance",
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondaryContainer,
            "attendance"
        ),
        AdminFeature(
            "Events",
            "Create and manage events",
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.tertiaryContainer,
            "events"
        ),
        AdminFeature(
            "Meals & Orders",
            "Manage meals and view orders",
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer,
            "meals"
        ),
        AdminFeature(
            "Messages & Announcements",
            "Send updates to parents",
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondaryContainer,
            "messages"
        ),
        AdminFeature(
            "Media Uploads",
            "Share photos & videos with consent",
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.tertiaryContainer,
            "media"
        )
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
