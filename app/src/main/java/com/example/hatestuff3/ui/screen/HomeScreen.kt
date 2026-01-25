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
import com.example.hatestuff3.ui.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    postViewModel: PostViewModel,
    onCreatePostClick: () -> Unit
    // Quitamos onCommentClick de aquí porque lo manejamos internamente con el BottomSheet
) {
    // 1. Estado de los posts
    val posts by postViewModel.allPosts.collectAsState(initial = emptyList())

    // 2. Estados para la ventana de comentarios (BottomSheet)
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedPost by remember { mutableStateOf<PostEntity?>(null) }

    // Colores
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
                    IconButton(onClick = { /* Menú opcional */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
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
                // ESTADO VACÍO
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
                // LISTA DE POSTS
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(posts, key = { it.id }) { post ->
                        PostItem(
                            post = post,
                            onLikeClick = { postViewModel.likePost(post) },
                            onCommentClick = {
                                // Al hacer clic en comentar, guardamos el post y abrimos la ventana
                                selectedPost = post
                                showBottomSheet = true
                            }
                        )
                    }
                }
            }
        }

        // --- VENTANA DESLIZANTE DE COMENTARIOS (BottomSheet) ---
        if (showBottomSheet && selectedPost != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF1E1E1E) // Fondo gris oscuro
            ) {
                CommentSection(
                    post = selectedPost!!,
                    viewModel = postViewModel
                )
            }
        }
    }
}

// --- COMPONENTE TARJETA DE POST ---
@Composable
fun PostItem(
    post: PostEntity,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit // Recibimos el evento de clic
) {
    val DarkCardColor = Color(0xFF1E1E1E)
    val HateRed = Color(0xFFD32F2F)
    val TextGray = Color(0xFFB0B0B0)

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
            // Cabecera
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
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contenido Texto
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )

            // Contenido Imagen
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

            // Botones de Acción
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // LIKE
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = if (post.likes > 0) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Odiar",
                        tint = HateRed
                    )
                }
                Text(text = "${post.likes} odios"
                    , color = TextGray, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.width(24.dp))

                // COMENTAR (Icono visible)
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

// --- SECCIÓN DE COMENTARIOS (INTERNA DEL BOTTOM SHEET) ---
@Composable
fun CommentSection(post: PostEntity, viewModel: PostViewModel) {
    val commentsFlow = remember(post.id) { viewModel.getComments(post.id) }
    val comments by commentsFlow.collectAsState(initial = emptyList())
    var commentText by remember { mutableStateOf("") }

    val myName = "Yo Mismo"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f) // Ocupa el 90% de la pantalla
            .imePadding() // 1. CRÍTICO: Detecta el teclado y empuja el contenido
            .padding(16.dp)
    ) {
        // Cabecera
        Text(
            text = "Comentarios (${comments.size})",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Divider(color = Color.DarkGray, thickness = 1.dp)

        // Lista de Comentarios
        LazyColumn(
            modifier = Modifier
                .weight(1f) // Ocupa todo el espacio disponible, empujando el input abajo
                .fillMaxWidth(),
            reverseLayout = true, // Estilo chat (nuevos abajo)
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
                CommentBubble(comment)
            }
        }

        // --- ZONA DE ESCRIBIR ---
        // La envolvemos en un Surface para darle contraste si quieres, o solo Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp) // Un poco de aire arriba y abajo
        ) {
            TextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Escribe tu odio...", color = Color.Gray) },
                modifier = Modifier.weight(1f),
                // Colores para asegurar que se lea bien
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1E1E1E), // Gris un poco más claro que el fondo
                    unfocusedContainerColor = Color(0xFF1E1E1E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFD32F2F),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 4 // Permite ver hasta 4 líneas de texto mientras escribes
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Botón de Enviar
            IconButton(
                onClick = {
                    if (commentText.isNotBlank()) {
                        viewModel.sendComment(post.id, commentText, myName)
                        commentText = ""
                    }
                },
                modifier = Modifier
                    .background(Color(0xFFD32F2F), CircleShape)
                    .size(48.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White)
            }
        }

        // TRUCO EXTRA: Un pequeño espacio al final por si el teclado queda muy justo
        Spacer(modifier = Modifier.height(10.dp))
    }
}
// --- NUEVO COMPONENTE: GLOBO DE TEXTO ---
@Composable
fun CommentBubble(comment: com.example.hatestuff3.data.local.database.post.CommentEntity) {
    // Definimos el color "Rojo Sangre" aquí
    // 0xFF7F0000 es un rojo sangre fuerte (ni negro ni neón)
    val BloodRed = Color(0xFF7F0000)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar
        Icon(
            Icons.Default.Person,
            contentDescription = null,
            tint = Color(0xFFB71C1C), // Rojo un poco más vivo para el icono
            modifier = Modifier.size(32.dp).padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // El Globo
        Column(
            modifier = Modifier
                .background(
                    color = BloodRed, // <--- AQUÍ ESTÁ EL NUEVO COLOR
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 16.dp,
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            // Nombre del usuario
            Text(
                text = comment.userName,
                color = Color(0xFFFF8A80), // Un rojo/rosa pálido para el nombre (destaca sobre sangre)
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Texto del comentario
            Text(
                text = comment.text,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}