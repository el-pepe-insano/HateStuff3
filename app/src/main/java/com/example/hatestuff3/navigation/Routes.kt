package com.example.hatestuff3.navigation

sealed class Route(val path: String){
    data object  Login: Route("login")
    data object Home: Route("Home")
    data object Register: Route("register")
    data object perfil: Route("perfil")
    data object newPost: Route("publicacion")
}