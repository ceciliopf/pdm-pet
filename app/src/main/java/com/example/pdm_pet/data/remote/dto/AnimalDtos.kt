package com.example.pdm_pet.data.remote.dto

// ... (CreateAnimalRequest que já criamos antes)

// NOVO: O que recebemos ao buscar animais
data class AnimalResponse(
    val id: Long,
    val provisionalName: String,
    val description: String?, // Pode vir nulo do banco
    val photos: List<String>?, // Lista de nomes de arquivos (ex: "abc-123.jpg")
    val status: String, // "ON_STREET", etc.
    val sex: String,    // "MALE", "FEMALE"
    val city: String? = "Uberaba", // O backend ainda não manda cidade no DTO de animal, deixei fixo ou null
    val state: String? = "MG",
    val approximateDistance: Double? // Calculado pelo backend
)