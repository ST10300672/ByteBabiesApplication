package com.bytebabies.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bytebabies.app.data.Repo
import com.bytebabies.app.navigation.Route
import com.bytebabies.app.ui.components.OutlinedTextFieldFull
import com.bytebabies.app.ui.components.TopBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Dialog

@Composable
fun ParentRegistrationScreen(nav: NavHostController) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var consentMedia by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopBar(title = "Parent Registration", nav = nav) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            OutlinedTextFieldFull(value = name, onVal = { name = it }, label = "Full Name")
            Spacer(Modifier.height(8.dp))
            OutlinedTextFieldFull(value = email, onVal = { email = it }, label = "Email")
            Spacer(Modifier.height(8.dp))
            OutlinedTextFieldFull(value = phone, onVal = { phone = it }, label = "Phone")
            Spacer(Modifier.height(8.dp))
            OutlinedTextFieldFull(value = password, onVal = { password = it }, label = "Password", isPassword = true)
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = consentMedia,
                    onCheckedChange = { consentMedia = it }
                )
                Spacer(Modifier.width(8.dp))
                Text("Consent to media")
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || password.isBlank()) {
                        dialogMessage = "Please fill all required fields."
                        showDialog = true
                        return@Button
                    }
                    loading = true
                    Repo.registerParent(name, email, phone, password, consentMedia) { success, error ->
                        loading = false
                        if (success) {
                            // After registration, log in automatically
                            Repo.login(email, password) { loginSuccess, loginError ->
                                if (loginSuccess && Repo.currentRole == com.bytebabies.app.model.Role.PARENT) {
                                    nav.navigate(Route.ParentHome.r) {
                                        popUpTo(Route.Login.r) { inclusive = true }
                                    }
                                } else {
                                    dialogMessage = loginError ?: "Failed to login"
                                    showDialog = true
                                }
                            }
                        } else {
                            dialogMessage = error ?: "Registration failed"
                            showDialog = true
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (loading) "Registering..." else "Register")
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = { nav.popBackStack() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Back to Login")
            }
        }
    }

    // ---------------- Error Dialog ----------------
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(dialogMessage)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { showDialog = false }, modifier = Modifier.align(Alignment.End)) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
