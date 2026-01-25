package com.example.hatestuff3.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hatestuff3.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    vm: AuthViewModel,
    onLoginOkNavigateHome: () -> Unit,
    onGoRegister: () -> Unit
) {
    // 1. Observamos el estado completo del ViewModel
    val state by vm.state.collectAsState()

    // Variables solo de UI (visibilidad de contraseña)
    var passwordVisible by remember { mutableStateOf(false) }

    // 2. Efecto de Navegación
    LaunchedEffect(state.isLoginSuccess) {
        if (state.isLoginSuccess) {
            onLoginOkNavigateHome()
            // Opcional: Limpiar estado al salir, aunque AuthViewModel.logout() lo hace
        }
    }

    // Colores del tema HateStuff
    val hateRed = Color(0xFFC62828)
    val textWhite = Color.White
    val textGray = Color.Gray

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = textWhite,
        unfocusedTextColor = textWhite,
        focusedBorderColor = hateRed,
        unfocusedBorderColor = textGray,
        focusedLabelColor = hateRed,
        unfocusedLabelColor = textGray,
        cursorColor = hateRed,
        errorBorderColor = Color.Red,
        errorLabelColor = Color.Red
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()), // Habilitar scroll para pantallas pequeñas
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título
        Text(
            text = "HATE STUFF",
            color = hateRed,
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bienvenido al odio",
            color = textGray,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Muestra error general si existe (ej: "Credenciales incorrectas")
        if (state.loginError != null) {
            Text(
                text = state.loginError!!,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // --- Campo Email ---
        OutlinedTextField(
            value = state.loginEmail, // Viene del VM
            onValueChange = { vm.onLoginEmailChange(it) }, // Va al VM
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = textGray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = state.loginError != null, // Se pone rojo si hay error
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Campo Password ---
        OutlinedTextField(
            value = state.loginPass, // Viene del VM
            onValueChange = { vm.onLoginPassChange(it) }, // Va al VM
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = textGray) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Ver password",
                        tint = textGray
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            isError = state.loginError != null,
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Botón Login ---
        Button(
            onClick = { vm.login() }, // Llamada sin argumentos
            enabled = !state.isLoading, // Desactivar si está cargando
            colors = ButtonDefaults.buttonColors(
                containerColor = hateRed,
                contentColor = textWhite,
                disabledContainerColor = Color.DarkGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = textWhite, modifier = Modifier.size(24.dp))
            } else {
                Text("ENTRAR", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón ir a Registro
        TextButton(onClick = onGoRegister) {
            Text("¿No tienes cuenta? Únete al lado oscuro", color = textGray)
        }
    }
}