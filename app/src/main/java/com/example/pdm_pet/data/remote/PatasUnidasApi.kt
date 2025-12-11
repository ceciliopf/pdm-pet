package com.example.pdm_pet.data.remote

import com.example.pdm_pet.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PatasUnidasApi {

    // --- USER ---
    @POST("/user/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/user/register-new-user")
    suspend fun register(@Body request: RegisterRequest): Response<Void>

    // NOVO: Busca dados públicos de um usuário (para o card do criador)
    @GET("/user/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<UserResponse>

    // --- ANIMAL ---
    @POST("/animalprofile/register-animal-profile")
    suspend fun createAnimal(@Body request: CreateAnimalRequest): Response<Void>

    // ATUALIZADO: Agora recebe lat/long para calcular a proximidade na tela de detalhes
    @GET("/animalprofile/{id}")
    suspend fun getAnimalById(
        @Path("id") id: Long,
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null
    ): Response<AnimalResponse>

    @GET("/user/check-email")
    suspend fun checkEmail(@Query("email") email: String): Response<Boolean>
    @GET("/animalprofile/proximos")
    suspend fun getNearbyAnimals(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Double = 20.0
    ): Response<List<AnimalResponse>>

    // Busca dados do próprio usuário logado
    @GET("/user/{id}")
    suspend fun getUserDetails(@Path("id") id: Long): Response<UserResponse>

    // Atualizar foto de perfil
    @PATCH("/user/{id}/update-photo")
    suspend fun updateUserPhoto(
        @Path("id") id: Long,
        @Body request: UpdatePhotoRequest
    ): Response<Void>

    // Remover foto de perfil
    @DELETE("/user/{id}/remove-photo") // Ou DELETE
    suspend fun deleteUserPhoto(@Path("id") id: Long): Response<Void>

    @PATCH("/animalprofile/alter-{id}")
    suspend fun updateAnimal(
        @Path("id") id: Long,
        @Body request: CreateAnimalRequest
    ): Response<Void>

    // Deletar animal
    @DELETE("/animalprofile/delete-{id}")
    suspend fun deleteAnimal(@Path("id") id: Long): Response<Void>
    
}
