package com.example.pdm_pet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.pdm_pet.navigation.AppNavigationGraph // <-- Importe o Graph
import com.example.pdm_pet.ui.theme.Pdm_petTheme // <-- Importe seu tema

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Usamos o Tema do app (que você já tinha)
            Pdm_petTheme {
                // E chamamos o nosso Mapa de Navegação!
                AppNavigationGraph()
            }
        }
    }
}