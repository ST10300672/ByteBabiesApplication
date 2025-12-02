package com.bytebabies.app.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.google.firebase.auth.FirebaseAuth

data class ParentFeature(
    val title: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun ParentDashboardScreen(navController: NavHostController) {

    val features = listOf(
        ParentFeature(
            "My Children",
            "View your child's profile and details",
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer,
            Route.ParentChildren.r
        ),

        ParentFeature(
            "Attendance",
            "Check today's attendance for your children",
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondaryContainer,
            Route.ParentAttendance.r
        ),

        ParentFeature(
            "Events",
            "See upcoming events and notifications",
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.tertiaryContainer,
            Route.ParentEvents.r
        ),

        ParentFeature(
            "Announcements",
            "View school announcements and messages",
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondaryContainer,
            Route.ParentAnnouncements.r
        ),

        ParentFeature(
            "Messages",
            "Receive announcements and messages",
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondaryContainer,
            Route.ParentMessages.r
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Parent Dashboard") },
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
                    onClick = { navController.navigate(feature.route) } // <-- FIXED
                )
            }
        }

    }
}
