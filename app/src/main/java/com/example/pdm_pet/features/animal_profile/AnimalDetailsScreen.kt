package com.example.pdm_pet.features.animal_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pdm_pet.ui.theme.caramelColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailsScreen(
    animalId: String, // Recebemos o ID do animal vindo da navegação
    onNavigateBack: () -> Unit,
    viewModel: AnimalDetailsViewModel = viewModel() // Injeção do ViewModel
) {
    // 1. Ao entrar na tela (ou se o ID mudar), buscamos os dados na API
    LaunchedEffect(animalId) {
        viewModel.loadAnimal(animalId.toLongOrNull() ?: 0L)
    }

    // Observamos os estados do ViewModel
    val animal = viewModel.animal
    val isLoading = viewModel.isLoading

    Scaffold(
        bottomBar = {
            // Barra fixa no fundo com o botão de ADOTAR
            // Só mostramos se tiver carregado o animal com sucesso
            if (!isLoading && animal != null) {
                BottomAppBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    Button(
                        onClick = { /* TODO: Iniciar fluxo de Adoção */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = caramelColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Quero Adotar 🐾", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    ) { paddingValues ->

        // Lógica de Estado da UI
        if (isLoading) {
            // TELA DE CARREGAMENTO
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = caramelColor)
            }
        } else if (animal != null) {
            // TELA COM DADOS (SUCESSO)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Permite rolar a tela
                    .padding(paddingValues)
            ) {
                // 1. FOTO GRANDE (Topo)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.LightGray)
                ) {
                    // Pega a URL completa da primeira foto
                    val photoUrl = viewModel.getFullImageUrl(animal.photos?.firstOrNull())

                    if (photoUrl != null) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Foto do animal",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Botão de Voltar sobre a foto
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(50))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                }

                // 2. CONTEÚDO
                Column(modifier = Modifier.padding(24.dp)) {

                    // Cabeçalho (Nome e Sexo)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = animal.provisionalName,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = caramelColor
                        )

                        // Ícone dinâmico baseada no sexo
                        val genderIcon = if (animal.sex == "MALE" || animal.sex == "Macho") {
                            Icons.Default.Male
                        } else {
                            Icons.Default.Female
                        }

                        Icon(
                            imageVector = genderIcon,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Localização (Exemplo estático ou pegando do objeto se tiver endereço)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        // Se quiser calcular distância real, precisaria da lat/long do usuário aqui
                        Text(
                            text = "Aprox. ${animal.approximateDistance ?: "?"} km",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sobre
                    Text("Sobre", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = animal.description ?: "Nenhuma descrição fornecida.",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Detalhes (Chips)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoCard(label = "Idade", value = animal.approximateAge ?: "?")
                        InfoCard(label = "Porte", value = animal.size ?: "?")
                        // Tradução simples do status
                        val statusDisplay = when(animal.status) {
                            "ON_STREET" -> "Na Rua"
                            "AVAILABLE_FOR_ADOPTION" -> "Para Adoção"
                            "ADOPTED" -> "Adotado"
                            else -> animal.status ?: "?"
                        }
                        InfoCard(label = "Status", value = statusDisplay)
                    }
                }
            }
        } else {
            // TELA DE ERRO (Não encontrou ou falhou)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Não foi possível carregar os detalhes.", color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onNavigateBack) {
                        Text("Voltar")
                    }
                }
            }
        }
    }
}

// Componente auxiliar pequeno para mostrar infos (Idade, Porte)
@Composable
fun InfoCard(label: String, value: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        modifier = Modifier.size(width = 100.dp, height = 70.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                color = caramelColor,
                fontSize = 14.sp,
                maxLines = 1
            )
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}