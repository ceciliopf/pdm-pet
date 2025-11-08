package com.example.pdm_pet.data.model


enum class UserType {
    COMMON,        // Usuário Comum
    PROTECTOR_ONG, // Protetor ou ONG
    ADOPTER        // Adotante (pode ser um status ou tipo)
}


data class User(
    val id: String, // ID único (ex: do Firebase Auth)
    val name: String,
    val email: String,
    val city: String,
    val state: String,
    val userType: UserType = UserType.COMMON,
    val isVerifiedProtector: Boolean = false, // RN-U03: Selo de verificação
    val profilePictureUrl: String? = null,

    // RN-U01: Infos adicionais para Adotantes
    val housingType: String? = null, // "Apartamento", "Casa com Muro", etc.
    val hasOtherPets: Boolean? = null
)