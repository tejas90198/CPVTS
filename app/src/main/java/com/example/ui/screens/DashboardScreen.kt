package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.api.ApiConfig
import com.example.data.model.CampusDashboardStats
import com.example.data.model.ParkingLot
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
fun DashboardScreen(
    repository: CpvtsRepository,
    onNavigateToLots: () -> Unit,
    onNavigateToTracking: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stats by remember { mutableStateOf<CampusDashboardStats?>(null) }
    var lots by remember { mutableStateOf<List<ParkingLot>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    fun loadData() {
        isLoading = true
        errorMessage = null
        scope.launch {
            val resStats = repository.getDashboardStats()
            val resLots = repository.getLots()
            isLoading = false
            resStats.onSuccess { stats = it }
                .onFailure { errorMessage = it.localizedMessage ?: "Failed to load dashboard statistics" }
            resLots.onSuccess { lots = it }
        }
    }

    LaunchedEffect(Unit) {
        loadData()
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
                    text = "Campus Mobility Overview",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Real-time occupancy, gate traffic & enforcement analytics",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButtonRow(onRefresh = { loadData() })
        }

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

                stats != null -> {
                    val s = stats!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().testTag("dashboard_overview_list"),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Hero Overall Campus Occupancy Card
                        item {
                            OverallOccupancyHero(stats = s)
                        }

                        // 4 Key Metric Cards
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MetricCard(
                                    title = "Vehicles On Campus",
                                    value = "${s.activeOnCampusVehicles}",
                                    subtitle = "Tracked at gates",
                                    icon = Icons.Default.DirectionsCar,
                                    accentColor = TechCyan,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    title = "Active Passes",
                                    value = "${s.totalPermitsActive}",
                                    subtitle = "${s.pendingPermitApplications} pending",
                                    icon = Icons.Default.AssignmentTurnedIn,
                                    accentColor = AmberGold,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MetricCard(
                                    title = "Gate Entries Today",
                                    value = "${s.todayGateEntries}",
                                    subtitle = "${s.todayGateExits} departures",
                                    icon = Icons.Default.LocalParking,
                                    accentColor = StatusAvailable,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    title = "Active Citations",
                                    value = "${s.todayViolationsCount}",
                                    subtitle = "Unpaid tickets",
                                    icon = Icons.Default.Security,
                                    accentColor = StatusFull,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Lot Summary Breakdown
                        item {
                            Text(
                                text = "Zone Occupancy Breakdown",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        items(lots, key = { it.id }) { lot ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "${lot.name} (${lot.code})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        StatusBadge(statusText = lot.calculatedStatus.name)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OccupancyProgress(occupied = lot.occupiedCount, capacity = lot.totalCapacity)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OverallOccupancyHero(stats: CampusDashboardStats) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("hero_occupancy_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CampusNavy),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOTAL CAMPUS CAPACITY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF93C5FD),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${stats.totalOccupied} / ${stats.totalCapacity} SPACES",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                stats.overallOccupancyPercentage >= 90 -> StatusFull
                                stats.overallOccupancyPercentage >= 75 -> AmberGold
                                else -> StatusAvailable
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${stats.overallOccupancyPercentage}%",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            val progress = if (stats.totalCapacity > 0) (stats.totalOccupied.toFloat() / stats.totalCapacity).coerceIn(0f, 1f) else 0f
            androidx.compose.material3.LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = when {
                    stats.overallOccupancyPercentage >= 90 -> StatusFull
                    stats.overallOccupancyPercentage >= 75 -> AmberGold
                    else -> StatusAvailable
                },
                trackColor = Color.White.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${(stats.totalCapacity - stats.totalOccupied).coerceAtLeast(0)} open parking spots available across all campus zones",
                fontSize = 12.sp,
                color = Color(0xFFE2E8F0)
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
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
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                }
                Text(
                    text = value,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun IconButtonRow(onRefresh: () -> Unit) {
    OutlinedButton(
        onClick = onRefresh,
        modifier = Modifier.height(34.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh stats", modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Refresh", fontSize = 11.sp)
    }
}
