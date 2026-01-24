package com.example.hatestuff3.navigation

sealed class Route(val path: String){
    data object Login: Route("login")
    data object Home: Route("home")        // Cambié "Home" a "home" (minúscula)
    data object Register: Route("register")
    data object Profile: Route("profile")  // Cambié "perfil" a "Profile" (inglés y mayúscula)
    data object NewPost: Route("create_post") // Cambié "newPost" a "NewPost". Path "create_post" es más claro.
}