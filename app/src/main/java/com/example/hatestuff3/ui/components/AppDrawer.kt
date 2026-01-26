package com.example.hatestuff3.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
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
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun AppDrawer(
    currentRoute: String?,
    items: List<DrawerItem>,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier = modifier) {
        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                selected = false,
                onClick = item.onClick,
                icon = { Icon(item.icon, contentDescription = item.label) },
                modifier = Modifier,
                colors = NavigationDrawerItemDefaults.colors()
            )
        }
    }
}

@Composable
fun defaultDrawerItems(
    onHome: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit
): List<DrawerItem> = listOf(
    DrawerItem("Inicio", Icons.Filled.Home, onHome),
    DrawerItem("Mi Perfil", Icons.Filled.Person, onProfile), // Navega al perfil real
    DrawerItem("Cerrar Sesión", Icons.Filled.ExitToApp, onLogout) // Ejecuta el logout
)