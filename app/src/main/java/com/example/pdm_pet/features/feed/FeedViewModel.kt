
package com.example.pdm_pet.features.feed

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.pdm_pet.data.model.AnimalStatus // Se você ainda não criou este Enum, pode usar String por enquanto

data class AnimalUiState(
    val id: Int,
    val name: String,
    val description: String,
    val location: String,
    val status: String,
    val gender: String
)

class FeedViewModel : ViewModel() {


    val animals = mutableStateListOf<AnimalUiState>()

    init {
        // Populando com dados falsos para testar o layout
        loadMockData()
    }

    private fun loadMockData() {
        animals.add(
            AnimalUiState(
                1, "Caramelo", "Super dócil, encontrado perto da padaria.",
                "Centro - 500m", "Na Rua", "Macho"
            )
        )
        animals.add(
            AnimalUiState(
                2, "Belinha", "Parece assustada, precisa de lar temporário.",
                "Bairro Abadia - 2km", "Precisa de Ajuda", "Fêmea"
            )
        )
        animals.add(
            AnimalUiState(
                3, "Rex", "Está mancando um pouco, mas é brincalhão.",
                "Pq. das Acácias - 1.5km", "Na Rua", "Macho"
            )
        )
        animals.add(
            AnimalUiState(
                4, "Princesa", "Resgatada ontem, disponível para adoção responsável.",
                "ONG Amor de Patas", "Para Adoção", "Fêmea"
            )
        )
    }
}