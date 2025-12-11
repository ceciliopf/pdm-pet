package com.example.pdm_pet.data.remote.dto

// O que enviamos para logar
data class LoginRequest(
    val email: String,
    val senha: String
)

// O que recebemos de volta (incluindo o Token JWT)
data class LoginResponse(
    val token: String,
    val id: String,
    val name: String,
    val email: String,
    val userType: String, // Enum vem como String
    val profilePictureUrl: String?
)

// O que enviamos para registrar (Baseado no RegistrarUsuarioRequestDto)
// Adicione o campo 'phone'
data class RegisterRequest(
    val name: String,
    val email: String,
    val senha: String,
    val city: String,
    val state: String,
    val phone: String, // <--- NOVO CAMPO OBRIGATÓRIO
    val userType: String = "COMMON",
    val userPhotoUrl: String? = null
)