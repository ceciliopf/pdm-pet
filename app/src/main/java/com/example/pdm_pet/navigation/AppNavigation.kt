package com.example.pdm_pet.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pdm_pet.features.auth.LoginScreen
import com.example.pdm_pet.features.auth.RegisterScreen


object AppRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
}

@Composable
fun AppNavigationGraph() {

    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN
    ) {


        composable(AppRoutes.LOGIN) {
            LoginScreen(

                onNavigateToRegister = {
                    navController.navigate(AppRoutes.REGISTER)
                }
            )
        }

        // Rota 2: Tela de Cadastro
        composable(AppRoutes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(AppRoutes.LOGIN)
                }
            )
        }

        // TODO: Adicionar rotas para "Feed", "Perfil do Animal", etc.
    }
}