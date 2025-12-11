package com.example.pdm_pet.features.animal_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pdm_pet.data.remote.RetrofitClient
import com.example.pdm_pet.data.remote.dto.CreateAnimalRequest
import com.example.pdm_pet.utils.UserSession
import kotlinx.coroutines.launch

class CreateAnimalViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun createAnimal(
        name: String,
        description: String,
        age: String,
        photos: List<String>,
        sex: String,
        size: String,
        status: String,
        latitude: Double,   // <--- NOVO PARÂMETRO
        longitude: Double,  // <--- NOVO PARÂMETRO
        onSuccess: () -> Unit
    ) {
        if (name.isBlank()) {
            errorMessage = "Nome é obrigatório."
            return
        }
        if (photos.isEmpty()) {
            errorMessage = "A foto é obrigatória para identificação."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val userId = UserSession.userId
                if (userId == 0L) {
                    errorMessage = "Erro de sessão. Faça login novamente."
                    isLoading = false
                    return@launch
                }

                val request = CreateAnimalRequest(
                    provisionalName = name,
                    description = description,
                    approximateAge = if(age.isBlank()) "Desconhecida" else age,
                    photos = photos,
                    createdByUserId = userId,
                    latitude = latitude,  // <--- Usa o valor recebido
                    longitude = longitude, // <--- Usa o valor recebido
                    status = status,
                    sex = sex,
                    size = size,
                )

                val response = RetrofitClient.api.createAnimal(request)

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    errorMessage = "Erro ao salvar: ${response.code()}"
                }

            } catch (e: Exception) {
                errorMessage = "Erro de conexão: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}