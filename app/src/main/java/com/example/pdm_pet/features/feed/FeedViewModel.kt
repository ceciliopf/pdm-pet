package com.example.pdm_pet.features.feed

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pdm_pet.data.remote.RetrofitClient
import kotlinx.coroutines.launch

data class AnimalUiState(
    val id: Long,
    val name: String,
    val description: String,
    val photoUrl: String?,
    val status: String,
    val gender: String,
    val distance: String
)

class FeedViewModel : ViewModel() {

    val animals = mutableStateListOf<AnimalUiState>()
    var isLoading = false
    var errorMsg: String? = null

    // URL de imagem do servidor online
    private val BASE_IMAGE_URL = "https://patas-unidas-api.onrender.com/animalprofile/image/"

    // Agora o fetchAnimals pede latitude e longitude
    @SuppressLint("DefaultLocale")
    fun fetchAnimals(latitude: Double, longitude: Double) {
        if (isLoading) return

        viewModelScope.launch {
            try {
                isLoading = true
                errorMsg = null
                println("Buscando animais próximos de Lat: $latitude, Long: $longitude")

                // Chama o endpoint /proximos
                val response = RetrofitClient.api.getNearbyAnimals(latitude, longitude, radius = 50.0)

                if (response.isSuccessful && response.body() != null) {
                    val remoteList = response.body()!!
                    animals.clear()

                    remoteList.forEach { remoteAnimal ->
                        val firstPhotoName = remoteAnimal.photos?.firstOrNull()
                        val fullImageUrl = if (!firstPhotoName.isNullOrBlank()) {
                            "$BASE_IMAGE_URL$firstPhotoName"
                        } else {
                            null
                        }

                        val genderDisplay = when(remoteAnimal.sex) {
                            "MALE" -> "Macho"
                            "FEMALE" -> "Fêmea"
                            else -> "Desconhecido"
                        }

                        val distValue = remoteAnimal.approximateDistance
                        val distDisplay = if (distValue != null) String.format("%.1f km", distValue) else "?"

                        animals.add(
                            AnimalUiState(
                                id = remoteAnimal.id,
                                name = remoteAnimal.provisionalName,
                                description = remoteAnimal.description ?: "Sem descrição",
                                photoUrl = fullImageUrl,
                                status = remoteAnimal.status ?: "Desconhecido",
                                gender = genderDisplay,
                                distance = distDisplay
                            )
                        )
                    }
                } else {
                    errorMsg = "Erro: ${response.code()}"
                    println("Erro na API: ${response.code()}")
                }
            } catch (e: Exception) {
                errorMsg = "Erro de conexão: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}