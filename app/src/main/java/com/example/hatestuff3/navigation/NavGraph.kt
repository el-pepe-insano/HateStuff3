package com.example.hatestuff3.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.hatestuff3.ui.components.AppDrawer
import com.example.hatestuff3.ui.components.AppTopBar
import com.example.hatestuff3.ui.components.defaultDrawerItems
import com.example.hatestuff3.ui.screen.CreatePostScreen
import com.example.hatestuff3.ui.screen.HomeScreen
import com.example.hatestuff3.ui.screen.LoginScreen
import com.example.hatestuff3.ui.screen.RegisterScreen
import com.example.hatestuff3.ui.viewmodel.AuthViewModel
import com.example.hatestuff3.ui.viewmodel.PostViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    postViewModel: PostViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Route.Login.path
    ) {

        // 1. LOGIN
        composable(Route.Login.path) {
            LoginScreen(
                vm = authViewModel,
                onLoginOkNavigateHome = {
                    navController.navigate(Route.Home.path) {
                        popUpTo(Route.Login.path) { inclusive = true }
                    }
                },
                onGoRegister = {
                    navController.navigate(Route.Register.path)
                }
            )
        }

        // 2. REGISTRO
        composable(Route.Register.path) {
            RegisterScreen(
                vm = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Route.Home.path) {
                        popUpTo(Route.Login.path) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // 3. HOME (FEED) - SOLUCIONADO EL ERROR DE PARÁMETROS
        composable(Route.Home.path) {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            fun closeAndNav(destination: String) {
                scope.launch {
                    drawerState.close()
                    navController.navigate(destination)
                }
            }

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    AppDrawer(
                        currentRoute = Route.Home.path,
                        items = defaultDrawerItems(
                            onHome = { scope.launch { drawerState.close() } },
                            onLogin = { closeAndNav(Route.Login.path) },
                            onRegister = { /* No necesario */ }
                        )
                    )
                }
            ) {
                Scaffold(
                    topBar = {
                        AppTopBar(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onHome = { /* Ya estamos aquí */ },
                            onRegister = { /* Oculto */ },
                            onLogin = { navController.navigate(Route.Login.path) }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        // SOLUCIÓN: Pasamos el postViewModel que requiere la HomeScreen
                        HomeScreen(
                            postViewModel = postViewModel,
                            onCreatePostClick = {
                                navController.navigate(Route.NewPost.path)
                            }
                        )
                    }
                }
            }
        }

        // 4. CREAR NUEVO POST
        composable(Route.NewPost.path) {
            CreatePostScreen(
                onPostCreated = { text, imageUri ->
                    // Sincronizado con PostViewModel.submitPost
                    postViewModel.submitPost(content = text, uri = imageUri)
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
    }
}