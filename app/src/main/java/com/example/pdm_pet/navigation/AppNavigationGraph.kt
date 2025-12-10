package com.example.pdm_pet.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pdm_pet.features.animal_profile.CreateAnimalScreen
import com.example.pdm_pet.features.auth.LoginScreen
import com.example.pdm_pet.features.auth.RegisterScreen
import com.example.pdm_pet.features.feed.FeedScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.pdm_pet.features.animal_profile.AnimalDetailsScreen

// 1. Definição das Rotas (URLs das telas)
object AppRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FEED = "feed"
    const val CREATE_ANIMAL = "create_animal"

    const val ANIMAL_DETAILS = "animal_details/{animalId}"
}

@Composable
fun AppNavigationGraph() {
    // 2. Controlador de navegação
    val navController = rememberNavController()

    NavHost(
        navController = navController,

        startDestination = AppRoutes.LOGIN
    ) {

        // --- TELA DE LOGIN ---
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(AppRoutes.REGISTER)
                }

            )
        }

        // --- TELA DE CADASTRO ---
        composable(AppRoutes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {

                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // --- TELA DE FEED ---
        composable(AppRoutes.FEED) {
            FeedScreen(
                onNavigateToCreateAnimal = {
                    navController.navigate(AppRoutes.CREATE_ANIMAL)
                },
                onNavigateToDetails = { animalId ->
                    // NAVEGAÇÃO COM ARGUMENTO: Substituímos o {animalId} pelo valor real
                    navController.navigate("animal_details/$animalId")
                }
            )
        }

        // --- TELA DE CRIAR ANIMAL (NOVO BLOCO) ---
        composable(AppRoutes.CREATE_ANIMAL) {
            CreateAnimalScreen(
                onNavigateBack = {
                    navController.popBackStack() // Volta para o Feed
                }
            )
        }

        composable(
            route = AppRoutes.ANIMAL_DETAILS,
            arguments = listOf(navArgument("animalId") { type = NavType.StringType })
        ) { backStackEntry ->

            val animalId = backStackEntry.arguments?.getString("animalId") ?: "0"

            AnimalDetailsScreen(
                animalId = animalId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

    }
}