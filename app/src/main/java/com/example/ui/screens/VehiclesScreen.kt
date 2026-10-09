package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CreateVehicleRequest
import com.example.data.model.Vehicle
import com.example.data.model.VehicleType
import com.example.data.repository.CpvtsRepository
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CampusNavy
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusFull
import com.example.ui.theme.TechCyan
import kotlinx.coroutines.launch

@Composable
fun VehiclesScreen(
    repository: CpvtsRepository,
    modifier: Modifier = Modifier
) {
    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedVehicleForQr by remember { mutableStateOf<Vehicle?>(null) }
    var vehicleToDelete by remember { mutableStateOf<Vehicle?>(null) }

    val scope = rememberCoroutineScope()

    fun loadVehicles() {
        isLoading = true
        errorMessage = null
        scope.launch {
            val res = repository.getVehicles()
            isLoading = false
            res.onSuccess {
                vehicles = it
            }.onFailure {
                errorMessage = it.localizedMessage ?: "Failed to load vehicles"
            }
        }
    }

    LaunchedEffect(Unit) {
        loadVehicles()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Registered Vehicles",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${vehicles.size} vehicles linked to your campus profile",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_vehicle_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Vehicle", fontSize = 12.sp)
                }
            }

            // Content
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CampusNavy)
                    }
                }

                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = StatusFull, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = errorMessage ?: "", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(onClick = { loadVehicles() }, colors = ButtonDefaults.buttonColors(containerColor = CampusNavy)) {
                            Text("Retry")
                        }
                    }
                }

                vehicles.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No vehicles registered yet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Register your car, EV, or motorcycle to obtain campus parking passes and gate access.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { showAddDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = CampusNavy)) {
                            Text("Register First Vehicle")
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("vehicles_list"),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(vehicles, key = { it.id }) { vehicle ->
                            VehicleCard(
                                vehicle = vehicle,
                                onShowQr = { selectedVehicleForQr = vehicle },
                                onDelete = { vehicleToDelete = vehicle }
                            )
                        }
                    }
                }
            }
        }

        // Add Vehicle Dialog
        if (showAddDialog) {
            AddVehicleDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { request ->
                    scope.launch {
                        val res = repository.registerVehicle(request)
                        res.onSuccess { loadVehicles() }
                    }
                    showAddDialog = false
                }
            )
        }

        // QR Dialog
        selectedVehicleForQr?.let { vehicle ->
            VehicleQrDialog(
                vehicle = vehicle,
                onDismiss = { selectedVehicleForQr = null }
            )
        }

        // Delete Confirm Dialog
        vehicleToDelete?.let { vehicle ->
            AlertDialog(
                onDismissRequest = { vehicleToDelete = null },
                title = { Text("Unregister Vehicle") },
                text = { Text("Are you sure you want to unregister ${vehicle.licensePlate} (${vehicle.make} ${vehicle.model}) from CPVTS?") },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                repository.deleteVehicle(vehicle.id)
                                loadVehicles()
                            }
                            vehicleToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusFull)
                    ) {
                        Text("Unregister")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { vehicleToDelete = null }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun VehicleCard(
    vehicle: Vehicle,
    onShowQr: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vehicle_card_${vehicle.licensePlate}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // License Plate styled Box
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CampusNavy)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = vehicle.licensePlate,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            fontFamily = FontFamily.Monospace,
                            color = CampusNavy,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Type Badge
                val (typeBg, typeFg, typeIcon) = when (vehicle.vehicleType) {
                    VehicleType.EV -> Triple(TechCyan.copy(alpha = 0.15f), TechCyan, Icons.Default.ElectricCar)
                    VehicleType.TWO_WHEELER -> Triple(AmberGold.copy(alpha = 0.15f), AmberGold, Icons.Default.TwoWheeler)
                    else -> Triple(CampusNavy.copy(alpha = 0.10f), CampusNavy, Icons.Default.DirectionsCar)
                }

                Surface(color = typeBg, shape = RoundedCornerShape(6.dp)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = typeIcon, contentDescription = null, tint = typeFg, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = vehicle.vehicleType.name.replace("_", " "),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = typeFg
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Vehicle specs
            Text(
                text = "${vehicle.year ?: ""} ${vehicle.make} ${vehicle.model}".trim(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Color: ${vehicle.color}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campus presence pill & Permit indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (vehicle.isCurrentlyOnCampus) Icons.Default.LocationOn else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (vehicle.isCurrentlyOnCampus) StatusAvailable else Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (vehicle.isCurrentlyOnCampus) "Parked on Campus" else "Off Campus",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (vehicle.isCurrentlyOnCampus) StatusAvailable else Color.Gray
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onShowQr,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.QrCode, contentDescription = "Digital Pass QR", tint = CampusNavy)
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Unregister vehicle", tint = StatusFull)
                    }
                }
            }
        }
    }
}

