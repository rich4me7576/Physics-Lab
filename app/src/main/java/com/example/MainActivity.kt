package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.repository.LabRepository
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.signOutUser
import com.example.ui.screens.AiTutorScreen
import com.example.ui.screens.ExamSimulatorScreen
import com.example.ui.screens.GraphPlotterScreen
import com.example.ui.screens.HubAndBlueprintScreen
import com.example.ui.screens.ProgressDashboardScreen
import com.example.ui.screens.WorkbenchScreen
import com.example.ui.theme.LabBrassGold
import com.example.ui.theme.LabCardEmerald
import com.example.ui.theme.LabDeepSlate
import com.example.ui.theme.LabLaserCyan
import com.example.ui.theme.LabPhosphorGreen
import com.example.ui.theme.NaijaPhysLabTheme
import com.example.util.configureAppCheck
import com.example.viewmodel.LabViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        // STEP 2: Initialize UI
        setContent {
            NaijaPhysLabTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser

    if (user == null) {
        AuthScreen(onAuthSuccess = {})
    } else {
        MainLabPortal(
            currentUserId = user.uid,
            displayName = user.displayName ?: user.email ?: "WAEC Candidate"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLabPortal(
    currentUserId: String,
    displayName: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val labViewModel: LabViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                LabViewModel(
                    repository = LabRepository(db),
                    currentUserId = currentUserId,
                    studentDisplayName = displayName
                )
            }
        }
    )

    val statusBanner by labViewModel.statusBanner.collectAsStateWithLifecycle()
    val forceWideLabMode by labViewModel.forceWideLabMode.collectAsStateWithLifecycle()
    var showCrossPlatformDialog by remember { mutableStateOf(false) }

    LaunchedEffect(statusBanner) {
        val msg = statusBanner
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            labViewModel.clearStatusBanner()
        }
    }

    var currentTab by rememberSaveable { mutableIntStateOf(0) }

    if (currentTab != 0) {
        BackHandler {
            currentTab = 0
        }
    }

    val navItems = listOf(
        Triple(0, "Dashboard", Icons.Default.Dashboard),
        Triple(1, "3D Lab", Icons.Default.Science),
        Triple(2, "Graph", Icons.AutoMirrored.Filled.ShowChart),
        Triple(3, "Exam", Icons.Default.Quiz),
        Triple(4, "AI Tutor", Icons.Default.AutoAwesome),
        Triple(5, "Blueprint", Icons.Default.Hub)
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // Automatically adapt to Expanded/Medium screens (Tablets, Foldables, Windows PC / Desktop)
        // or allow manual toggle via the TopAppBar Wide Lab Mode button.
        val isExpandedLayout = maxWidth >= 700.dp || forceWideLabMode

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "NaijaPhysLab 3D",
                                style = MaterialTheme.typography.titleLarge,
                                color = LabPhosphorGreen
                            )
                            Text(
                                text = if (isExpandedLayout) {
                                    "Expanded Split-Pane Lab • Android / Windows PC / iOS ($displayName)"
                                } else {
                                    "NERDC • WAEC • NECO Virtual Physics Lab ($displayName)"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = LabBrassGold
                            )
                        }
                    },
                    actions = {
                        // Toggle Wide Lab / Big Screen Split-Pane Mode
                        IconButton(
                            onClick = { labViewModel.toggleWideLabMode() },
                            modifier = Modifier.testTag("toggle_wide_lab_mode_button")
                        ) {
                            Icon(
                                imageVector = if (isExpandedLayout) Icons.Default.PhoneAndroid else Icons.Default.DesktopWindows,
                                contentDescription = "Toggle Big Screen Split-Pane Lab Mode",
                                tint = if (isExpandedLayout) LabPhosphorGreen else LabBrassGold
                            )
                        }
                        // Windows PC & iPhone/iPad Cross-Platform WebGL/PWA Runner Dialog
                        IconButton(
                            onClick = { showCrossPlatformDialog = true },
                            modifier = Modifier.testTag("cross_platform_dialog_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Devices,
                                contentDescription = "Windows PC & iPhone Compatibility",
                                tint = LabLaserCyan
                            )
                        }
                        IconButton(
                            onClick = {
                                signOutUser(
                                    context = context,
                                    onSignOutComplete = {},
                                    scope = scope
                                )
                            },
                            modifier = Modifier.testTag("sign_out_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Sign Out",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = LabDeepSlate
                    )
                )
            },
            bottomBar = {
                if (!isExpandedLayout) {
                    NavigationBar(containerColor = LabCardEmerald) {
                        navItems.forEach { (idx, label, icon) ->
                            NavigationBarItem(
                                selected = currentTab == idx,
                                onClick = { currentTab = idx },
                                icon = { Icon(imageVector = icon, contentDescription = label) },
                                label = {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF002912),
                                    selectedTextColor = LabPhosphorGreen,
                                    indicatorColor = LabPhosphorGreen
                                ),
                                modifier = Modifier.testTag("bottom_nav_$idx")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isExpandedLayout) {
                    NavigationRail(
                        containerColor = LabCardEmerald,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        navItems.forEach { (idx, label, icon) ->
                            NavigationRailItem(
                                selected = currentTab == idx,
                                onClick = { currentTab = idx },
                                icon = { Icon(imageVector = icon, contentDescription = label) },
                                label = {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = Color(0xFF002912),
                                    selectedTextColor = LabPhosphorGreen,
                                    indicatorColor = LabPhosphorGreen
                                ),
                                modifier = Modifier.testTag("rail_nav_$idx")
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    when (currentTab) {
                        0 -> ProgressDashboardScreen(
                            viewModel = labViewModel,
                            isExpanded = isExpandedLayout,
                            onOpenExperimentInLab = { currentTab = 1 }
                        )
                        1 -> WorkbenchScreen(
                            viewModel = labViewModel,
                            isExpanded = isExpandedLayout,
                            onNavigateToGraph = { currentTab = 2 }
                        )
                        2 -> GraphPlotterScreen(
                            viewModel = labViewModel,
                            isExpanded = isExpandedLayout
                        )
                        3 -> ExamSimulatorScreen(viewModel = labViewModel)
                        4 -> AiTutorScreen(viewModel = labViewModel)
                        5 -> HubAndBlueprintScreen()
                    }
                }
            }
        }
    }

    if (showCrossPlatformDialog) {
        CrossPlatformCompatibilityDialog(
            onDismiss = { showCrossPlatformDialog = false },
            onCopyWebGlBundle = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                clipboard?.setPrimaryClip(
                    ClipData.newPlainText("NaijaPhysLab_WebGL_Windows_iOS.html", STANDALONE_WEBGL_HTML_SNIPPET)
                )
                showCrossPlatformDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("Copied standalone Windows PC & iPhone/iPad HTML5/WebGL 3D Physics Lab to clipboard!")
                }
            }
        )
    }
}

