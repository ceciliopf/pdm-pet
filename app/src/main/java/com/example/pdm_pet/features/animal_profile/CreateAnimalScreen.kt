package com.example.pdm_pet.features.animal_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pdm_pet.ui.theme.caramelColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAnimalScreen(
    onNavigateBack: () -> Unit
) {
    // Estado local simples para o formulário (depois movemos para ViewModel)
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Pet", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Permite rolar se o teclado abrir
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. ÁREA DA FOTO (Placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                    .clickable { /* TODO: Abrir Câmera */ },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Foto",
                        modifier = Modifier.size(48.dp),
                        tint = Color.DarkGray
                    )
                    Text("Toque para adicionar foto", color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. FORMULÁRIO
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome (ou apelido)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição (saúde, comportamento)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Simulação de localização (depois pegaremos do GPS)
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Ponto de Referência") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. BOTÃO SALVAR
            Button(
                onClick = {
                    // TODO: Salvar no Firebase
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = caramelColor)
            ) {
                Text("Salvar Pet", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}