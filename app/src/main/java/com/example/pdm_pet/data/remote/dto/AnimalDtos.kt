package com.example.pdm_pet.data.remote.dto

data class CreateAnimalRequest(
    val provisionalName: String,
    val description: String,
    val photos: List<String>,
    val createdByUserId: Long,
    val latitude: Double,
    val longitude: Double,
    // Novos campos que faltavam:
    val status: String,
    val size: String,
    val sex: String,
    val approximateAge: String,
    // CORREÇÃO CRÍTICA: Data obrigatória no banco
    val createdAt: Long = System.currentTimeMillis()
)

// ... (Mantenha o AnimalResponse se houver)