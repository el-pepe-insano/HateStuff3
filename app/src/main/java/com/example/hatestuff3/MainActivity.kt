package com.example.hatestuff3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.hatestuff3.data.local.database.repository.PostRepository
import com.example.hatestuff3.data.local.database.repository.UserRepository
import com.example.hatestuff3.data.remote.RemoteModule
import com.example.hatestuff3.navigation.AppNavGraph
import com.example.hatestuff3.ui.theme.HateStuff3Theme
import com.example.hatestuff3.ui.viewmodel.AdminViewModel
import com.example.hatestuff3.ui.viewmodel.AuthViewModel
import com.example.hatestuff3.ui.viewmodel.AuthViewModelFactory
import com.example.hatestuff3.ui.viewmodel.PostViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userApi = RemoteModule.userApi
        val postApi = RemoteModule.postApi
        val commentApi = RemoteModule.commentApi

        val userRepository = UserRepository(userApi)
        val postRepository = PostRepository(postApi, commentApi, applicationContext)

        val combinedFactory = AuthViewModelFactory(
            userRepository = userRepository,
            postRepository = postRepository
        )

        setContent {
            HateStuff3Theme {
                val navController = rememberNavController()

                val authViewModel: AuthViewModel = viewModel(factory = combinedFactory)
                val postViewModel: PostViewModel = viewModel(factory = combinedFactory)

                AppNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    postViewModel = postViewModel,
                    adminViewModel = viewModel(factory = combinedFactory)
                )
            }
        }
    }
}