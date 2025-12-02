package com.bytebabies.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bytebabies.app.data.Repo
import com.bytebabies.app.model.AttendanceRecord
import com.bytebabies.app.model.Child
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentAttendanceScreen(navController: NavController) {

    var children by remember { mutableStateOf<List<Child>>(emptyList()) }
    var attendanceRecords by remember { mutableStateOf<Map<String, List<AttendanceRecord>>>(emptyMap()) }
    val parentId = Repo.currentParentId

    LaunchedEffect(parentId) {
        if (parentId != null) {
            // Fetch only children assigned to this parent
            Repo.fetchChildren { allChildren ->
                children = allChildren.filter { it.parentId == parentId }

                // For each child, fetch their attendance
                allChildren.filter { it.parentId == parentId }.forEach { child ->
                    Repo.fetchAttendanceForChild(child.id) { records ->
                        attendanceRecords = attendanceRecords + (child.id to records)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Children's Attendance") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(children) { child ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(text = "Name: ${child.name}", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        val records = attendanceRecords[child.id] ?: emptyList()
                        if (records.isEmpty()) {
                            Text("No attendance records yet")
                        } else {
                            records.forEach { record ->
                                Text(
                                    "${record.date.format(DateTimeFormatter.ISO_DATE)}: ${if (record.present) "Present" else "Absent"}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
