package com.example.pdm_pet.features.animal_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    LaunchedEffect(animalId) {
        val id = animalId.toLongOrNull() ?: 0L
        // Passamos o contexto para o ViewModel pegar o GPS
        viewModel.loadAnimal(context, id)
    }

    val animal = viewModel.animal
    val creator = viewModel.creatorUser // Dados do dono do post
    val isLoading = viewModel.isLoading
    val errorMsg = viewModel.errorMsg

    Scaffold(
        bottomBar = {
            if (!isLoading && animal != null) {
                BottomAppBar(containerColor = Color.White, tonalElevation = 8.dp) {
                    Button(
                        onClick = { /* Lógica de adoção */ },
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
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
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = caramelColor)
            }
        } else if (animal != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
            ) {
                // --- FOTO DO ANIMAL ---
                Box(modifier = Modifier.fillMaxWidth().height(300.dp).background(Color.LightGray)) {
                    val photoUrl = viewModel.getFullImageUrl(animal.photos?.firstOrNull())
                    if (photoUrl != null) {
                        AsyncImage(model = photoUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(16.dp).background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(50))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                }

                Column(modifier = Modifier.padding(24.dp)) {
                    // --- TÍTULO E GÊNERO ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(animal.provisionalName, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = caramelColor)
                        val genderIcon = if (animal.sex == "MALE") Icons.Default.Male else Icons.Default.Female
                        Icon(genderIcon, null, modifier = Modifier.size(32.dp), tint = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- LOCALIZAÇÃO E PROXIMIDADE ---
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))

                        // Exibição da Distância
                        val distText = if (animal.approximateDistance != null)
                            "Aprox. %.1f km de você".format(animal.approximateDistance)
                        else "Distância desconhecida"

                        Text(distText, fontSize = 16.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- CARD DO CRIADOR (QUEM POSTOU) ---
                    if (creator != null) {
                        Text("Publicado por", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Foto do Usuário
                                val userImgUrl = viewModel.getUserImageUrl(creator.profilePictureUrl)
                                if (userImgUrl != null) {
                                    AsyncImage(
                                        model = userImgUrl,
                                        contentDescription = "Foto do usuário",
                                        modifier = Modifier.size(50.dp).clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(50.dp).background(Color.Gray, CircleShape).padding(8.dp),
                                        tint = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    creator.name?.let { Text(it, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                                    Text("${creator.city ?: "?"} - ${creator.state ?: "?"}", fontSize = 12.sp, color = Color.DarkGray)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // --- SOBRE ---
                    Text("Sobre", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(animal.description ?: "Sem descrição.", fontSize = 16.sp, lineHeight = 24.sp, color = Color.DarkGray)

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- DETALHES ---
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        InfoCard("Idade", animal.approximateAge ?: "?")
                        InfoCard("Porte", animal.size ?: "?")
                        val statusDisplay = when (animal.status) {
                            "ON_STREET" -> "Na Rua"
                            "AVAILABLE_FOR_ADOPTION" -> "Para Adoção"
                            else -> animal.status ?: "?"
                        }
                        InfoCard("Status", statusDisplay)
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(errorMsg ?: "Erro desconhecido", color = Color.Red)
            }
        }
    }
}

// --- CERTIFIQUE-SE DE QUE ESTA FUNÇÃO ESTÁ NO FINAL DO ARQUIVO ---
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