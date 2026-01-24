package com.example.hatestuff3.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hatestuff3.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreenVm(
    vm: AuthViewModel,
    onLoginOkNavigateHome: () -> Unit,
    onGoRegister: () -> Unit
){
    //instanciar el viewmodel para poder usarlo
    //val vm: AuthViewModel = viewModel()
    //variable publica para los datos del formulario de login (stateFlow)
    val state by vm.login.collectAsStateWithLifecycle()

    //verificar si el login es exitoso que debe hacer la pantalla
    if(state.success){
        vm.clearLoginResult() //limpia banderas/flags
        onLoginOkNavigateHome() //redirecciono a la pantalla HomeScreen
    }
    LoginScreen(
        //datos que estan el data class del LoginUiState
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
    val bg = MaterialTheme.colorScheme.secondaryContainer
    //variable para manejar si la clave es visible o no en el formulario
    var showPass by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize() //ocupa todo
            .background(bg) //color de fondo de la caja
            .padding(16.dp), //margenes interiores a la caja
        contentAlignment = Alignment.Center //centra todos sus elementos internos
    ){
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Inicio de sesión",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.width(12.dp))
            //aqui deberian ir los campos del formulario
            //crear el campo para el correo electronico
            OutlinedTextField(
                value = email, //donde se va a guardar lo que escriba
                onValueChange = onEmailChange, //que se ejecutara cuando escriba algo
                label = { Text("Correo: ")}, //titulo a mostrar
                singleLine = true, //todo lo que escriba se ve en una sola linea
                isError = emailError != null, //marca error si corresponde
                keyboardOptions = KeyboardOptions( //tipo de teclado en el telefono
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if(emailError != null){
                Text(emailError, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.width(9.dp))
            //CLAVE
            OutlinedTextField(
                value = pass, //donde se va a guardar lo que escriba
                onValueChange = onPassChange, //que se ejecutara cuando escriba algo
                label = { Text("Contraseña: ")}, //titulo a mostrar
                singleLine = true, //todo lo que escriba se ve en una sola linea
                isError = passError != null, //marca error si corresponde
                visualTransformation = if(showPass) VisualTransformation.None
                else PasswordVisualTransformation(), //permite modificar la visualizacion
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass}) {
                        Icon(
                            imageVector = if(showPass) Icons.Filled.VisibilityOff
                            else Icons.Filled.Visibility,
                            contentDescription = if(showPass) "Ocultar Clave" else "Mostrar Clave"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if(passError != null){
                Text(passError, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.width(9.dp))
            //botones de accion del formulario
            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if(isSubmitting){
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(7.dp))
                    Text("Validando...")
                } else {
                    Text("Entrar")
                }
            }
            if(errorMsg != null){
                Spacer(Modifier.width(9.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.width(12.dp))
            OutlinedButton(onClick = onGoRegister, modifier = Modifier.fillMaxWidth()) {
                Text("Crear Cuenta")
            }
        }
    }

}