package com.example.hatestuff3.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hatestuff3.R

@Composable
fun AppTopBar(
    onOpenDrawer: () -> Unit,
    onHome: (() -> Unit)? = null,
    onLogin: (() -> Unit)? = null,
    onAdminClick: (() -> Unit)? = null,
    // NUEVOS PARÁMETROS: Para que la barra funcione de verdad
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    // Hemos eliminado 'var searchText' porque ahora el texto viene de fuera (del ViewModel)

    val hateBlack = Color(0xFF000000)
    val bloodRed = Color(0xFF8B0000)
    val offWhite = Color(0xFFD1D1D1)
    val darkGray = Color(0xFF1A1A1A)

    Surface(
        color = hateBlack,
        modifier = Modifier.fillMaxWidth().drawBehind {
            val strokeWidth = 3.dp.toPx()
            val y = size.height - strokeWidth / 2
            drawLine(Color(0xFF660000), Offset(0f, y), Offset(size.width, y), strokeWidth)
        }
    ) {
        Column(modifier = Modifier.padding(bottom = 10.dp)) {
            // PISO 1: LOGO
            Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.logoblanco),
                    contentDescription = "Logo HateStuff",
                    modifier = Modifier.height(55.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // PISO 2: NAVEGACIÓN + BUSCADOR
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onOpenDrawer) {
                    Icon(Icons.Filled.Menu, "Menú", tint = offWhite)
                }

                Box(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                    HateSearchBar(
                        text = searchQuery, // Usamos el dato real
                        onTextChange = onSearchQueryChange, // Enviamos el cambio al ViewModel
                        placeholder = "Buscar usuarios...",
                        cursorColor = bloodRed,
                        backgroundColor = darkGray,
                        textColor = offWhite
                    )
                }

                // ESCUDO DE ADMIN
                if (onAdminClick != null) {
                    IconButton(onClick = onAdminClick) {
                        Icon(Icons.Filled.Security, "Admin Panel", tint = bloodRed)
                    }
                }

                // Menú de opciones
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Filled.MoreVert, "Opciones", tint = offWhite)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(hateBlack)
                    ) {
                        if (onHome != null) {
                            DropdownMenuItem(
                                text = { Text("HOME", color = bloodRed, fontWeight = FontWeight.Bold) },
                                onClick = { showMenu = false; onHome() }
                            )
                        }
                        if (onLogin != null) {
                            DropdownMenuItem(
                                text = { Text("LOGIN", color = bloodRed, fontWeight = FontWeight.Bold) },
                                onClick = { showMenu = false; onLogin() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HateSearchBar(text: String, onTextChange: (String) -> Unit, placeholder: String, cursorColor: Color, backgroundColor: Color, textColor: Color) {
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        textStyle = TextStyle(color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium),
        cursorBrush = SolidColor(cursorColor),
        singleLine = true,
        decorationBox = { innerTextField ->
            Row(modifier = Modifier.fillMaxWidth().height(40.dp).background(backgroundColor, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Search, null, tint = textColor.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (text.isEmpty()) Text(placeholder, color = textColor.copy(alpha = 0.5f), fontSize = 14.sp)
                    innerTextField()
                }
                if (text.isNotEmpty()) IconButton(onClick = { onTextChange("") }, modifier = Modifier.size(18.dp)) { Icon(Icons.Filled.Close, "Borrar", tint = textColor.copy(alpha = 0.5f)) }
            }
        }
    )
}