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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hatestuff3.data.local.database.post.PostEntity
import com.example.hatestuff3.data.local.database.user.UserEntity
import com.example.hatestuff3.ui.viewmodel.PostViewModel
import com.example.hatestuff3.ui.viewmodel.AuthViewModel
import com.example.hatestuff3.ui.components.AppTopBar

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
    val posts by postViewModel.filteredPosts.collectAsState()
    val searchQuery by postViewModel.searchQuery.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedPost by remember { mutableStateOf<PostEntity?>(null) }

    // Estados para borrar y editar
    var postToDelete by remember { mutableStateOf<PostEntity?>(null) }
    var postToEdit by remember { mutableStateOf<PostEntity?>(null) } // <--- NUEVO
    var editContent by remember { mutableStateOf("") } // <--- NUEVO

    val BackgroundColor = Color(0xFF000000)
    val HateRed = Color(0xFF8B0000)

    // --- DIÁLOGO DE BORRAR ---
    if (postToDelete != null) {
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
            text = { Text("Esta queja desaparecerá del abismo para siempre.", color = Color(0xFFB0B0B0)) },
            confirmButton = {
                Button(
                    onClick = {
                        postViewModel.deletePost(postToDelete!!)
                        postToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HateRed)
                ) { Text("SÍ, ELIMINAR", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { postToDelete = null }) { Text("CANCELAR", color = Color.White) }
            }
        )
    }

    // --- DIÁLOGO DE EDITAR (NUEVO) ---
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
                        cursorColor = HateRed,
                        focusedBorderColor = HateRed,
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
                    colors = ButtonDefaults.buttonColors(containerColor = HateRed)
                ) { Text("GUARDAR", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { postToEdit = null }) { Text("CANCELAR", color = Color.White) }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            val isAdmin = currentUser?.role == "ADMIN"
            AppTopBar(
                onOpenDrawer = onOpenDrawer,
                onLogin = if (currentUser == null) { {} } else null,
                onAdminClick = if (isAdmin) onNavigateToAdmin else null,
                searchQuery = searchQuery,
                onSearchQueryChange = { postViewModel.onSearchQueryChanged(it) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePostClick,
                containerColor = HateRed,
                contentColor = Color.White,
                shape = CircleShape
            ) { Icon(Icons.Default.Add, contentDescription = "Nueva Queja") }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundColor)
        ) {
            if (posts.isEmpty()) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    val icon = if (searchQuery.isNotEmpty()) Icons.Default.SearchOff else Icons.Default.SentimentDissatisfied
                    val text = if (searchQuery.isNotEmpty()) "No se encontró a ese usuario." else "Nadie se ha quejado aún..."

                    Icon(icon, null, tint = Color.DarkGray, modifier = Modifier.size(64.dp))
                    Text(text, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(16.dp)) {
                    items(posts, key = { it.id }) { post ->
                        PostItem(
                            post = post,
                            currentUser = currentUser,
                            onLikeClick = { postViewModel.likePost(post) },
                            onCommentClick = {
                                selectedPost = post
                                showBottomSheet = true
                            },
                            onDeleteClick = { postToDelete = post },
                            // --- LÓGICA DE EDICIÓN ---
                            onEditClick = {
                                editContent = post.content
                                postToEdit = post
                            },
                            onUserClick = { userName -> onUserClick(userName) }
                        )
                    }
                }
            }
        }

        if (showBottomSheet && selectedPost != null && currentUser != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF121212)
            ) {
                CommentSection(post = selectedPost!!, viewModel = postViewModel, currentUser = currentUser!!)
            }
        }
    }
}

