package com.example.hatestuff3.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
fun RegisterScreen(
    vm: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val RegBackground = Color(0xFF121212)
    val RegCardBg = Color(0xFF1E1E1E)
    val RegBloodRed = Color(0xFFC62828)
    val RegTextWhite = Color.White
    val RegTextGray = Color(0xFFB0B0B0)

    val state by vm.state.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }

    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(state.isRegisterSuccess) {
        if (state.isRegisterSuccess) {
            onRegisterSuccess()
            vm.clearStates()
        }
    }


    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = RegTextWhite,
        unfocusedTextColor = RegTextWhite,
        focusedBorderColor = RegBloodRed,
        unfocusedBorderColor = RegTextGray,
        focusedLabelColor = RegBloodRed,
        unfocusedLabelColor = RegTextGray,
        cursorColor = RegBloodRed,
        errorBorderColor = Color.Red,
        errorLabelColor = Color.Red,
        errorSupportingTextColor = Color.Red
    )

    Box(
        modifier = Modifier.fillMaxSize().background(RegBackground),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(24.dp).verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RegCardBg),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Crear Cuenta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = RegBloodRed
                )

                // NOMBRE DE USUARIO
                OutlinedTextField(
                    value = state.regName,
                    onValueChange = { vm.onRegNameChange(it) },
                    label = { Text("Nombre de usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.regNameError != null, // Activa el borde rojo
                    supportingText = {
                        if (state.regNameError != null) Text(state.regNameError!!)
                    },
                    singleLine = true,
                    colors = textFieldColors
                )

                //  EMAIL
                OutlinedTextField(
                    value = state.regEmail,
                    onValueChange = { vm.onRegEmailChange(it) },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = state.regEmailError != null,
                    supportingText = {
                        if (state.regEmailError != null) Text(state.regEmailError!!)
                    },
                    singleLine = true,
                    colors = textFieldColors
                )

                // --- PASSWORD ---
                OutlinedTextField(
                    value = state.regPass,
                    onValueChange = { vm.onRegPassChange(it) },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null,
                                tint = RegTextWhite
                            )
                        }
                    },
                    isError = state.regPassError != null,
                    supportingText = {
                        if (state.regPassError != null) Text(state.regPassError!!)
                    },
                    singleLine = true,
                    colors = textFieldColors
                )

                // --- CONFIRM PASSWORD ---
                OutlinedTextField(
                    value = state.regConfirm,
                    onValueChange = { vm.onRegConfirmChange(it) },
                    label = { Text("Confirmar Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null,
                                tint = RegTextWhite
                            )
                        }
                    },
                    isError = state.regConfirmError != null,
                    supportingText = {
                        if (state.regConfirmError != null) Text(state.regConfirmError!!)
                    },
                    singleLine = true,
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- BOTÓN REGISTRAR ---
                Button(
                    onClick = { vm.register() }, // Delegamos toda la lógica al VM
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RegBloodRed,
                        contentColor = RegTextWhite
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("REGISTRARME", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = onBackToLogin) {
                    Text("¿Ya tienes cuenta? Inicia sesión", color = RegTextWhite)
                }
            }
        }
    }
}