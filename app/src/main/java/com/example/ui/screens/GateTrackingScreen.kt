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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.data.model.ActiveTracking
import com.example.data.model.GateEntryRequest
import com.example.data.model.GateExitRequest
import com.example.data.model.TrackingLog
import com.example.data.repository.CpvtsRepository
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CampusNavy
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusFull
import kotlinx.coroutines.launch

@Composable
fun GateTrackingScreen(
    repository: CpvtsRepository,
    modifier: Modifier = Modifier
) {
    var activeList by remember { mutableStateOf<List<ActiveTracking>>(emptyList()) }
    var historyList by remember { mutableStateOf<List<TrackingLog>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Active On Campus, 1: History Logs
    var searchQuery by remember { mutableStateOf("") }

    var showEntryDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var vehicleToExit by remember { mutableStateOf<ActiveTracking?>(null) }

    val scope = rememberCoroutineScope()

    fun loadData() {
        isLoading = true
        errorMessage = null
        scope.launch {
            val resActive = repository.getActiveTracking()
            val resHistory = repository.getTrackingHistory()
            isLoading = false

            resActive.onSuccess { activeList = it }
                .onFailure { errorMessage = it.localizedMessage ?: "Failed to fetch active tracking" }

            resHistory.onSuccess { historyList = it }
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    val filteredActive = remember(activeList, searchQuery) {
        if (searchQuery.isBlank()) activeList
        else activeList.filter {
            it.licensePlate.contains(searchQuery, ignoreCase = true) ||
                    (it.driverName?.contains(searchQuery, ignoreCase = true) == true) ||
                    (it.parkLocationLot?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
                    text = "Gate Access & Tracking",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${activeList.size} vehicles currently tracked on campus",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { showEntryDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusAvailable),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("gate_entry_button")
                ) {
                    Icon(imageVector = Icons.Default.Input, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Entry", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showExitDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("gate_exit_button")
                ) {
                    Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Exit", fontSize = 11.sp)
                }
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = CampusNavy
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Active on Campus (${activeList.size})", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Gate Activity Logs", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
            )
        }

        if (selectedTab == 0) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("tracking_search_input"),
                placeholder = { Text("Filter by license plate or driver...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )
        }

        // Body
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CampusNavy)
                    }
                }

                errorMessage != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = StatusFull, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(errorMessage ?: "", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(onClick = { loadData() }, colors = ButtonDefaults.buttonColors(containerColor = CampusNavy)) {
                            Text("Retry")
                        }
                    }
                }

                selectedTab == 0 -> {
                    if (filteredActive.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No vehicles currently on campus matching query", color = Color.Gray, fontSize = 14.sp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().testTag("active_tracking_list"),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredActive, key = { it.id }) { item ->
                                ActiveTrackingCard(
                                    tracking = item,
                                    onLogExit = { vehicleToExit = item }
                                )
                            }
                        }
                    }
                }

                else -> {
                    // History Logs Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().testTag("tracking_history_list"),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(historyList, key = { it.id }) { log ->
                            GateHistoryRow(log = log)
                        }
                    }
                }
            }
        }
    }

    // Gate Entry Dialog
    if (showEntryDialog) {
        GateEntryDialog(
            onDismiss = { showEntryDialog = false },
            onConfirm = { req ->
                scope.launch {
                    repository.logGateEntry(req)
                    loadData()
                }
                showEntryDialog = false
            }
        )
    }

    // Gate Exit Dialog
    if (showExitDialog) {
        GateExitDialog(
            initialPlate = "",
            onDismiss = { showExitDialog = false },
            onConfirm = { req ->
                scope.launch {
                    repository.logGateExit(req)
                    loadData()
                }
                showExitDialog = false
            }
        )
    }

    // Quick Exit Dialog from row item
    vehicleToExit?.let { item ->
        GateExitDialog(
            initialPlate = item.licensePlate,
            onDismiss = { vehicleToExit = null },
            onConfirm = { req ->
                scope.launch {
                    repository.logGateExit(req)
                    loadData()
                }
                vehicleToExit = null
            }
        )
    }
}

