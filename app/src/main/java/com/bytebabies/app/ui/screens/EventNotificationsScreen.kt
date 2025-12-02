package com.bytebabies.app.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.bytebabies.app.data.Repo
import com.bytebabies.app.model.Event
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun EventNotificationsScreen(nav: NavHostController) {
    var events by remember { mutableStateOf(listOf<Event>()) }
    var showDialog by remember { mutableStateOf(false) }
    var editEvent by remember { mutableStateOf<Event?>(null) }
    var showMessage by remember { mutableStateOf<Pair<String, Boolean>?>(null) }

    LaunchedEffect(Unit) {
        Repo.fetchEvents { list -> events = list }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Events",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(events) { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { editEvent = event }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(event.title, style = MaterialTheme.typography.titleMedium)
                        Text(event.description, style = MaterialTheme.typography.bodyMedium)
                        Text("Date: ${event.date.format(DateTimeFormatter.ISO_DATE)}", style = MaterialTheme.typography.bodySmall)
                        Text("Location: ${event.location}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        showMessage?.let { (msg, success) ->
            Text(
                text = msg,
                color = if (success) androidx.compose.ui.graphics.Color.Green else androidx.compose.ui.graphics.Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { editEvent = null; showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Event")
        }
    }

    if (showDialog || editEvent != null) {
        AddEditEventDialog(
            event = editEvent,
            onDismiss = { showDialog = false; editEvent = null },
            onSave = { e ->
                if (editEvent == null) {
                    // Create event
                    Repo.createEvent(e.title, e.description, e.date, e.location) { success, msg ->
                        if (success) {
                            Repo.fetchEvents { events = it }
                            showMessage = "Event added successfully" to true
                            showDialog = false
                        } else {
                            showMessage = msg?.let { it to false } ?: ("Failed to add event" to false)
                        }
                    }
                } else {
                    // Update event
                    Repo.updateEvent(e.id, e.title, e.description, e.date, e.location) { success, msg ->
                        if (success) {
                            Repo.fetchEvents { events = it }
                            showMessage = "Event updated successfully" to true
                            showDialog = false
                            editEvent = null
                        } else {
                            showMessage = msg?.let { it to false } ?: ("Failed to update event" to false)
                        }
                    }
                }
            },
            onDelete = editEvent?.let { event ->
                {
                    Repo.deleteEvent(event.id) { success, msg ->
                        if (success) {
                            Repo.fetchEvents { events = it }
                            showMessage = "Event deleted successfully" to true
                            editEvent = null
                            showDialog = false
                        } else {
                            showMessage = msg?.let { it to false } ?: ("Failed to delete event" to false)
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun AddEditEventDialog(
    event: Event? = null,
    onDismiss: () -> Unit,
    onSave: (Event) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var title by remember { mutableStateOf(event?.title ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var location by remember { mutableStateOf(event?.location ?: "") }
    var date by remember { mutableStateOf(event?.date ?: LocalDate.now()) }

    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val calendar = Calendar.getInstance().apply {
            set(date.year, date.monthValue - 1, date.dayOfMonth)
        }

        DatePickerDialog(
            context,
            { _, year, month, day ->
                date = LocalDate.of(year, month + 1, day)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    if (event == null) "Add Event" else "Edit Event",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = date.format(DateTimeFormatter.ISO_DATE),
                    onValueChange = {},
                    label = { Text("Date") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    onDelete?.let {
                        TextButton(onClick = it) { Text("Delete") }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onSave(
                            Event(
                                id = event?.id ?: "",
                                title = title,
                                description = description,
                                date = date,
                                location = location
                            )
                        )
                    }) { Text("Save") }
                }
            }
        }
    }
}
