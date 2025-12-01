package com.example.pdm_pet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pdm_pet.ui.theme.caramelColor // Importe sua cor

@Composable
fun AnimalCard(
    name: String,
    description: String,
    location: String,
    status: String, // Ex: "Na Rua", "Para Adoção"
    gender: String, // "Macho" ou "Fêmea"
    onAdoptClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), // Espaçamento externo
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // 1. ÁREA DA FOTO (Placeholder por enquanto)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray), // Cor de fundo temporária
                contentAlignment = Alignment.Center
            ) {
                // Aqui entraria a imagem real (AsyncImage).
                // Por enquanto, um ícone gigante:
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )

                // Badge de Status (ex: "Na Rua") sobre a foto
                AssistChip(
                    onClick = { },
                    label = { Text(status, fontWeight = FontWeight.Bold) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = caramelColor.copy(alpha = 0.9f),
                        labelColor = Color.White
                    ),
                    border = null
                )
            }

            // 2. INFORMAÇÕES
            Column(modifier = Modifier.padding(16.dp)) {

                // Nome e Gênero
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // Ícone de gênero simples
                    Text(
                        text = if (gender == "Macho") "♂" else "♀",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (gender == "Macho") Color.Blue else Color.Magenta
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Localização
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Localização",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = location, color = Color.Gray, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Descrição curta
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    maxLines = 2 // Limita a 2 linhas
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. BOTÕES DE AÇÃO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botões pequenos (Like e Share)
                    Row {
                        IconButton(onClick = { /* TODO: Like */ }) {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = "Favoritar", tint = caramelColor)
                        }
                        IconButton(onClick = { /* TODO: Share */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Compartilhar", tint = Color.Gray)
                        }
                    }

                    // Botão Principal (Adotar)
                    Button(
                        onClick = onAdoptClick,
                        colors = ButtonDefaults.buttonColors(containerColor = caramelColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Quero Adotar", color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimalCardPreview() {
    AnimalCard(
        name = "Caramelo",
        description = "Encontrado perto da praça, parece dócil mas está com fome.",
        location = "Centro, Uberaba - 500m",
        status = "Na Rua",
        gender = "Macho",
        onAdoptClick = {}
    )
}