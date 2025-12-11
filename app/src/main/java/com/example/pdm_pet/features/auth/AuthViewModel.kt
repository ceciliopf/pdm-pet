package com.example.pdm_pet.features.auth

import android.graphics.Bitmap
import android.util.Base64
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
import java.io.ByteArrayOutputStream

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val city: String = "",
    val state: String = "",
    val photoBase64: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    // ================= LOGIN =================
    var loginUiState by mutableStateOf(LoginUiState())
        private set

    fun onUsernameChange(n: String) { loginUiState = loginUiState.copy(username = n) }
    fun onPasswordChange(n: String) { loginUiState = loginUiState.copy(password = n) }

    fun login() {
        if (loginUiState.isLoading) return

        viewModelScope.launch {
            loginUiState = loginUiState.copy(isLoading = true, error = null, isSuccess = false)
            try {
                val request = LoginRequest(
                    email = loginUiState.username,
                    senha = loginUiState.password
                )
                val response = RetrofitClient.api.login(request)

                if (response.isSuccessful && response.body() != null) {
                    UserSession.setLoggedInUser(response.body()!!)
                    loginUiState = loginUiState.copy(isLoading = false, isSuccess = true)
                } else {
                    val msg = if (response.code() == 403) "Credenciais inválidas" else "Erro: ${response.code()}"
                    loginUiState = loginUiState.copy(isLoading = false, error = msg)
                }
            } catch (e: Exception) {
                loginUiState = loginUiState.copy(isLoading = false, error = "Erro de conexão.")
                e.printStackTrace()
            }
        }
    }

    // ================= CADASTRO (REGISTER) =================
    var registerUiState by mutableStateOf(RegisterUiState())
        private set

    fun onNameChange(n: String) { registerUiState = registerUiState.copy(name = n) }
    fun onEmailChange(n: String) { registerUiState = registerUiState.copy(email = n) }
    fun onPhoneChange(n: String) { registerUiState = registerUiState.copy(phone = n) }
    fun onRegisterPasswordChange(n: String) { registerUiState = registerUiState.copy(password = n) }
    fun onConfirmPasswordChange(n: String) { registerUiState = registerUiState.copy(confirmPassword = n) }
    fun onCityChange(n: String) { registerUiState = registerUiState.copy(city = n) }
    fun onStateChange(n: String) { registerUiState = registerUiState.copy(state = n) }

    fun onPhotoSelected(bitmap: Bitmap?) {
        if (bitmap == null) return
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteArray = outputStream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        registerUiState = registerUiState.copy(photoBase64 = base64String)
    }

    fun register() {
        if (registerUiState.isLoading) return

        // Validações
        if (registerUiState.name.isBlank() || registerUiState.email.isBlank()) {
            registerUiState = registerUiState.copy(error = "Preencha todos os campos.")
            return
        }
        if (registerUiState.phone.isBlank()) {
            registerUiState = registerUiState.copy(error = "Telefone é obrigatório.")
            return
        }
        if (registerUiState.password != registerUiState.confirmPassword) {
            registerUiState = registerUiState.copy(error = "As senhas não conferem.")
            return
        }

        viewModelScope.launch {
            registerUiState = registerUiState.copy(isLoading = true, error = null)
            try {
                // 1. CHECAGEM DE E-MAIL: Verifica se o e-mail já existe na base
                val checkEmailResponse = RetrofitClient.api.checkEmail(registerUiState.email)

                // Se a API retornar true, significa que o e-mail já está cadastrado
                if (checkEmailResponse.isSuccessful && checkEmailResponse.body() == true) {
                    registerUiState = registerUiState.copy(
                        isLoading = false,
                        error = "Este e-mail já está em uso por outro usuário."
                    )
                    return@launch
                }

                // 2. SE O E-MAIL ESTIVER LIVRE, SEGUE COM O CADASTRO
                val req = RegisterRequest(
                    name = registerUiState.name,
                    email = registerUiState.email,
                    senha = registerUiState.password,
                    city = registerUiState.city,
                    state = registerUiState.state,
                    phone = registerUiState.phone,
                    userType = "COMMON",
                    userPhotoUrl = registerUiState.photoBase64
                )

                val resp = RetrofitClient.api.register(req)

                if (resp.isSuccessful) {
                    registerUiState = registerUiState.copy(isLoading = false, error = null)
                    // Sucesso: A UI vai notar o erro == null e pode navegar
                } else {
                    registerUiState = registerUiState.copy(isLoading = false, error = "Erro no cadastro: ${resp.code()}")
                }
            } catch (e: Exception) {
                registerUiState = registerUiState.copy(isLoading = false, error = "Erro técnico: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}