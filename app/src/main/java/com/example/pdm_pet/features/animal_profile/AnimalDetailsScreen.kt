package com.example.pdm_pet.features.animal_profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailsScreen(
    animalId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit = {},
    viewModel: AnimalDetailsViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(animalId) {
        val id = animalId.toLongOrNull() ?: 0L
        viewModel.loadAnimal(context, id)
    }

    val animal = viewModel.animal
    val creator = viewModel.creatorUser
    val isLoading = viewModel.isLoading
    val errorMsg = viewModel.errorMsg
    var showDeleteDialog by remember { mutableStateOf(false) }

    // --- FUNÇÃO PARA ABRIR WHATSAPP ---
    fun openWhatsApp() {
        val phone = creator?.phone
        if (phone.isNullOrBlank()) {
            Toast.makeText(context, "Telefone do doador não disponível.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Limpa o número (deixa só dígitos)
            val cleanPhone = phone.filter { it.isDigit() }
            // Adiciona código do país se faltar (Assumindo Brasil 55)
            val finalPhone = if (cleanPhone.startsWith("55")) cleanPhone else "55$cleanPhone"

            val message = "Olá! Vi o ${animal?.provisionalName ?: "pet"} no App Patas Unidas e tenho interesse em adotar!"
            val url = "https://api.whatsapp.com/send?phone=$finalPhone&text=${URLEncoder.encode(message, "UTF-8")}"

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp não instalado.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        bottomBar = {
            if (!isLoading && animal != null) {
                BottomAppBar(containerColor = Color.White, tonalElevation = 8.dp) {

                    // BOTÃO ADOTAR -> ABRE WHATSAPP
                    Button(
                        onClick = { openWhatsApp() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = caramelColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        // Ícone do WhatsApp (opcional, pode usar um vetor SVG ou texto)
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Quero Adotar 🐾", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = caramelColor)
            }
        } else if (animal != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
            ) {
                // --- FOTO E CABEÇALHO ---
                Box(
                    modifier = Modifier.fillMaxWidth().height(300.dp).background(Color.LightGray)
                ) {
                    val photoUrl = viewModel.getFullImageUrl(animal.photos?.firstOrNull())
                    if (photoUrl != null) {
                        AsyncImage(model = photoUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(16.dp).background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }

                    if (viewModel.isOwner) {
                        Row(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                            IconButton(
                                onClick = { onNavigateToEdit(animalId) },
                                modifier = Modifier.background(Color.White.copy(alpha = 0.8f), CircleShape)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = caramelColor)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.background(Color.White.copy(alpha = 0.8f), CircleShape)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color.Red)
                            }
                        }
                    }
                }

                // --- CONTEÚDO ---
                Column(modifier = Modifier.padding(24.dp)) {
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

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        val distText = if (animal.approximateDistance != null) "Aprox. %.1f km".format(animal.approximateDistance) else "?"
                        Text(distText, fontSize = 16.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- CARD DO CRIADOR (COM CONTATO) ---
                    if (creator != null) {
                        Text("Publicado por", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                val userImgUrl = viewModel.getUserImageUrl(creator.profilePictureUrl)
                                if (userImgUrl != null) {
                                    AsyncImage(model = userImgUrl, contentDescription = null, modifier = Modifier.size(50.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                                } else {
                                    Icon(Icons.Default.Person, null, modifier = Modifier.size(50.dp).background(Color.Gray, CircleShape).padding(8.dp), tint = Color.White)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(creator.name ?: "Anônimo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    val contact = if (!creator.phone.isNullOrBlank()) "Contato: ${creator.phone}" else "Sem contato"
                                    Text(contact, fontSize = 12.sp, color = caramelColor)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    Text("Sobre", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(animal.description ?: "Sem descrição.", fontSize = 16.sp, lineHeight = 24.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(24.dp))

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
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(errorMsg ?: "Erro desconhecido", color = Color.Red)
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Excluir Postagem?") },
                text = { Text("Tem certeza que deseja remover este animal?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteAnimal(onSuccess = onNavigateBack)
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) { Text("Excluir") }
                },
                dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
            )
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
            Text(value, fontWeight = FontWeight.Bold, color = caramelColor, fontSize = 13.sp, maxLines = 1)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}