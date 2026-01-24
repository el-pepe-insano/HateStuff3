package com.example.hatestuff3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.hatestuff3.data.local.database.AppDatabase
import com.example.hatestuff3.data.local.database.repository.PostRepository
import com.example.hatestuff3.data.local.database.repository.UserRepository
import com.example.hatestuff3.navigation.AppNavGraph
import com.example.hatestuff3.ui.viewmodel.AuthViewModel
import com.example.hatestuff3.ui.viewmodel.AuthViewModelFactory
import com.example.hatestuff3.ui.viewmodel.PostViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot(){
    // --- 1. CONFIGURACIÓN DE BASE DE DATOS Y REPOSITORIOS ---
    val context = LocalContext.current.applicationContext

    // Instancia de la BD
    val db = AppDatabase.getInstance(context)

    // DAOs
    val userDao = db.userDao()
    val postDao = db.postDao() // <--- Nuevo

    // Repositorios
    val userRepository = UserRepository(userDao)
    val postRepository = PostRepository(postDao) // <--- Nuevo

    // --- 2. CONFIGURACIÓN DE VIEWMODELS ---

    // Creamos la fábrica con AMBOS repositorios
    val factory = AuthViewModelFactory(userRepository, postRepository)

    // ViewModel de Autenticación (Login/Registro)
    val authViewModel: AuthViewModel = viewModel(factory = factory)

    // ViewModel de Posts (Muro/Feed) <--- Nuevo (listo para usarse)
    val postViewModel: PostViewModel = viewModel(factory = factory)

    // --- 3. NAVEGACIÓN ---
    val navController = rememberNavController()

    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            // Aún no pasamos postViewModel aquí para evitar errores hasta que editemos AppNavGraph
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                postViewModel = postViewModel
            )
        }
    }
}