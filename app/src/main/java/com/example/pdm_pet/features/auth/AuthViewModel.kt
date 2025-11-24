package com.example.pdm_pet.features.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// --- ESTADO PARA A TELA DE LOGIN ---
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

// --- ESTADO PARA A TELA DE CADASTRO ---
data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val city: String = "",
    val state: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    // --- ESTADO E LÓGICA DE LOGIN (O que está faltando) ---
    var loginUiState by mutableStateOf(LoginUiState())
        private set

    fun onUsernameChange(newUsername: String) {
        loginUiState = loginUiState.copy(username = newUsername)
    }

    fun onPasswordChange(newPassword: String) {
        loginUiState = loginUiState.copy(password = newPassword)
    }

    fun login() {
        if (loginUiState.isLoading) return

        viewModelScope.launch {
            loginUiState = loginUiState.copy(isLoading = true)
            kotlinx.coroutines.delay(2000) // Simula rede

            if (loginUiState.username == "admin" && loginUiState.password == "1234") {
                loginUiState = loginUiState.copy(isLoading = false, error = null)
                // TODO: Navegar para o Feed
            } else {
                loginUiState = loginUiState.copy(
                    isLoading = false,
                    error = "Usuário ou senha inválidos"
                )
            }
        }
    }


    // --- ESTADO E LÓGICA PARA CADASTRO ---
    var registerUiState by mutableStateOf(RegisterUiState())
        private set

    fun onNameChange(newName: String) {
        registerUiState = registerUiState.copy(name = newName)
    }
    fun onEmailChange(newEmail: String) {
        registerUiState = registerUiState.copy(email = newEmail)
    }
    fun onRegisterPasswordChange(newPass: String) {
        registerUiState = registerUiState.copy(password = newPass)
    }
    fun onConfirmPasswordChange(newPass: String) {
        registerUiState = registerUiState.copy(confirmPassword = newPass)
    }
    fun onCityChange(newCity: String) {
        registerUiState = registerUiState.copy(city = newCity)
    }
    fun onStateChange(newState: String) {
        registerUiState = registerUiState.copy(state = newState)
    }

    fun register() {
        if (registerUiState.isLoading) return

        if (registerUiState.password != registerUiState.confirmPassword) {
            registerUiState = registerUiState.copy(error = "As senhas não conferem.")
            return
        }
        // ... (outras validações)

        viewModelScope.launch {
            registerUiState = registerUiState.copy(isLoading = true, error = null)
            kotlinx.coroutines.delay(2000) // Simula rede
            println("Usuário ${registerUiState.name} cadastrado!")
            registerUiState = registerUiState.copy(isLoading = false)
            // TODO: Navegar para Login
        }
    }
}