package com.example.pdm_pet.data.remote.dto

data class CreateAnimalRequest(
    val provisionalName: String,
    val description: String,
    val photos: List<String>, // Sua lista de Base64
    val createdByUserId: Long, // ID do usuário logado
    val latitude: Double,
    val longitude: Double,

    // Valores padrão para simplificar (ou você pode criar campos na tela para eles)
    val status: String = "ON_STREET", // ON_STREET, AVAILABLE_FOR_ADOPTION, etc.
    val size: String = "MEDIUM",      // SMALL, MEDIUM, LARGE
    val sex: String = "UNKNOWN",      // MALE, FEMALE, UNKNOWN
    val approximateAge: String = "Desconhecida"
)