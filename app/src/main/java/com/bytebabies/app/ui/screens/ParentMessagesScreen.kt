package com.bytebabies.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bytebabies.app.data.Repo
import com.bytebabies.app.model.Message
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentMessagesScreen(navController: NavController) {
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var newMessage by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    // Fetch messages
    LaunchedEffect(Unit) {
        Repo.fetchParentMessages { list ->
            messages = list.filter { it.fromParentId == Repo.currentParentId || it.toAdmin } // Show relevant
                .sortedBy { it.timestamp } // Oldest first
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    val isParent = message.fromParentId == Repo.currentParentId
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isParent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message.timestamp,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = newMessage,
                    onValueChange = { newMessage = it },
                    placeholder = { Text("Type a message...") },
                    singleLine = true
                )

                IconButton(
                    onClick = {
                        if (newMessage.isNotBlank() && !isSending) {
                            isSending = true
                            Repo.sendParentMessage(newMessage) { success, error ->
                                if (success) {
                                    newMessage = ""
                                    // Refresh messages
                                    Repo.fetchParentMessages { list ->
                                        messages = list.filter { it.fromParentId == Repo.currentParentId || it.toAdmin }
                                            .sortedBy { it.timestamp }
                                    }
                                }
                                isSending = false
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }

            }
        }
    }
}
