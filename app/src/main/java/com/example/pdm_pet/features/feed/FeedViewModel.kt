package com.example.pdm_pet.features.feed

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pdm_pet.data.remote.RetrofitClient
import kotlinx.coroutines.launch

// Estado de UI para cada Card
data class AnimalUiState(
    val id: Long,
    val name: String,
    val description: String,
    val photoUrl: String?, // Agora é a URL completa da internet
    val status: String?,
    val gender: String,
    val distance: String
)

class FeedViewModel : ViewModel() {

    // Lista observável pelo Jetpack Compose
    val animals = mutableStateListOf<AnimalUiState>()

    // URL base para montar o link da imagem
    private val BASE_IMAGE_URL = "http://10.0.2.2:8080/animalprofile/image/"

    init {
        fetchAnimals()
    }

    fun fetchAnimals() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getRecentAnimals()

                if (response.isSuccessful && response.body() != null) {
                    val remoteList = response.body()!!

                    animals.clear() // Limpa a lista antiga/mockada

                    // Converte cada item da API para o formato da tela
                    remoteList.forEach { remoteAnimal ->

                        // Pega a primeira foto da lista, se existir
                        val firstPhotoName = remoteAnimal.photos?.firstOrNull()
                        val fullImageUrl = if (firstPhotoName != null) {
                            "$BASE_IMAGE_URL$firstPhotoName"
                        } else {
                            null
                        }

                        // Traduz o Gênero (Opcional, pode fazer na UI também)
                        val genderDisplay = when(remoteAnimal.sex) {
                            "MALE" -> "Macho"
                            "FEMALE" -> "Fêmea"
                            else -> "Desconhecido"
                        }

                        animals.add(
                            AnimalUiState(
                                id = remoteAnimal.id,
                                name = remoteAnimal.provisionalName,
                                description = remoteAnimal.description ?: "Sem descrição",
                                photoUrl = fullImageUrl,
                                status = remoteAnimal.status, // Ex: ON_STREET
                                gender = genderDisplay,
                                distance = "?.? km" // O backend manda double, podemos formatar depois
                            )
                        )
                    }
                } else {
                    println("Erro ao buscar animais: ${response.code()}")
                }
            } catch (e: Exception) {
                println("Falha na conexão: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}