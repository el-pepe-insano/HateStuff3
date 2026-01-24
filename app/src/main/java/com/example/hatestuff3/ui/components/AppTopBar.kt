package com.example.hatestuff3.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
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
fun AppTopBar (
    onOpenDrawer: () -> Unit, //abre el menu desplegable
    onHome: () -> Unit,//ir a home
    onLogin: () -> Unit,// redirije al login
    onRegister: () -> Unit //redirije al registro
){
    //variable remember son acciones hechas anteriormente y son asincronicas0
    //creamos una variable que recuerde el esstado del menu desplegable de 3 puntitos para abajo
    var showMenu by remember { mutableStateOf(false) }

    //barra alineada en el centro del topbar
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Text(
                text = "Menu Superior",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1, // cantidad de lineas en que se puede mostrar texto
                overflow = TextOverflow.Ellipsis //agrega los 3 puntos suspensivos y no se muestra textro entero
            )
        },
        //icono de cogollo para menu desplegable
        navigationIcon = {
            IconButton(onClick = onOpenDrawer)  {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
            }
        },
        //iconos conn action
        actions = {
            IconButton(onClick = onHome) {
                Icon(imageVector = Icons.Filled.Home, contentDescription = "Home")
            }

            IconButton(onClick = onLogin) {
                Icon(imageVector = Icons.Filled.Person, contentDescription = "Login")
            }

            IconButton(onClick = onRegister) {
                Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Register")
            }

            IconButton(onClick ={ showMenu = true}) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Ver mas")
            }
            DropdownMenu(
                expanded = showMenu,//si esta abierto o cerrado
                onDismissRequest = {showMenu = false}

            ) {
                DropdownMenuItem(
                    text = {Text("ir al home")},
                    onClick = {showMenu = false; onHome()}

                )
                DropdownMenuItem(
                    text = {Text("ir al Inicio de sesion")},
                    onClick = {showMenu = false; onLogin()}

                )
                DropdownMenuItem(
                    text = {Text("ir al Registro")},
                    onClick = {showMenu = false; onRegister()}

                )

            }

        }
    )
}