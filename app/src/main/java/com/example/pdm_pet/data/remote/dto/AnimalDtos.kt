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

data class AnimalResponse(
    val id: Long,
    val createdByUserId: Long,
    val managedByUserId: Long,
    val photos: List<String>?, // Backend manda uma lista de nomes de arquivos
    val latitude: Double?,
    val longitude: Double?,
    val status: String?,       // Ex: "ON_STREET"
    val createdAt: Long?,
    val provisionalName: String, // Nome provisório
    val description: String?,
    val size: String?,         // Ex: "MEDIUM"
    val sex: String?,          // Ex: "MALE"
    val approximateAge: String?,
    val approximateDistance: Double? // Calculado pelo backend se passar lat/long
)