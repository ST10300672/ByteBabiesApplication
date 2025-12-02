package com.bytebabies.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bytebabies.app.data.Repo
import com.bytebabies.app.model.Child
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AttendanceDashboardScreen(nav: NavHostController) {
    MaterialTheme(
        colorScheme = lightColorScheme()
    ) {
        AttendanceDashboardScreenContent()
    }
}

@Composable
fun AttendanceDashboardScreenContent() {
    var children by remember { mutableStateOf(listOf<Child>()) }
    var attendanceMap by remember { mutableStateOf(mutableMapOf<String, Boolean>()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showMessage by remember { mutableStateOf<Pair<String, Boolean>?>(null) }

    // Fetch data
    LaunchedEffect(Unit) {
        Repo.fetchChildren { list ->
            children = when (Repo.currentRole) {
                com.bytebabies.app.model.Role.ADMIN -> list
                else -> emptyList()
            }
            children.forEach { attendanceMap[it.id] = false }
        }

        Repo.fetchAttendanceForDate(selectedDate) { records ->
            records.forEach { record ->
                attendanceMap[record.childId] = record.present
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Attendance - ${selectedDate.format(DateTimeFormatter.ISO_DATE)}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(children) { child ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = child.name, style = MaterialTheme.typography.titleMedium)
                    Checkbox(
                        checked = attendanceMap[child.id] ?: false,
                        onCheckedChange = { isChecked ->
                            attendanceMap = attendanceMap.toMutableMap().also { it[child.id] = isChecked }
                        }
                    )
                }
            }
        }

        showMessage?.let { (msg, isSuccess) ->
            Text(
                text = msg,
                color = if (isSuccess) androidx.compose.ui.graphics.Color.Green else androidx.compose.ui.graphics.Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                var allSuccess = true
                children.forEach { child ->
                    val present = attendanceMap[child.id] ?: false
                    Repo.markAttendance(child.id, selectedDate, present) { success, _ ->
                        if (!success) allSuccess = false
                    }
                }
                showMessage = if (allSuccess) {
                    "Attendance saved successfully" to true
                } else {
                    "Failed to save some attendance records" to false
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save Attendance")
        }
    }
}