@Composable
fun ActiveTrackingCard(
    tracking: ActiveTracking,
    onLogExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = tracking.licensePlate,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        color = CampusNavy
                    )
                }

                StatusBadge(
                    statusText = if (tracking.hasValidPermit) "PERMIT VALID" else "NO PERMIT",
                    statusType = if (tracking.hasValidPermit) "ACTIVE" else "FULL"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tracking.vehicleDescription,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!tracking.driverName.isNullOrBlank()) {
                Text(
                    text = "Driver: ${tracking.driverName} (${tracking.driverRole ?: "Campus Member"})",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = AmberGold, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = tracking.parkLocationLot ?: "General Lot",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Entered via ${tracking.entryGate} • ${tracking.entryTime}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                OutlinedButton(
                    onClick = onLogExit,
                    modifier = Modifier.height(30.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CampusNavy)
                ) {
                    Text("Log Exit", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun GateHistoryRow(log: TrackingLog) {
    val isEntry = log.action.equals("ENTRY", ignoreCase = true)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isEntry) StatusAvailable.copy(alpha = 0.15f) else Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isEntry) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (isEntry) StatusAvailable else CampusNavy,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = log.licensePlate,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${log.action} • ${log.gate}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = log.timestamp,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                if (log.permitChecked) {
                    Text("Verified", fontSize = 10.sp, color = StatusAvailable, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun GateEntryDialog(
    onDismiss: () -> Unit,
    onConfirm: (GateEntryRequest) -> Unit
) {
    var plate by remember { mutableStateOf("") }
    var selectedGate by remember { mutableStateOf("North Gate (Main Entry)") }
    var assignedLot by remember { mutableStateOf("North Campus Deck") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Gate Entry", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = plate,
                    onValueChange = { plate = it.uppercase() },
                    label = { Text("License Plate *") },
                    placeholder = { Text("e.g. 7XYZ890") },
                    modifier = Modifier.fillMaxWidth().testTag("entry_plate_input"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("Entry Gate", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                listOf("North Gate (Main Entry)", "West Gate (Engineering)", "South Gate (Arena)").forEach { g ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable { selectedGate = g }.padding(vertical = 3.dp)
                    ) {
                        Text(
                            text = if (selectedGate == g) "● $g" else "○ $g",
                            fontSize = 12.sp,
                            fontWeight = if (selectedGate == g) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedGate == g) CampusNavy else Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = assignedLot,
                    onValueChange = { assignedLot = it },
                    label = { Text("Destination Lot") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(error ?: "", color = StatusFull, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (plate.isBlank()) {
                        error = "Please enter vehicle plate"
                        return@Button
                    }
                    onConfirm(GateEntryRequest(licensePlate = plate.trim().uppercase(), gateName = selectedGate, assignedLot = assignedLot))
                },
                colors = ButtonDefaults.buttonColors(containerColor = StatusAvailable),
                modifier = Modifier.testTag("confirm_gate_entry_button")
            ) {
                Text("Confirm Entry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun GateExitDialog(
    initialPlate: String,
    onDismiss: () -> Unit,
    onConfirm: (GateExitRequest) -> Unit
) {
    var plate by remember { mutableStateOf(initialPlate) }
    var selectedGate by remember { mutableStateOf("North Gate") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Gate Exit", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = plate,
                    onValueChange = { plate = it.uppercase() },
                    label = { Text("License Plate *") },
                    placeholder = { Text("e.g. 7XYZ890") },
                    modifier = Modifier.fillMaxWidth().testTag("exit_plate_input"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("Exit Gate", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                listOf("North Gate", "South Gate", "West Gate").forEach { g ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable { selectedGate = g }.padding(vertical = 3.dp)
                    ) {
                        Text(
                            text = if (selectedGate == g) "● $g" else "○ $g",
                            fontSize = 12.sp,
                            fontWeight = if (selectedGate == g) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                if (error != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(error ?: "", color = StatusFull, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (plate.isBlank()) {
                        error = "Please enter vehicle plate"
                        return@Button
                    }
                    onConfirm(GateExitRequest(licensePlate = plate.trim().uppercase(), exitGate = selectedGate))
                },
                colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
                modifier = Modifier.testTag("confirm_gate_exit_button")
            ) {
                Text("Confirm Exit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
