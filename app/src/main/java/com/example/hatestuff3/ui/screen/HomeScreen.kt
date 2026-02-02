
package com.example.hatestuff3.ui.screen

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hatestuff3.data.local.database.user.UserEntity
import com.example.hatestuff3.data.remote.dto.CommentDto
import com.example.hatestuff3.data.remote.dto.PostDto
import com.example.hatestuff3.ui.components.AppTopBar
import com.example.hatestuff3.ui.viewmodel.AuthViewModel
import com.example.hatestuff3.ui.viewmodel.PostViewModel

private const val IMAGE_BASE_URL = "http://192.168.0.165:8082"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    postViewModel: PostViewModel,
    authViewModel: AuthViewModel,
    onCreatePostClick: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onUserClick: (String) -> Unit,
    onOpenDrawer: () -> Unit = {}
) {
    val posts by postViewModel.posts.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val filteredPosts = remember(posts, searchQuery) {
        if (searchQuery.isEmpty()) posts
        else posts.filter {
            it.userName.contains(searchQuery, ignoreCase = true) ||
                    it.content.contains(searchQuery, ignoreCase = true)
        }
    }

    // Estados para diálogos y BottomSheet
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedPost by remember { mutableStateOf<PostDto?>(null) }
    var postToDelete by remember { mutableStateOf<PostDto?>(null) }
    var postToEdit by remember { mutableStateOf<PostDto?>(null) }
    var editContent by remember { mutableStateOf("") }

    val HateRed = Color(0xFF8B0000)
    val BackgroundColor = Color(0xFF000000)

    // DIÁLOGO: Eliminar
    postToDelete?.let { post ->
        AlertDialog(
            onDismissRequest = { postToDelete = null },
            containerColor = Color(0xFF1A1A1A),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = HateRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("¿BORRAR ODIO?", color = Color.White, fontWeight = FontWeight.ExtraBold)
                }
            },
            text = { Text("Esta queja desaparecerá del abismo.", color = Color(0xFFB0B0B0)) },
            confirmButton = {
                Button(
                    onClick = {
                        post.id?.let { postViewModel.deletePost(it) }
                        postToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HateRed)
                ) { Text("SÍ, ELIMINAR", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { postToDelete = null }) { Text("CANCELAR", color = Color.White) }
            }
        )
    }

    // DIÁLOGO: Editar
    postToEdit?.let { post ->
        AlertDialog(
            onDismissRequest = { postToEdit = null },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("CORREGIR ODIO", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = editContent,
                    onValueChange = { editContent = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = HateRed,
                        focusedBorderColor = HateRed
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        post.id?.let { postViewModel.updatePost(it, editContent) }
                        postToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HateRed)
                ) { Text("GUARDAR", color = Color.White) }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            AppTopBar(
                onOpenDrawer = onOpenDrawer,
                onLogin = if (currentUser == null) { {} } else null,
                onAdminClick = if (currentUser?.role == "ADMIN") onNavigateToAdmin else null,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )
        },
        floatingActionButton = {
            if (currentUser != null) {
                FloatingActionButton(
                    onClick = onCreatePostClick,
                    containerColor = HateRed,
                    contentColor = Color.White,
                    shape = CircleShape
                ) { Icon(Icons.Default.Add, contentDescription = "Nueva Queja") }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(BackgroundColor)) {
            if (filteredPosts.isEmpty()) {
                Text("Nadie se ha quejado aún...", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(filteredPosts, key = { it.id ?: 0L }) { post ->
                        PostItem(
                            post = post,
                            currentUser = currentUser,
                            onLikeClick = {
                                if (currentUser != null && post.id != null) {
                                    postViewModel.likePost(post.id, currentUser!!.name)
                                }
                            },
                            onCommentClick = {
                                selectedPost = post
                                post.id?.let { postViewModel.loadComments(it) }
                                showBottomSheet = true
                            },
                            onDeleteClick = { postToDelete = post },
                            onEditClick = {
                                editContent = post.content
                                postToEdit = post
                            },
                            onUserClick = onUserClick
                        )
                    }
                }
            }
        }

        if (showBottomSheet && selectedPost != null && currentUser != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                containerColor = Color(0xFF121212)
            ) {
                CommentSection(post = selectedPost!!, viewModel = postViewModel, currentUser = currentUser!!)
            }
        }
    }
}

@Composable
fun PostItem(
    post: PostDto,
    currentUser: UserEntity?,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onUserClick: (String) -> Unit
) {
    val HateRed = Color(0xFF8B0000)
    val isLikedByCurrentUser = currentUser != null && post.likedBy.contains(currentUser.name)

    val scale by animateFloatAsState(
        targetValue = if (isLikedByCurrentUser) 1.2f else 1.0f,
        label = "likeScale"
    )

    val canDelete = currentUser != null && (currentUser.name == post.userName || currentUser.role == "ADMIN")
    val canEdit = currentUser != null && currentUser.name == post.userName

    Card(
        modifier = Modifier.fillMaxWidth().border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, tint = HateRed, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    post.userName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onUserClick(post.userName) }
                )
                Spacer(modifier = Modifier.weight(1f))
                if (canEdit) IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, null, tint = Color.Gray) }
                if (canDelete) IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, null, tint = Color.Gray) }
            }

            Text(post.content, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))

            post.imageUri?.let { uri ->
                val fullUrl = if (uri.startsWith("/")) "$IMAGE_BASE_URL$uri" else "$IMAGE_BASE_URL/$uri"
                AsyncImage(
                    model = fullUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLikeClick) {
                    Icon(if (isLikedByCurrentUser) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = HateRed, modifier = Modifier.scale(scale))
                }
                Text("${post.likes} odios", color = Color.Gray)
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = onCommentClick) {
                    Icon(Icons.Default.Comment, null, tint = Color.White)
                }
                Text("Responder", color = Color.Gray)
            }
        }
    }
}

@Composable
fun CommentSection(post: PostDto, viewModel: PostViewModel, currentUser: UserEntity) {
    val comments by viewModel.activePostComments.collectAsState()
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f).padding(16.dp)) {
        Text("RESPUESTAS", color = Color.White, fontWeight = FontWeight.Bold)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(comments) { comment ->
                CommentBubble(comment, currentUser) {
                    comment.id?.let { id -> post.id?.let { pId -> viewModel.deleteComment(id, pId) } }
                }
            }
        }
        Row {
            TextField(value = text, onValueChange = { text = it }, modifier = Modifier.weight(1f))
            IconButton(onClick = {
                if (text.isNotBlank() && post.id != null) {
                    viewModel.sendComment(post.id, text, currentUser.name)
                    text = ""
                }
            }) { Icon(Icons.Default.Send, null, tint = Color(0xFF8B0000)) }
        }
    }
}

@Composable
fun CommentBubble(comment: CommentDto, currentUser: UserEntity, onDeleteClick: () -> Unit) {
    Row(modifier = Modifier.padding(8.dp).background(Color(0xFF252525), RoundedCornerShape(8.dp)).padding(8.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(comment.userName, color = Color.Red, fontSize = 12.sp)
            Text(comment.content, color = Color.White)
        }
        if (currentUser.name == comment.userName || currentUser.role == "ADMIN") {
            IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, null, tint = Color.Gray, modifier = Modifier.size(16.dp)) }
        }
    }
}