package com.example.pdm_pet.data.remote.dto

// --- DTO para ENVIAR (Cadastro) ---
data class CreateAnimalRequest(
    val provisionalName: String,
    val description: String,
    val photos: List<String>?,
    val createdByUserId: Long,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val size: String,
    val sex: String,
    val approximateAge: String,
    val createdAt: Long = System.currentTimeMillis()
)

// --- DTO para RECEBER (Feed) ---
data class AnimalResponse(
    val id: Long,
    val createdByUserId: Long,
    val managedByUserId: Long,
    val photos: List<String>?,
    val latitude: Double?,
    val longitude: Double?,
    val status: String?,
    val createdAt: Long?,
    val provisionalName: String,
    val description: String?,
    val size: String?,
    val sex: String?,
    val approximateAge: String?,
    val approximateDistance: Double?
)