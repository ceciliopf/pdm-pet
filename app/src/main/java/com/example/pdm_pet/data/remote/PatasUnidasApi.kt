package com.example.pdm_pet.data.remote

import com.example.pdm_pet.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PatasUnidasApi {

    // --- USER ---
    @POST("/user/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/user/register-new-user")
    suspend fun register(@Body request: RegisterRequest): Response<Void>

    // --- ANIMAL ---
    @POST("/animalprofile/register-animal-profile")
    suspend fun createAnimal(@Body request: CreateAnimalRequest): Response<Void>

    @GET("/animalprofile/{id}")
    suspend fun getAnimalById(@Path("id") id: Long): Response<AnimalResponse>

    // Endpoint atualizado para "/proximos" conforme solicitado
    @GET("/animalprofile/proximos")
    suspend fun getNearbyAnimals(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Double = 20.0
    ): Response<List<AnimalResponse>>
}