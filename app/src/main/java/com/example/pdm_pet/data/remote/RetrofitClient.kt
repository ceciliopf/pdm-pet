package com.example.pdm_pet.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    val api: PatasUnidasApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // <--- Mude aqui (remova 'baseUrl =')
            .addConverterFactory(GsonConverterFactory.create()) // <--- Mude aqui (remova 'factory =')
            .build()
            .create(PatasUnidasApi::class.java)
    }
}