package com.example.hatestuff3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.hatestuff3.data.local.database.AppDatabase
import com.example.hatestuff3.navigation.AppNavGraph
import com.example.hatestuff3.ui.theme.HateStuff3Theme
import com.example.hatestuff3.ui.viewmodel.AdminViewModel
import com.example.hatestuff3.ui.viewmodel.AuthViewModel
import com.example.hatestuff3.ui.viewmodel.AuthViewModelFactory
import com.example.hatestuff3.ui.viewmodel.PostViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val database = AppDatabase.getDatabase(applicationContext)
        val combinedFactory = AuthViewModelFactory(
            userDao = database.userDao(),
            postDao = database.postDao(),
            commentDao = database.commentDao()
        )

        setContent {
            HateStuff3Theme {
                val navController = rememberNavController()

                val authViewModel: AuthViewModel = viewModel(factory = combinedFactory)
                val postViewModel: PostViewModel = viewModel(factory = combinedFactory)
                val adminViewModel: AdminViewModel = viewModel(factory = combinedFactory)

                AppNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    postViewModel = postViewModel,
                    adminViewModel = adminViewModel
                )
            }
        }
    }
}