package com.example.hatestuff3.ui.screen

import android.annotation.SuppressLint // IMPORTANTE
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

// --- SOLUCIÓN: La etiqueta va AQUÍ, antes de "fun" ---
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(
    userName: String,
    postViewModel: PostViewModel,
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit
) {
    // 1. Datos
    val allPosts by postViewModel.filteredPosts.collectAsState()
    val userPosts = allPosts.filter { it.userName == userName }
    val currentUser by authViewModel.currentUser.collectAsState()
    val targetUserProfile by authViewModel.getUserPublicInfo(userName).collectAsState(initial = null)

    // 2. Colores y Estilos (ROJO SANGRE PRESERVADO)
    val BackgroundColor = Color(0xFF000000)
    val SurfaceColor = Color(0xFF121212)
    val HateRed = Color(0xFF8B0000) // Rojo Sangre
    val ModeratorBlue = Color(0xFF00BCD4)
    val UserGray = Color(0xFF757575)

    val realRole = targetUserProfile?.role ?: "USER"
    val isProfileAdmin = realRole == "ADMIN"
    val isProfileMod = realRole == "MOD"

    // Color principal del perfil según el rol
    val profileAccentColor = when {
        isProfileAdmin -> HateRed
        isProfileMod -> ModeratorBlue
        else -> UserGray
    }

    // Degradado para el fondo del banner
    val bannerBrush = Brush.verticalGradient(
        colors = listOf(
            profileAccentColor.copy(alpha = 0.5f), // Un poco más intenso
            BackgroundColor
        )
    )

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            // TopBar transparente para que se vea el degradado y la flecha atrás
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        // AQUÍ ESTABA EL ERROR. Ahora usamos paddingValues solo abajo para evitar tapar contenido,
        // pero arriba lo ignoramos (top = 0.dp) para que el banner quede detrás de la barra transparente.

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor),
            // Solo respetamos el padding de abajo (navegación), arriba queremos que llegue al borde
            contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 16.dp)
        ) {

            // --- ITEM 1: CABECERA DEL PERFIL (BANNER + INFO) ---
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Fondo con degradado (Banner)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(bannerBrush)
                    )

                    // Contenido del Perfil
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 100.dp, start = 16.dp, end = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Surface(
                                shape = CircleShape,
                                border = if (isProfileAdmin || isProfileMod) BorderStroke(3.dp, profileAccentColor) else null,
                                shadowElevation = 10.dp,
                                color = SurfaceColor,
                                modifier = Modifier.size(110.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if(isProfileAdmin || isProfileMod) profileAccentColor else Color.LightGray,
                                        modifier = Modifier.size(60.dp)
                                    )
                                }
                            }

                            // Icono pequeño de rol (Corona/Escudo)
                            if (isProfileAdmin || isProfileMod) {
                                Surface(
                                    color = BackgroundColor,
                                    shape = CircleShape,
                                    modifier = Modifier.offset(x = 6.dp, y = (-6).dp).border(2.dp, BackgroundColor, CircleShape)
                                ) {
                                    Text(
                                        text = if (isProfileAdmin) "👑" else "🛡️",
                                        fontSize = 22.sp,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Nombre
                        Text(
                            text = userName,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Badge de Rol (Chip)
                        Surface(
                            color = profileAccentColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, profileAccentColor.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = when {
                                    isProfileAdmin -> "ADMINISTRADOR SUPREMO"
                                    isProfileMod -> "MODERADOR DEL ABISMO"
                                    else -> "HABITANTE DEL ABISMO"
                                }.uppercase(),
                                color = profileAccentColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Descripción
                        val description = targetUserProfile?.bio
                        Text(
                            text = if (!description.isNullOrBlank()) "\"$description\"" else "Sin manifiesto definido.",
                            color = if (!description.isNullOrBlank()) Color.White.copy(alpha = 0.8f) else Color.Gray,
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .padding(vertical = 4.dp),
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Panel de Estadísticas
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceColor)
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${userPosts.size}",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "ODIOS ESPARCIDOS",
                                    color = Color.Gray,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            // Separador vertical
                            Box(modifier = Modifier.height(30.dp).width(1.dp).background(Color.White.copy(alpha = 0.1f)))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "---", // Placeholder
                                    color = Color.Gray,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "SEGUIDORES",
                                    color = Color.Gray,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = SurfaceColor, thickness = 2.dp)
            }

            // --- ITEM 2: LISTA DE POSTS ---
            if (userPosts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Block,
                                contentDescription = null,
                                tint = Color.DarkGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Silencio absoluto...",
                                color = Color.Gray,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            } else {
                items(userPosts) { post ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        PostItem(
                            post = post,
                            currentUser = currentUser,
                            onLikeClick = { postViewModel.likePost(post) },
                            onCommentClick = { },
                            onDeleteClick = { postViewModel.deletePost(post) },
                            onUserClick = { }
                        )
                    }
                }
            }
        }
    }
}