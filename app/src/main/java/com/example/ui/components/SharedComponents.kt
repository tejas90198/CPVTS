package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.data.api.ApiClient
import com.example.data.api.ApiConfig
import com.example.data.api.DataSourceMode
import com.example.data.model.LotStatus
import com.example.data.model.PermitStatus
import com.example.data.model.ViolationStatus
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CampusNavy
import com.example.ui.theme.CampusNavyDark
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusAvailableBg
import com.example.ui.theme.StatusFull
import com.example.ui.theme.StatusFullBg
import com.example.ui.theme.StatusLimited
import com.example.ui.theme.StatusLimitedBg
import kotlinx.coroutines.launch

@Composable
fun ModeBanner(
    onOpenConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentMode by ApiConfig.modeState.collectAsState()
    val baseUrl by ApiConfig.baseUrlState.collectAsState()

    val isDemo = currentMode == DataSourceMode.DEMO
    val bgColor = if (isDemo) Color(0xFFFEF3C7) else Color(0xFFE0E7FF)
    val contentColor = if (isDemo) Color(0xFF92400E) else Color(0xFF1E3A8A)
    val borderColor = if (isDemo) Color(0xFFFDE68A) else Color(0xFFC7D2FE)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenConfig() }
            .testTag("mode_banner_click"),
        color = bgColor,
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = borderColor)
                .padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isDemo) Icons.Default.CloudOff else Icons.Default.Cloud,
                    contentDescription = "Data Mode Indicator",
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isDemo) "DEMO / PREVIEW DATA MODE" else "LIVE BACKEND API MODE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = if (isDemo) "Sample campus dataset • Tap to switch or connect" else "URL: ${baseUrl.take(38)}... • Tap to change",
                        fontSize = 10.sp,
                        color = contentColor.copy(alpha = 0.85f),
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configure connection",
                    tint = contentColor,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Config",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun ConfigDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val currentMode by ApiConfig.modeState.collectAsState()
    val currentUrl by ApiConfig.baseUrlState.collectAsState()

    var selectedMode by remember(currentMode) { mutableStateOf(currentMode) }
    var inputUrl by remember(currentUrl) { mutableStateOf(currentUrl) }
    var pingResult by remember { mutableStateOf<String?>(null) }
    var isPinging by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(ApiConfig.activeRole) }

    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = CampusNavy,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Backend & Role Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Data Source Mode",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Demo Mode Radio
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedMode = DataSourceMode.DEMO }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedMode == DataSourceMode.DEMO,
                        onClick = { selectedMode = DataSourceMode.DEMO }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Demo Mode (Preview Sample Data)",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Instant interactive evaluation without external server",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Live API Radio
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedMode = DataSourceMode.LIVE }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedMode == DataSourceMode.LIVE,
                        onClick = { selectedMode = DataSourceMode.LIVE }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Live Backend API",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Connects to CPVTS REST backend server",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Backend Base URL",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = {
                        inputUrl = it
                        pingResult = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("config_base_url_input"),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            inputUrl = ApiConfig.DEFAULT_BASE_URL
                            pingResult = null
                        },
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Reset Default", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            isPinging = true
                            pingResult = null
                            scope.launch {
                                val (ok, msg) = ApiClient.pingServer(inputUrl)
                                isPinging = false
                                pingResult = msg
                            }
                        },
                        enabled = !isPinging,
                        colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
                        modifier = Modifier.height(34.dp)
                    ) {
                        if (isPinging) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Test Ping", fontSize = 11.sp)
                        }
                    }
                }

                if (pingResult != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = pingResult ?: "",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (pingResult?.contains("reached", ignoreCase = true) == true) StatusAvailable else StatusFull
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Active Persona / Role",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("STUDENT" to "Student", "OFFICER" to "Officer", "ADMIN" to "Admin").forEach { (roleKey, label) ->
                        val isSelected = selectedRole == roleKey
                        OutlinedButton(
                            onClick = { selectedRole = roleKey },
                            colors = if (isSelected) {
                                ButtonDefaults.outlinedButtonColors(
                                    containerColor = CampusNavy,
                                    contentColor = Color.White
                                )
                            } else {
                                ButtonDefaults.outlinedButtonColors()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                        ) {
                            Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    ApiConfig.setMode(selectedMode)
                    ApiConfig.setBaseUrl(inputUrl)
                    if (selectedRole != ApiConfig.activeRole) {
                        val name = when (selectedRole) {
                            "OFFICER" -> "Officer J. Miller"
                            "ADMIN" -> "Admin Sarah Parker"
                            else -> "Alex Chen"
                        }
                        val email = when (selectedRole) {
                            "OFFICER" -> "security.miller@campus.edu"
                            "ADMIN" -> "parking.admin@campus.edu"
                            else -> "alex.chen@campus.edu"
                        }
                        ApiConfig.setUserProfile(name, email, selectedRole)
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
                modifier = Modifier.testTag("config_save_button")
            ) {
                Text("Save & Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun OccupancyProgress(
    occupied: Int,
    capacity: Int,
    modifier: Modifier = Modifier
) {
    val percentage = if (capacity > 0) ((occupied.toFloat() / capacity) * 100).toInt() else 0
    val progress = if (capacity > 0) (occupied.toFloat() / capacity).coerceIn(0f, 1f) else 0f

    val color = when {
        percentage >= 95 -> StatusFull
        percentage >= 75 -> StatusLimited
        else -> StatusAvailable
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$occupied / $capacity occupied",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$percentage%",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.18f)
        )
    }
}

@Composable
fun StatusBadge(
    statusText: String,
    modifier: Modifier = Modifier,
    statusType: String = statusText
) {
    val (bg, fg) = when (statusType.uppercase()) {
        "AVAILABLE", "ACTIVE", "PAID", "APPROVED" -> Pair(StatusAvailableBg, StatusAvailable)
        "LIMITED", "PENDING", "APPEALED" -> Pair(StatusLimitedBg, StatusLimited)
        "FULL", "EXPIRED", "UNPAID", "REJECTED" -> Pair(StatusFullBg, StatusFull)
        else -> Pair(Color(0xFFE2E8F0), Color(0xFF475569))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = statusText.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = fg,
            letterSpacing = 0.5.sp
        )
    }
}
