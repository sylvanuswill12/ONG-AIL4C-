package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.theme.AilBackgroundLight
import com.example.ui.theme.AilGreenAccent
import com.example.ui.theme.AilGreenDark
import com.example.ui.theme.AilGreenLight
import com.example.ui.theme.AilOrangeDark
import com.example.ui.theme.AilOrangeLight
import com.example.ui.theme.AilOrangePillBg
import com.example.ui.theme.AilOrangePrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AilViewModel
import com.example.ui.viewmodel.AppScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
    val isUserAdminAuthorized by viewModel.isUserAdminAuthorized.collectAsStateWithLifecycle()
    val isAuthDialogOpen by viewModel.isAuthDialogOpen.collectAsStateWithLifecycle()
    val userProfile by viewModel.currentUserProfile.collectAsStateWithLifecycle()
    val syncStatus by viewModel.cloudSyncStatus.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showAdminPinDialog by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Auto-protect ADMIN screen: if an unauthorized user attempts to view it, redirect to HOME
    LaunchedEffect(currentScreen, isUserAdminAuthorized) {
        if (currentScreen == AppScreen.ADMIN && !isUserAdminAuthorized) {
            viewModel.navigateTo(AppScreen.HOME)
            viewModel.showToast("Accès réservé aux administrateurs autorisés.")
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier
                    .width(310.dp)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Drawer Header with Vibrant Orange Gradient & Green Accent
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(AilOrangePrimary, AilOrangeDark)
                                )
                            )
                            .statusBarsPadding()
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White,
                                    shadowElevation = 3.dp,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        ResolveImage(
                                            imageName = "img_ail4c_logo",
                                            contentDescription = "Logo AIL4C",
                                            modifier = Modifier.size(38.dp)
                                        )
                                    }
                                }

                                Surface(
                                    color = Color.White.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Eco,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "ONG AIL4C",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = userProfile?.fullName ?: "Membre Éco-Citoyen",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            Text(
                                text = userProfile?.email ?: "ongail4c@gmail.com",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )

                            if (isUserAdminAuthorized) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = AilGreenAccent,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "ADMINISTRATEUR OFFICIEL",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Section 1: Navigation Principale
                    DrawerSectionTitle(title = "NAVIGATION PRINCIPALE")

                    DrawerMenuItem(
                        icon = Icons.Default.Home,
                        title = "Accueil",
                        selected = currentScreen == AppScreen.HOME,
                        onClick = {
                            viewModel.navigateTo(AppScreen.HOME)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerMenuItem(
                        icon = Icons.Default.Eco,
                        title = "Actions Terrain & Événements",
                        selected = currentScreen == AppScreen.ACTIONS,
                        onClick = {
                            viewModel.navigateTo(AppScreen.ACTIONS)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerMenuItem(
                        icon = Icons.Default.Newspaper,
                        title = "Actualités & Communiqués",
                        selected = currentScreen == AppScreen.NEWS,
                        onClick = {
                            viewModel.navigateTo(AppScreen.NEWS)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerMenuItem(
                        icon = Icons.Default.School,
                        title = "Formations Métiers Verts",
                        selected = currentScreen == AppScreen.TRAININGS,
                        onClick = {
                            viewModel.navigateTo(AppScreen.TRAININGS)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerMenuItem(
                        icon = Icons.Default.Public,
                        title = "Projets & Campagnes",
                        selected = currentScreen == AppScreen.PROJECTS,
                        onClick = {
                            viewModel.navigateTo(AppScreen.PROJECTS)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerMenuItem(
                        icon = Icons.Default.PermMedia,
                        title = "Galerie Photos & Vidéos",
                        selected = currentScreen == AppScreen.MEDIA,
                        onClick = {
                            viewModel.navigateTo(AppScreen.MEDIA)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1EBE4))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Section 2: Services & Intelligence
                    DrawerSectionTitle(title = "SERVICES & INTELLIGENCE")

                    DrawerMenuItem(
                        icon = Icons.Default.AutoAwesome,
                        title = "ÉcoBot IA (Assistant Connecté)",
                        selected = currentScreen == AppScreen.AI_ASSISTANT,
                        iconTint = AilOrangePrimary,
                        onClick = {
                            viewModel.navigateTo(AppScreen.AI_ASSISTANT)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerMenuItem(
                        icon = Icons.Default.Info,
                        title = "À Propos & Gouvernance AIL4C",
                        selected = currentScreen == AppScreen.ABOUT,
                        onClick = {
                            viewModel.navigateTo(AppScreen.ABOUT)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerMenuItem(
                        icon = Icons.Default.Person,
                        title = "Mon Profil & Badges",
                        selected = currentScreen == AppScreen.PROFILE,
                        onClick = {
                            viewModel.navigateTo(AppScreen.PROFILE)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerMenuItem(
                        icon = Icons.Default.Sync,
                        title = "Synchronisation en direct",
                        selected = false,
                        onClick = {
                            viewModel.triggerManualCloudSync()
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    if (isUserAdminAuthorized) {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1EBE4))
                        Spacer(modifier = Modifier.height(8.dp))

                        DrawerSectionTitle(title = "ADMINISTRATION")

                        DrawerMenuItem(
                            icon = Icons.Default.AdminPanelSettings,
                            title = "Console Administrateur",
                            selected = currentScreen == AppScreen.ADMIN,
                            iconTint = AilGreenAccent,
                            onClick = {
                                viewModel.navigateTo(AppScreen.ADMIN)
                                coroutineScope.launch { drawerState.close() }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1EBE4))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Déconnexion
                    DrawerMenuItem(
                        icon = Icons.Default.Logout,
                        title = "Se déconnecter",
                        selected = false,
                        iconTint = Color(0xFFDC2626),
                        onClick = {
                            viewModel.logoutUser()
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = AilBackgroundLight,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        // Clean Hamburger Menu 3 Traits
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            },
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .testTag("topbar_menu_drawer_btn")
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = AilOrangePillBg,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Ouvrir le menu",
                                        tint = AilOrangePrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ResolveImage(
                                imageName = "img_ail4c_logo",
                                contentDescription = "Logo AIL4C",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AIL4C",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = AilOrangeDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = AilGreenLight,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(AilGreenAccent)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "CI",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AilGreenDark
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        // Single Clean Action: Live Cloud Sync status button
                        IconButton(
                            onClick = { viewModel.triggerManualCloudSync() },
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .testTag("topbar_cloud_sync_btn")
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (syncStatus.isSyncing) AilOrangeLight else AilOrangePillBg,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = "Synchroniser en direct",
                                        tint = if (syncStatus.isSyncing) AilOrangePrimary else if (syncStatus.isOnline) AilGreenAccent else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = AilOrangeDark
                    )
                )
            },
            bottomBar = {
                Surface(
                    color = Color.White,
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
                                selectedIconColor = AilOrangePrimary,
                                selectedTextColor = AilOrangePrimary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = AilOrangePillBg
                            )
                        )

                        NavigationBarItem(
                            selected = currentScreen == AppScreen.ACTIONS,
                            onClick = { viewModel.navigateTo(AppScreen.ACTIONS) },
                            icon = { Icon(Icons.Default.Eco, contentDescription = "Actions") },
                            label = { Text("Actions", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.ACTIONS) FontWeight.Bold else FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AilOrangePrimary,
                                selectedTextColor = AilOrangePrimary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = AilOrangePillBg
                            )
                        )

                        NavigationBarItem(
                            selected = currentScreen == AppScreen.NEWS,
                            onClick = { viewModel.navigateTo(AppScreen.NEWS) },
                            icon = { Icon(Icons.Default.Newspaper, contentDescription = "Actualités") },
                            label = { Text("Actualités", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.NEWS) FontWeight.Bold else FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AilOrangePrimary,
                                selectedTextColor = AilOrangePrimary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = AilOrangePillBg
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
                                selectedIconColor = AilOrangePrimary,
                                selectedTextColor = AilOrangePrimary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = AilOrangePillBg
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
                // Offline Notification Bar if disconnected
                AnimatedVisibility(visible = !syncStatus.isOnline) {
                    Surface(
                        color = Color(0xFFDC2626),
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
                                text = "Mode hors ligne • Connexion Internet requise pour le fonctionnement et la synchronisation en direct.",
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

                    // Bouton rond flottant ÉcoBot IA positionné en bas à droite
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
                                        listOf(Color.White, AilOrangeLight)
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
                                            listOf(AilOrangePrimary, AilOrangeDark)
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
}

@Composable
fun DrawerSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.ExtraBold,
        color = AilOrangeDark.copy(alpha = 0.8f),
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    iconTint: Color = AilOrangePrimary
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (selected) AilOrangePrimary else iconTint,
                modifier = Modifier.size(20.dp)
            )
        },
        label = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) AilOrangeDark else MaterialTheme.colorScheme.onSurface
            )
        },
        selected = selected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = AilOrangePillBg,
            unselectedContainerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("drawer_item_${title.take(8)}")
    )
}
