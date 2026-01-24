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
    // --- SOLUCIÓN: Nombres ÚNICOS para evitar conflictos ---
    val RegBackground = Color(0xFF121212)  // Antes DarkBackground
    val RegCardBg = Color(0xFF1E1E1E)      // Antes CardBackground
    val RegBloodRed = Color(0xFFC62828)    // Antes BloodRed
    val RegTextWhite = Color.White
    val RegTextGray = Color(0xFFB0B0B0)

    val state by vm.register.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(state.success) {
        if (state.success) {
            onRegisterSuccess()
        }
    }

    // Configuración de colores usando los nombres NUEVOS
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
        modifier = Modifier
            .fillMaxSize()
            .background(RegBackground), // Usamos RegBackground
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(scrollState),
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
                    color = RegBloodRed // Usamos RegBloodRed
                )

                if (state.errorMsg != null) {
                    Text(text = state.errorMsg!!, color = Color.Red, fontSize = 14.sp)
                }

                // NOMBRE
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { vm.onNameChange(it) },
                    label = { Text("Nombre de usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.nameError != null,
                    supportingText = { if(state.nameError != null) Text(state.nameError!!) },
                    singleLine = true,
                    colors = textFieldColors
                )

                // CORREO
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { vm.onRegisterEmailChange(it) },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = state.emailError != null,
                    supportingText = { if(state.emailError != null) Text(state.emailError!!) },
                    singleLine = true,
                    colors = textFieldColors
                )

                // CONTRASEÑA
                OutlinedTextField(
                    value = state.pass,
                    onValueChange = { vm.onRegisterPassChange(it) },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Ver contraseña",
                                tint = RegTextWhite
                            )
                        }
                    },
                    isError = state.passError != null,
                    supportingText = { if(state.passError != null) Text(state.passError!!) },
                    singleLine = true,
                    colors = textFieldColors
                )

                // CONFIRMAR CONTRASEÑA
                OutlinedTextField(
                    value = state.confirm,
                    onValueChange = { vm.onConfirmChange(it) },
                    label = { Text("Confirmar Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Ver confirmación",
                                tint = RegTextWhite
                            )
                        }
                    },
                    isError = state.confirmError != null,
                    supportingText = { if(state.confirmError != null) Text(state.confirmError!!) },
                    singleLine = true,
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(16.dp))

                // BOTÓN REGISTRAR
                Button(
                    onClick = { vm.submitRegister() },
                    enabled = state.canSubmit && !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.canSubmit) RegBloodRed else Color.DarkGray,
                        contentColor = RegTextWhite,
                        disabledContainerColor = Color.DarkGray,
                        disabledContentColor = RegTextGray
                    )
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(color = RegTextWhite, modifier = Modifier.size(24.dp))
                    } else {
                        Text("REGISTRARME", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Botón volver
                TextButton(onClick = onBackToLogin) {
                    Text("¿Ya tienes cuenta? Inicia sesión", color = RegTextWhite)
                }
            }
        }
    }
}