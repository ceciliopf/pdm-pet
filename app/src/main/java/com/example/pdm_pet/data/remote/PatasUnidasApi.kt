package com.example.pdm_pet.data.remote

import com.example.pdm_pet.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PatasUnidasApi {

    // --- USER CONTROLLER ---

    @POST("/user/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/user/register-new-user")
    suspend fun register(@Body request: RegisterRequest): Response<Void>

    // --- ANIMAL PROFILE CONTROLLER ---
    @POST("/animalprofile/register-animal-profile")
    suspend fun createAnimal(@Body request: CreateAnimalRequest): Response<Void>
    // (Vamos adicionar os de Animal depois que o login funcionar)

    // NOVO: Busca os animais recentes
    @GET("/animalprofile/get-recent")
    suspend fun getRecentAnimals(): Response<List<AnimalResponse>>
}