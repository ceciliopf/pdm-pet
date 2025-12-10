package com.example.pdm_pet.features.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pdm_pet.ui.theme.caramelColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val user = viewModel.currentUser
    val isLoading = viewModel.isLoading
    val message = viewModel.message
    val context = LocalContext.current

    // Launcher para pegar foto da galeria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it))
            }
            // Chama o ViewModel para atualizar assim que selecionar
            viewModel.updatePhoto(bitmap)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Meu Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- ÁREA DA FOTO ---
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                // A Foto em si
                val photoUrl = viewModel.getUserImageUrl(user?.profilePictureUrl)

                if (photoUrl != null) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Foto de Perfil",
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder se não tiver foto
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color.White
                        )
                    }
                }

                // Botãozinho flutuante de editar (câmera)
                SmallFloatingActionButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    containerColor = caramelColor,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Alterar Foto")
                }
            }

            // --- NOME E EMAIL ---
            if (user != null) {
                user.name?.let { Text(text = it, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = caramelColor) }
                Spacer(modifier = Modifier.height(4.dp))
                user.email?.let { Text(text = it, fontSize = 16.sp, color = Color.Gray) }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "${user.city ?: ""} - ${user.state ?: ""}", fontSize = 14.sp, color = Color.Gray)
            } else if (isLoading) {
                CircularProgressIndicator(color = caramelColor)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- AÇÕES ---

            // Botão Alterar Foto (Texto)
            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Alterar Foto da Galeria")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Remover Foto
            // Só mostra se tiver foto para remover
            if (user?.profilePictureUrl != null) {
                OutlinedButton(
                    onClick = { viewModel.deletePhoto() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Remover Foto Atual")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botão Sair (Logout)
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Sair da Conta")
            }

            // --- MENSAGENS DE FEEDBACK ---
            if (message != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = message, color = if(message.contains("Erro")) Color.Red else Color(0xFF4CAF50))
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = caramelColor)
            }
        }
    }
}