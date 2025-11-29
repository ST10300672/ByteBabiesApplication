package com.bytebabies.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bytebabies.app.data.Repo
import com.bytebabies.app.model.Teacher
import com.bytebabies.app.ui.components.OutlinedTextFieldFull
import com.bytebabies.app.ui.components.TopBar

@Composable
fun AdminTeachersScreen(nav: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var assignedClass by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    // For editing
    var showEditDialog by remember { mutableStateOf(false) }
    var editTeacher by remember { mutableStateOf<Teacher?>(null) }

    val teachers = remember { Repo.uiTeachers }

    // Load teachers at start
    LaunchedEffect(Unit) {
        Repo.fetchTeachers { list ->
            teachers.clear()
            teachers.addAll(list)
        }
    }

    Scaffold(
        topBar = { TopBar("Admin • Teachers", nav = nav) }
    ) { pad ->

        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
        ) {
            Text("Add Teacher", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextFieldFull(value = name, onVal = { name = it }, label = "Name")
            Spacer(Modifier.height(4.dp))
            OutlinedTextFieldFull(value = email, onVal = { email = it }, label = "Email")
            Spacer(Modifier.height(4.dp))
            OutlinedTextFieldFull(value = phone, onVal = { phone = it }, label = "Phone")
            Spacer(Modifier.height(4.dp))
            OutlinedTextFieldFull(value = assignedClass, onVal = { assignedClass = it }, label = "Assigned Class")

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        loading = true
                        Repo.createTeacher(name, email, phone, assignedClass) { ok, _ ->
                            loading = false
                            if (ok) {
                                Repo.fetchTeachers { list ->
                                    teachers.clear()
                                    teachers.addAll(list)
                                }
                                name = ""
                                email = ""
                                phone = ""
                                assignedClass = ""
                            }
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (loading) "Saving..." else "Save")
            }

            Spacer(Modifier.height(16.dp))

            Text("All Teachers", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            LazyColumn {
                items(teachers) { t: Teacher ->
                    ListItem(
                        headlineContent = { Text(t.name) },
                        supportingContent = { Text("${t.email} | ${t.phone} | ${t.assignedClass}") },
                        trailingContent = {
                            Row {
                                TextButton(onClick = {
                                    // Open edit dialog
                                    editTeacher = t
                                    showEditDialog = true
                                }) {
                                    Text("Edit")
                                }
                                TextButton(onClick = {
                                    Repo.deleteTeacher(t.id) { ok, _ ->
                                        if (ok) {
                                            Repo.fetchTeachers { list ->
                                                teachers.clear()
                                                teachers.addAll(list)
                                            }
                                        }
                                    }
                                }) {
                                    Text("Delete")
                                }
                            }
                        }
                    )
                    Divider()
                }
            }
        }
    }

    // ---------------------
    // EDIT DIALOG
    // ---------------------
    if (showEditDialog && editTeacher != null) {
        EditTeacherDialog(
            teacher = editTeacher!!,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                Repo.updateTeacher(
                    id = updated.id,
                    name = updated.name,
                    email = updated.email,
                    phone = updated.phone,
                    assignedClass = updated.assignedClass
                ) { ok, _ ->
                    if (ok) {
                        Repo.fetchTeachers { list ->
                            teachers.clear()
                            teachers.addAll(list)
                        }
                    }
                }
                showEditDialog = false
            }
        )
    }
}

@Composable
fun EditTeacherDialog(
    teacher: Teacher,
    onDismiss: () -> Unit,
    onSave: (Teacher) -> Unit
) {
    var name by remember { mutableStateOf(teacher.name) }
    var email by remember { mutableStateOf(teacher.email) }
    var phone by remember { mutableStateOf(teacher.phone) }
    var assignedClass by remember { mutableStateOf(teacher.assignedClass) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Teacher") },

        text = {
            Column {
                OutlinedTextFieldFull(value = name, onVal = { name = it }, label = "Name")
                Spacer(Modifier.height(4.dp))
                OutlinedTextFieldFull(value = email, onVal = { email = it }, label = "Email")
                Spacer(Modifier.height(4.dp))
                OutlinedTextFieldFull(value = phone, onVal = { phone = it }, label = "Phone")
                Spacer(Modifier.height(4.dp))
                OutlinedTextFieldFull(value = assignedClass, onVal = { assignedClass = it }, label = "Assigned Class")
            }
        },

        confirmButton = {
            TextButton(onClick = {
                onSave(
                    teacher.copy(
                        name = name,
                        email = email,
                        phone = phone,
                        assignedClass = assignedClass
                    )
                )
            }) {
                Text("Save")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
