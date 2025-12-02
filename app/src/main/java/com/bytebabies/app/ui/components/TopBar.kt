package com.bytebabies.app.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    onLogout: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(title) },
        actions = {
            onLogout?.let {
                TextButton(onClick = it) {
                    Text(
                        "Logout",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    )
}
