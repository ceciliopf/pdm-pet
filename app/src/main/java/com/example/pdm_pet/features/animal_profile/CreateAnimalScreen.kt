package com.example.pdm_pet.features.animal_profile

import android.content.Context
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pdm_pet.ui.theme.caramelColor
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- FUNÇÃO UTILITÁRIA PARA CONVERSÃO (Bitmap -> Base64) ---
fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    // Comprime para JPEG com qualidade 70% para reduzir o tamanho da string final
    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()

    // Retorna a String Base64 sem quebras de linha (NO_WRAP)
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}

// --- FUNÇÃO AUXILIAR PARA CRIAR ARQUIVO TEMPORÁRIO DA CÂMERA ---
fun createImageFile(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.externalCacheDir
    val file = File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
    // ATENÇÃO: A string abaixo deve ser igual ao 'authorities' definido no AndroidManifest.xml
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAnimalScreen(
    onNavigateBack: () -> Unit,
    // Injeção do ViewModel
    viewModel: CreateAnimalViewModel = viewModel()
) {
    val context = LocalContext.current

    // --- ESTADOS DO FORMULÁRIO ---
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var locationReference by remember { mutableStateOf("") }

    // --- ESTADOS DA IMAGEM ---
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Controle do Dialog "Câmera ou Galeria?"
    var showImageSourceDialog by remember { mutableStateOf(false) }

    // Variável para guardar o caminho da foto que a câmera vai tirar
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // --- LAUNCHERS (INTENTS) ---

    // 1. Galeria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Se o usuário cancelou, uri vem null
        uri?.let {
            photoUri = it
            // Converte URI da galeria para Bitmap
            photoBitmap = if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
        }
    }

    // 2. Câmera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            photoUri = tempCameraUri
            // Converte URI do arquivo temporário para Bitmap
            photoBitmap = if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, tempCameraUri!!)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, tempCameraUri!!)
                ImageDecoder.decodeBitmap(source)
            }
        }
    }

    // --- UI (SCAFFOLD) ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Pet", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->

        // CONTEÚDO COM SCROLL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. ÁREA DA FOTO (CLICÁVEL)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
                    .clickable {
                        // Abre o Dialog para escolher
                        showImageSourceDialog = true
                    },
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    // Exibe a foto selecionada/tirada
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Foto do animal",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder
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

            // 2. CAMPOS DE TEXTO
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

            // EXIBIR MENSAGEM DE ERRO (SE HOUVER)
            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // 3. BOTÃO SALVAR
            Button(
                onClick = {
                    // Prepara a lista de fotos (Base64)
                    val photosList = ArrayList<String>()

                    photoBitmap?.let { bitmap ->
                        val base64String = bitmapToBase64(bitmap)
                        photosList.add(base64String)
                    }

                    // Chama o ViewModel
                    viewModel.createAnimal(
                        name = name,
                        description = "$description (Ref: $locationReference)",
                        photos = photosList,
                        onSuccess = {
                            onNavigateBack() // Volta para o Feed
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = caramelColor),
                enabled = !viewModel.isLoading // Evita duplo clique
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

    // --- DIALOG DE ESCOLHA (CÂMERA OU GALERIA) ---
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Escolher Foto") },
            text = { Text("Selecione a origem da imagem:") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    // Lógica para CÂMERA
                    try {
                        val uri = createImageFile(context)
                        tempCameraUri = uri // Guarda a URI para ler depois
                        cameraLauncher.launch(uri)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }) {
                    Text("Câmera")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    // Lógica para GALERIA
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Galeria")
                }
            }
        )
    }
}