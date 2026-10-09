package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.ApiConfig
import com.example.data.api.DataSourceMode
import com.example.data.repository.CpvtsRepository
import com.example.ui.components.ConfigDialog
import com.example.ui.components.ModeBanner
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EnforcementScreen
import com.example.ui.screens.GateTrackingScreen
import com.example.ui.screens.LotsScreen
import com.example.ui.screens.PermitsScreen
import com.example.ui.screens.VehiclesScreen
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CampusNavy
import com.example.ui.theme.CampusNavyDark
import com.example.ui.theme.CardBorderWarm
import com.example.ui.theme.NavSurfaceWarm
import com.example.ui.theme.TextBronzeGray
import com.example.ui.theme.TextDarkCharcoal
import com.example.ui.theme.Tier1AmberGold
import com.example.ui.theme.Tier1CreamTint
import com.example.ui.theme.Tier2IceBlueTint
import com.example.ui.theme.Tier2SlateBlue
import com.example.ui.theme.Tier2SlateBlueDark
import com.example.ui.theme.MyApplicationTheme

enum class CpvtsTab(val title: String, val icon: ImageVector, val tag: String) {
    OVERVIEW("Overview", Icons.Default.Dashboard, "tab_overview"),
    LOTS("Lots", Icons.Default.LocalParking, "tab_lots"),
    VEHICLES("Vehicles", Icons.Default.DirectionsCar, "tab_vehicles"),
    PERMITS("Permits", Icons.Default.AssignmentTurnedIn, "tab_permits"),
    GATES("Gates", Icons.Default.Security, "tab_gates"),
    ENFORCEMENT("Enforce", Icons.Default.Gavel, "tab_enforcement")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApiConfig.init(this)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CpvtsMainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CpvtsMainApp() {
    val repository = remember { CpvtsRepository() }
    var currentTab by remember { mutableStateOf(CpvtsTab.OVERVIEW) }
    var showConfigDialog by remember { mutableStateOf(false) }

    val currentMode by ApiConfig.modeState.collectAsState()

    // Handle system back navigation to return to Home/Overview tab
    BackHandler(enabled = currentTab != CpvtsTab.OVERVIEW) {
        currentTab = CpvtsTab.OVERVIEW
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = NavSurfaceWarm,
                        titleContentColor = TextDarkCharcoal,
                        actionIconContentColor = TextDarkCharcoal
                    ),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Tier1AmberGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "P",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextDarkCharcoal,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "CPVTS",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = TextDarkCharcoal,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Campus Mobility & Parking",
                                    fontSize = 11.sp,
                                    color = TextBronzeGray
                                )
                            }
                        }
                    },
                    actions = {
                        // Role Badge (Tier 2 Ice Blue)
                        Surface(
                            color = Tier2IceBlueTint,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = ApiConfig.activeRole,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Tier2SlateBlue
                            )
                        }

                        IconButton(
                            onClick = { showConfigDialog = true },
                            modifier = Modifier.testTag("top_bar_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Backend Settings",
                                tint = TextDarkCharcoal
                            )
                        }
                    }
                )

                // Mode Banner (Live API vs Demo Mode)
                ModeBanner(onOpenConfig = { showConfigDialog = true })
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = NavSurfaceWarm,
                tonalElevation = 2.dp
            ) {
                CpvtsTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Tier2SlateBlueDark,
                            selectedTextColor = Tier2SlateBlueDark,
                            unselectedIconColor = TextBronzeGray,
                            unselectedTextColor = TextBronzeGray,
                            indicatorColor = Tier2IceBlueTint
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                CpvtsTab.OVERVIEW -> DashboardScreen(
                    repository = repository,
                    onNavigateToLots = { currentTab = CpvtsTab.LOTS },
                    onNavigateToTracking = { currentTab = CpvtsTab.GATES }
                )
                CpvtsTab.LOTS -> LotsScreen(repository = repository)
                CpvtsTab.VEHICLES -> VehiclesScreen(repository = repository)
                CpvtsTab.PERMITS -> PermitsScreen(repository = repository)
                CpvtsTab.GATES -> GateTrackingScreen(repository = repository)
                CpvtsTab.ENFORCEMENT -> EnforcementScreen(repository = repository)
            }
        }
    }

    ConfigDialog(
        isOpen = showConfigDialog,
        onDismiss = { showConfigDialog = false }
    )
}
