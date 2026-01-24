package com.example.hatestuff3.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.* // Importante: Material3
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hatestuff3.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreenVm(
    vm: AuthViewModel,
    onLoginOkNavigateHome: () -> Unit,
    onGoRegister: () -> Unit
){
    // 1. Recolectamos el estado
    val state by vm.login.collectAsStateWithLifecycle()

    // 2. Efecto de Navegación
    LaunchedEffect(state.success) {
        if(state.success){
            vm.clearLoginResult()
            onLoginOkNavigateHome()
        }
    }

    // 3. Pasamos datos a la UI
    LoginScreen(
        email = state.email,
        pass = state.pass,
        emailError = state.emailError,
        passError = state.passError,
        errorMsg = state.errorMsg,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        onEmailChange = vm::onLoginEmailChange,
        onPassChange = vm::onLoginPassChange,
        onSubmit = vm::submitLogin,
        onGoRegister = onGoRegister
    )
}

@Composable
private fun LoginScreen(
    email: String,
    pass: String,
    emailError: String?,
    passError: String?,
    errorMsg: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    onEmailChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoRegister: () -> Unit
){
    // --- DEFINICIÓN DE COLORES LOCALES ---
    // Los definimos aquí adentro para evitar el error "Conflicting declarations"
    val localRed = Color(0xFFFF2400)
    val localDark = Color(0xFF121212)
    val localWhite = Color(0xFFFFFFFF)

    var showPass by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(localDark) // Usamos el color local
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "HateStuff",
                style = MaterialTheme.typography.headlineMedium,
                color = localRed,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            // --- CAMPO CORREO ---
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Correo", color = Color.Gray) },
                singleLine = true,
                isError = emailError != null,
                // CONFIGURACIÓN DE COLORES DEL TEXTFIELD
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = localWhite,
                    unfocusedTextColor = localWhite,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = localRed,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = localRed,
                    errorBorderColor = localRed
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            if(emailError != null){
                Text(emailError, color = localRed, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(16.dp))

            // --- CAMPO CONTRASEÑA ---
            OutlinedTextField(
                value = pass,
                onValueChange = onPassChange,
                label = { Text("Contraseña", color = Color.Gray) },
                singleLine = true,
                isError = passError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = localWhite,
                    unfocusedTextColor = localWhite,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = localRed,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = localRed,
                    errorBorderColor = localRed
                ),
                visualTransformation = if(showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if(showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Ver Clave",
                            tint = localRed
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if(passError != null){
                Text(passError, color = localRed, style = MaterialTheme.typography.labelSmall)
            }

            if(errorMsg != null){
                Spacer(Modifier.height(8.dp))
                Text(errorMsg, color = localRed, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(24.dp))

            // --- BOTÓN ENTRAR ---
            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = localRed,
                    disabledContainerColor = Color.DarkGray
                ),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if(isSubmitting){
                    CircularProgressIndicator(color = localWhite, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Validando...", color = localWhite)
                } else {
                    Text("ENTRAR", color = localWhite, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            TextButton(
                onClick = onGoRegister,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿No tienes cuenta? Regístrate aquí", color = Color.Gray)
            }
        }
    }
}