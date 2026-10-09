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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Payment
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
import com.example.data.model.CreateViolationRequest
import com.example.data.model.Vehicle
import com.example.data.model.ViolationCitation
import com.example.data.model.ViolationStatus
import com.example.data.repository.CpvtsRepository
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CampusNavy
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusFull
import com.example.ui.theme.StatusLimited
import kotlinx.coroutines.launch

@Composable
fun EnforcementScreen(
    repository: CpvtsRepository,
    modifier: Modifier = Modifier
) {
    var violations by remember { mutableStateOf<List<ViolationCitation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Plate Lookup State
    var searchPlate by remember { mutableStateOf("") }
    var lookedUpVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var lookupExecuted by remember { mutableStateOf(false) }
    var isLookingUp by remember { mutableStateOf(false) }

    var showIssueDialog by remember { mutableStateOf(false) }
    var citationToAppeal by remember { mutableStateOf<ViolationCitation?>(null) }

    val scope = rememberCoroutineScope()

    fun loadViolations() {
        isLoading = true
        errorMessage = null
        scope.launch {
            val res = repository.getViolations()
            isLoading = false
            res.onSuccess { violations = it }
                .onFailure { errorMessage = it.localizedMessage ?: "Failed to load violations" }
        }
    }

    LaunchedEffect(Unit) {
        loadViolations()
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
                    text = "Enforcement & Citations",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Plate verification, patrol compliance & violation records",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showIssueDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = StatusFull),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("issue_citation_button")
            ) {
                Icon(imageVector = Icons.Default.AddAlert, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Issue Citation", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Quick License Plate Scanner / Lookup Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Text(
                    text = "Quick Plate Verification & Patrol Check",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = CampusNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchPlate,
                        onValueChange = {
                            searchPlate = it.uppercase()
                            lookupExecuted = false
                        },
                        modifier = Modifier.weight(1f).testTag("lookup_plate_input"),
                        placeholder = { Text("e.g. 7XYZ890 or 4KLM999") },
                        singleLine = true,
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (searchPlate.isNotBlank()) {
                                isLookingUp = true
                                scope.launch {
                                    val res = repository.lookupVehicle(searchPlate)
                                    isLookingUp = false
                                    lookupExecuted = true
                                    lookedUpVehicle = res.getOrNull()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
                        modifier = Modifier.height(52.dp).testTag("lookup_plate_button")
                    ) {
                        if (isLookingUp) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Verify", fontSize = 12.sp)
                        }
                    }
                }

                // Lookup Results Card
                if (lookupExecuted) {
                    Spacer(modifier = Modifier.height(10.dp))
                    if (lookedUpVehicle != null) {
                        val veh = lookedUpVehicle!!
                        Surface(
                            color = StatusAvailable.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${veh.licensePlate} • ${veh.make} ${veh.model}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = CampusNavy
                                    )
                                    StatusBadge(statusText = "REGISTERED", statusType = "ACTIVE")
                                }
                                Text("Owner: ${veh.ownerName ?: "Campus Member"}", fontSize = 12.sp)
                                Text(
                                    text = if (veh.isCurrentlyOnCampus) "Status: Currently Checked-in on Campus" else "Status: Not logged at gates",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (veh.isCurrentlyOnCampus) StatusAvailable else Color.Gray
                                )
                            }
                        }
                    } else {
                        Surface(
                            color = StatusFull.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = StatusFull, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Unregistered Vehicle", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = StatusFull)
                                    Text("No permit or student/staff record on file for $searchPlate.", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Violations List Header
        PaddingValues(horizontal = 16.dp).let {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Violations & Citations (${violations.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // Violations List
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
                        Text(errorMessage ?: "", color = StatusFull, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(onClick = { loadViolations() }, colors = ButtonDefaults.buttonColors(containerColor = CampusNavy)) {
                            Text("Retry")
                        }
                    }
                }

                violations.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No citations recorded", color = Color.Gray)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().testTag("violations_list"),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(violations, key = { it.id }) { citation ->
                            ViolationCard(
                                citation = citation,
                                onPay = {
                                    scope.launch {
                                        repository.payViolation(citation.id)
                                        loadViolations()
                                    }
                                },
                                onAppeal = { citationToAppeal = citation }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showIssueDialog) {
        IssueCitationDialog(
            initialPlate = searchPlate,
            onDismiss = { showIssueDialog = false },
            onConfirm = { req ->
                scope.launch {
                    repository.issueViolation(req)
                    loadViolations()
                }
                showIssueDialog = false
            }
        )
    }

    citationToAppeal?.let { citation ->
        AppealCitationDialog(
            citation = citation,
            onDismiss = { citationToAppeal = null },
            onConfirm = { reason ->
                scope.launch {
                    repository.appealViolation(citation.id, reason)
                    loadViolations()
                }
                citationToAppeal = null
            }
        )
    }
}

@Composable
fun ViolationCard(
    citation: ViolationCitation,
    onPay: () -> Unit,
    onAppeal: () -> Unit,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF1F5F9))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = citation.licensePlate,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = CampusNavy
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = citation.citationNumber,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusBadge(statusText = citation.status.name, statusType = citation.status.name)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = citation.violationType,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Location: ${citation.lotName} • Issued: ${citation.issuedAt}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!citation.officerNotes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Officer Note: \"${citation.officerNotes}\"",
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color.DarkGray
                )
            }

            if (!citation.appealReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Appeal Reason: \"${citation.appealReason}\"",
                    fontSize = 11.sp,
                    color = AmberGold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fine: ₹${"%.0f".format(citation.fineAmount)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = if (citation.status == ViolationStatus.PAID) StatusAvailable else StatusFull
                )

                if (citation.status == ViolationStatus.UNPAID) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = onAppeal,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Appeal", fontSize = 11.sp)
                        }
                        Button(
                            onClick = onPay,
                            colors = ButtonDefaults.buttonColors(containerColor = StatusAvailable),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Pay Fine", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IssueCitationDialog(
    initialPlate: String,
    onDismiss: () -> Unit,
    onConfirm: (CreateViolationRequest) -> Unit
) {
    var plate by remember { mutableStateOf(initialPlate) }
    var lotName by remember { mutableStateOf("North Campus Deck") }
    var selectedReason by remember { mutableStateOf("Parked in Fire Lane / Restricted Zone") }
    var fineAmountText by remember { mutableStateOf("1000") }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Issue Parking Citation", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = plate,
                    onValueChange = { plate = it.uppercase() },
                    label = { Text("License Plate *") },
                    placeholder = { Text("e.g. 7XYZ890") },
                    modifier = Modifier.fillMaxWidth().testTag("citation_plate_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = lotName,
                    onValueChange = { lotName = it },
                    label = { Text("Lot / Location *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Violation Reason", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                listOf(
                    "Parked in Fire Lane / Restricted Zone" to "1000",
                    "No Active Permit Displayed" to "500",
                    "EV Spot Abuse (Not Charging)" to "750",
                    "Expired Daily/Visitor Meter" to "300"
                ).forEach { (reason, defaultFine) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedReason = reason
                                fineAmountText = defaultFine
                            }
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedReason == reason) "● $reason" else "○ $reason",
                            fontSize = 11.sp,
                            fontWeight = if (selectedReason == reason) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedReason == reason) CampusNavy else Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fineAmountText,
                    onValueChange = { fineAmountText = it },
                    label = { Text("Fine Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Officer Notes") },
                    placeholder = { Text("e.g. Blocked lane near west ramp") },
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
                        error = "License plate required"
                        return@Button
                    }
                    val amount = fineAmountText.toDoubleOrNull() ?: 50.0
                    onConfirm(
                        CreateViolationRequest(
                            licensePlate = plate.trim().uppercase(),
                            lotName = lotName.trim(),
                            violationType = selectedReason,
                            fineAmount = amount,
                            officerNotes = notes.ifBlank { null }
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = StatusFull),
                modifier = Modifier.testTag("confirm_issue_citation_button")
            ) {
                Text("Issue Citation")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AppealCitationDialog(
    citation: ViolationCitation,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Submit Violation Appeal", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Citation: ${citation.citationNumber} (₹${"%.0f".format(citation.fineAmount)})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "Provide your academic or emergency reason for parking in this stall.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Appeal Statement") },
                    placeholder = { Text("e.g. Medical emergency or valid pass displayed in window") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
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
                    if (reason.isBlank()) {
                        error = "Please provide an appeal reason"
                        return@Button
                    }
                    onConfirm(reason.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = CampusNavy)
            ) {
                Text("Submit Appeal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
