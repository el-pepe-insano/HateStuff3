package com.example.hatestuff3.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
    val DarkBackground = Color(0xFF121212)
    val BloodRed = Color(0xFFC62828)
    val TextWhite = Color.White
    val TextLightGray = Color(0xFFB0B0B0)

    // --- CONEXIÓN AL VIEWMODEL ---
    val state by vm.login.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    // --- LÓGICA DE NAVEGACIÓN SEGURA ---
    // Solo cambia de pantalla si el ViewModel confirma que los datos son correctos
    LaunchedEffect(state.success) {
        if (state.success) {
            onLoginOkNavigateHome()
            vm.clearLoginResult() // Limpia el estado para la próxima vez
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextWhite,
        unfocusedTextColor = TextWhite,
        focusedBorderColor = BloodRed,
        unfocusedBorderColor = TextLightGray,
        focusedLabelColor = BloodRed,
        unfocusedLabelColor = TextLightGray,
        cursorColor = BloodRed
    )

    Box(
        modifier = Modifier.fillMaxSize().background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "HateStuff",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = BloodRed,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Mensaje de error si las credenciales fallan
                if (state.errorMsg != null) {
                    Text(text = state.errorMsg!!, color = Color.Red, fontSize = 14.sp)
                }

                // Campo Correo conectado al VM
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { vm.onLoginEmailChange(it) },
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    isError = state.emailError != null,
                    supportingText = { if(state.emailError != null) Text(state.emailError!!) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                // Campo Contraseña conectado al VM
                OutlinedTextField(
                    value = state.pass,
                    onValueChange = { vm.onLoginPassChange(it) },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null,
                                tint = TextWhite
                            )
                        }
                    },
                    colors = textFieldColors,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botón ENTRAR con estado de carga
                Button(
                    onClick = { vm.submitLogin() }, // Llama a la validación real
                    enabled = state.canSubmit && !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BloodRed,
                        disabledContainerColor = Color.DarkGray
                    )
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(color = TextWhite, modifier = Modifier.size(24.dp))
                    } else {
                        Text("ENTRAR", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = onGoRegister) {
                    Text("¿No tienes cuenta? Regístrate aquí", color = TextWhite)
                }
            }
        }
    }
}