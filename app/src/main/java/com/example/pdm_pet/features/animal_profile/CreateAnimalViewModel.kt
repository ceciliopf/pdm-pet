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

    // Estados para controlar a UI (Loading e Erros)
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun createAnimal(
        name: String,
        description: String,
        photos: List<String>,
        onSuccess: () -> Unit
    ) {
        // Validação básica
        if (name.isBlank()) {
            errorMessage = "O nome do pet é obrigatório."
            return
        }
        if (photos.isEmpty()) {
            errorMessage = "Adicione pelo menos uma foto."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // 1. Pega o ID do usuário logado na Sessão
                val userId = UserSession.userId

                if (userId == 0L) {
                    errorMessage = "Erro de sessão: Faça login novamente."
                    isLoading = false
                    return@launch
                }

                // 2. Cria o objeto de requisição
                // Nota: Lat/Long fixos para teste. Num app real, use o FusedLocationProviderClient.
                val request = CreateAnimalRequest(
                    provisionalName = name,
                    description = description,
                    photos = photos,
                    createdByUserId = userId,
                    latitude = -19.747, // Exemplo: Uberaba
                    longitude = -47.939,
                    status = "ON_STREET",
                    sex = "UNKNOWN",
                    size = "MEDIUM",
                    approximateAge = "Desconhecida"
                )

                // 3. Chama a API
                val response = RetrofitClient.api.createAnimal(request)

                if (response.isSuccessful) {
                    onSuccess() // Navega de volta
                } else {
                    errorMessage = "Erro ao salvar animal: Código ${response.code()}"
                }

            } catch (e: Exception) {
                errorMessage = "Falha na conexão: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}