package com.example.pdm_pet.data.model

// RN-C02: Status do Cão
enum class AnimalStatus {
    ON_STREET,              // Na Rua
    TEMP_HOME,              // Em Lar Temporário
    RESCUED_BY_ONG,         // Resgatado por ONG
    AVAILABLE_FOR_ADOPTION, // Disponível para Adoção
    ADOPTED                 // Adotado! (RN-A05)
}

// RN-C03: Características
enum class AnimalSize {
    SMALL,
    MEDIUM,
    LARGE
}

enum class AnimalSex {
    MALE,
    FEMALE,
    UNKNOWN // Idade e sexo podem ser difíceis de estimar
}

// Classe simples para geolocalização (RN-C02)
data class GeoLocation(
    val latitude: Double,
    val longitude: Double
)

// RN-C01, C02, C03: O Perfil do Animal
data class AnimalProfile(
    val id: String, // ID único do perfil do animal
    val createdByUserId: String, // ID do usuário que criou (RN-C01)

    // RN-C05: Quem gerencia o perfil (pode ser transferido)
    val managedByUserId: String,

    val photos: List<String>, // Lista de URLs das fotos (mínimo 1, RN-C02)
    val location: GeoLocation, // Localização exata (RN-C02)
    val status: AnimalStatus, // RN-C02
    val createdAt: Long = System.currentTimeMillis(), // Para filtro (RN-F02)

    // Informações Desejáveis (RN-C03)
    val provisionalName: String? = null,
    val description: String? = null,
    val size: AnimalSize? = null,
    val sex: AnimalSex? = null,
    val approximateAge: String? = null // Usar String é flexível (ex: "Filhote", "3 anos")
)