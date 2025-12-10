package com.example.pdm_pet.utils

import com.example.pdm_pet.data.remote.dto.LoginResponse

object UserSession {
    // Variável para guardar o usuário logado em memória
    var currentUser: LoginResponse? = null
        private set

    fun setLoggedInUser(user: LoginResponse) {
        currentUser = user
    }

    fun logout() {
        currentUser = null
    }

    // Atalho para pegar o ID com segurança (retorna 0 se não tiver logado)
    val userId: Long
        get() = currentUser?.id?.toLongOrNull() ?: 0L
}