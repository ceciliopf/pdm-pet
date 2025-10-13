package com.example.pdm_pet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

// A MainActivity é a "porta de entrada" principal do seu aplicativo Android.
// Ela herda de ComponentActivity, que é a classe base para atividades que usam Jetpack Compose.
class MainActivity : ComponentActivity() {

    // O método onCreate é chamado quando a Activity é criada pela primeira vez.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent é a função principal do Jetpack Compose.
        // O código dentro deste bloco define o layout da sua atividade.
        setContent {
            // É uma boa prática envolver seu aplicativo em um tema.
            // Se você criou um projeto novo com Compose, ele já vem com um tema
            // (geralmente nomeado como "SeuProjetoTheme").
            // Aqui, usamos o MaterialTheme padrão como exemplo.
            MaterialTheme {
                // Surface é um container que representa uma "superfície" da UI.
                // Usamos o modifier fillMaxSize() para que ele ocupe a tela inteira.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background // Define a cor de fundo padrão do tema
                ) {
                    // Aqui, finalmente, chamamos a tela de login que criamos!
                    // É esta linha que faz a sua UI aparecer na tela.
                    LoginScreen()
                }
            }
        }
    }
}