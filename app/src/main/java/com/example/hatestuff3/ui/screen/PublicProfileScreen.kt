package com.example.hatestuff3.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hatestuff3.ui.viewmodel.PostViewModel
import com.example.hatestuff3.ui.viewmodel.AuthViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(
    userName: String,
    postViewModel: PostViewModel,
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit
) {
    val allPosts by postViewModel.posts.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Filtrado optimizado con remember
    val userPosts = remember(allPosts, userName) {
        allPosts.filter { it.userName == userName }
    }

    // Obtenemos la info del usuario (Asegúrate de que esta función devuelva un StateFlow o Flow)
    val targetUserProfile by authViewModel.getUserPublicInfo(userName).collectAsState(initial = null)

    // Colores de la marca
    val BackgroundColor = Color(0xFF000000)
    val SurfaceColor = Color(0xFF121212)
    val HateRed = Color(0xFF8B0000)
    val ModeratorBlue = Color(0xFF00BCD4)
    val UserGray = Color(0xFF757575)

    val realRole = targetUserProfile?.role ?: "USER"
    val isProfileAdmin = realRole == "ADMIN"
    val isProfileMod = realRole == "MOD"

    val profileAccentColor = when {
        isProfileAdmin -> HateRed
        isProfileMod -> ModeratorBlue
        else -> UserGray
    }

    val bannerBrush = Brush.verticalGradient(
        colors = listOf(profileAccentColor.copy(alpha = 0.4f), BackgroundColor)
    )

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Banner
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(bannerBrush))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 90.dp, start = 16.dp, end = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar dinámico
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Surface(
                                shape = CircleShape,
                                border = BorderStroke(2.dp, profileAccentColor),
                                color = SurfaceColor,
                                modifier = Modifier.size(110.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = profileAccentColor,
                                    modifier = Modifier.padding(25.dp)
                                )
                            }
                            if (isProfileAdmin || isProfileMod) {
                                Text(
                                    text = if (isProfileAdmin) "👑" else "🛡️",
                                    fontSize = 24.sp,
                                    modifier = Modifier.offset(x = (4).dp, y = (4).dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(userName, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)

                        // Badge de Rol
                        Surface(
                            color = profileAccentColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, profileAccentColor.copy(alpha = 0.5f)),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = realRole.uppercase(),
                                color = profileAccentColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        // Bio
                        Text(
                            text = targetUserProfile?.bio?.let { "\"$it\"" } ?: "Sin manifiesto.",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 16.dp, start = 20.dp, end = 20.dp)
                        )

                        // Estadísticas
                        Row(
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .fillMaxWidth()
                                .background(SurfaceColor, RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(userPosts.size.toString(), "ODIOS")
                            Divider(Modifier.height(30.dp).width(1.dp), color = Color.DarkGray)
                            StatItem(realRole, "RANGO")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // LISTA DE POSTS
            if (userPosts.isEmpty()) {
                item {
                    Text(
                        "Este usuario no ha esparcido odio todavía.",
                        color = Color.DarkGray,
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(userPosts, key = { it.id ?: 0L }) { post ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        PostItem(
                            post = post,
                            currentUser = currentUser,
                            // CORRECCIÓN: firma completa de onLikeClick
                            onLikeClick = {
                                if (currentUser != null && post.id != null) {
                                    postViewModel.likePost(post.id, currentUser!!.name)
                                }
                            },
                            onCommentClick = { /* Ver hilo */ },
                            onDeleteClick = { post.id?.let { postViewModel.deletePost(it) } },
                            onEditClick = { /* No editable desde perfil público */ },
                            onUserClick = { /* Ya estamos aquí */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(label, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}