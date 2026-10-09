package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LotStatus
import com.example.data.model.ParkingLot
import com.example.data.model.ParkingSlot
import com.example.data.model.SlotType
import com.example.data.repository.CpvtsRepository
import com.example.ui.components.OccupancyProgress
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CampusNavy
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusFull
import com.example.ui.theme.StatusLimited
import com.example.ui.theme.TechCyan
import kotlinx.coroutines.launch

@Composable
fun LotsScreen(
    repository: CpvtsRepository,
    modifier: Modifier = Modifier
) {
    var lots by remember { mutableStateOf<List<ParkingLot>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var selectedLotForDetails by remember { mutableStateOf<ParkingLot?>(null) }

    val scope = rememberCoroutineScope()

    fun loadLots() {
        isLoading = true
        errorMessage = null
        scope.launch {
            val result = repository.getLots()
            isLoading = false
            result.onSuccess {
                lots = it
            }.onFailure {
                errorMessage = it.localizedMessage ?: "Failed to load parking lots"
            }
        }
    }

    LaunchedEffect(Unit) {
        loadLots()
    }

    val filteredLots = remember(lots, searchQuery, selectedFilter) {
        lots.filter { lot ->
            val matchesQuery = lot.name.contains(searchQuery, ignoreCase = true) ||
                    lot.code.contains(searchQuery, ignoreCase = true) ||
                    lot.zone.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "AVAILABLE" -> lot.calculatedStatus == LotStatus.AVAILABLE
                "LIMITED" -> lot.calculatedStatus == LotStatus.LIMITED
                "EV" -> lot.evSlots > 0
                "ZONE_A" -> lot.zone.contains("Zone A", ignoreCase = true)
                "ZONE_B" -> lot.zone.contains("Zone B", ignoreCase = true)
                "ZONE_C" -> lot.zone.contains("Zone C", ignoreCase = true)
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search & Filter Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lots_search_input"),
                placeholder = { Text("Search by lot name, zone, or code...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search lots")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "ALL" to "All Lots",
                    "AVAILABLE" to "Available Only",
                    "EV" to "EV Charging",
                    "ZONE_A" to "Zone A",
                    "ZONE_B" to "Zone B",
                    "ZONE_C" to "Zone C"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = selectedFilter == key,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CampusNavy,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Content Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = CampusNavy)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Checking campus lot sensors...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
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
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = StatusFull,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Connection Error",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { loadLots() },
                            colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
                            modifier = Modifier.testTag("lots_retry_button")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry Connection")
                        }
                    }
                }

                filteredLots.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalParking,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No parking lots matched your criteria",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("lots_list"),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredLots, key = { it.id }) { lot ->
                            LotCard(
                                lot = lot,
                                onClick = { selectedLotForDetails = lot }
                            )
                        }
                    }
                }
            }
        }
    }

    // Detail Dialog
    selectedLotForDetails?.let { lot ->
        LotDetailDialog(
            lot = lot,
            repository = repository,
            onDismiss = { selectedLotForDetails = null }
        )
    }
}

@Composable
fun LotCard(
    lot: ParkingLot,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("lot_card_${lot.code}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CampusNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = lot.code.removePrefix("LOT-"),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = lot.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = lot.zone,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                StatusBadge(
                    statusText = lot.calculatedStatus.name,
                    statusType = lot.calculatedStatus.name
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Occupancy Progress
            OccupancyProgress(
                occupied = lot.occupiedCount,
                capacity = lot.totalCapacity
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Amenities row (Available, EV, Accessible, Rate)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Available spaces tag
                    Surface(
                        color = StatusAvailable.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${lot.availableCount} free",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StatusAvailable
                        )
                    }

                    if (lot.evSlots > 0) {
                        Surface(
                            color = TechCyan.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ElectricCar,
                                    contentDescription = "EV Charging",
                                    tint = TechCyan,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${lot.evSlots - lot.evOccupied} EV",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TechCyan
                                )
                            }
                        }
                    }

                    if (lot.accessibleSlots > 0) {
                        Surface(
                            color = Color(0xFF6366F1).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Accessible,
                                    contentDescription = "Accessible",
                                    tint = Color(0xFF6366F1),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${lot.accessibleSlots - lot.accessibleOccupied} acc",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF6366F1)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "₹${"%.0f".format(lot.ratePerHour)}/hr",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun LotDetailDialog(
    lot: ParkingLot,
    repository: CpvtsRepository,
    onDismiss: () -> Unit
) {
    var slots by remember { mutableStateOf<List<ParkingSlot>>(emptyList()) }
    var isLoadingSlots by remember { mutableStateOf(true) }
    var selectedSlot by remember { mutableStateOf<ParkingSlot?>(null) }
    var checkInNotice by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(lot.id) {
        isLoadingSlots = true
        val res = repository.getSlotsForLot(lot.id)
        isLoadingSlots = false
        res.onSuccess { slots = it }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lot.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    StatusBadge(statusText = lot.calculatedStatus.name)
                }
                Text(
                    text = "${lot.zone} • ${lot.code}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = lot.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                OccupancyProgress(occupied = lot.occupiedCount, capacity = lot.totalCapacity)

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Live Stall Map / Representative Slots",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (isLoadingSlots) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(slots) { slot ->
                            val slotColor = when {
                                slot.isOccupied -> Color(0xFFF1F5F9)
                                slot.type == SlotType.EV -> TechCyan.copy(alpha = 0.15f)
                                slot.type == SlotType.ACCESSIBLE -> Color(0xFF6366F1).copy(alpha = 0.15f)
                                else -> StatusAvailable.copy(alpha = 0.15f)
                            }
                            val borderCol = if (slot == selectedSlot) CampusNavy else Color.Transparent

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(slotColor)
                                    .border(2.dp, borderCol, RoundedCornerShape(6.dp))
                                    .clickable {
                                        if (!slot.isOccupied) {
                                            selectedSlot = slot
                                            checkInNotice = null
                                        }
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = slot.slotNumber.takeLast(3),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (slot.isOccupied) Color.Gray else CampusNavy
                                    )
                                    Text(
                                        text = if (slot.isOccupied) "FULL" else "OPEN",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (slot.isOccupied) StatusFull else StatusAvailable
                                    )
                                }
                            }
                        }
                    }
                }

                if (selectedSlot != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color(0xFFE0E7FF),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Selected Stall: ${selectedSlot?.slotNumber}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Ready for your vehicle check-in", fontSize = 11.sp, color = CampusNavy)
                            }
                            Button(
                                onClick = {
                                    checkInNotice = "Spot ${selectedSlot?.slotNumber} confirmed for active session!"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Park Here", fontSize = 11.sp)
                            }
                        }
                    }
                }

                if (checkInNotice != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = checkInNotice ?: "",
                        color = StatusAvailable,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CampusNavy)
            ) {
                Text("Done")
            }
        }
    )
}
