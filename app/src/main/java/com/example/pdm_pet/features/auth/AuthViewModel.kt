package com.example.pdm_pet.features.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pdm_pet.data.remote.RetrofitClient
import com.example.pdm_pet.data.remote.dto.LoginRequest
import com.example.pdm_pet.data.remote.dto.RegisterRequest
import com.example.pdm_pet.utils.UserSession
import kotlinx.coroutines.launch

// --- ESTADOS DE UI ---
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

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

    // ================= LOGIN =================
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
            loginUiState = loginUiState.copy(isLoading = true, error = null)

            try {
                val request = LoginRequest(
                    email = loginUiState.username,
                    senha = loginUiState.password
                )

                val response = RetrofitClient.api.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!

                    // 1. Salva o usuário na Sessão (Memória) para usar o ID depois
                    UserSession.setLoggedInUser(authData)

                    // Nota: Aqui você também poderia salvar o token no SharedPreferences (TokenManager)
                    // TokenManager.saveToken(context, authData.token)

                    println("Login Sucesso: ${authData.name} (ID: ${authData.id})")

                    // Limpa estado de erro/loading e o Compose vai reagir (navegação deve ser feita na UI)
                    loginUiState = loginUiState.copy(isLoading = false, error = null)

                } else {
                    val msg = if (response.code() == 403) "Credenciais inválidas" else "Erro: ${response.code()}"
                    loginUiState = loginUiState.copy(isLoading = false, error = msg)
                }

            } catch (e: Exception) {
                loginUiState = loginUiState.copy(
                    isLoading = false,
                    error = "Falha na conexão. Verifique a internet."
                )
                e.printStackTrace()
            }
        }
    }


    // ================= CADASTRO (REGISTER) =================
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

        // Validações locais
        if (registerUiState.password != registerUiState.confirmPassword) {
            registerUiState = registerUiState.copy(error = "As senhas não conferem.")
            return
        }
        if (registerUiState.name.isBlank() || registerUiState.email.isBlank()) {
            registerUiState = registerUiState.copy(error = "Preencha todos os campos.")
            return
        }

        viewModelScope.launch {
            registerUiState = registerUiState.copy(isLoading = true, error = null)

            try {
                // Cria o DTO de registro
                val request = RegisterRequest(
                    name = registerUiState.name,
                    email = registerUiState.email,
                    senha = registerUiState.password,
                    city = registerUiState.city,
                    state = registerUiState.state,
                    userType = "COMMON" // Padrão
                )

                val response = RetrofitClient.api.register(request)

                if (response.isSuccessful) {
                    println("Cadastro realizado com sucesso!")
                    registerUiState = registerUiState.copy(isLoading = false, error = null)
                    // O sucesso é indicado pelo isLoading = false e error = null
                } else {
                    // Tenta ler a mensagem de erro do corpo, se houver, ou usa código genérico
                    val errorMsg = if(response.code() == 409) "E-mail já cadastrado" else "Erro no cadastro: ${response.code()}"
                    registerUiState = registerUiState.copy(isLoading = false, error = errorMsg)
                }

            } catch (e: Exception) {
                registerUiState = registerUiState.copy(
                    isLoading = false,
                    error = "Sem conexão com o servidor."
                )
                e.printStackTrace()
            }
        }
    }
}