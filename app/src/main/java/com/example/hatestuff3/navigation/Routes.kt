package com.example.hatestuff3.navigation

sealed class Route(val path: String){
    data object Login: Route("login")
    data object Home: Route("home")
    data object Register: Route("register")
    data object Profile: Route("profile")
    data object NewPost: Route("create_post")
}