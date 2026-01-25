package com.example.hatestuff3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.hatestuff3.data.local.database.AppDatabase
import com.example.hatestuff3.navigation.AppNavGraph
import com.example.hatestuff3.ui.theme.HateStuff3Theme
import com.example.hatestuff3.ui.viewmodel.AuthViewModel
import com.example.hatestuff3.ui.viewmodel.AuthViewModelFactory // Asegúrate que tu Factory esté aquí o en ui.factory
import com.example.hatestuff3.ui.viewmodel.PostViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Obtener la instancia de la Base de Datos
        val database = AppDatabase.getDatabase(applicationContext)

        // 2. Usar TU Factory personalizada
        // AHORA LE PASAMOS LOS 3 DAOS: User, Post y Comment
        val combinedFactory = AuthViewModelFactory(
            userDao = database.userDao(),
            postDao = database.postDao(),
            commentDao = database.commentDao() // <--- ESTO ES LO QUE FALTABA
        )

        setContent {
            HateStuff3Theme {
                val navController = rememberNavController()

                // 3. Instanciar los ViewModels usando la MISMA factory
                // La factory detectará automáticamente si necesitas Auth o Post
                val authViewModel: AuthViewModel = viewModel(factory = combinedFactory)
                val postViewModel: PostViewModel = viewModel(factory = combinedFactory)

                // 4. Iniciar la navegación
                AppNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    postViewModel = postViewModel
                )
            }
        }
    }
}