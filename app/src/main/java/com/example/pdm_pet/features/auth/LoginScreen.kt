package com.example.pdm_pet.features.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import com.example.pdm_pet.ui.theme.caramelColor // Importa sua cor do tema

@Composable
fun LoginScreen(
    // Recebe o ViewModel e o callback de navegação
    authViewModel: AuthViewModel = viewModel(),
    onNavigateToRegister: () -> Unit
) {
    // O estado agora vem DIRETAMENTE do ViewModel
    val uiState = authViewModel.loginUiState

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White // Usa a cor padrão do Compose
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bem-Vindo",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = caramelColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Faça login para continuar",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Campo de texto para o nome de usuário/email
            OutlinedTextField(
                value = uiState.username,
                onValueChange = { authViewModel.onUsernameChange(it) },
                label = { Text("Usuário ou E-mail") },
                isError = uiState.error != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person, // Padrão 'Filled'
                        contentDescription = "Ícone de usuário",
                        tint = caramelColor
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = caramelColor,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = caramelColor,
                    focusedLabelColor = caramelColor
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de texto para a senha
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { authViewModel.onPasswordChange(it) },
                label = { Text("Senha") },
                isError = uiState.error != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock, // Padrão 'Filled'
                        contentDescription = "Ícone de cadeado",
                        tint = caramelColor
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = caramelColor,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = caramelColor,
                    focusedLabelColor = caramelColor
                )
            )

            // Mostra a mensagem de erro, se existir
            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.error,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botão de Login
            Button(
                onClick = { authViewModel.login() },
                enabled = !uiState.isLoading, // Desabilita o botão enquanto carrega
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = caramelColor)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(text = "Entrar", color = Color.White, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão de texto para NAVEGAR para Cadastro
            TextButton(onClick = { onNavigateToRegister() }) {
                Text("Não tem conta? Cadastre-se", color = caramelColor)
            }
        }
    }
}

// O Preview precisa de um valor padrão para o callback
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onNavigateToRegister = {}) // Passa uma função vazia para o preview
}