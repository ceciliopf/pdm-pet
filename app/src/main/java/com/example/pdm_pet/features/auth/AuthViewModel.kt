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

// --- ESTADO DE LOGIN ATUALIZADO ---
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false // NOVO CAMPO: Indica se o login deu certo
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
            // Reseta o estado antes de tentar (erro null, success false)
            loginUiState = loginUiState.copy(isLoading = true, error = null, isSuccess = false)

            try {
                val request = LoginRequest(
                    email = loginUiState.username,
                    senha = loginUiState.password
                )

                val response = RetrofitClient.api.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!

                    // Salva na Sessão
                    UserSession.setLoggedInUser(authData)

                    println("Login Sucesso: ${authData.name}")

                    // SUCESSO! A tela vai perceber essa mudança
                    loginUiState = loginUiState.copy(isLoading = false, isSuccess = true)

                } else {
                    val msg = if (response.code() == 403) "E-mail ou senha incorretos" else "Erro: ${response.code()}"
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

    // ... (Métodos de cadastro permanecem iguais aos que te enviei antes)
    fun onNameChange(n: String) { registerUiState = registerUiState.copy(name = n) }
    fun onEmailChange(n: String) { registerUiState = registerUiState.copy(email = n) }
    fun onRegisterPasswordChange(n: String) { registerUiState = registerUiState.copy(password = n) }
    fun onConfirmPasswordChange(n: String) { registerUiState = registerUiState.copy(confirmPassword = n) }
    fun onCityChange(n: String) { registerUiState = registerUiState.copy(city = n) }
    fun onStateChange(n: String) { registerUiState = registerUiState.copy(state = n) }

    fun register() {
        if (registerUiState.isLoading) return

        if (registerUiState.password != registerUiState.confirmPassword) {
            registerUiState = registerUiState.copy(error = "As senhas não conferem.")
            return
        }

        viewModelScope.launch {
            registerUiState = registerUiState.copy(isLoading = true, error = null)
            try {
                val req = RegisterRequest(
                    name = registerUiState.name,
                    email = registerUiState.email,
                    senha = registerUiState.password,
                    city = registerUiState.city,
                    state = registerUiState.state,
                    userType = "COMMON"
                )
                val resp = RetrofitClient.api.register(req)
                if (resp.isSuccessful) {
                    registerUiState = registerUiState.copy(isLoading = false, error = null)
                    // Você pode adicionar um isSuccess aqui também se quiser navegar automático
                } else {
                    registerUiState = registerUiState.copy(isLoading = false, error = "Erro: ${resp.code()}")
                }
            } catch (e: Exception) {
                registerUiState = registerUiState.copy(isLoading = false, error = e.message)
            }
        }
    }
}