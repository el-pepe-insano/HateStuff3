package com.example.hatestuff3.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.hatestuff3.ui.screen.ProfileScreen
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
    val currentUser by authViewModel.currentUser.collectAsState()

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
                    navController.navigate(Route.Login.path) {
                        popUpTo(Route.Register.path) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // 3. HOME
        composable(Route.Home.path) {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    AppDrawer(
                        currentRoute = Route.Home.path,
                        items = defaultDrawerItems(
                            onHome = { scope.launch { drawerState.close() } },
                            onProfile = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate(Route.Profile.path)
                                }
                            },
                            onLogout = {
                                scope.launch {
                                    drawerState.close()
                                    authViewModel.logout()
                                    // Limpiamos la pila al salir
                                    navController.navigate(Route.Login.path) {
                                        popUpTo(0)
                                    }
                                }
                            }
                        )
                    )
                }
            ) {
                Scaffold(
                    topBar = {
                        AppTopBar(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onHome = null, onLogin = null, onRegister = null
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
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

        // 4. PERFIL
        composable(Route.Profile.path) {
            // Verificación extra de seguridad
            if (currentUser != null) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    user = currentUser!!,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Route.Login.path) {
                            popUpTo(0)
                        }
                    }
                )
            } else {
                // Si no hay usuario, volver al login
                LaunchedEffect(Unit) {
                    navController.navigate(Route.Login.path) { popUpTo(0) }
                }
            }
        }

        // 5. NEW POST
        composable(Route.NewPost.path) {
            val userState by authViewModel.currentUser.collectAsState()

            CreatePostScreen(
                onPostCreated = { content, imageUri ->
                    userState?.let { user ->
                        // --- CORRECCIÓN AQUÍ ---
                        // Adaptado al nuevo PostViewModel con validaciones
                        postViewModel.submitPost(
                            content = content,
                            imageUri = imageUri?.toString(),
                            userName = user.name, // Ya no usamos userId
                            onSuccess = {
                                // Solo volvemos si se guardó correctamente
                                navController.popBackStack()
                            },
                            onError = {
                                // Opcional: Aquí podrías mostrar un aviso si falla
                                // Por ahora no hacemos nada para mantenerlo simple
                            }
                        )
                    }
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
    }
}