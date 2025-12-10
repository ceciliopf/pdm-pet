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
import androidx.compose.material.icons.filled.ArrowDropDown
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

// --- FUNÇÕES UTILITÁRIAS ---
fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}

fun createImageFile(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.externalCacheDir
    val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

// --- COMPONENTE DROPDOWN REUTILIZÁVEL ---
@Composable
fun SimpleDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Expandir")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAnimalScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateAnimalViewModel = viewModel()
) {
    val context = LocalContext.current

    // --- ESTADOS DO FORMULÁRIO ---
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    // Estados para os Dropdowns (Valores visíveis vs Valores para API)
    var selectedSex by remember { mutableStateOf("Desconhecido") }
    val sexOptions = listOf("Macho" to "MALE", "Fêmea" to "FEMALE", "Desconhecido" to "UNKNOWN")

    var selectedSize by remember { mutableStateOf("Médio") }
    val sizeOptions = listOf("Pequeno" to "SMALL", "Médio" to "MEDIUM", "Grande" to "LARGE")

    var selectedStatus by remember { mutableStateOf("Na Rua") }
    val statusOptions = listOf(
        "Na Rua" to "ON_STREET",
        "Lar Temporário" to "TEMP_HOME",
        "Para Adoção" to "AVAILABLE_FOR_ADOPTION"
    )

    // --- ESTADOS DA IMAGEM ---
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // --- LAUNCHERS ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            photoUri = it
            photoBitmap = if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION") MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it))
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            photoUri = tempCameraUri
            photoBitmap = if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION") MediaStore.Images.Media.getBitmap(context.contentResolver, tempCameraUri!!)
            } else {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, tempCameraUri!!))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Pet", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ÁREA DA FOTO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
                    .clickable { showImageSourceDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Foto",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.DarkGray)
                        Text("Toque para adicionar foto", color = Color.DarkGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CAMPOS
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nome Provisório") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Descrição e Referência") }, modifier = Modifier.fillMaxWidth(), minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = age, onValueChange = { age = it },
                label = { Text("Idade Aproximada (ex: 2 anos)") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // DROPDOWNS
            SimpleDropdown("Sexo", sexOptions.map { it.first }, selectedSex) { selectedSex = it }
            Spacer(modifier = Modifier.height(16.dp))
            SimpleDropdown("Porte", sizeOptions.map { it.first }, selectedSize) { selectedSize = it }
            Spacer(modifier = Modifier.height(16.dp))
            SimpleDropdown("Status", statusOptions.map { it.first }, selectedStatus) { selectedStatus = it }

            Spacer(modifier = Modifier.height(32.dp))

            if (viewModel.errorMessage != null) {
                Text(viewModel.errorMessage!!, color = Color.Red, fontSize = 14.sp)
            }

            // BOTÃO SALVAR
            Button(
                onClick = {
                    val photosList = ArrayList<String>()
                    photoBitmap?.let { photosList.add(bitmapToBase64(it)) }

                    // Converte os valores visuais de volta para ENUMS da API
                    val apiSex = sexOptions.find { it.first == selectedSex }?.second ?: "UNKNOWN"
                    val apiSize = sizeOptions.find { it.first == selectedSize }?.second ?: "MEDIUM"
                    val apiStatus = statusOptions.find { it.first == selectedStatus }?.second ?: "ON_STREET"

                    viewModel.createAnimal(
                        name = name,
                        description = description,
                        age = age,
                        photos = photosList,
                        sex = apiSex,
                        size = apiSize,
                        status = apiStatus,
                        onSuccess = { onNavigateBack() }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = caramelColor),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Salvar Pet", color = Color.White)
                }
            }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Escolher Foto") },
            text = { Text("Selecione a origem:") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    val uri = createImageFile(context)
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