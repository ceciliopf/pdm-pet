package com.example.pdm_pet.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit // Importante para o KTX funcionar

object TokenManager {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"

    // Função auxiliar para pegar o SharedPreferences
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Salvar o token
    fun saveToken(context: Context, token: String) {
        // CORREÇÃO: Chame getPreferences(context) antes do .edit
        getPreferences(context).edit {
            putString(KEY_AUTH_TOKEN, token)
            // O 'apply()' é automático nessa sintaxe do KTX
        }
    }

    // Recuperar o token
    fun getToken(context: Context): String? {
        return getPreferences(context).getString(KEY_AUTH_TOKEN, null)
    }

    // Limpar o token (Logout)
    fun clearToken(context: Context) {
        getPreferences(context).edit {
            remove(KEY_AUTH_TOKEN)
        }
    }
}