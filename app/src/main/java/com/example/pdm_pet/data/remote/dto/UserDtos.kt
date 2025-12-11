package com.example.pdm_pet.data.remote.dto

data class UserResponse(
    val id: Long,
    val name: String?,
    val email: String?,
    val city: String?,
    val state: String?,
    val phone: String?, // <--- NOVO CAMPO (Pode vir nulo de usuários antigos)
    val profilePictureUrl: String?,
    val userType: String?
)
data class UpdatePhotoRequest(
    val photoBase64: String? // Pode ser null se for para limpar, ou a String da nova foto
)