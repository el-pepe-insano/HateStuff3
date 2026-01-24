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
import com.example.hatestuff3.ui.screen.HomeScreen
import com.example.hatestuff3.ui.screen.LoginScreenVm
import com.example.hatestuff3.ui.screen.RegisterScreen
import com.example.hatestuff3.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch


@Composable
fun AppNavGraph(
    navController: NavHostController, //libreria para manipular navegación (controlar navegacion)
    authViewModel: AuthViewModel
){
    //manejar el estado del drawer (menu lateral desplegable)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    //uso de corutina para manipular el cierre/apertura del drawer
    val scope = rememberCoroutineScope()

    //Helpers de navegaciones (para poder reutilizarlos)
    val goHome: () -> Unit = { navController.navigate(Route.Home.path) } //ir al Home
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) } //ir al Registro
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) } //ir al Login

    //contenedor principal para nuestro menu lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { //contenido del menu
            AppDrawer( //llamamos al component AppDrawer
                currentRoute = null,
                items = defaultDrawerItems(
                    onHome = {
                        scope.launch { drawerState.close() } //cierra el menu lateral
                        goHome() // helper de redireccion
                    },
                    onLogin = {
                        scope.launch { drawerState.close() } //cierra el menu lateral
                        goLogin() // helper de redireccion
                    },
                    onRegister = {
                        scope.launch { drawerState.close() } //cierra el menu lateral
                        goRegister() // helper de redireccion
                    }
                )

            )
        }
    ) {
        //dibujamos la ubicacion del topbar y las screen de mi app
        Scaffold( //contendor que me permite crear topBar
            topBar = { //barra de navegación superior
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } }, //abre el drawer
                    onHome = goHome,
                    onRegister = goRegister,
                    onLogin = goLogin
                )
            }
        ) { innerPadding -> //padding para evitar solapar contenidos
            //construir el contenedor para mostrar los destinos de las navegaciones
            NavHost(
                navController = navController, //controlador de navegacion
                startDestination = Route.Home.path, //primera pagina a mostrar al iniciar la app
                modifier = Modifier.padding(innerPadding) //respeta el espacio del topBar
            ){
                //screen aceptadas para dibujar
                composable(Route.Home.path){
                    HomeScreen(
                        onGoLogin = goLogin,
                        onGoRegister = goRegister
                    )
                }
                composable(Route.Login.path){
                    LoginScreenVm(
                        onLoginOkNavigateHome = goLogin,
                        onGoRegister = goRegister
                    )
                }
                composable(Route.Register.path){
                    RegisterScreen(
                        onGoLogin = goLogin,
                        onRegistered = goRegister
                    )
                }
            }
        }
    }

}