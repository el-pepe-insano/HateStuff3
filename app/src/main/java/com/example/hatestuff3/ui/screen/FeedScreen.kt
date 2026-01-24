package com.example.hatestuff3.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hatestuff3.data.local.database.post.PostEntity
import com.example.hatestuff3.ui.viewmodel.PostViewModel

// --- COLORES PERSONALIZADOS ---
val ScarletRed = Color(0xFFFF2400) // Rojo Escarlata
val DarkBackground = Color(0xFF121212) // Gris muy oscuro (casi negro)
val CardBackground = Color(0xFF1E1E1E) // Gris un poco más claro para las tarjetas
val TextWhite = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    navController: NavController,
    postViewModel: PostViewModel,
    userId: Int // Necesitamos saber quién está logueado (por ahora pasaremos un ID temporal)
) {
    // Escuchamos la lista de posts en tiempo real
    val posts by postViewModel.posts.collectAsState()

    // Estado para el texto que escribe el usuario
    var newPostContent by remember { mutableStateOf("") }

    Scaffold(
        containerColor = DarkBackground, // Fondo general de la app
        topBar = {
            TopAppBar(
                title = { Text("HateStuff", color = ScarletRed, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // --- CAJA PARA PUBLICAR ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newPostContent,
                    onValueChange = { newPostContent = it },
                    placeholder = { Text("¿Qué odias hoy?", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = ScarletRed,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = ScarletRed,
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (newPostContent.isNotBlank()) {
                            postViewModel.agregarPost(newPostContent, userId)
                            newPostContent = "" // Limpiar caja
                        }
                    },
                    modifier = Modifier
                        .background(ScarletRed, shape = RoundedCornerShape(50))
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Publicar", tint = TextWhite)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- LISTA DE POSTS ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(posts) { post ->
                    PostItem(post = post, onLikeClick = { postViewModel.darLike(post.id) })
                }
            }
        }
    }
}

@Composable
fun PostItem(post: PostEntity, onLikeClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Autor (Temporalmente solo mostramos ID hasta que hagamos la relación completa)
            Text(
                text = "Usuario #${post.authorId}",
                color = ScarletRed,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Contenido del post
            Text(text = post.content, color = TextWhite, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(12.dp))

            // Botón de Like y Fecha
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = if (post.likesCount > 0) ScarletRed else Color.Gray
                    )
                }
                Text(text = "${post.likesCount}", color = TextWhite)
            }
        }
    }
}