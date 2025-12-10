package com.example.pdm_pet.features.feed

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pdm_pet.ui.components.AnimalCard
import com.example.pdm_pet.ui.theme.caramelColor
import com.google.android.gms.location.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    feedViewModel: FeedViewModel = viewModel(),
    onNavigateToCreateAnimal: () -> Unit,
    onNavigateToDetails: (String) -> Unit
) {
    val animals = feedViewModel.animals
    val context = LocalContext.current
    var hasLocationPermission by remember { mutableStateOf(false) }
    var locationStatus by remember { mutableStateOf("Aguardando permissão...") }

    // Cliente de Localização do Google (Fused Location)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Função para pegar a localização exata
    @SuppressLint("MissingPermission") // Só chamamos se tiver permissão
    fun getCurrentLocation() {
        locationStatus = "Obtendo GPS..."

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                locationStatus = "Localizado!"
                // Manda a latitude/longitude real para o ViewModel
                feedViewModel.fetchAnimals(location.latitude, location.longitude)
            } else {
                // Se lastLocation for null (GPS desligado recentemente), força uma atualização
                val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()
                val callback = object : LocationCallback() {
                    override fun onLocationResult(res: LocationResult) {
                        res.lastLocation?.let { loc ->
                            feedViewModel.fetchAnimals(loc.latitude, loc.longitude)
                            fusedLocationClient.removeLocationUpdates(this)
                        }
                    }
                }
                fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
            }
        }.addOnFailureListener {
            locationStatus = "Erro ao pegar GPS."
        }
    }

    // Lançador para pedir permissão
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasLocationPermission = granted
        if (granted) {
            getCurrentLocation()
        } else {
            locationStatus = "Permissão de GPS negada."
        }
    }

    // Ao iniciar a tela, verifica ou pede permissão
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            hasLocationPermission = true
            getCurrentLocation()
        } else {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Adote um Amigo", fontWeight = FontWeight.Bold, color = caramelColor) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                actions = {
                    IconButton(onClick = {
                        if(hasLocationPermission) getCurrentLocation()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Atualizar", tint = caramelColor)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToCreateAnimal() },
                containerColor = caramelColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Cadastrar Animal")
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->

        if (animals.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = if (feedViewModel.isLoading) "Buscando..." else "Nenhum animal próximo.", color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Status: $locationStatus", fontSize = 12.sp, color = Color.LightGray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(animals) { animal ->
                    AnimalCard(
                        name = animal.name,
                        description = animal.description,
                        photoUrl = animal.photoUrl,
                        location = animal.distance, // Agora mostra a distância real
                        status = animal.status,
                        gender = animal.gender,
                        onAdoptClick = { onNavigateToDetails(animal.id.toString()) }
                    )
                }
            }
        }
    }
}