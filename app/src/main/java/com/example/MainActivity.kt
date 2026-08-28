package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.ResolveImage
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.ActionsScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MediaScreen
import com.example.ui.screens.NewsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.TrainingsScreen
import com.example.ui.theme.AilEmerald
import com.example.ui.theme.AilEmeraldDark
import com.example.ui.theme.AilEmeraldLight
import com.example.ui.theme.AilForestDark
import com.example.ui.theme.AilForestGreen
import com.example.ui.theme.AilGold
import com.example.ui.theme.AilMint
import com.example.ui.theme.AilMintDarkGreen
import com.example.ui.theme.AilMintLight
import com.example.ui.theme.AilMintPillBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AilViewModel
import com.example.ui.viewmodel.AppScreen
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: AilViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AilAppMain(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AilAppMain(viewModel: AilViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()
    val isUserAdminAuthorized by viewModel.isUserAdminAuthorized.collectAsStateWithLifecycle()
    val isAuthDialogOpen by viewModel.isAuthDialogOpen.collectAsStateWithLifecycle()
    val userProfile by viewModel.currentUserProfile.collectAsStateWithLifecycle()
    val syncStatus by viewModel.cloudSyncStatus.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showAdminPinDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Auto-protect ADMIN screen: if an unauthorized user attempts to view it, redirect to HOME
    LaunchedEffect(currentScreen, isUserAdminAuthorized) {
        if (currentScreen == AppScreen.ADMIN && !isUserAdminAuthorized) {
            viewModel.navigateTo(AppScreen.HOME)
            viewModel.showToast("Accès réservé aux administrateurs autorisés (atchouyaosylvain59@gmail.com, ail4c03@gmail.com).")
        }
    }

    if (showAdminPinDialog) {
        AdminLoginDialog(
            onDismiss = { showAdminPinDialog = false },
            onLogin = { pin ->
                if (viewModel.loginAdmin(pin)) {
                    showAdminPinDialog = false
                    viewModel.navigateTo(AppScreen.ADMIN)
                }
            }
        )
    }

    // If no user account is logged in / registered, display AuthScreen first at startup
    if (userProfile == null) {
        AuthScreen(
            viewModel = viewModel,
            onBack = null,
            initialRegisterMode = true
        )
        return
    }

    // Modal Auth Screen if opened via quick login or profile switch
    if (isAuthDialogOpen) {
        AuthScreen(
            viewModel = viewModel,
            onBack = { viewModel.closeAuthDialog() },
            initialRegisterMode = false
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ResolveImage(
                            imageName = "img_ail4c_logo",
                            contentDescription = "Logo AIL4C",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AIL4C",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // Cloud Sync status & instant refresh
                    Surface(
                        onClick = { viewModel.triggerManualCloudSync() },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("topbar_cloud_sync_btn")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Synchroniser en direct",
                                tint = if (syncStatus.isSyncing) AilGold else if (syncStatus.isOnline) AilEmerald else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // AI Assistant quick shortcut
                    Surface(
                        onClick = { viewModel.navigateTo(AppScreen.AI_ASSISTANT) },
                        shape = CircleShape,
                        color = if (currentScreen == AppScreen.AI_ASSISTANT) AilEmerald else AilMintPillBg,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("topbar_ai_assistant_btn")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "ÉcoBot IA",
                                tint = if (currentScreen == AppScreen.AI_ASSISTANT) Color.White else AilEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // À Propos quick shortcut
                    Surface(
                        onClick = { viewModel.navigateTo(AppScreen.ABOUT) },
                        shape = CircleShape,
                        color = if (currentScreen == AppScreen.ABOUT) AilEmerald else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("topbar_about_btn")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "À Propos",
                                tint = if (currentScreen == AppScreen.ABOUT) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // User Profile / Login quick shortcut
                    Surface(
                        onClick = {
                            if (userProfile != null) {
                                viewModel.navigateTo(AppScreen.PROFILE)
                            } else {
                                viewModel.openAuthDialog()
                            }
                        },
                        shape = CircleShape,
                        color = if (currentScreen == AppScreen.PROFILE) AilEmerald else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("topbar_user_profile_btn")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profil",
                                tint = if (currentScreen == AppScreen.PROFILE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Admin Lock / Management (Shown ONLY for authorized admin emails)
                    if (isUserAdminAuthorized) {
                        Surface(
                            onClick = {
                                viewModel.navigateTo(AppScreen.ADMIN)
                            },
                            shape = CircleShape,
                            color = if (currentScreen == AppScreen.ADMIN) AilEmerald else AilMintPillBg,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag("topbar_admin_lock_btn")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Panneau Admin AIL4C",
                                    tint = if (currentScreen == AppScreen.ADMIN) Color.White else AilEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.HOME,
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Accueil") },
                        label = { Text("Accueil", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.HOME) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AilEmerald,
                            selectedTextColor = AilEmerald,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = AilMintPillBg
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.ACTIONS,
                        onClick = { viewModel.navigateTo(AppScreen.ACTIONS) },
                        icon = { Icon(Icons.Default.Eco, contentDescription = "Action") },
                        label = { Text("Action", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.ACTIONS) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AilEmerald,
                            selectedTextColor = AilEmerald,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = AilMintPillBg
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.NEWS,
                        onClick = { viewModel.navigateTo(AppScreen.NEWS) },
                        icon = { Icon(Icons.Default.Newspaper, contentDescription = "Actualités") },
                        label = { Text("Actualités", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.NEWS) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AilEmerald,
                            selectedTextColor = AilEmerald,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = AilMintPillBg
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.PROFILE,
                        onClick = {
                            if (userProfile != null) {
                                viewModel.navigateTo(AppScreen.PROFILE)
                            } else {
                                viewModel.openAuthDialog()
                            }
                        },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                        label = { Text("Profil", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.PROFILE) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AilEmerald,
                            selectedTextColor = AilEmerald,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = AilMintPillBg
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Offline / Sync Notification Bar if disconnected
            AnimatedVisibility(visible = !syncStatus.isOnline) {
                Surface(
                    color = Color(0xFFC0392B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mode hors ligne • Connexion Internet requise pour le fonctionnement 24h/24 et la synchronisation en direct.",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (currentScreen) {
                AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                AppScreen.ACTIONS -> ActionsScreen(viewModel = viewModel)
                AppScreen.PROJECTS -> ProjectsScreen(viewModel = viewModel)
                AppScreen.TRAININGS -> TrainingsScreen(viewModel = viewModel)
                AppScreen.NEWS -> NewsScreen(viewModel = viewModel)
                AppScreen.MEDIA -> MediaScreen(viewModel = viewModel)
                AppScreen.AI_ASSISTANT -> AiAssistantScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(AppScreen.HOME) })
                AppScreen.ABOUT -> AboutScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(AppScreen.HOME) })
                AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(AppScreen.HOME) })
                AppScreen.ADMIN -> AdminScreen(viewModel = viewModel)
            }

            // Bouton rond flottant ÉcoBot IA positionné juste en haut de l'onglet Profil (en bas à droite)
            if (currentScreen != AppScreen.AI_ASSISTANT) {
                Surface(
                    onClick = { viewModel.navigateTo(AppScreen.AI_ASSISTANT) },
                    shape = CircleShape,
                    color = Color.Transparent,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 14.dp)
                        .size(56.dp)
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                listOf(Color.White.copy(alpha = 0.9f), AilMintLight)
                            ),
                            shape = CircleShape
                        )
                        .testTag("floating_ecobot_ai_btn")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(AilEmerald, AilForestDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "ÉcoBot IA - Assistant Intelligent & Connecté",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "ÉCOBOT",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
}
