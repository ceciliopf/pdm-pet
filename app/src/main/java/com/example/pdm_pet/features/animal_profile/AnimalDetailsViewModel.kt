package com.example.pdm_pet.features.animal_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pdm_pet.data.remote.RetrofitClient
import com.example.pdm_pet.data.remote.dto.AnimalResponse
import kotlinx.coroutines.launch

class AnimalDetailsViewModel : ViewModel() {

    var animal by mutableStateOf<AnimalResponse?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    // Variável nova para mostrar o erro na tela
    var errorMsg by mutableStateOf<String?>(null)
        private set

    // Lembre-se de verificar se este IP é o mesmo do FeedViewModel!
    private val BASE_IMAGE_URL = "https://patas-unidas-api.onrender.com/animalprofile/image/"

    fun loadAnimal(id: Long) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMsg = null // Limpa erros anteriores

                // Verifica se o ID é válido
                if (id == 0L) {
                    errorMsg = "ID inválido (0)"
                    return@launch
                }

                val response = RetrofitClient.api.getAnimalById(id)

                if (response.isSuccessful && response.body() != null) {
                    animal = response.body()
                } else {
                    // Captura o código de erro (ex: 404, 500)
                    errorMsg = "Erro na API: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Captura erro de conexão ou conversão de dados
                errorMsg = "Erro técnico: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun getFullImageUrl(photoName: String?): String? {
        return if (!photoName.isNullOrBlank()) "$BASE_IMAGE_URL$photoName" else null
    }
}