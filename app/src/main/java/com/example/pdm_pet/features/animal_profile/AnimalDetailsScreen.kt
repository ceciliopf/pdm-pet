package com.example.pdm_pet.features.animal_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Importação corrigida
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
    animalId: String,
    onNavigateBack: () -> Unit,
    viewModel: AnimalDetailsViewModel = viewModel()
) {
    // Carrega o animal assim que a tela abre
    LaunchedEffect(animalId) {
        val id = animalId.toLongOrNull() ?: 0L
        viewModel.loadAnimal(id)
    }

    val animal = viewModel.animal
    val isLoading = viewModel.isLoading
    val errorMsg = viewModel.errorMsg

    Scaffold(
        bottomBar = {
            // Só mostra o botão de adotar se carregou com sucesso
            if (!isLoading && animal != null) {
                BottomAppBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    Button(
                        onClick = { /* Lógica de adoção aqui */ },
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
        if (isLoading) {
            // --- TELA DE CARREGAMENTO ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = caramelColor)
            }
        } else if (animal != null) {
            // --- TELA DE SUCESSO (MOSTRAR DADOS) ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
            ) {
                // 1. FOTO
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.LightGray)
                ) {
                    val photoUrl = viewModel.getFullImageUrl(animal.photos?.firstOrNull())

                    if (photoUrl != null) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Foto do animal",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Botão Voltar
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(50))
                    ) {
                        // Ícone atualizado
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                }

                // 2. INFORMAÇÕES
                Column(modifier = Modifier.padding(24.dp)) {
                    // Nome e Ícone de Sexo
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

                    // Localização / Distância
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Aprox. ${animal.approximateDistance ?: "?"} km",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Descrição
                    Text("Sobre", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = animal.description ?: "Sem descrição.",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Cards de Detalhes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoCard(label = "Idade", value = animal.approximateAge ?: "?")
                        InfoCard(label = "Porte", value = animal.size ?: "?")

                        // Traduz o status para exibir bonito
                        val statusDisplay = when (animal.status) {
                            "ON_STREET" -> "Na Rua"
                            "AVAILABLE_FOR_ADOPTION" -> "Para Adoção"
                            "TEMP_HOME" -> "Lar Temp."
                            "ADOPTED" -> "Adotado"
                            else -> animal.status ?: "?"
                        }
                        InfoCard(label = "Status", value = statusDisplay)
                    }
                }
            }
        } else {
            // --- TELA DE ERRO ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Male, // Pode usar um icone de alerta se quiser
                        contentDescription = "Erro",
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // AQUI MOSTRAMOS O MOTIVO DO ERRO:
                    Text(
                        text = errorMsg ?: "Não foi possível carregar os detalhes.",
                        color = Color.Red,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onNavigateBack) {
                        Text("Voltar")
                    }
                }
            }
        }
    }
}

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
                fontSize = 13.sp,
                maxLines = 1
            )
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}