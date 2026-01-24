package com.example.hatestuff3.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hatestuff3.data.local.database.post.PostEntity
import com.example.hatestuff3.ui.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    postViewModel: PostViewModel,
    onCreatePostClick: () -> Unit
) {
    // Escucha los cambios de la base de datos en tiempo real
    val posts by postViewModel.posts.collectAsState()

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("HATE STUFF", fontWeight = FontWeight.ExtraBold, color = Color(0xFFC62828)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212)
                ),
                actions = {
                    IconButton(onClick = { /* Menú futuro */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePostClick,
                containerColor = Color(0xFFC62828),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Queja")
            }
        }
    ) { paddingValues ->
        // Si no hay posts, mostramos un mensaje para que no se vea vacío
        if (posts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                Text("Nadie se ha quejado aún... qué raro.", color = Color.Gray)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
        ) {
            // Usamos el ID de la base de datos como llave para mejorar el rendimiento
            items(posts, key = { it.id }) { post ->
                PostItem(
                    post = post,
                    onLikeClick = { postViewModel.darLike(post.id) }
                )
            }
        }
    }
}

@Composable
fun PostItem(
    post: PostEntity,
    onLikeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // CABECERA: Usuario
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFFC62828),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = post.authorName, // Asegúrate de que authorName esté en tu PostEntity
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Alguien está molesto...",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // TEXTO: La Queja
            Text(
                text = post.content,
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )

            // IMAGEN: Si el post tiene imagen guardada
            post.imageUri?.let { uri ->
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = uri,
                    contentDescription = "Imagen de la queja",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FOOTER: Likes y Compartir
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = if (post.likesCount > 0) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = Color(0xFFC62828)
                    )
                }
                Text(
                    text = "${post.likesCount} odios",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* Compartir */ }) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.Gray)
                }
            }
        }
    }
}