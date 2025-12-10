package com.example.pdm_pet.data.remote.dto

data class UserResponse(
    val id: Long?,
    val name: String?,
    val email: String?, // Opcional exibir
    val city: String?,
    val state: String?,
    val profilePictureUrl: String?, // URL da foto
    val userType: String
)
data class UpdatePhotoRequest(
    val photoBase64: String? // Pode ser null se for para limpar, ou a String da nova foto
)