@Composable
fun AddVehicleDialog(
    onDismiss: () -> Unit,
    onAdd: (CreateVehicleRequest) -> Unit
) {
    var plate by remember { mutableStateOf("") }
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var yearText by remember { mutableStateOf("2024") }
    var selectedType by remember { mutableStateOf(VehicleType.FOUR_WHEELER) }
    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register New Vehicle", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = plate,
                    onValueChange = { plate = it.uppercase() },
                    label = { Text("License Plate *") },
                    placeholder = { Text("e.g. 7XYZ890") },
                    modifier = Modifier.fillMaxWidth().testTag("add_vehicle_plate_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = make,
                        onValueChange = { make = it },
                        label = { Text("Make *") },
                        placeholder = { Text("Toyota") },
                        modifier = Modifier.weight(1f).testTag("add_vehicle_make_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Model *") },
                        placeholder = { Text("RAV4") },
                        modifier = Modifier.weight(1f).testTag("add_vehicle_model_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = { Text("Color *") },
                        placeholder = { Text("Silver") },
                        modifier = Modifier.weight(1f).testTag("add_vehicle_color_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = yearText,
                        onValueChange = { yearText = it },
                        label = { Text("Year") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Vehicle Category", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        VehicleType.FOUR_WHEELER to "Car",
                        VehicleType.EV to "EV/Hybrid",
                        VehicleType.TWO_WHEELER to "Motorcycle"
                    ).forEach { (type, label) ->
                        val isSelected = selectedType == type
                        OutlinedButton(
                            onClick = { selectedType = type },
                            colors = if (isSelected) ButtonDefaults.outlinedButtonColors(containerColor = CampusNavy, contentColor = Color.White)
                            else ButtonDefaults.outlinedButtonColors(),
                            modifier = Modifier.weight(1f).height(34.dp)
                        ) {
                            Text(label, fontSize = 10.sp)
                        }
                    }
                }

                if (validationError != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = validationError ?: "", color = StatusFull, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (plate.isBlank() || make.isBlank() || model.isBlank() || color.isBlank()) {
                        validationError = "Please complete all required fields (*)"
                        return@Button
                    }
                    val yr = yearText.toIntOrNull()
                    onAdd(
                        CreateVehicleRequest(
                            licensePlate = plate.trim().uppercase(),
                            make = make.trim(),
                            model = model.trim(),
                            color = color.trim(),
                            year = yr,
                            vehicleType = selectedType
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
                modifier = Modifier.testTag("confirm_add_vehicle_button")
            ) {
                Text("Register")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun VehicleQrDialog(
    vehicle: Vehicle,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gate Scanner Pass", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${vehicle.licensePlate} • ${vehicle.make} ${vehicle.model}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = CampusNavy
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Simulated High-contrast University Pass QR Box
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(2.dp, CampusNavy, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "QR Code",
                            tint = Color.Black,
                            modifier = Modifier.size(110.dp)
                        )
                        Text(
                            text = "CPVTS-PASS-${vehicle.licensePlate}",
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Present at campus gate cameras or enforcement scanners for rapid entry and permit validation.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = CampusNavy)) {
                Text("Done")
            }
        }
    )
}
