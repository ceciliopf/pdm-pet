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
import com.example.pdm_pet.features.profile.ProfileScreen
import com.example.pdm_pet.utils.UserSession

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
                    // Vai para o Feed e remove o Login da pilha (para não voltar ao login se apertar 'Voltar')
                    navController.navigate("feed") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // --- TELA DE CADASTRO ---
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = {
                    // Volta para a tela anterior (Login)
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
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                }
            )
        }

        // --- TELA DE PERFIL ---
        composable("profile") {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    UserSession.logout() // Limpa o token da sessão
                    // Volta para o Login e limpa toda a pilha de telas
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
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