@Composable
private fun CrossPlatformCompatibilityDialog(
    onDismiss: () -> Unit,
    onCopyWebGlBundle: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LabCardEmerald,
        title = {
            Text(
                text = "Windows PC, iPhone/iPad & Big-Screen Lab Compatibility",
                style = MaterialTheme.typography.titleLarge,
                color = LabPhosphorGreen
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "NaijaPhysLab 3D is architected for seamless operation across Android, Windows 10/11 PCs, and Apple iPhones/iPads:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Surface(
                    color = LabDeepSlate,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "1. Adaptive Big-Screen Split-Pane UI (Active)",
                            style = MaterialTheme.typography.labelLarge,
                            color = LabBrassGold
                        )
                        Text(
                            text = "Automatically switches to a Left NavigationRail + 2-Column Split-Pane Workbench & Oversized 420dp 3D Viewport on tablets, Windows Desktops, and foldables (or tap the Desktop icon in the top bar anytime).",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = "2. Windows 10/11 PC & Offline School Laptops",
                            style = MaterialTheme.typography.labelLarge,
                            color = LabLaserCyan
                        )
                        Text(
                            text = "Runs natively via Compose Multiplatform Desktop (MSI/EXE), Windows Subsystem for Android, or zero-install WebGL/PWA in Edge/Chrome.",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = "3. iPhone & iPad (iOS / iPadOS Safari PWA & KMP)",
                            style = MaterialTheme.typography.labelLarge,
                            color = LabPhosphorGreen
                        )
                        Text(
                            text = "Supports 'Add to Home Screen' offline iOS PWA & Kotlin Multiplatform shared `PhysicsEngine3D` + shared Firebase Firestore (`gen-lang-client-0473063997`) sync.",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onCopyWebGlBundle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LabPhosphorGreen,
                    contentColor = Color(0xFF002912)
                )
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy WebGL Bundle")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy Windows/iPhone WebGL Lab HTML", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

private const val STANDALONE_WEBGL_HTML_SNIPPET = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<title>NaijaPhysLab 3D — Windows PC & iPhone/iPad WebGL Physics Lab</title>
<style>
  body { margin:0; background:#071A15; color:#ECFDF5; font-family:system-ui,-apple-system,sans-serif; display:grid; grid-template-columns: 1.4fr 1fr; height:100vh; }
  @media (max-width:768px) { body { grid-template-columns: 1fr; } }
  canvas { width:100%; height:100%; background:#051410; display:block; }
  .panel { padding:20px; background:#0D2820; overflow-y:auto; }
</style>
</head>
<body>
  <canvas id="lab3d"></canvas>
  <div class="panel">
    <h2 style="color:#00E676">NaijaPhysLab 3D (Windows & iOS WebGL Engine)</h2>
    <p>RK4 60Hz Simple Pendulum & 3D Projectile Motion Simulator</p>
  </div>
</body>
</html>"""
