package com.example.hatestuff3.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.VerifiedUser // <-- IMPORTAR ICONO
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hatestuff3.data.local.database.user.UserEntity
import com.example.hatestuff3.ui.viewmodel.AdminViewModel
import com.example.hatestuff3.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    adminViewModel: AdminViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val users by adminViewModel.allUsers.collectAsState(initial = emptyList())
    val message by adminViewModel.statusMessage.collectAsState()
    val myRole by authViewModel.currentUserRole.collectAsState()

    LaunchedEffect(Unit) {
        adminViewModel.fetchAllUsers(myRole)
    }

    LaunchedEffect(message) {
        if (message != null) {
            // Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            // adminViewModel.clearMessage()
        }
    }

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
                    onDelete = { adminViewModel.deleteUser(user, myRole) },
                    // <<--- CORRECCIÓN 1: Lógica de rotación de roles ---
                    onPromote = {
                        val nextRole = when (user.role) {
                            "USER" -> "MODERADOR"
                            "MODERADOR" -> "ADMIN"
                            "ADMIN" -> "USER"
                            else -> "USER" // Caso por defecto
                        }
                        adminViewModel.updateRole(user, nextRole, myRole)
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

                val roleColor = when(user.role) {
                    "ADMIN" -> Color.Yellow
                    "MODERADOR" -> Color.Cyan
                    else -> Color(0xFFD32F2F)
                }

                Text(
                    text = "ROL: ${user.role ?: "USER"}",
                    color = roleColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // <<--- CORRECCIÓN 2: Iconos y colores dinámicos según el rol ---
            IconButton(onClick = onPromote) {
                val icon = when(user.role) {
                    "ADMIN" -> Icons.Default.Security
                    "MODERADOR" -> Icons.Default.VerifiedUser
                    else -> Icons.Outlined.VerifiedUser // Usar el icono de contorno para USER
                }
                val tint = when(user.role) {
                    "ADMIN" -> Color.Yellow
                    "MODERADOR" -> Color.Cyan
                    else -> Color.Gray
                }
                Icon(icon, contentDescription = "Cambiar Rol", tint = tint)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
            }
        }
    }
}