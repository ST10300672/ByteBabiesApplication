package com.bytebabies.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bytebabies.app.data.Repo
import com.bytebabies.app.model.Child
import com.bytebabies.app.ui.components.TopBar

@Composable
fun ParentChildrenScreen(navController: NavController) {
    var children by remember { mutableStateOf<List<Child>>(emptyList()) }

    // Fetch only children assigned to the current parent
    LaunchedEffect(Unit) {
        Repo.fetchChildren { allChildren ->
            children = allChildren.filter { it.parentId == Repo.currentParentId }
        }
    }

    Scaffold(
        topBar = {
            TopBar(title = "My Children")
        }
    ) { padding ->
        if (children.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No children assigned.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(children) { child ->
                    ChildRowParent(child = child)
                }
            }
        }
    }
}

@Composable
fun ChildRowParent(child: Child) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Name: ${child.name}", style = MaterialTheme.typography.titleMedium)
            Text("Age: ${child.age}")
            Text("Emergency Contact: ${child.emergencyContact}")
            Text("Allergies: ${child.allergies}")
            Text("Medical Notes: ${child.medicalNotes}")
        }
    }
}
