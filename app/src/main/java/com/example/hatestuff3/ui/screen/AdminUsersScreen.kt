package com.example.hatestuff3.ui.screen



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hatestuff3.data.local.database.user.UserEntity
import com.example.hatestuff3.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(adminViewModel: AdminViewModel, onBack: () -> Unit) {
    val users by adminViewModel.allUsers.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GESTIÓN DE HATERS", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
            )
        },
        containerColor = Color.Black
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(users) { user ->
                UserItem(
                    user = user,
                    onDelete = { adminViewModel.deleteUser(user) },
                    onPromote = {
                        val nextRole = when(user.role) {
                            "USER" -> "MOD"
                            "MOD" -> "ADMIN"
                            else -> "USER"
                        }
                        adminViewModel.updateRole(user, nextRole)
                    }
                )
            }
        }
    }
}

@Composable
fun UserItem(user: UserEntity, onDelete: () -> Unit, onPromote: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text(user.email, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "ROL: ${user.role}",
                    color = if (user.role == "ADMIN") Color.Yellow else Color(0xFFD32F2F),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Botón para cambiar Rango
            IconButton(onClick = onPromote) {
                Icon(Icons.Default.Security, contentDescription = "Cambiar Rol", tint = Color.Cyan)
            }

            // Botón para Borrar (Baneo)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
            }
        }
    }
}