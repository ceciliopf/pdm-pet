package com.example.pdm_pet.features.auth


// --- IMPORTS QUE FALTAM ---
import androidx.lifecycle.ViewModel // Para a classe ViewModel (Erro principal)
import androidx.lifecycle.viewModelScope // Para o .launch { }
import androidx.compose.runtime.getValue // Para o delegate "by"
import androidx.compose.runtime.mutableStateOf // Para o estado
import androidx.compose.runtime.setValue // Para o delegate "by"
import kotlinx.coroutines.launch // Para o .launch { }
// -----------------------------

import androidx.compose.runtime.remember // (Este você provavelmente já tinha)
// --- CÓDIGO DA TELA DE LOGIN (JÁ EXISTENTE) ---
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

// --- NOVO CÓDIGO PARA A TELA DE CADASTRO ---
// RN-U02: Estado para os campos de cadastro
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

    // --- ESTADO E LÓGICA DE LOGIN (JÁ EXISTENTE) ---
    var loginUiState by mutableStateOf(LoginUiState())
        private set

    // ... (funções onUsernameChange, onPasswordChange, login)


    // --- NOVO ESTADO E LÓGICA PARA CADASTRO ---
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

        // Validação simples
        if (registerUiState.password != registerUiState.confirmPassword) {
            registerUiState = registerUiState.copy(error = "As senhas não conferem.")
            return
        }
        if (registerUiState.name.isBlank() || registerUiState.email.isBlank() || registerUiState.city.isBlank()) {
            registerUiState = registerUiState.copy(error = "Preencha todos os campos.")
            return
        }

        viewModelScope.launch {
            registerUiState = registerUiState.copy(isLoading = true, error = null)

            // --- SIMULAÇÃO DE CADASTRO ---
            // (Aqui entraria a chamada ao Firebase Auth para criar um usuário)
            kotlinx.coroutines.delay(2000)

            println("Usuário ${registerUiState.name} cadastrado com sucesso!")
            registerUiState = registerUiState.copy(isLoading = false)
            // TODO: Navegar para a tela de Feed ou Login
            // --- FIM DA SIMULAÇÃO ---
        }
    }
}