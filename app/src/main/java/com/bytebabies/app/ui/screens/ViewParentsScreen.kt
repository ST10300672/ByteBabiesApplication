package com.bytebabies.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.bytebabies.app.data.Repo
import com.bytebabies.app.model.Parent

@Composable
fun ViewParentsScreen(navController: NavController? = null) {
    var parents by remember { mutableStateOf(listOf<Parent>()) }
    var isEditing by remember { mutableStateOf(false) }
    var selectedParent by remember { mutableStateOf<Parent?>(null) }
    var showMessage by remember { mutableStateOf<Pair<String, Boolean>?>(null) }

    // Fetch parents on first composition
    LaunchedEffect(Unit) {
        Repo.fetchParents { list -> parents = list }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            text = "Parents",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(parents) { parent ->
                ParentItem(
                    parent = parent,
                    onEdit = {
                        selectedParent = it
                        isEditing = true
                    },
                    onDelete = { p ->
                        Repo.deleteParent(p.id) { success, msg ->
                            if (success) {
                                Repo.fetchParents { list -> parents = list }
                                showMessage = "Parent deleted successfully" to true
                            } else {
                                showMessage = (msg ?: "Failed to delete parent") to false
                            }
                        }
                    }
                )
            }
        }

        // Show message if any
        showMessage?.let { (message, isSuccess) ->
            Text(
                text = message,
                color = if (isSuccess) Color.Green else Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    // Edit dialog
    if (isEditing && selectedParent != null) {
        EditParentDialog(
            parent = selectedParent!!,
            onDismiss = { isEditing = false },
            onSave = { updatedParent ->
                Repo.updateParent(
                    id = updatedParent.id,
                    name = updatedParent.name,
                    email = updatedParent.email,
                    phone = updatedParent.phone,
                    consentMedia = updatedParent.consentMedia
                ) { success, msg ->
                    if (success) {
                        Repo.fetchParents { list -> parents = list }
                        isEditing = false
                        showMessage = "Parent updated successfully" to true
                    } else {
                        showMessage = (msg ?: "Failed to update parent") to false
                    }
                }
            }
        )
    }
}

@Composable
fun ParentItem(parent: Parent, onEdit: (Parent) -> Unit, onDelete: (Parent) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onEdit(parent) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = parent.name, style = MaterialTheme.typography.titleMedium)
                Text(text = parent.email, style = MaterialTheme.typography.bodyMedium)
                Text(text = parent.phone, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = { onDelete(parent) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Parent",
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun EditParentDialog(parent: Parent, onDismiss: () -> Unit, onSave: (Parent) -> Unit) {
    var name by remember { mutableStateOf(parent.name) }
    var email by remember { mutableStateOf(parent.email) }
    var phone by remember { mutableStateOf(parent.phone) }
    var consent by remember { mutableStateOf(parent.consentMedia) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Edit Parent", style = MaterialTheme.typography.headlineSmall)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Checkbox(checked = consent, onCheckedChange = { consent = it })
                    Text(text = "Consent for Media", modifier = Modifier.padding(start = 8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onSave(parent.copy(
                            name = name,
                            email = email,
                            phone = phone,
                            consentMedia = consent
                        ))
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
