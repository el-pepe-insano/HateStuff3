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
import com.example.hatestuff3.data.remote.dto.PostDto
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
    val allPosts by postViewModel.posts.collectAsState()
    val context = LocalContext.current

    val myPosts = remember(allPosts, currentUser) {
        val name = currentUser?.name
        if (name == null) emptyList() else allPosts.filter { it.userName == name }
    }

    var isEditingProfile by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedBio by remember { mutableStateOf("") }
    var editedAvatarUri by remember { mutableStateOf<String?>(null) }

    var postToDelete by remember { mutableStateOf<PostDto?>(null) }
    var postToEdit by remember { mutableStateOf<PostDto?>(null) }
    var editContent by remember { mutableStateOf("") }

    val hateRed = Color(0xFF8B0000)

    LaunchedEffect(currentUser) {
        currentUser?.let {
            if (!isEditingProfile) {
                editedName = it.name
                editedBio = it.bio ?: ""
                editedAvatarUri = it.profilePictureUri
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { editedAvatarUri = it.toString() }
    }

    postToDelete?.let { post ->
        AlertDialog(
            onDismissRequest = { postToDelete = null },
            containerColor = Color(0xFF121212),
            title = { Text("¿ELIMINAR?", color = Color.White) },
            confirmButton = {
                Button(onClick = {
                    post.id?.let { postViewModel.deletePost(it) }
                    postToDelete = null
                }, colors = ButtonDefaults.buttonColors(containerColor = hateRed)) {
                    Text("BORRAR")
                }
            }
        )
    }

    postToEdit?.let { post ->
        AlertDialog(
            onDismissRequest = { postToEdit = null },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("EDITAR", color = Color.White) },
            text = {
                OutlinedTextField(value = editContent, onValueChange = { editContent = it })
            },
            confirmButton = {
                Button(onClick = {
                    post.id?.let { postViewModel.updatePost(it, editContent) }
                    postToEdit = null
                }, colors = ButtonDefaults.buttonColors(containerColor = hateRed)) {
                    Text("GUARDAR")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MI PERFIL", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF121212)),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (isEditingProfile) {
                            currentUser?.let { user ->
                                val finalPath = if (editedAvatarUri != null && editedAvatarUri != user.profilePictureUri) {
                                    copyImageToInternalStorage(context, Uri.parse(editedAvatarUri))
                                } else editedAvatarUri

                                authViewModel.updateUserProfile(user.id, editedName, editedBio, finalPath)
                            }
                            isEditingProfile = false
                        } else isEditingProfile = true
                    }) {
                        Icon(if (isEditingProfile) Icons.Default.Check else Icons.Default.Edit, null, tint = Color.White)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = hateRed)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Box(contentAlignment = Alignment.BottomEnd) {
                    if (!editedAvatarUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = editedAvatarUri,
                            contentDescription = null,
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
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(2.dp, if (isEditingProfile) Color.Green else hateRed, CircleShape)
                                .clickable(enabled = isEditingProfile) {
                                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isEditingProfile) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        label = { Text("Nombre") }
                    )
                } else {
                    Text(currentUser?.name ?: "Cargando...", color = Color.White, style = MaterialTheme.typography.headlineMedium)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (myPosts.isEmpty()) {
                item { Text("Aún no hay quejas.", color = Color.Gray, modifier = Modifier.padding(20.dp)) }
            } else {
                items(myPosts, key = { it.id ?: 0L }) { post ->
                    PostItem(
                        post = post,
                        currentUser = currentUser,
                        onLikeClick = {
                            if (currentUser != null && post.id != null) {
                                postViewModel.likePost(post.id, currentUser!!.name)
                            }
                        },
                        onCommentClick = { /* Opcional en Perfil */ },
                        onDeleteClick = { postToDelete = post },
                        onEditClick = {
                            editContent = post.content
                            postToEdit = post
                        },
                        onUserClick = { /* Ya estamos aquí */ }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}