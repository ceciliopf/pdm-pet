package com.example.pdm_pet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Definição das cores para fácil reutilização
val whiteColor = Color.White
val caramelColor = Color(0xFFC58C5A) // Um tom de caramelo elegante

@Composable
fun LoginScreen() {
    // Estados para armazenar o que o usuário digita.
    // O "remember" garante que o estado seja mantido quando a tela for redesenhada.
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Surface é um container que nos permite definir a cor de fundo.
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = whiteColor
    ) {
        // Column organiza os elementos verticalmente.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp), // Adiciona margens nas laterais
            verticalArrangement = Arrangement.Center, // Centraliza tudo na vertical
            horizontalAlignment = Alignment.CenterHorizontally // Centraliza tudo na horizontal
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
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuário ou E-mail") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
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
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(), // Esconde a senha
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
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

            Spacer(modifier = Modifier.height(32.dp))

            // Botão de Login
            Button(
                onClick = { /* Ação de login aqui (não funcional) */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = caramelColor)
            ) {
                Text(text = "Entrar", color = whiteColor, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão de texto para "Esqueceu a senha?"
            TextButton(onClick = { /* Ação aqui */ }) {
                Text("Esqueceu a senha?", color = caramelColor)
            }
        }
    }
}

// A anotação @Preview permite visualizar o Composable no painel de design do Android Studio
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    // Você pode colocar seu Composable dentro de um tema se tiver um definido
    // Ex: SuaAppTheme { LoginScreen() }
    LoginScreen()
}