package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.School
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.ResolveImage
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
import com.example.ui.theme.AilForestDark
import com.example.ui.theme.AilForestGreen
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

    // Modal Auth Screen if opened via quick login
    if (isAuthDialogOpen) {
        AuthScreen(
            viewModel = viewModel,
            onBack = { viewModel.closeAuthDialog() }
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
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = AilMintPillBg,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "BOUAKÉ",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = AilEmerald,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                },
                actions = {
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
                                contentDescription = "IA AWA",
                                tint = if (currentScreen == AppScreen.AI_ASSISTANT) Color.White else AilEmerald,
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
                        label = { Text("Accueil", fontSize = 10.sp, fontWeight = if (currentScreen == AppScreen.HOME) FontWeight.Bold else FontWeight.Medium) },
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
                        icon = { Icon(Icons.Default.Eco, contentDescription = "Actions") },
                        label = { Text("Actions", fontSize = 10.sp, fontWeight = if (currentScreen == AppScreen.ACTIONS) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AilEmerald,
                            selectedTextColor = AilEmerald,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = AilMintPillBg
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.PROJECTS,
                        onClick = { viewModel.navigateTo(AppScreen.PROJECTS) },
                        icon = { Icon(Icons.Default.Handshake, contentDescription = "Projets") },
                        label = { Text("Projets", fontSize = 10.sp, fontWeight = if (currentScreen == AppScreen.PROJECTS) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AilEmerald,
                            selectedTextColor = AilEmerald,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = AilMintPillBg
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.TRAININGS,
                        onClick = { viewModel.navigateTo(AppScreen.TRAININGS) },
                        icon = { Icon(Icons.Default.School, contentDescription = "Formations") },
                        label = { Text("Formations", fontSize = 10.sp, fontWeight = if (currentScreen == AppScreen.TRAININGS) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AilEmerald,
                            selectedTextColor = AilEmerald,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = AilMintPillBg
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.AI_ASSISTANT,
                        onClick = { viewModel.navigateTo(AppScreen.AI_ASSISTANT) },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "IA AWA") },
                        label = { Text("IA AWA", fontSize = 10.sp, fontWeight = if (currentScreen == AppScreen.AI_ASSISTANT) FontWeight.Bold else FontWeight.Medium) },
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
                        label = { Text("Profil", fontSize = 10.sp, fontWeight = if (currentScreen == AppScreen.PROFILE) FontWeight.Bold else FontWeight.Medium) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                AppScreen.ACTIONS -> ActionsScreen(viewModel = viewModel)
                AppScreen.PROJECTS -> ProjectsScreen(viewModel = viewModel)
                AppScreen.TRAININGS -> TrainingsScreen(viewModel = viewModel)
                AppScreen.NEWS -> NewsScreen(viewModel = viewModel)
                AppScreen.MEDIA -> MediaScreen(viewModel = viewModel)
                AppScreen.AI_ASSISTANT -> AiAssistantScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(AppScreen.HOME) })
                AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel, onBack = { viewModel.navigateTo(AppScreen.HOME) })
                AppScreen.ADMIN -> AdminScreen(viewModel = viewModel)
            }
        }
    }
}
