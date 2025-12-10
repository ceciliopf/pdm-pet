package com.example.pdm_pet.features.animal_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pdm_pet.data.remote.RetrofitClient
import com.example.pdm_pet.data.remote.dto.CreateAnimalRequest
import kotlinx.coroutines.launch

class CreateAnimalViewModel : ViewModel() {

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
        if (name.isBlank() || photos.isEmpty()) {
            errorMessage = "Nome e pelo menos uma foto são obrigatórios."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // TODO: Em um app real, pegue o ID do usuário logado (ex: SharedPreferences ou DataStore)
                // Por enquanto, vou fixar o ID 1 para testes
                val userIdFixo = 1L

                // TODO: Em um app real, pegue a localização do GPS
                // Por enquanto, coordenadas fixas de Uberaba
                val latFixa = -19.747
                val lonFixa = -47.939

                val request = CreateAnimalRequest(
                    provisionalName = name,
                    description = description,
                    photos = photos,
                    createdByUserId = userIdFixo,
                    latitude = latFixa,
                    longitude = lonFixa
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