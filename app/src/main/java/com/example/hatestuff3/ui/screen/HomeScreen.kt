package com.example.hatestuff3.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.hatestuff3.data.local.database.post.PostEntity
import com.example.hatestuff3.data.local.database.user.UserEntity
import com.example.hatestuff3.ui.viewmodel.PostViewModel
import com.example.hatestuff3.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    postViewModel: PostViewModel,
    authViewModel: AuthViewModel,
    onCreatePostClick: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val posts by postViewModel.allPosts.collectAsState(initial = emptyList())
    val currentUser by authViewModel.currentUser.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedPost by remember { mutableStateOf<PostEntity?>(null) }

    val BackgroundColor = Color.Black
    val HateRed = Color(0xFFD32F2F)

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("HATE STUFF", color = HateRed, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF121212)
                ),
                actions = {
                    // ESCUDO AHORA EN BLANCO
                    if (currentUser?.role == "ADMIN") {
                        IconButton(onClick = onNavigateToAdmin) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Panel Admin",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePostClick,
                containerColor = HateRed,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Queja")
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundColor)
        ) {
            if (posts.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.SentimentDissatisfied,
                        contentDescription = "Vacio",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Nadie se ha quejado aún...", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(posts, key = { it.id }) { post ->
                        PostItem(
                            post = post,
                            currentUser = currentUser,
                            onLikeClick = { postViewModel.likePost(post) },
                            onCommentClick = {
                                selectedPost = post
                                showBottomSheet = true
                            },
                            onDeleteClick = {
                                postViewModel.deletePost(post)
                            }
                        )
                    }
                }
            }
        }

        if (showBottomSheet && selectedPost != null && currentUser != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF1E1E1E)
            ) {
                CommentSection(
                    post = selectedPost!!,
                    viewModel = postViewModel,
                    currentUser = currentUser!!
                )
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
    onDeleteClick: () -> Unit
) {
    val DarkCardColor = Color(0xFF1E1E1E)
    val HateRed = Color(0xFFD32F2F)
    val TextGray = Color(0xFFB0B0B0)

    // LÓGICA DE SEGURIDAD CORREGIDA: Solo Admin o el dueño borran
    val canDelete = when {
        currentUser?.role == "ADMIN" -> true
        currentUser?.role == "MOD" -> true
        currentUser?.name == post.userName -> true
        else -> false
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Usuario",
                    tint = HateRed,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = post.userName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Alguien está molesto...",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (canDelete) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )

            if (!post.imageUri.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = post.imageUri,
                    contentDescription = "Imagen del post",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFF2C2C2C))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = if (post.likes > 0) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Odiar",
                        tint = HateRed
                    )
                }
                Text(text = "${post.likes} odios", color = TextGray, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.width(24.dp))

                IconButton(onClick = onCommentClick) {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = "Comentar",
                        tint = Color.White
                    )
                }
                Text(text = "Comentar", color = TextGray, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun CommentSection(post: PostEntity, viewModel: PostViewModel, currentUser: UserEntity) {
    val commentsFlow = remember(post.id) { viewModel.getComments(post.id) }
    val comments by commentsFlow.collectAsState(initial = emptyList())
    var commentText by remember { mutableStateOf("") }
    val myName = currentUser.name

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .imePadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Comentarios (${comments.size})",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Divider(color = Color.DarkGray, thickness = 1.dp)

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            reverseLayout = true,
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (comments.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("Nadie ha odiado esto todavía...", color = Color.Gray)
                    }
                }
            }
            items(comments) { comment ->
                CommentBubble(
                    comment = comment,
                    currentUser = currentUser,
                    onDeleteClick = { viewModel.deleteComment(comment) }
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp)
        ) {
            TextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Escribe tu odio...", color = Color.Gray) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1E1E1E),
                    unfocusedContainerColor = Color(0xFF1E1E1E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFD32F2F),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 4
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (commentText.isNotBlank()) {
                        viewModel.sendComment(post.id, commentText, myName)
                        commentText = ""
                    }
                },
                modifier = Modifier.background(Color(0xFFD32F2F), CircleShape).size(48.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun CommentBubble(
    comment: com.example.hatestuff3.data.local.database.post.CommentEntity,
    currentUser: UserEntity?,
    onDeleteClick: () -> Unit
) {
    val BloodRed = Color(0xFF7F0000)

    // LÓGICA DE PODERES PARA COMENTARIOS CORREGIDA
    val canDelete = when {
        currentUser?.role == "ADMIN" -> true
        currentUser?.role == "MOD" -> true
        currentUser?.name == comment.userName -> true
        else -> false
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Color(0xFFB71C1C),
            modifier = Modifier.size(32.dp).padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = BloodRed,
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 16.dp,
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text = comment.userName,
                color = Color(0xFFFF8A80),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = comment.text,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // MOSTRAR PAPELERA SI TIENE PERMISO
        if (canDelete) {
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Borrar comentario",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}