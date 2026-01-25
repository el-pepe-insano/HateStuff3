package com.example.hatestuff3.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hatestuff3.data.local.database.user.UserEntity
import com.example.hatestuff3.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    user: UserEntity,
    onLogout: () -> Unit
) {
    // Estados locales para la edición
    var bio by remember { mutableStateOf(user.bio) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(user.profilePictureUri?.let { Uri.parse(it) }) }

    // Lanzador para seleccionar imagen de la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) selectedImageUri = uri
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "EDITAR PERFIL",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFC62828)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- FOTO DE PERFIL EDITABLE (CORREGIDA) ---
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1A1A))
                    .border(2.dp, Color(0xFFC62828), CircleShape)
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri == null) {
                    // Si no hay imagen, mostramos el Icono (ImageVector) correctamente
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(65.dp),
                        tint = Color.Gray
                    )
                } else {
                    // Si hay URI, usamos AsyncImage para cargar la foto real
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Superposición "CAMBIAR"
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        "CAMBIAR",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // --- CAMPO DE BIOGRAFÍA ---
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Tu biografía del odio", color = Color(0xFFC62828)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFC62828),
                    unfocusedBorderColor = Color.DarkGray,
                    cursorColor = Color(0xFFC62828)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- BOTÓN GUARDAR (ESTILIZADO Y CORREGIDO) ---
            Button(
                onClick = {
                    // Solo intentamos guardar si el ID no es 0 (usuario no cargado)
                    if (user.id != 0L) {
                        authViewModel.updateProfile(
                            userId = user.id,
                            newBio = bio,
                            newPhotoUri = selectedImageUri?.toString()
                        )
                    }
                },
                enabled = bio.isNotBlank(), // No dejar guardar bios vacías
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("CONFIRMAR CAMBIOS", fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para cerrar sesión
            TextButton(onClick = onLogout) {
                Text("CERRAR SESIÓN", color = Color.Gray, fontWeight = FontWeight.Medium)
            }
        }
    }
}