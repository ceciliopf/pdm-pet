package com.example.pdm_pet.utils

import android.content.Context
import androidx.core.content.edit
import com.example.pdm_pet.data.remote.dto.LoginResponse

object TokenManager {
    private const val PREFS_NAME = "app_prefs"

    // Chaves para salvar cada pedacinho de informação
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_ID = "user_id"
    private const val KEY_NAME = "user_name"
    private const val KEY_EMAIL = "user_email"
    private const val KEY_TYPE = "user_type"
    private const val KEY_PHOTO = "user_photo"

    // Salva o usuário completo
    fun saveUser(context: Context, user: LoginResponse) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(KEY_TOKEN, user.token)
            putString(KEY_ID, user.id)
            putString(KEY_NAME, user.name)
            putString(KEY_EMAIL, user.email)
            putString(KEY_TYPE, user.userType)
            putString(KEY_PHOTO, user.profilePictureUrl)
        }
    }

    // Tenta recuperar o usuário salvo
    fun getUser(context: Context): LoginResponse? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val token = prefs.getString(KEY_TOKEN, null)
        val id = prefs.getString(KEY_ID, null)
        val name = prefs.getString(KEY_NAME, null)

        // Se não tiver token ou id, não tem usuário válido
        if (token == null || id == null || name == null) return null

        return LoginResponse(
            token = token,
            id = id,
            name = name,
            email = prefs.getString(KEY_EMAIL, "") ?: "",
            userType = prefs.getString(KEY_TYPE, "COMMON") ?: "COMMON",
            profilePictureUrl = prefs.getString(KEY_PHOTO, null)
        )
    }

    // Apaga tudo (Logout)
    fun clearUser(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            clear()
        }
    }
}