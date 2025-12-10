package com.example.pdm_pet.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pdm_pet.features.animal_profile.AnimalDetailsScreen
import com.example.pdm_pet.features.animal_profile.CreateAnimalScreen
import com.example.pdm_pet.features.auth.LoginScreen
import com.example.pdm_pet.features.auth.RegisterScreen
import com.example.pdm_pet.features.feed.FeedScreen

@Composable
fun AppNavigationGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        // --- TELA DE LOGIN ---
        composable("login") {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onNavigateToHome = {
                    // Navega para o Feed e remove o Login da pilha (BackStack)
                    navController.navigate("feed") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // --- TELA DE CADASTRO (USUÁRIO) ---
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // --- TELA DE FEED (HOME) ---
        composable("feed") {
            FeedScreen(
                onNavigateToCreateAnimal = {
                    navController.navigate("create_animal")
                },
                onNavigateToDetails = { animalId ->
                    navController.navigate("details/$animalId")
                }
            )
        }

        // --- TELA DE CRIAR ANIMAL ---
        composable("create_animal") {
            CreateAnimalScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // --- TELA DE DETALHES DO ANIMAL ---
        composable(
            route = "details/{animalId}",
            arguments = listOf(navArgument("animalId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Recupera o ID passado na rota
            val animalId = backStackEntry.arguments?.getString("animalId") ?: "0"

            AnimalDetailsScreen(
                animalId = animalId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}