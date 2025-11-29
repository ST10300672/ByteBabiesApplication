package com.bytebabies.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bytebabies.app.data.Repo
import com.bytebabies.app.navigation.Route
import com.bytebabies.app.ui.components.OutlinedTextFieldFull

@Composable
fun LoginScreen(nav: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("ByteBabies Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(32.dp))

        // -------- Email Field --------
        OutlinedTextFieldFull(
            value = email,
            onVal = { email = it },
            label = "Email",
            keyboardType = KeyboardType.Email
        )

        Spacer(Modifier.height(16.dp))

        // -------- Password Field --------
        OutlinedTextFieldFull(
            value = password,
            onVal = { password = it },
            label = "Password",
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        Spacer(Modifier.height(24.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // -------- Login Button --------
        Button(
            onClick = {
                loading = true
                errorMessage = null
                Repo.login(email, password) { success, error ->
                    loading = false
                    if (success) {
                        when (Repo.currentRole) {
                            com.bytebabies.app.model.Role.ADMIN -> nav.navigate(Route.AdminHome.r) {
                                popUpTo(Route.Login.r) { inclusive = true }
                            }
                            com.bytebabies.app.model.Role.PARENT -> nav.navigate(Route.ParentHome.r) {
                                popUpTo(Route.Login.r) { inclusive = true }
                            }
                            else -> errorMessage = "Unknown role"
                        }
                    } else {
                        errorMessage = error ?: "Login failed"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            Text(if (loading) "Logging in..." else "Login")
        }

        Spacer(Modifier.height(16.dp))

        // -------- Register Button --------
        TextButton(
            onClick = { nav.navigate(Route.ParentRegister.r) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Register as a new parent")
        }
    }
}
