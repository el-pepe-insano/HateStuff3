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
import com.example.hatestuff3.data.local.database.repository.UserRepository
import com.example.hatestuff3.navigation.AppNavGraph
import com.example.hatestuff3.ui.viewmodel.AuthViewModel
import com.example.hatestuff3.ui.viewmodel.AuthViewModelFactory

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
    //manipular los datos para la creacion de la BD y el repositorio
    //obtenemos el contexto actual de la app
    val context = LocalContext.current.applicationContext
    //creamos u obtenemos la instancia de la BD
    val db = AppDatabase.getInstance(context)
    //creamos todos los DAO
    val userDao = db.userDao()
    //creamos los repositorios asociados a los DAO
    val userRepository = UserRepository(userDao)
    //creamos los viewmodel
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )

    //crear un cotrolador de navegación principal
    val navController = rememberNavController()
    MaterialTheme { //modifico la plantilla de diseño al de material design
        //contenedor principal para el fondo de mi app
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(navController = navController, authViewModel = authViewModel)
        }
    }
}