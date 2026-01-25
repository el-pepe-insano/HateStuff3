package com.example.hatestuff3.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onOpenDrawer: () -> Unit,
    // CAMBIO IMPORTANTE: Ahora todos son opcionales (pueden ser nulos)
    // Esto permite que el NavGraph pase 'null' cuando quiera ocultar el botón.
    onHome: (() -> Unit)? = null,
    onLogin: (() -> Unit)? = null,
    onRegister: (() -> Unit)? = null
) {
    // Variable para controlar el estado del menú desplegable
    var showMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Text(
                text = "HateStuff",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Abrir Menú")
            }
        },
        actions = {
            // Lógica inteligente: Solo mostramos el icono si la función NO es nula.

            // 1. Botón Home
            if (onHome != null) {
                IconButton(onClick = { onHome() }) {
                    Icon(imageVector = Icons.Filled.Home, contentDescription = "Inicio")
                }
            }

            // 2. Botón Login
            if (onLogin != null) {
                IconButton(onClick = { onLogin() }) {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = "Login")
                }
            }

            // 3. Botón Registro
            if (onRegister != null) {
                IconButton(onClick = { onRegister() }) {
                    Icon(imageVector = Icons.Filled.PersonAdd, contentDescription = "Registro")
                }
            }

            // 4. Botón Menú "Ver más" (Siempre visible)
            IconButton(onClick = { showMenu = true }) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Ver más")
            }

            // Menú desplegable
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                // Solo mostramos las opciones del menú si existen
                if (onHome != null) {
                    DropdownMenuItem(
                        text = { Text("Ir al Home") },
                        onClick = {
                            showMenu = false
                            onHome()
                        }
                    )
                }

                if (onLogin != null) {
                    DropdownMenuItem(
                        text = { Text("Inicio de Sesión") },
                        onClick = {
                            showMenu = false
                            onLogin()
                        }
                    )
                }

                if (onRegister != null) {
                    DropdownMenuItem(
                        text = { Text("Registro") },
                        onClick = {
                            showMenu = false
                            onRegister()
                        }
                    )
                }
            }
        }
    )
}