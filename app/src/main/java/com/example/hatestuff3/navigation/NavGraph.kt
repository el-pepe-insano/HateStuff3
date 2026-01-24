package com.example.hatestuff3.navigation

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
import com.example.hatestuff3.ui.screen.FeedScreen
import com.example.hatestuff3.ui.screen.HomeScreen
import com.example.hatestuff3.ui.screen.LoginScreenVm
import com.example.hatestuff3.ui.screen.RegisterScreen
import com.example.hatestuff3.ui.viewmodel.AuthViewModel
import com.example.hatestuff3.ui.viewmodel.PostViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    postViewModel: PostViewModel
){
    //manejar el estado del drawer (menu lateral desplegable)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    //uso de corutina para manipular el cierre/apertura del drawer
    val scope = rememberCoroutineScope()

    //Helpers de navegaciones
    val goHome: () -> Unit = { navController.navigate(Route.Home.path) } //ir al Home
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) } //ir al Registro
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) } //ir al Login
    val goFeed: () -> Unit = { navController.navigate("feed") } // Ir al Feed

    //contenedor principal para nuestro menu lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = null,
                items = defaultDrawerItems(
                    onHome = {
                        scope.launch { drawerState.close() }
                        goHome()
                    },
                    onLogin = {
                        scope.launch { drawerState.close() }
                        goLogin()
                    },
                    onRegister = {
                        scope.launch { drawerState.close() }
                        goRegister()
                    }
                )
            )
        }
    ) {
        //dibujamos la ubicacion del topbar y las screen de mi app
        Scaffold(
            topBar = {
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onHome = goHome,
                    onRegister = goRegister,
                    onLogin = goLogin
                )
            }
        ) { innerPadding ->
            //construir el contenedor para mostrar los destinos de las navegaciones
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ){
                // HOME
                composable(Route.Home.path){
                    HomeScreen(
                        onGoLogin = goLogin,
                        onGoRegister = goRegister
                    )
                }

                // LOGIN
                composable(Route.Login.path){
                    LoginScreenVm(
                        vm = authViewModel, // <--- ¡AQUÍ ESTABA EL ERROR! AGREGADO.
                        onLoginOkNavigateHome = goFeed,
                        onGoRegister = goRegister
                    )
                }

                // REGISTRO
                composable(Route.Register.path){
                    RegisterScreen(
                        onGoLogin = goLogin,
                        onRegistered = goRegister
                    )
                }

                // FEED (MURO)
                composable("feed"){
                    FeedScreen(
                        navController = navController,
                        postViewModel = postViewModel,
                        userId = 1
                    )
                }
            }
        }
    }
}