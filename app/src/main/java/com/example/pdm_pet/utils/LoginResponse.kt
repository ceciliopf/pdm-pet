package com.example.pdm_pet.utils

import android.content.Context
import com.example.pdm_pet.data.remote.dto.LoginResponse

object UserSession {
    // Variável em memória
    var currentUser: LoginResponse? = null
        private set

    // Verifica se está logado
    val isLoggedIn: Boolean
        get() = currentUser != null

    // Atalho para ID (converte String para Long com segurança)
    val userId: Long
        get() = currentUser?.id?.toLongOrNull() ?: 0L

    // FUNÇÃO NOVA: Salva na memória E no celular
    fun login(context: Context, user: LoginResponse) {
        currentUser = user
        TokenManager.saveUser(context, user)
    }

    // FUNÇÃO NOVA: Tenta restaurar a sessão ao abrir o app
    fun checkSession(context: Context) {
        val savedUser = TokenManager.getUser(context)
        if (savedUser != null) {
            currentUser = savedUser
        }
    }

    // FUNÇÃO NOVA: Logout completo
    fun logout(context: Context) {
        currentUser = null
        TokenManager.clearUser(context)
    }

    // Mantido apenas para compatibilidade, mas prefira usar o login(context, user)
    fun setLoggedInUser(user: LoginResponse) {
        currentUser = user
    }

    // Mantido para compatibilidade
    fun logout() {
        currentUser = null
    }
}