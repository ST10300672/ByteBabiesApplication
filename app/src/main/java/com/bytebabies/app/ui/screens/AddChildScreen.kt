package com.bytebabies.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bytebabies.app.data.Repo
import com.bytebabies.app.model.Parent
import com.bytebabies.app.model.Teacher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChildScreen(navController: NavController) {

    // ---------- Form State ----------
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var emergency by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // ---------- Parents ----------
    var parents by remember { mutableStateOf<List<Parent>>(emptyList()) }
    var selectedParent by remember { mutableStateOf<Parent?>(null) }
    var parentExpanded by remember { mutableStateOf(false) }

    // ---------- Teachers ----------
    var teachers by remember { mutableStateOf<List<Teacher>>(emptyList()) }
    var selectedTeacher by remember { mutableStateOf<Teacher?>(null) }
    var teacherExpanded by remember { mutableStateOf(false) }

    // ---------- Load data ----------
    LaunchedEffect(Unit) {
        Repo.fetchParents { parents = it }
        Repo.fetchTeachers { teachers = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Child") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ---------- Child Name ----------
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Child Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // ---------- Age ----------
            OutlinedTextField(
                value = age,
                onValueChange = { age = it.filter { c -> c.isDigit() } },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth()
            )

            // ---------- Emergency Contact ----------
            OutlinedTextField(
                value = emergency,
                onValueChange = { emergency = it },
                label = { Text("Emergency Contact") },
                modifier = Modifier.fillMaxWidth()
            )

            // ---------- Allergies ----------
            OutlinedTextField(
                value = allergies,
                onValueChange = { allergies = it },
                label = { Text("Allergies") },
                modifier = Modifier.fillMaxWidth()
            )

            // ---------- Medical Notes ----------
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Medical Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            // ---------- Parent Dropdown ----------
            Box {
                OutlinedTextField(
                    value = selectedParent?.name ?: "Select Parent",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Parent") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { parentExpanded = !parentExpanded }) {
                            Icon(
                                imageVector = if (parentExpanded) Icons.Filled.ArrowBack else Icons.Filled.ArrowBack,
                                contentDescription = "Expand"
                            )
                        }
                    }
                )

                DropdownMenu(
                    expanded = parentExpanded,
                    onDismissRequest = { parentExpanded = false }
                ) {
                    parents.forEach { parent ->
                        DropdownMenuItem(
                            text = { Text(parent.name) },
                            onClick = {
                                selectedParent = parent
                                parentExpanded = false
                            }
                        )
                    }
                }
            }

            // ---------- Teacher Dropdown ----------
            Box {
                OutlinedTextField(
                    value = selectedTeacher?.name ?: "Select Teacher",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Teacher") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { teacherExpanded = !teacherExpanded }) {
                            Icon(
                                imageVector = if (teacherExpanded) Icons.Filled.ArrowBack else Icons.Filled.ArrowBack,
                                contentDescription = "Expand"
                            )
                        }
                    }
                )

                DropdownMenu(
                    expanded = teacherExpanded,
                    onDismissRequest = { teacherExpanded = false }
                ) {
                    teachers.forEach { teacher ->
                        DropdownMenuItem(
                            text = { Text(teacher.name) },
                            onClick = {
                                selectedTeacher = teacher
                                teacherExpanded = false
                            }
                        )
                    }
                }
            }

            // ---------- Save Button ----------
            Button(
                onClick = {
                    if (name.isNotBlank() && selectedParent != null && selectedTeacher != null) {
                        Repo.createChild(
                            name,
                            selectedParent!!.id,
                            selectedTeacher!!.id,
                            age.toIntOrNull() ?: 0,
                            emergency,
                            allergies,
                            notes
                        ) { success, _ ->
                            if (success) navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Child")
            }
        }
    }
}
