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
import com.example.pdm_pet.utils.UserSession
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AnimalDetailsViewModel : ViewModel() {

    var animal by mutableStateOf<AnimalResponse?>(null)
        private set

    var creatorUser by mutableStateOf<UserResponse?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMsg by mutableStateOf<String?>(null)
        private set

    // Verifica se o usuário logado é o dono do post
    val isOwner: Boolean
        get() = animal?.createdByUserId == UserSession.userId

    // URLs base (Ajuste conforme seu servidor)
    private val BASE_IMAGE_URL = "https://patas-unidas-api.onrender.com/animalprofile/image/"
    private val BASE_USER_IMAGE_URL = "https://patas-unidas-api.onrender.com/user/image/"

    @SuppressLint("MissingPermission")
    fun loadAnimal(context: Context, id: Long) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMsg = null

                // 1. Tenta pegar GPS para calcular distância
                val fusedLocation = LocationServices.getFusedLocationProviderClient(context)
                val location = try {
                    fusedLocation.lastLocation.await()
                } catch (e: Exception) { null }

                // 2. Busca Animal
                val response = RetrofitClient.api.getAnimalById(
                    id = id,
                    latitude = location?.latitude,
                    longitude = location?.longitude
                )

                if (response.isSuccessful && response.body() != null) {
                    val loadedAnimal = response.body()!!
                    animal = loadedAnimal
                    // 3. Busca Criador
                    fetchCreatorInfo(loadedAnimal.createdByUserId)
                } else {
                    errorMsg = "Erro na API: ${response.code()}"
                    isLoading = false // Se falhar aqui, encerra
                }
            } catch (e: Exception) {
                errorMsg = "Erro técnico: ${e.message}"
                isLoading = false
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
                isLoading = false
            }
        }
    }

    fun deleteAnimal(onSuccess: () -> Unit) {
        val id = animal?.id ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.api.deleteAnimal(id)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    errorMsg = "Erro ao deletar: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMsg = "Erro: ${e.message}"
            } finally {
                isLoading = false
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