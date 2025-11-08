package com.example.pdm_pet.features.auth


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

val whiteColor = Color.White
val caramelColor = Color(0xFFC58C5A)


@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState = authViewModel.registerUiState

    Surface(modifier = Modifier.fillMaxSize(), color = whiteColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crie sua Conta",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = caramelColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ajude a salvar um amigo",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Campos de Cadastro (RN-U02)
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { authViewModel.onNameChange(it) },
                label = { Text("Nome Completo") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = caramelColor) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { authViewModel.onEmailChange(it) },
                label = { Text("E-mail") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = caramelColor) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.city,
                onValueChange = { authViewModel.onCityChange(it) },
                label = { Text("Cidade") },
                leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = caramelColor) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.state,
                onValueChange = { authViewModel.onStateChange(it) },
                label = { Text("Estado") },
                leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = caramelColor) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { authViewModel.onRegisterPasswordChange(it) },
                label = { Text("Senha") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = caramelColor) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = { authViewModel.onConfirmPasswordChange(it) },
                label = { Text("Confirmar Senha") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = caramelColor) },
                isError = uiState.error?.contains("senhas") == true, // Marca erro se as senhas não conferem
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = uiState.error, color = Color.Red, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { authViewModel.register() },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = caramelColor)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = whiteColor, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Cadastrar", color = whiteColor, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { /* TODO: Navegar para Login */ }) {
                Text("Já tem uma conta? Entre", color = caramelColor)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}