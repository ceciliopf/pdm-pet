package com.example.pdm_pet.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pdm_pet.features.animal_profile.AnimalDetailsScreen
import com.example.pdm_pet.features.animal_profile.CreateAnimalScreen
import com.example.pdm_pet.features.animal_profile.EditAnimalScreen
import com.example.pdm_pet.features.auth.LoginScreen
import com.example.pdm_pet.features.auth.RegisterScreen
import com.example.pdm_pet.features.feed.FeedScreen
import com.example.pdm_pet.features.profile.ProfileScreen
import com.example.pdm_pet.utils.UserSession

@Composable
fun AppNavigationGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Decide onde começar baseada se já existe login salvo
    val startRoute = if (UserSession.isLoggedIn) "feed" else "login"

    NavHost(navController = navController, startDestination = startRoute) {

        // --- LOGIN ---
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToHome = {
                    navController.navigate("feed") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // --- REGISTER ---
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // --- FEED ---
        composable("feed") {
            FeedScreen(
                onNavigateToCreateAnimal = { navController.navigate("create_animal") },
                onNavigateToDetails = { id -> navController.navigate("details/$id") },
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }

        // --- PERFIL ---
        composable("profile") {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    UserSession.logout(context) // Limpa sessão do disco
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true } // Limpa pilha
                    }
                }
            )
        }

        // --- CRIAR ANIMAL ---
        composable("create_animal") {
            CreateAnimalScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- DETALHES DO ANIMAL ---
        composable(
            route = "details/{animalId}",
            arguments = listOf(navArgument("animalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getString("animalId") ?: "0"
            AnimalDetailsScreen(
                animalId = animalId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate("edit_animal/$id") }
            )
        }

        // --- EDITAR ANIMAL (ROTA NOVA) ---
        composable(
            route = "edit_animal/{animalId}",
            arguments = listOf(navArgument("animalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getString("animalId") ?: "0"
            EditAnimalScreen(
                animalId = animalId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}