package com.example.hatestuff3.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val allPosts by postViewModel.allPosts.collectAsState(initial = emptyList())
    val context = LocalContext.current

    val myPosts = allPosts.filter { it.userName == currentUser?.name }

    // Estados de edición de PERFIL
    var isEditingProfile by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedBio by remember { mutableStateOf("") }
    var editedAvatarUri by remember { mutableStateOf<String?>(null) }

    // Estados para borrar/editar POSTS
    var postToDelete by remember { mutableStateOf<PostEntity?>(null) }
    var postToEdit by remember { mutableStateOf<PostEntity?>(null) } // Para editar post
    var editContent by remember { mutableStateOf("") } // Texto del post a editar

    val hateRed = Color(0xFF8B0000)
    val backgroundBlack = Color.Black

    LaunchedEffect(currentUser, isEditingProfile) {
        if (!isEditingProfile && currentUser != null) {
            editedName = currentUser!!.name
            editedBio = currentUser!!.bio ?: ""
            editedAvatarUri = currentUser!!.profilePictureUri
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { editedAvatarUri = it.toString() }
    }

    // --- DIÁLOGO BORRAR POST ---
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
                    colors = ButtonDefaults.buttonColors(containerColor = hateRed)
                ) { Text("BORRAR", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { postToDelete = null }) { Text("CANCELAR", color = Color.White) }
            }
        )
    }

    // --- DIÁLOGO EDITAR POST ---
    if (postToEdit != null) {
        AlertDialog(
            onDismissRequest = { postToEdit = null },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("CORREGIR ODIO", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editContent,
                    onValueChange = { editContent = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = hateRed,
                        focusedBorderColor = hateRed,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        postViewModel.updatePost(postToEdit!!, editContent)
                        postToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = hateRed)
                ) { Text("GUARDAR", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { postToEdit = null }) { Text("CANCELAR", color = Color.White) }
            }
        )
    }

    Scaffold(
        containerColor = backgroundBlack,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MI PERFIL", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF121212)),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (isEditingProfile) {
                            currentUser?.let { user ->
                                val finalAvatarPath = if (editedAvatarUri != null && editedAvatarUri != user.profilePictureUri) {
                                    copyImageToInternalStorage(context, Uri.parse(editedAvatarUri))
                                } else {
                                    editedAvatarUri
                                }
                                authViewModel.updateUserProfile(
                                    userId = user.id,
                                    newName = editedName,
                                    newBio = editedBio,
                                    newAvatarUri = finalAvatarPath
                                )
                            }
                            isEditingProfile = false
                        } else {
                            isEditingProfile = true
                        }
                    }) {
                        Icon(
                            imageVector = if (isEditingProfile) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditingProfile) "Guardar" else "Editar Perfil",
                            tint = if (isEditingProfile) Color.Green else Color.White
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión", tint = hateRed)
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
                // AVATAR
                Box(contentAlignment = Alignment.BottomEnd) {
                    if (editedAvatarUri != null) {
                        AsyncImage(
                            model = editedAvatarUri,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(2.dp, if (isEditingProfile) Color.Green else hateRed, CircleShape)
                                .clickable(enabled = isEditingProfile) {
                                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = if (currentUser?.role == "ADMIN") Color(0xFFFFD700) else hateRed,
                            modifier = Modifier
                                .size(120.dp)
                                .clickable(enabled = isEditingProfile) {
                                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }
                        )
                    }
                    if (isEditingProfile) {
                        Box(modifier = Modifier
                            .offset(x = 4.dp, y = 4.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .border(1.dp, Color.White, CircleShape)
                            .padding(6.dp)) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Cambiar Foto", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        when (currentUser?.role) {
                            "ADMIN" -> Text("👑", fontSize = 28.sp)
                            "MOD" -> Text("🛡️", fontSize = 28.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // NOMBRE
                if (isEditingProfile) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Nombre") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            cursorColor = hateRed, focusedBorderColor = hateRed, unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = hateRed, unfocusedLabelColor = Color.Gray
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

                if (!isEditingProfile) {
                    Surface(
                        color = when(currentUser?.role) { "ADMIN" -> hateRed; "MOD" -> Color(0xFF00BCD4); else -> Color(0xFF333333) },
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

                // BIO
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("SOBRE MÍ", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isEditingProfile) {
                            OutlinedTextField(
                                value = editedBio,
                                onValueChange = { editedBio = it },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                    cursorColor = hateRed, focusedBorderColor = hateRed, unfocusedBorderColor = Color.Transparent
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
                Text("MIS QUEJAS (${myPosts.size})", modifier = Modifier.fillMaxWidth(), color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (myPosts.isEmpty()) {
                item {
                    Text("No has esparcido odio todavía.", color = Color.DarkGray, modifier = Modifier.padding(top = 40.dp))
                }
            } else {
                items(myPosts, key = { it.id }) { post ->
                    PostItem(
                        post = post,
                        currentUser = currentUser,
                        onLikeClick = { postViewModel.likePost(post) },
                        onCommentClick = { }, // En perfil quizás no mostramos comentarios por ahora
                        onDeleteClick = { postToDelete = post },
                        onUserClick = { _ -> },
                        onEditClick = {
                            editContent = post.content
                            postToEdit = post
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}