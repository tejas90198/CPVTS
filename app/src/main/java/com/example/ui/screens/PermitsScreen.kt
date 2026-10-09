package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.ApiConfig
import com.example.data.model.ApplyPermitRequest
import com.example.data.model.ParkingPermit
import com.example.data.model.PermitStatus
import com.example.data.model.PermitType
import com.example.data.repository.CpvtsRepository
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CampusNavy
import com.example.ui.theme.CampusNavyDark
import com.example.ui.theme.CampusNavyLight
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusFull
import com.example.ui.theme.StatusLimited
import kotlinx.coroutines.launch

@Composable
fun PermitsScreen(
    repository: CpvtsRepository,
    modifier: Modifier = Modifier
) {
    var permits by remember { mutableStateOf<List<ParkingPermit>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showApplyDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val isAdminOrOfficer = ApiConfig.activeRole == "ADMIN" || ApiConfig.activeRole == "OFFICER"

    fun loadPermits() {
        isLoading = true
        errorMessage = null
        scope.launch {
            val res = repository.getPermits()
            isLoading = false
            res.onSuccess {
                permits = it
            }.onFailure {
                errorMessage = it.localizedMessage ?: "Failed to load parking permits"
            }
        }
    }

    LaunchedEffect(Unit) {
        loadPermits()
    }

    val activePermit = permits.firstOrNull { it.status == PermitStatus.ACTIVE }

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
                    text = "Campus Parking Permits",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Digital parking passes authorized by Campus Mobility",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showApplyDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("apply_permit_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Apply Pass", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

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
                    Button(onClick = { loadPermits() }, colors = ButtonDefaults.buttonColors(containerColor = CampusNavy)) {
                        Text("Retry")
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("permits_list"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Featured Digital Pass Badge
                    if (activePermit != null) {
                        item {
                            DigitalPassCard(permit = activePermit)
                        }
                    }

                    item {
                        Text(
                            text = "All Permits & Applications",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    items(permits, key = { it.id }) { permit ->
                        PermitRowCard(
                            permit = permit,
                            isAdminOrOfficer = isAdminOrOfficer,
                            onStatusChange = { newStatus ->
                                scope.launch {
                                    repository.updatePermitStatus(permit.id, newStatus)
                                    loadPermits()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showApplyDialog) {
        ApplyPermitDialog(
            onDismiss = { showApplyDialog = false },
            onApply = { req ->
                scope.launch {
                    repository.applyPermit(req)
                    loadPermits()
                }
                showApplyDialog = false
            }
        )
    }
}

@Composable
fun DigitalPassCard(
    permit: ParkingPermit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("active_pass_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CampusNavyDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CampusNavy, CampusNavyDark)
                    )
                )
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = AmberGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CPVTS DIGITAL PARKING PASS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AmberGold,
                            letterSpacing = 1.sp
                        )
                    }
                    StatusBadge(statusText = "ACTIVE", statusType = "ACTIVE")
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = permit.vehiclePlate,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = permit.permitType.name.replace("_", " "),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFCBD5E1)
                        )
                    }

                    // QR representation
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "Pass QR",
                            tint = Color.Black,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.15f))
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("AUTHORIZED ZONES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                        Text(permit.allowedZone, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("VALID UNTIL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                        Text(permit.validTo, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun PermitRowCard(
    permit: ParkingPermit,
    isAdminOrOfficer: Boolean,
    onStatusChange: (PermitStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("permit_row_${permit.permitNumber}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                Column {
                    Text(
                        text = permit.permitNumber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${permit.vehiclePlate} • ${permit.permitType.name.replace("_", " ")}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusBadge(statusText = permit.status.name, statusType = permit.status.name)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Allowed: ${permit.allowedZone}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Period: ${permit.validFrom} to ${permit.validTo}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Admin / Officer review actions if pending
            if (isAdminOrOfficer && permit.status == PermitStatus.PENDING) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { onStatusChange(PermitStatus.REJECTED) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusFull),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Reject", fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onStatusChange(PermitStatus.ACTIVE) },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusAvailable),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Approve Pass", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ApplyPermitDialog(
    onDismiss: () -> Unit,
    onApply: (ApplyPermitRequest) -> Unit
) {
    var plate by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(PermitType.SEMESTER) }
    var selectedZone by remember { mutableStateOf("Zone A (North) & Zone C (West)") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Apply for Parking Permit", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = plate,
                    onValueChange = { plate = it.uppercase() },
                    label = { Text("Vehicle License Plate *") },
                    placeholder = { Text("e.g. 7XYZ890") },
                    modifier = Modifier.fillMaxWidth().testTag("apply_permit_plate_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Pass Type", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        PermitType.SEMESTER to "Semester",
                        PermitType.DAILY to "Daily",
                        PermitType.EV_CHARGING to "EV Pass"
                    ).forEach { (t, lbl) ->
                        val isSel = selectedType == t
                        OutlinedButton(
                            onClick = { selectedType = t },
                            colors = if (isSel) ButtonDefaults.outlinedButtonColors(containerColor = CampusNavy, contentColor = Color.White)
                            else ButtonDefaults.outlinedButtonColors(),
                            modifier = Modifier.weight(1f).height(34.dp)
                        ) {
                            Text(lbl, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Designated Zone", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        "Zone A & C" to "Zone A & C",
                        "Zone B Core" to "Zone B Core",
                        "All Campus" to "All Campus"
                    ).forEach { (zVal, lbl) ->
                        val isSel = selectedZone == zVal
                        OutlinedButton(
                            onClick = { selectedZone = zVal },
                            colors = if (isSel) ButtonDefaults.outlinedButtonColors(containerColor = CampusNavy, contentColor = Color.White)
                            else ButtonDefaults.outlinedButtonColors(),
                            modifier = Modifier.weight(1f).height(34.dp)
                        ) {
                            Text(lbl, fontSize = 10.sp)
                        }
                    }
                }

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorText ?: "", color = StatusFull, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (plate.isBlank()) {
                        errorText = "Please enter vehicle license plate"
                        return@Button
                    }
                    onApply(
                        ApplyPermitRequest(
                            vehiclePlate = plate.trim().uppercase(),
                            permitType = selectedType,
                            allowedZone = selectedZone
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
                modifier = Modifier.testTag("submit_permit_application_button")
            ) {
                Text("Submit Application")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
