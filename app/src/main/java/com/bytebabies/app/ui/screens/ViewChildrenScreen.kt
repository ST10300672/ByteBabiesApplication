package com.bytebabies.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bytebabies.app.data.Repo
import com.bytebabies.app.model.Child
import com.bytebabies.app.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewChildrenScreen(navController: NavController) {

    var children by remember { mutableStateOf<List<Child>>(emptyList()) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingChild by remember { mutableStateOf<Child?>(null) }

    // Fetch children from Firestore
    LaunchedEffect(Unit) {
        Repo.fetchChildren { children = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Children") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Route.AdminAddChild.r) }
            ) {
                Icon(Icons.Default.Add, "Add Child")
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(children) { child ->
                ChildRow(
                    child = child,
                    onDelete = {
                        Repo.deleteChild(child.id) { success, _ ->
                            if (success) {
                                children = children.filter { it.id != child.id }
                            }
                        }
                    },
                    onEdit = {
                        editingChild = child
                        showEditDialog = true
                    }
                )
            }
        }
    }

    // ----------------- Edit Dialog -----------------
    if (showEditDialog && editingChild != null) {
        var name by remember { mutableStateOf(editingChild!!.name) }
        var age by remember { mutableStateOf(editingChild!!.age.toString()) }
        var emergencyContact by remember { mutableStateOf(editingChild!!.emergencyContact) }
        var allergies by remember { mutableStateOf(editingChild!!.allergies) }
        var medicalNotes by remember { mutableStateOf(editingChild!!.medicalNotes) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Child") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it.filter { c -> c.isDigit() } },
                        label = { Text("Age") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = emergencyContact,
                        onValueChange = { emergencyContact = it },
                        label = { Text("Emergency Contact") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = allergies,
                        onValueChange = { allergies = it },
                        label = { Text("Allergies") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = medicalNotes,
                        onValueChange = { medicalNotes = it },
                        label = { Text("Medical Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // Update the child in Firestore
                    val updates = mapOf<String, Any>(
                        "name" to name,
                        "age" to (age.toIntOrNull() ?: editingChild!!.age),
                        "emergencyContact" to emergencyContact,
                        "allergies" to allergies,
                        "medicalNotes" to medicalNotes
                    )

                    Repo.updateChild(editingChild!!.id, updates) { success, _ ->
                        if (success) {
                            // Update local list
                            children = children.map {
                                if (it.id == editingChild!!.id) it.copy(
                                    name = name,
                                    age = age.toIntOrNull() ?: it.age,
                                    emergencyContact = emergencyContact,
                                    allergies = allergies,
                                    medicalNotes = medicalNotes
                                ) else it
                            }
                        }
                    }
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ChildRow(child: Child, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Name: ${child.name}", style = MaterialTheme.typography.titleMedium)
            Text("Parent ID: ${child.parentId}")
            Text("Teacher ID: ${child.teacherId}")
            Text("Age: ${child.age}")
            Text("Emergency Contact: ${child.emergencyContact}")
            Text("Allergies: ${child.allergies}")
            Text("Medical Notes: ${child.medicalNotes}")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) { Text("Edit") }
                TextButton(onClick = onDelete) { Text("Delete") }
            }
        }
    }
}
