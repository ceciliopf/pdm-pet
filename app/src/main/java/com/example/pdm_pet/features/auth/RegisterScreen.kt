package com.example.pdm_pet.features.auth

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pdm_pet.ui.theme.caramelColor
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Funções Utilitárias para Imagem ---
private fun createTempImageFile(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.externalCacheDir
    val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT < 28) {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
    }
}

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel = viewModel(),
    onNavigateToLogin: () -> Unit
) {
    val uiState = authViewModel.registerUiState
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // --- ESTADOS DA FOTO ---
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    // Lançador Galeria
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bitmap = loadBitmapFromUri(context, it)
            photoBitmap = bitmap
            authViewModel.onPhotoSelected(bitmap)
        }
    }

    // Lançador Câmera
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempCameraUri != null) {
            val bitmap = loadBitmapFromUri(context, tempCameraUri!!)
            photoBitmap = bitmap
            authViewModel.onPhotoSelected(bitmap)
        }
    }

    // Se o cadastro foi bem sucedido (erro nulo e loading parou, mas idealmente teríamos um flag isSuccess)
    // Para simplificar, vamos verificar se o erro é nulo após clicar em registrar?
    // O ideal seria o ViewModel ter um 'isSuccess' igual no Login.
    // Por enquanto, o usuário clica em "Já tem conta" ou podemos adicionar um LaunchedEffect se adicionarmos isSuccess no RegisterUiState.

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crie sua Conta", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = caramelColor)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Ajude a salvar um amigo", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            // --- FOTO DE PERFIL ---
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable { showImageSourceDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (photoBitmap != null) {
                    Image(
                        bitmap = photoBitmap!!.asImageBitmap(),
                        contentDescription = "Foto",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(40.dp))
                }
            }
            Text("Adicionar foto", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // --- FORMULÁRIO ---

            // Nome
            OutlinedTextField(
                value = uiState.name, onValueChange = { authViewModel.onNameChange(it) },
                label = { Text("Nome Completo") },
                leadingIcon = { Icon(Icons.Filled.Person, null, tint = caramelColor) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Email
            OutlinedTextField(
                value = uiState.email, onValueChange = { authViewModel.onEmailChange(it) },
                label = { Text("E-mail") },
                leadingIcon = { Icon(Icons.Filled.Email, null, tint = caramelColor) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Telefone (WhatsApp)
            OutlinedTextField(
                value = uiState.phone, onValueChange = { authViewModel.onPhoneChange(it) },
                label = { Text("WhatsApp (com DDD)") },
                placeholder = { Text("Ex: 34999998888") },
                leadingIcon = { Icon(Icons.Filled.Phone, null, tint = caramelColor) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Cidade
            OutlinedTextField(
                value = uiState.city, onValueChange = { authViewModel.onCityChange(it) },
                label = { Text("Cidade") },
                leadingIcon = { Icon(Icons.Filled.LocationCity, null, tint = caramelColor) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Estado
            OutlinedTextField(
                value = uiState.state, onValueChange = { authViewModel.onStateChange(it) },
                label = { Text("Estado") },
                leadingIcon = { Icon(Icons.Filled.Map, null, tint = caramelColor) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Senha
            OutlinedTextField(
                value = uiState.password, onValueChange = { authViewModel.onRegisterPasswordChange(it) },
                label = { Text("Senha") },
                leadingIcon = { Icon(Icons.Filled.Lock, null, tint = caramelColor) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Confirmar Senha
            OutlinedTextField(
                value = uiState.confirmPassword, onValueChange = { authViewModel.onConfirmPasswordChange(it) },
                label = { Text("Confirmar Senha") },
                leadingIcon = { Icon(Icons.Filled.Lock, null, tint = caramelColor) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            // Mensagem de Erro
            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = uiState.error, color = Color.Red, fontSize = 14.sp)
            } else if (!uiState.isLoading && uiState.name.isNotBlank() && !authViewModel.registerUiState.isLoading) {
                // Dica: Poderíamos verificar sucesso aqui, mas por simplicidade mostramos só o erro
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botão Cadastrar
            Button(
                onClick = {
                    authViewModel.register()
                    // Se quiser navegar automaticamente ao clicar e não houver erro imediato (melhor seria observar um estado isSuccess)
                    // onNavigateToLogin() <--- Não coloque aqui direto, espere o sucesso.
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = caramelColor)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Cadastrar", fontSize = 18.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { onNavigateToLogin() }) {
                Text("Já tem uma conta? Entre", color = caramelColor)
            }
        }
    }

    // --- DIALOG ESCOLHA DE FOTO ---
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Escolher Foto") },
            text = { Text("Selecione a origem:") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    val uri = createTempImageFile(context)
                    tempCameraUri = uri
                    cameraLauncher.launch(uri)
                }) { Text("Câmera") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    galleryLauncher.launch("image/*")
                }) { Text("Galeria") }
            }
        )
    }
}