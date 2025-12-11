package com.example.pdm_pet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.pdm_pet.navigation.AppNavigationGraph
import com.example.pdm_pet.ui.theme.Pdm_petTheme
import com.example.pdm_pet.utils.UserSession // Importe o UserSession

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. TENTA RESTAURAR A SESSÃO ANTES DE ABRIR O APP
        UserSession.checkSession(this)

        setContent {
            Pdm_petTheme {
                AppNavigationGraph()
            }
        }
    }
}