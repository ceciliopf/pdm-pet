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
import androidx.compose.material.icons.filled.Person
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
    onNavigateToDetails: (String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val animals = feedViewModel.animals
    val context = LocalContext.current

    // Estados para controle de permissão e status do GPS
    var hasLocationPermission by remember { mutableStateOf(false) }
    var locationStatus by remember { mutableStateOf("Aguardando permissão...") }

    // Cliente de Localização do Google (Fused Location)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Função para pegar a localização exata
    @SuppressLint("MissingPermission") // Supressão segura pois verificamos antes de chamar
    fun getCurrentLocation() {
        locationStatus = "Obtendo GPS..."

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                locationStatus = "Localizado!"
                // Manda a latitude/longitude real para o ViewModel buscar animais próximos
                feedViewModel.fetchAnimals(location.latitude, location.longitude)
            } else {
                // Se lastLocation for null (GPS desligado recentemente), força uma atualização
                val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()
                val callback = object : LocationCallback() {
                    override fun onLocationResult(res: LocationResult) {
                        res.lastLocation?.let { loc ->
                            feedViewModel.fetchAnimals(loc.latitude, loc.longitude)
                            // Remove o listener para não ficar gastando bateria
                            fusedLocationClient.removeLocationUpdates(this)
                        }
                    }
                }
                fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
                locationStatus = "Atualizando GPS..."
            }
        }.addOnFailureListener {
            locationStatus = "Erro ao pegar GPS."
        }
    }

    // Lançador para pedir permissão ao sistema
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

    // Ao iniciar a tela, verifica se já tem permissão ou pede
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
                title = {
                    Text(
                        text = "Adote um Amigo",
                        fontWeight = FontWeight.Bold,
                        color = caramelColor
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),

                // ÍCONE DE PERFIL (ESQUERDA)
                navigationIcon = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Meu Perfil",
                            tint = Color.Gray
                        )
                    }
                },

                // ÍCONE DE ATUALIZAR (DIREITA)
                actions = {
                    IconButton(onClick = {
                        if(hasLocationPermission) getCurrentLocation()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar",
                            tint = caramelColor
                        )
                    }
                }
            )
        },

        // BOTÃO FLUTUANTE PARA CRIAR NOVO ANIMAL
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToCreateAnimal() },
                containerColor = caramelColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Cadastrar Animal")
            }
        },

        containerColor = Color(0xFFF5F5F5) // Fundo cinza claro
    ) { paddingValues ->

        if (animals.isEmpty()) {
            // ESTADO DE LISTA VAZIA OU CARREGANDO
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (feedViewModel.isLoading) {
                        CircularProgressIndicator(color = caramelColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Buscando amigos próximos...", color = Color.Gray)
                    } else {
                        Text(
                            text = feedViewModel.errorMsg ?: "Nenhum animal encontrado.",
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "GPS: $locationStatus", fontSize = 12.sp, color = Color.LightGray)
                }
            }
        } else {
            // LISTA DE ANIMAIS
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(animals) { animal ->
                    AnimalCard(
                        name = animal.name,
                        description = animal.description,
                        photoUrl = animal.photoUrl,
                        location = animal.distance, // Mostra a distância formatada (ex: "2.5 km")
                        status = animal.status,
                        gender = animal.gender,
                        onAdoptClick = {
                            onNavigateToDetails(animal.id.toString())
                        }
                    )
                }
            }
        }
    }
}