package com.example.pdm_pet.features.animal_profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
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
import java.io.ByteArrayOutputStream

// --- FUNÇÃO UTILITÁRIA PARA CONVERSÃO ---
fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    // Comprime para JPEG com qualidade 70% para reduzir o tamanho da string final
    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()

    // Retorna a String Base64 sem quebras de linha (NO_WRAP)
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAnimalScreen(
    onNavigateBack: () -> Unit,
    // Injeção do ViewModel que criamos
    viewModel: CreateAnimalViewModel = viewModel()
) {
    val context = LocalContext.current

    // Estado do formulário local
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Campo visual para ponto de referência (a lat/long será fixa no ViewModel por enquanto)
    var locationReference by remember { mutableStateOf("") }

    // Estados para a Imagem
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Lançador para abrir a Galeria do celular
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
        uri?.let {
            // Lógica para converter URI em Bitmap (compatível com Android P+ e anteriores)
            photoBitmap = if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Pet", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Permite rolar a tela com teclado
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. ÁREA DA FOTO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
                    .clickable {
                        // Abre a galeria filtrando apenas imagens
                        launcher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    // Exibe a foto selecionada
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Foto selecionada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder se não houver foto
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Adicionar Foto",
                            modifier = Modifier.size(48.dp),
                            tint = Color.DarkGray
                        )
                        Text("Toque para adicionar foto", color = Color.DarkGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. FORMULÁRIO DE TEXTO
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome (ou apelido)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição (saúde, comportamento)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = locationReference,
                onValueChange = { locationReference = it },
                label = { Text("Ponto de Referência") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // EXIBIR MENSAGEM DE ERRO DO VIEWMODEL (SE HOUVER)
            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // 3. BOTÃO SALVAR INTEGRADO
            Button(
                onClick = {
                    // Prepara a lista de fotos (Base64)
                    val photosList = ArrayList<String>()

                    photoBitmap?.let { bitmap ->
                        val base64String = bitmapToBase64(bitmap)
                        photosList.add(base64String)
                    }

                    // Chama o ViewModel para enviar ao Backend
                    viewModel.createAnimal(
                        name = name,
                        description = "$description (Ref: $locationReference)", // Junta a ref na descrição
                        photos = photosList,
                        onSuccess = {
                            // Se o cadastro for bem-sucedido, volta para o Feed
                            onNavigateBack()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = caramelColor),
                // Desabilita o botão enquanto estiver carregando
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Salvar Pet", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}