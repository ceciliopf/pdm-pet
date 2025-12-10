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

    // Ajuste aqui também se estiver no celular físico!
    private val BASE_IMAGE_URL = "http://10.0.2.2:8080/animalprofile/image/"

    fun loadAnimal(id: Long) {
        viewModelScope.launch {
            try {
                isLoading = true
                val response = RetrofitClient.api.getAnimalById(id)
                if (response.isSuccessful) {
                    animal = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun getFullImageUrl(photoName: String?): String? {
        return if (photoName != null) "$BASE_IMAGE_URL$photoName" else null
    }
}