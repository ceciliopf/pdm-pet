package com.example.pdm_pet.features.animal_profile

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pdm_pet.data.remote.RetrofitClient
import com.example.pdm_pet.data.remote.dto.AnimalResponse
import com.example.pdm_pet.data.remote.dto.UserResponse
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AnimalDetailsViewModel : ViewModel() {

    var animal by mutableStateOf<AnimalResponse?>(null)
        private set

    // Estado para guardar o "Dono" do post
    var creatorUser by mutableStateOf<UserResponse?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMsg by mutableStateOf<String?>(null)
        private set

    private val BASE_IMAGE_URL = "https://patas-unidas-api.onrender.com/animalprofile/image/"
    private val BASE_USER_IMAGE_URL = "https://patas-unidas-api.onrender.com/user/image/" // Ajuste se for diferente

    @SuppressLint("MissingPermission")
    fun loadAnimal(context: Context, id: Long) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMsg = null

                // 1. Tenta pegar o GPS atual para calcular a distância
                val fusedLocation = LocationServices.getFusedLocationProviderClient(context)
                val location = try {
                    fusedLocation.lastLocation.await()
                } catch (e: Exception) { null }

                // 2. Busca o Animal (enviando Lat/Long se tiver)
                val response = RetrofitClient.api.getAnimalById(
                    id = id,
                    latitude = location?.latitude,
                    longitude = location?.longitude
                )

                if (response.isSuccessful && response.body() != null) {
                    val loadedAnimal = response.body()!!
                    animal = loadedAnimal

                    // 3. AGORA BUSCA O USUÁRIO CRIADOR
                    fetchCreatorInfo(loadedAnimal.createdByUserId)

                } else {
                    errorMsg = "Erro na API: ${response.code()}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMsg = "Erro técnico: ${e.message}"
            } finally {
                // Só para o loading se der erro no animal. Se der certo, espera carregar o user (opcional)
                if (animal == null) isLoading = false
            }
        }
    }

    private fun fetchCreatorInfo(userId: Long) {
        viewModelScope.launch {
            try {
                val userResponse = RetrofitClient.api.getUserById(userId)
                if (userResponse.isSuccessful) {
                    creatorUser = userResponse.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false // Finaliza o loading geral
            }
        }
    }

    fun getFullImageUrl(photoName: String?): String? {
        return if (!photoName.isNullOrBlank()) "$BASE_IMAGE_URL$photoName" else null
    }

    fun getUserImageUrl(photoName: String?): String? {
        return if (!photoName.isNullOrBlank()) "$BASE_USER_IMAGE_URL$photoName" else null
    }
}