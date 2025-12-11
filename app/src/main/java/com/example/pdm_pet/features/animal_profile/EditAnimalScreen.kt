package com.example.pdm_pet.features.animal_profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pdm_pet.ui.theme.caramelColor

// Reutilizamos o Dropdown que você já tem no CreateAnimalScreen.kt
// Se ele estiver privado lá, torne-o público ou copie e cole aqui.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAnimalScreen(
    animalId: String,
    onNavigateBack: () -> Unit,
    viewModel: EditAnimalViewModel = viewModel()
) {
    // Carrega os dados ao abrir
    LaunchedEffect(animalId) {
        viewModel.loadAnimalData(animalId.toLongOrNull() ?: 0L)
    }

    // Se salvou com sucesso, volta
    LaunchedEffect(viewModel.isSuccess) {
        if (viewModel.isSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Pet", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        if (viewModel.isLoading && viewModel.name.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = caramelColor)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Campos de Edição
                OutlinedTextField(
                    value = viewModel.name, onValueChange = { viewModel.name = it },
                    label = { Text("Nome Provisório") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.description, onValueChange = { viewModel.description = it },
                    label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth(), minLines = 3
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Dropdowns (Usando os valores do ViewModel)
                // Nota: Copie a função SimpleDropdown do CreateAnimalScreen se necessário
                // Aqui estou simplificando a exibição para focar na lógica:

                Text("Status Atual: ${viewModel.status}", fontWeight = FontWeight.Bold)
                // (Adicione aqui os Dropdowns para Sexo, Porte e Status igual na tela de Criar, ligando nas variáveis do viewModel)

                Spacer(modifier = Modifier.height(24.dp))

                if (viewModel.message != null) {
                    Text(viewModel.message!!, color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = { viewModel.saveChanges() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = caramelColor),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text("Salvar Alterações", color = Color.White)
                    }
                }
            }
        }
    }
}