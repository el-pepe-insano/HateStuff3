package com.example.hatestuff3.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hatestuff3.data.local.database.post.PostEntity
import com.example.hatestuff3.ui.viewmodel.AuthViewModel
import com.example.hatestuff3.ui.viewmodel.PostViewModel
import com.example.hatestuff3.copyImageToInternalStorage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    postViewModel: PostViewModel,
    onLogout: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val allPosts by postViewModel.allPosts.collectAsState(initial = emptyList())
    val context = LocalContext.current // Necesario para guardar la imagen

    // Filtrar posts del usuario
    val myPosts = allPosts.filter { it.userName == currentUser?.name }

    // --- ESTADOS DE EDICIÓN ---
    var isEditing by remember { mutableStateOf(false) }

    // Variables temporales para editar
    var editedName by remember { mutableStateOf("") }
    var editedBio by remember { mutableStateOf("") }
    var editedAvatarUri by remember { mutableStateOf<String?>(null) }

    // Sincronizar datos cuando carga el usuario o cambia el modo edición
    LaunchedEffect(currentUser, isEditing) {
        if (!isEditing && currentUser != null) {
            editedName = currentUser!!.name
            editedBio = currentUser!!.bio ?: ""
            editedAvatarUri = currentUser!!.profilePictureUri
        }
    }

    // --- SELECTOR DE IMAGEN (GALERÍA) ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            // Guardamos temporalmente la URI elegida para mostrarla antes de guardar
            editedAvatarUri = it.toString()
        }
    }

    // Estado para confirmar borrado de post
    var postToDelete by remember { mutableStateOf<PostEntity?>(null) }

    val HateRed = Color(0xFF8B0000) // Rojo Sangre
    val BackgroundBlack = Color.Black

    // DIÁLOGO DE CONFIRMACIÓN DE BORRADO
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
                    // Botón Editar / Guardar
                    IconButton(onClick = {
                        if (isEditing) {
                            // --- LOGICA DE GUARDADO ---
                            currentUser?.let { user ->
                                // 1. Si la imagen cambió y no es nula, la guardamos en almacenamiento interno
                                val finalAvatarPath = if (editedAvatarUri != null && editedAvatarUri != user.profilePictureUri) {
                                    copyImageToInternalStorage(context, Uri.parse(editedAvatarUri))
                                } else {
                                    editedAvatarUri // Mantenemos la que estaba
                                }

                                // 2. Llamamos al ViewModel
                                authViewModel.updateUserProfile(
                                    userId = user.id,
                                    newName = editedName,
                                    newBio = editedBio,
                                    newAvatarUri = finalAvatarPath
                                )
                            }
                            isEditing = false
                        } else {
                            // Entrar en modo edición
                            isEditing = true
                        }
                    }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Guardar" else "Editar",
                            tint = if (isEditing) Color.Green else Color.White
                        )
                    }

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

                // --- AVATAR (EDITABLE) ---
                Box(contentAlignment = Alignment.BottomEnd) {
                    // Imagen Circular
                    if (editedAvatarUri != null) {
                        AsyncImage(
                            model = editedAvatarUri,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(2.dp, if (isEditing) Color.Green else HateRed, CircleShape)
                                .clickable(enabled = isEditing) {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Icono por defecto si no hay foto
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = if (currentUser?.role == "ADMIN") Color(0xFFFFD700) else HateRed,
                            modifier = Modifier
                                .size(120.dp)
                                .clickable(enabled = isEditing) {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                        )
                    }

                    // Icono de "Cámara" superpuesto si se está editando
                    if (isEditing) {
                        Box(
                            modifier = Modifier
                                .offset(x = 4.dp, y = 4.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                                .border(1.dp, Color.White, CircleShape)
                                .padding(6.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Cambiar Foto", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        // Icono de Rol si NO se está editando
                        when (currentUser?.role) {
                            "ADMIN" -> Text("👑", fontSize = 28.sp)
                            "MOD" -> Text("🛡️", fontSize = 28.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- NOMBRE (EDITABLE) ---
                if (isEditing) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Nombre de Usuario") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = HateRed,
                            focusedBorderColor = HateRed,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = HateRed,
                            unfocusedLabelColor = Color.Gray
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                } else {
                    Text(
                        text = currentUser?.name ?: "Sin Nombre",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }

                // Etiqueta de Rol
                if (!isEditing) {
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- BIO / DESCRIPCIÓN (EDITABLE) ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("SOBRE MÍ", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (isEditing) {
                            OutlinedTextField(
                                value = editedBio,
                                onValueChange = { editedBio = it },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = HateRed,
                                    focusedBorderColor = HateRed,
                                    unfocusedBorderColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                text = currentUser?.bio?.ifBlank { "Sin descripción..." } ?: "Sin descripción...",
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
                    // IMPORTANTE: Asegúrate de que PostItem maneje imágenes correctamente
                    PostItem(
                        post = post,
                        currentUser = currentUser,
                        onLikeClick = { postViewModel.likePost(post) },
                        onCommentClick = { },
                        onDeleteClick = { postToDelete = post },
                        onUserClick = {} // En mi propio perfil, clic al user no hace nada o recarga
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}