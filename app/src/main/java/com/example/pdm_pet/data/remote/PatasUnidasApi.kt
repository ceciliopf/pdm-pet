package com.example.pdm_pet.data.remote

import com.example.pdm_pet.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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

    @GET("/animalprofile/{id}")
    suspend fun getAnimalById(@Path("id") id: Long): Response<AnimalResponse>

    @GET("/animalprofile/get-recent")
    suspend fun getRecentAnimals(): Response<List<AnimalResponse>>
}