package com.example.hatestuff3.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen (
    onRegistered: () -> Unit,
    onGoLogin: () -> Unit
){
    val bg = MaterialTheme.colorScheme.tertiaryContainer
    Box(
        modifier = Modifier
            .fillMaxSize()//ocupa todo
            .background(bg)//color de fondo de la caja
            .padding(16.dp),
        contentAlignment = Alignment.Center //centra todo
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registro de usuario",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.width(12.dp))
            //aqui deberian ir los campos del formulario
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)

            ) {
                Button(onClick = onRegistered) { Text("Registrar") }
                OutlinedButton(onClick = onGoLogin) { Text("volver a inicio sesion") }
            }
        }
    }
}