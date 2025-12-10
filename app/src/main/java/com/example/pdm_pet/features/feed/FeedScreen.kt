package com.example.pdm_pet.features.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pdm_pet.ui.components.AnimalCard
import com.example.pdm_pet.ui.theme.caramelColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    // Injeção do ViewModel
    feedViewModel: FeedViewModel = viewModel(),
    onNavigateToCreateAnimal: () -> Unit,
    onNavigateToDetails: (String) -> Unit
) {
    // Observamos a lista de animais do ViewModel (agora vindo da API)
    val animals = feedViewModel.animals

    // Opcional: Recarrega a lista sempre que a tela for aberta
    LaunchedEffect(Unit) {
        feedViewModel.fetchAnimals()
    }

    Scaffold(
        // 1. Barra Superior
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Adote um Amigo",
                        fontWeight = FontWeight.Bold,
                        color = caramelColor
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },

        // 2. Botão Flutuante (FAB) para Criar Novo Animal
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToCreateAnimal() },
                containerColor = caramelColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Cadastrar Animal")
            }
        },

        containerColor = Color(0xFFF5F5F5) // Fundo cinza claro
    ) { paddingValues ->

        if (animals.isEmpty()) {
            // Estado de Lista Vazia (ou Carregando)
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Buscando amigos próximos...", color = Color.Gray)
            }
        } else {
            // Lista de Animais
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), // Respeita o espaço da TopBar
                contentPadding = PaddingValues(16.dp), // Margem interna
                verticalArrangement = Arrangement.spacedBy(16.dp) // Espaço entre os cards
            ) {
                items(animals) { animal ->
                    AnimalCard(
                        name = animal.name,
                        description = animal.description,
                        photoUrl = animal.photoUrl, // URL vinda do ViewModel
                        location = "Aprox. ${animal.distance}", // Ex: "Aprox. 2.5 km"
                        status = animal.status,
                        gender = animal.gender,
                        onAdoptClick = {
                            // Passa o ID para a tela de detalhes
                            onNavigateToDetails(animal.id.toString())
                        }
                    )
                }
            }
        }
    }
}