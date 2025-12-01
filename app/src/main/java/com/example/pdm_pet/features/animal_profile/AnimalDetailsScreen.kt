package com.example.pdm_pet.features.animal_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Female
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pdm_pet.ui.theme.caramelColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailsScreen(
    animalId: String, // Recebemos o ID do animal
    onNavigateBack: () -> Unit
) {
    // SIMULAÇÃO: Num app real, usaríamos esse ID para buscar no banco de dados.
    // Por enquanto, vamos mostrar dados fixos para testar o layout.
    val name = if(animalId == "1") "Caramelo" else "Pet $animalId"
    val gender = "Macho"

    Scaffold(
        bottomBar = {
            // Barra fixa no fundo com o botão de ADOTAR
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                Button(
                    onClick = { /* Iniciar Adoção */ },
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
    ) { paddingValues ->
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
                // Botão de Voltar sobre a foto
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(50))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
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
                    Text(text = name, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = caramelColor)
                    Icon(
                        imageVector = if(gender == "Macho") Icons.Default.Male else Icons.Default.Female,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Localização
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Centro, Uberaba - 500m", fontSize = 16.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sobre
                Text("Sobre", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Este cão foi encontrado perto da padaria. Ele é extremamente dócil, gosta de crianças e parece já ter tido um lar. Está um pouco magro mas saudável.",
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Detalhes (Chips)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoCard(label = "Idade", value = "2 Anos")
                    InfoCard(label = "Porte", value = "Médio")
                    InfoCard(label = "Castrado", value = "Não")
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
            Text(value, fontWeight = FontWeight.Bold, color = caramelColor)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}