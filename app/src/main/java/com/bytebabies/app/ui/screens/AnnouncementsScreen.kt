package com.bytebabies.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.bytebabies.app.data.Repo
import com.bytebabies.app.model.Message
import java.time.format.DateTimeFormatter

@Composable
fun AnnouncementsScreen(nav: NavHostController) {
    var announcements by remember { mutableStateOf(listOf<Message>()) }
    var showDialog by remember { mutableStateOf(false) }
    var newContent by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf<Pair<String, Boolean>?>(null) }

    // Fetch announcements on load
    LaunchedEffect(Unit) {
        Repo.fetchAnnouncements { list -> announcements = list }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Announcements",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(announcements) { msg ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(msg.content, style = MaterialTheme.typography.bodyLarge)
                        Text("Posted: ${msg.timestamp.format(DateTimeFormatter.ISO_DATE)}",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Add Announcement")
        }

        showMessage?.let { (msg, success) ->
            Text(
                text = msg,
                color = if (success) androidx.compose.ui.graphics.Color.Green else androidx.compose.ui.graphics.Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false; newContent = "" }) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("New Announcement", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newContent,
                        onValueChange = { newContent = it },
                        label = { Text("Content") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { showDialog = false; newContent = "" }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (newContent.isNotBlank()) {
                                Repo.postAnnouncement(newContent) { success, msg ->
                                    if (success) {
                                        Repo.fetchAnnouncements { announcements = it }
                                        showMessage = "Announcement added" to true
                                        showDialog = false
                                        newContent = ""
                                    } else showMessage = msg?.let { it to false } ?: ("Failed to post" to false)
                                }
                            }
                        }) { Text("Post") }
                    }
                }
            }
        }
    }
}
