package com.example.hatestuff3.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hatestuff3.R
import com.example.hatestuff3.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    vm: AuthViewModel,
    onLoginOkNavigateHome: () -> Unit,
    onGoRegister: () -> Unit
) {
    val state by vm.state.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoginSuccess) {
        if (state.isLoginSuccess) {
            onLoginOkNavigateHome()
        }
    }

    val hateRed = Color(0xFFC62828)
    val textWhite = Color.White
    val textGray = Color.Gray

    // Gradiente para el botón
    val hateGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF5D0000), Color(0xFFC62828))
    )

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
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // --- LOGO EN LUGAR DE TEXTO ---
        Image(
            painter = painterResource(id = R.drawable.logo_hatestuffnolo),
            contentDescription = "Logo Hate Stuff",
            modifier = Modifier
                .size(330.dp) // Aumentado de 180.dp a 250.dp
                .padding(bottom = 8.dp), // Reducido el padding para que no empuje tanto el texto hacia abajo
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Bienvenido al odio",
            color = textGray,
            fontSize = 16.sp,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        if (state.loginError != null) {
            Text(
                text = state.loginError!!,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        OutlinedTextField(
            value = state.loginEmail,
            onValueChange = { vm.onLoginEmailChange(it) },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Person, null, tint = textGray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = state.loginError != null,
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.loginPass,
            onValueChange = { vm.onLoginPassChange(it) },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = textGray) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = textGray
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            isError = state.loginError != null,
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- BOTÓN CON GRADIENTE SANGRIENTO ---
        Button(
            onClick = { vm.login() },
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .background(
                    brush = if (!state.isLoading) hateGradient else Brush.linearGradient(listOf(Color.DarkGray, Color.DarkGray)),
                    shape = RoundedCornerShape(28.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, // Transparent para que se vea el background gradiente
                contentColor = textWhite,
                disabledContainerColor = Color.Transparent
            ),
            contentPadding = PaddingValues() // Elimina el padding interno para que el gradiente llene todo
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = textWhite, modifier = Modifier.size(24.dp))
                } else {
                    Text("ENTRAR", fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 1.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onGoRegister) {
            Text("¿No tienes cuenta? Únete al lado oscuro", color = textGray)
        }
    }
}