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
import com.bytebabies.app.model.Message
import com.google.ai.client.generativeai.type.content
import java.time.format.DateTimeFormatter

@Composable
fun AdminMessagesScreen(nav: NavHostController) {
    var messages by remember { mutableStateOf(listOf<Message>()) }

    LaunchedEffect(Unit) {
        Repo.fetchParentMessages { list -> messages = list }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Parent Messages",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(messages) { msg ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(msg.content, style = MaterialTheme.typography.bodyLarge)
                        Text("From Parent ID: ${msg.fromParentId ?: "Unknown"}",
                            style = MaterialTheme.typography.bodySmall)
                        Text("Sent: ${msg.timestamp.format(DateTimeFormatter.ISO_DATE)}",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
