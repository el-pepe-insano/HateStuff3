package com.example.hatestuff3.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerItem(
    val label: String,
    val icon: ImageVector, //icono del item del menu
    val onClick: () -> Unit

)

@Composable
fun AppDrawer(
    currentRoute: String?,
    items: List<DrawerItem>,
    modifier: Modifier = Modifier

){
    //crea la ventana modal para el menu lateral desplegable
    ModalDrawerSheet(
        modifier = modifier
    ) {
        //dibujar todos los items del menu
        //recordando que vienen en una lista
        items.forEach { item ->  //fuarda en la variable item cada elemento que consiga en la lista
            NavigationDrawerItem(//muestra los items del menu de la lista con diseño
                label = { Text(item.label) },
                selected = false, //identifica si el item del menu esta selecionado de manera automatica
                onClick = item.onClick,
                icon = { Icon(item.icon, contentDescription = item.label) },
                modifier = Modifier,
                colors = NavigationDrawerItemDefaults.colors() //estilo x defecto

            )
        }
    }
}

//funcion para rellenar la lista de items del menu
@Composable
fun defaultDrawerItems(
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit
): List<DrawerItem> = listOf(
    DrawerItem("ir a la paggina principar", Icons.Filled.Home, onHome),
    DrawerItem("ir al Inicio de sesion", Icons.Filled.AccountCircle, onHome),
    DrawerItem("ir al Registro", Icons.Filled.Person, onHome),





    )