package com.example.hatestuff3.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hatestuff3.data.local.database.post.PostEntity
import com.example.hatestuff3.ui.viewmodel.AuthViewModel
import com.example.hatestuff3.ui.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    postViewModel: PostViewModel,
    onLogout: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val allPosts by postViewModel.allPosts.collectAsState(initial = emptyList())

    // Filtrar solo los posts que pertenecen a este usuario
    val myPosts = allPosts.filter { it.userName == currentUser?.name }

    var isEditing by remember { mutableStateOf(false) }
    var editedBio by remember { mutableStateOf(currentUser?.bio ?: "") }

    // Estado para confirmar borrado desde el perfil
    var postToDelete by remember { mutableStateOf<PostEntity?>(null) }

    val HateRed = Color(0xFFD32F2F)
    val BackgroundBlack = Color.Black

    // DIÁLOGO DE CONFIRMACIÓN (Pilar 2 en Perfil)
    if (postToDelete != null) {
        AlertDialog(
            onDismissRequest = { postToDelete = null },
            containerColor = Color(0xFF121212),
            title = { Text("¿ELIMINAR TU QUEJA?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que quieres retirar este odio del abismo?", color = Color.Gray) },
            confirmButton = {
                Button(
                    onClick = {
                        postViewModel.deletePost(postToDelete!!)
                        postToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HateRed)
                ) {
                    Text("BORRAR", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { postToDelete = null }) {
                    Text("CANCELAR", color = Color.White)
                }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundBlack,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MI PERFIL", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF121212)),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = HateRed)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))

                // --- AVATAR Y RANGO ---
                Box(contentAlignment = Alignment.BottomEnd) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = if (currentUser?.role == "ADMIN") Color(0xFFFFD700) else HateRed,
                        modifier = Modifier.size(120.dp)
                    )
                    when (currentUser?.role) {
                        "ADMIN" -> Text("👑", fontSize = 28.sp)
                        "MOD" -> Text("🛡️", fontSize = 28.sp)
                    }
                }

                Text(
                    text = currentUser?.name ?: "Sin Nombre",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )

                // Etiqueta de Rol
                Surface(
                    color = when(currentUser?.role) {
                        "ADMIN" -> HateRed
                        "MOD" -> Color(0xFF00BCD4)
                        else -> Color(0xFF333333)
                    },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = currentUser?.role ?: "USER",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- SECCIÓN DE BIO ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("SOBRE MÍ", color = Color.Gray, fontWeight = FontWeight.Bold)
                            IconButton(onClick = {
                                if (isEditing) {
                                    authViewModel.updateProfile(currentUser!!.id, editedBio, null)
                                }
                                isEditing = !isEditing
                            }) {
                                Icon(
                                    imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = if (isEditing) Color.Green else Color.White
                                )
                            }
                        }

                        if (isEditing) {
                            TextField(
                                value = editedBio,
                                onValueChange = { editedBio = it },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Black,
                                    unfocusedContainerColor = Color.Black,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = HateRed
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                text = currentUser?.bio ?: "No hay biografía aún...",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "MIS QUEJAS (${myPosts.size})",
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // --- LISTA DE POSTS PROPIOS ---
            if (myPosts.isEmpty()) {
                item {
                    Text(
                        "No has esparcido odio todavía.",
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 40.dp)
                    )
                }
            } else {
                items(myPosts, key = { it.id }) { post ->
                    // Reutilizamos el PostItem que ya configuramos en HomeScreen
                    PostItem(
                        post = post,
                        currentUser = currentUser,
                        onLikeClick = { postViewModel.likePost(post) },
                        onCommentClick = { /* Opcional: abrir hilos */ },
                        onDeleteClick = { postToDelete = post }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}