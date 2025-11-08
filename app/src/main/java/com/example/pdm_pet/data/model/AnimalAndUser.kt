package com.example.pdm_pet.data.model

class AnimalAndUser {

    // RN-F03: Comentários em um post
    data class Comment(
        val id: String,
        val animalProfileId: String, // Link para o animal
        val userId: String, // Link para o usuário que comentou
        val text: String,
        val timestamp: Long
    )

    // RN-A01: Representa o início de um chat/processo de adoção
    data class AdoptionRequest(
        val id: String,
        val animalProfileId: String, // O cão em questão
        val adopterUserId: String, // O interessado (Adotante)
        val responsibleUserId: String, // O Protetor/ONG ou quem postou
        val status: String = "PENDING", // Ex: PENDING, APPROVED, REJECTED
        val preAdoptionForm: Map<String, String>? = null // RN-A03: Questionário
    )

    // RN-S02: Modelo para Denúncias
    data class Report(
        val id: String,
        val reportedByUserId: String, // Quem denunciou
        val timestamp: Long,
        val reason: String, // Motivo (ex: "VENDA_PROIBIDA", "FALSO")

        // A denúncia pode ser de um usuário ou de um post (perfil do cão)
        val reportedUserId: String? = null,
        val reportedAnimalProfileId: String? = null
    )
}