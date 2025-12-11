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

class EditAnimalViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
    var message by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false)

    // Estados do Formulário
    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var age by mutableStateOf("")
    var sex by mutableStateOf("UNKNOWN")
    var size by mutableStateOf("MEDIUM")
    var status by mutableStateOf("ON_STREET")
    var photoBase64 by mutableStateOf<String?>(null) // Para simplificar, focamos em manter ou trocar a foto principal

    // Dados originais para controle
    private var currentAnimalId: Long = 0
    private var currentPhotoUrl: String? = null // URL da foto antiga, caso não troque

    fun loadAnimalData(id: Long) {
        if (id == 0L || isLoading) return

        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.api.getAnimalById(id)
                if (response.isSuccessful && response.body() != null) {
                    val animal = response.body()!!
                    currentAnimalId = animal.id

                    // Preenche os campos
                    name = animal.provisionalName
                    description = animal.description ?: ""
                    age = animal.approximateAge ?: ""
                    sex = animal.sex ?: "UNKNOWN"
                    size = animal.size ?: "MEDIUM"
                    status = animal.status ?: "ON_STREET"

                    // Guarda a referência da foto antiga (apenas nome do arquivo)
                    currentPhotoUrl = animal.photos?.firstOrNull()
                } else {
                    message = "Erro ao carregar dados."
                }
            } catch (e: Exception) {
                message = "Erro de conexão."
            } finally {
                isLoading = false
            }
        }
    }

    fun saveChanges() {
        if (name.isBlank()) {
            message = "Nome é obrigatório."
            return
        }

        viewModelScope.launch {
            isLoading = true
            message = null
            try {
                // Se o usuário selecionou uma nova foto (photoBase64 não é null), mandamos ela.
                // Se não, teríamos que manter a antiga.
                // OBS: A lógica exata de "manter foto antiga" depende do seu backend.
                // Aqui vamos supor que se mandar lista vazia, ele mantém, ou enviamos a nova.

                val photosList = if (photoBase64 != null) listOf(photoBase64!!) else null

                // Nota: O backend pode precisar de ajuste para não apagar a foto se a lista vier vazia no update.
                // Ou você pode impedir a edição sem reenviar a foto por enquanto.

                val request = CreateAnimalRequest(
                    provisionalName = name,
                    description = description,
                    approximateAge = age,
                    photos = photosList, // Envia nova ou vazia
                    createdByUserId = UserSession.userId, // Mantém o dono
                    latitude = 0.0, // Backend deve ignorar ou manter a antiga
                    longitude = 0.0,
                    status = status,
                    sex = sex,
                    size = size
                )

                val response = RetrofitClient.api.updateAnimal(currentAnimalId, request)
                if (response.isSuccessful) {
                    message = "Atualizado com sucesso!"
                    isSuccess = true
                } else {
                    message = "Erro ao salvar: ${response.code()}"
                }
            } catch (e: Exception) {
                message = "Erro: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}