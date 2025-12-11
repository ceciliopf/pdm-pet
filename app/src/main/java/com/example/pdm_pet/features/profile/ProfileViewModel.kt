package com.example.pdm_pet.features.profile

import android.graphics.Bitmap
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pdm_pet.data.remote.RetrofitClient
import com.example.pdm_pet.data.remote.dto.UpdatePhotoRequest
import com.example.pdm_pet.data.remote.dto.UserResponse
import com.example.pdm_pet.utils.UserSession
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ProfileViewModel : ViewModel() {

    var currentUser by mutableStateOf<UserResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var message by mutableStateOf<String?>(null)
        private set

    // IP do servidor para montar a URL da imagem
    private val BASE_USER_IMAGE_URL = "https://patas-unidas-api.onrender.com/user/image/"

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val userId = UserSession.userId
        if (userId == 0L) return

        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.api.getUserDetails(userId)
                if (response.isSuccessful) {
                    currentUser = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun updatePhoto(bitmap: Bitmap) {
        val userId = UserSession.userId
        if (userId == 0L) return

        viewModelScope.launch {
            isLoading = true
            message = null
            try {
                // 1. Converte Bitmap para Base64
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

                // 2. Envia para API

                val request = UpdatePhotoRequest(profilePictureUrl = base64String) // Mudou de photoBase64 para profilePictureUrl
                val response = RetrofitClient.api.updateUserPhoto(userId, request)

                if (response.isSuccessful) {
                    message = "Foto atualizada com sucesso!"
                    loadUserProfile() // Recarrega para mostrar a nova
                } else {
                    message = "Erro ao atualizar: ${response.code()}"
                }
            } catch (e: Exception) {
                message = "Erro de conexão: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deletePhoto() {
        val userId = UserSession.userId
        if (userId == 0L) return

        viewModelScope.launch {
            isLoading = true
            message = null
            try {
                val response = RetrofitClient.api.deleteUserPhoto(userId)
                if (response.isSuccessful) {
                    message = "Foto removida com sucesso!"
                    loadUserProfile() // Recarrega para mostrar sem foto
                } else {
                    message = "Erro ao remover: ${response.code()}"
                }
            } catch (e: Exception) {
                message = "Erro de conexão: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun getUserImageUrl(photoName: String?): String? {
        return if (!photoName.isNullOrBlank()) "$BASE_USER_IMAGE_URL$photoName" else null
    }
}