@Composable
fun PostItem(
    post: PostEntity,
    currentUser: UserEntity?,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit, // Parámetro para editar
    onUserClick: (String) -> Unit = {}
) {
    val HateRed = Color(0xFF8B0000)

    val scale by animateFloatAsState(
        targetValue = if (post.likes > 0) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "likeScale"
    )

    //SEGURIDAD
    val adminNames = listOf("admin", "administrador", "root", "system", "hatestuffgod", "pro", "profe", "dios")

    val isAuthorAdmin = post.userName.lowercase() in adminNames
    val isAuthorMod = post.userName.lowercase().contains("mod")

    val canDelete = when {
        currentUser == null -> false
        currentUser.name == post.userName -> true
        currentUser.role == "ADMIN" -> true
        currentUser.role == "MOD" -> !isAuthorAdmin
        else -> false
    }

    // Solo el autor original puede editar el texto
    val canEdit = currentUser != null && currentUser.name == post.userName

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A).copy(alpha = 0.85f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Icon(Icons.Default.Person, null, tint = HateRed, modifier = Modifier.size(40.dp))
                    if (isAuthorAdmin) Text("👑", fontSize = 12.sp)
                    else if (isAuthorMod) Text("🛡️", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onUserClick(post.userName) }
                            .padding(2.dp)
                    ) {
                        Text(text = post.userName, color = Color.White, fontWeight = FontWeight.Bold)
                        if (isAuthorAdmin) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ADMIN", color = HateRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        } else if (isAuthorMod) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("MOD", color = Color(0xFF00BCD4), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(text = "Esparciendo odio...", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Spacer(modifier = Modifier.weight(1f))

                // BOTONES DE ACCIÓN (Editar y Borrar)
                Row {
                    if (canEdit) {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Gray)
                        }
                    }
                    if (canDelete) {
                        IconButton(onClick = onDeleteClick) {
                            Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.DarkGray)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = post.content, color = Color.White, style = MaterialTheme.typography.bodyLarge)

            if (!post.imageUri.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = post.imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = if (post.likes > 0) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = HateRed,
                        modifier = Modifier.scale(scale)
                    )
                }
                Text(text = "${post.likes} odios", color = Color.Gray, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(24.dp))
                IconButton(onClick = onCommentClick) { Icon(Icons.Default.Comment, null, tint = Color.White) }
                Text(text = "Comentar", color = Color.Gray)
            }
        }
    }
}

@Composable
fun CommentSection(post: PostEntity, viewModel: PostViewModel, currentUser: UserEntity) {
    val comments by viewModel.getComments(post.id).collectAsState(initial = emptyList())
    var commentText by remember { mutableStateOf("") }
    val HateRed = Color(0xFF8B0000)

    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f).imePadding().padding(16.dp)) {
        Text("RESPUESTAS (${comments.size})", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), reverseLayout = false) {
            items(comments) { comment ->
                CommentBubble(comment = comment, currentUser = currentUser, onDeleteClick = { viewModel.deleteComment(comment) })
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)) {
            TextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Añade más leña al fuego...", color = Color.Gray) },
                modifier = Modifier.weight(1f).border(1.dp, Brush.verticalGradient(listOf(Color.Transparent, HateRed.copy(alpha = 0.3f))), RoundedCornerShape(24.dp)),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFF0A0A0A), unfocusedContainerColor = Color(0xFF0A0A0A), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { if (commentText.isNotBlank()) { viewModel.sendComment(post.id, commentText, currentUser.name); commentText = "" } },
                modifier = Modifier.background(HateRed, CircleShape)
            ) { Icon(Icons.Default.Send, null, tint = Color.White) }
        }
    }
}

@Composable
fun CommentBubble(comment: com.example.hatestuff3.data.local.database.post.CommentEntity, currentUser: UserEntity?, onDeleteClick: () -> Unit) {
    val adminNames = listOf("admin", "administrador", "root", "system", "hatestuffgod", "Dios", "profe","pro")

    val isAuthorAdmin = comment.userName.lowercase() in adminNames
    val isAuthorMod = comment.userName.lowercase().contains("mod")

    val canDelete = when {
        currentUser == null -> false
        currentUser.name == comment.userName -> true
        currentUser.role == "ADMIN" -> true
        currentUser.role == "MOD" -> !isAuthorAdmin
        else -> false
    }

    val borderColor = when {
        isAuthorAdmin -> Color(0xFF8B0000)
        isAuthorMod -> Color(0xFF00BCD4)
        else -> Color.Transparent
    }

    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.Top) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Icon(Icons.Default.Person, null, tint = if(isAuthorAdmin || isAuthorMod) borderColor else Color.Gray, modifier = Modifier.size(35.dp))
            if(isAuthorAdmin) Text("👑", fontSize = 10.sp) else if(isAuthorMod) Text("🛡️", fontSize = 10.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .background(if(isAuthorAdmin) Color(0xFF2A0000) else if(isAuthorMod) Color(0xFF001A1A) else Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                .border(if(isAuthorAdmin || isAuthorMod) 1.dp else 0.dp, borderColor, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.userName,
                    color = if(isAuthorAdmin) Color(0xFFFFD700) else if(isAuthorMod) Color(0xFF00E5FF) else Color(0xFFFF8A80),
                    fontWeight = FontWeight.Black, fontSize = 12.sp
                )
                if (isAuthorAdmin) Text(" • ADMIN", color = Color(0xFF8B0000), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
            }
            Text(text = comment.text, color = Color.White, style = MaterialTheme.typography.bodyMedium)
        }
        if (canDelete) {
            IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, null, tint = Color.DarkGray, modifier = Modifier.size(16.dp)) }
        }
    }
}