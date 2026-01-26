package com.example.hatestuff3.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onPostCreated: (String, Uri?) -> Unit,
    onCancel: () -> Unit
) {
    var textState by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val isPostButtonEnabled = textState.isNotBlank() || selectedImageUri != null

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("NUEVA QUEJA", fontWeight = FontWeight.ExtraBold, color = Color(0xFFC62828)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Cancelar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212)),
                actions = {
                    TextButton(
                        onClick = { onPostCreated(textState, selectedImageUri) },
                        enabled = isPostButtonEnabled
                    ) {
                        Text(
                            "PUBLICAR",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = if (isPostButtonEnabled) Color(0xFFC62828) else Color.DarkGray
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                placeholder = { Text("¿Qué te molesta hoy? Suéltalo todo...", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1A1A1A),
                    unfocusedContainerColor = Color(0xFF121212),
                    focusedBorderColor = Color(0xFFC62828), // Borde rojo al escribir
                    unfocusedBorderColor = Color.DarkGray,
                    cursorColor = Color(0xFFC62828)
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // VISTA PREVIA DE LA FOTO
            selectedImageUri?.let { uri ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1A1A1A))
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Foto seleccionada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Botón para quitar la foto con estilo
                    IconButton(
                        onClick = { selectedImageUri = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(24.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // BOTÓN PARA AÑADIR FOTO
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A1A1A),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC62828)) // Borde rojo fino
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color(0xFFC62828))
                Spacer(Modifier.width(12.dp))
                Text("ADJUNTAR PRUEBAS", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}