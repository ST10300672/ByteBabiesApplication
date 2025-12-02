package com.bytebabies.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bytebabies.app.data.Repo
import com.bytebabies.app.ui.components.FeatureCard
import com.bytebabies.app.ui.components.TopBar
import com.bytebabies.app.ui.components.bbGradient
import androidx.compose.ui.graphics.Color
import com.bytebabies.app.navigation.Route

data class ParentFeature(
    val title: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val route: String
)

@Composable
fun ParentDashboardScreen(nav: NavHostController) {
    val features = listOf(
        ParentFeature(
            "My Children",
            "View your child's profile and details",
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer,
            "parent_children" // updated to match Route.ParentChildren.r
        ),

        ParentFeature(
            "Attendance",
            "Check today's attendance for your children",
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondaryContainer,
            route = "parent_attendance" // updated
        ),

        ParentFeature(
            "Events",
            "See upcoming events and notifications",
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.tertiaryContainer,
            "events"
        ),
        ParentFeature(
            "Meals & Orders",
            "View daily meals and order for your children",
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer,
            "meals"
        ),
        ParentFeature(
            "Messages",
            "Receive announcements and messages",
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondaryContainer,
            "messages"
        )
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(title = "Parent Dashboard")

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
