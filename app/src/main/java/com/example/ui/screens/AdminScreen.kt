package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EcoActionEntity
import com.example.data.model.ImpactMetricEntity
import com.example.data.model.MentorTrainerEntity
import com.example.data.model.NewsArticleEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.TrainingApplicationEntity
import com.example.data.model.TrainingEntity
import com.example.data.model.VolunteerRegistrationEntity
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.EcoCategoryBadge
import com.example.ui.components.ResolveImage
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AilEmerald
import com.example.ui.theme.AilEmeraldDark
import com.example.ui.theme.AilEmeraldLight
import com.example.ui.theme.AilForestDark
import com.example.ui.theme.AilForestGreen
import com.example.ui.theme.AilGold
import com.example.ui.theme.AilMint
import com.example.ui.theme.AilMintDarkGreen
import com.example.ui.theme.AilMintLight
import com.example.ui.theme.AilSoftYellow
import com.example.ui.theme.AilTagTraining
import com.example.ui.theme.AilTerracotta
import com.example.ui.viewmodel.AilViewModel
import com.example.ui.viewmodel.AppScreen
import java.io.File
import java.io.FileOutputStream

enum class AdminTab {
    HOME_CONFIG,
    ORG_INFO,
    GLOBAL_TEXTS,
    NEWS,
    ACTIONS,
    PROJECTS,
    TRAININGS,
    TRAINERS_MENTORS,
    VOLUNTEERS,
    APPLICATIONS,
    METRICS
}

@Composable
fun AdminScreen(
    viewModel: AilViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()
    val isUserAdminAuthorized by viewModel.isUserAdminAuthorized.collectAsStateWithLifecycle()
    val userProfile by viewModel.currentUserProfile.collectAsStateWithLifecycle()

    val allNews by viewModel.allNews.collectAsStateWithLifecycle()
    val allActions by viewModel.allActions.collectAsStateWithLifecycle()
    val allProjects by viewModel.allProjects.collectAsStateWithLifecycle()
    val allTrainings by viewModel.allTrainings.collectAsStateWithLifecycle()
    val allMentorsTrainers by viewModel.allMentorsTrainers.collectAsStateWithLifecycle()
    val volunteers by viewModel.volunteerRegistrations.collectAsStateWithLifecycle()
    val applications by viewModel.trainingApplications.collectAsStateWithLifecycle()
    val impactMetrics by viewModel.impactMetrics.collectAsStateWithLifecycle()
    val syncStatus by viewModel.cloudSyncStatus.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(AdminTab.NEWS) }

    // Dialog state for Create/Edit News
    var editingNews by remember { mutableStateOf<NewsArticleEntity?>(null) }
    var showNewsDialog by remember { mutableStateOf(false) }

    // Dialog state for Create/Edit Action
    var editingAction by remember { mutableStateOf<EcoActionEntity?>(null) }
    var showActionDialog by remember { mutableStateOf(false) }

    // Dialog state for Create/Edit Project
    var editingProject by remember { mutableStateOf<ProjectEntity?>(null) }
    var showProjectDialog by remember { mutableStateOf(false) }

    // Dialog state for Create/Edit Training
    var editingTraining by remember { mutableStateOf<TrainingEntity?>(null) }
    var showTrainingDialog by remember { mutableStateOf(false) }

    // Dialog state for Create/Edit Mentor & Trainer
    var editingMentor by remember { mutableStateOf<MentorTrainerEntity?>(null) }
    var showMentorDialog by remember { mutableStateOf(false) }

    // Dialog state for Impact Metric edit
    var editingMetric by remember { mutableStateOf<ImpactMetricEntity?>(null) }

    // CSV Export dialog / preview
    var csvExportContent by remember { mutableStateOf<String?>(null) }

    // Delete confirmation
    var itemToDelete by remember { mutableStateOf<Pair<String, Long>?>(null) }

    if (!isUserAdminAuthorized) {
        // Restricted state for unauthorized users
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFFFFEBEE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Accès Administrateur Restreint",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB71C1C),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Seules les adresses email autorisées suivantes peuvent apporter des modifications à l'application AIL4C :",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "• atchouyaosylvain59@gmail.com",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "• ail4c03@gmail.com",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.openAuthDialog() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_open_auth_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AilEmerald)
                    ) {
                        Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Se connecter avec un compte admin", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(
                        onClick = { viewModel.navigateTo(com.example.ui.viewmodel.AppScreen.HOME) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Retour à l'accueil", color = Color.Gray)
                    }
                }
            }
        }
        return
    }

    if (!isAdminLoggedIn) {
        // Authorized user but session locked
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFFE8F8F0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = null,
                            tint = AilForestGreen,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Espace d'Administration Sécurisé",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AilForestGreen,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Compte administrateur reconnu (${userProfile?.email ?: userProfile?.identifier}). Entrez le code PIN pour déverrouiller la console de gestion.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    var pinInput by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { pinInput = it },
                        label = { Text("Mot de passe Administrateur") },
                        placeholder = { Text("Entrez AIL4CCI") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_screen_pin_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.loginAdmin(pinInput)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_screen_unlock_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AilForestGreen)
                    ) {
                        Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Accéder à l'Administration", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
        return
    }

    // Delete Confirmation Dialog
    if (itemToDelete != null) {
        val (type, id) = itemToDelete!!
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Confirmer la suppression") },
            text = { Text("Êtes-vous sûr de vouloir supprimer cet élément ($type) ? Cette action est irréversible.") },
            confirmButton = {
                Button(
                    onClick = {
                        when (type) {
                            "news" -> viewModel.deleteNews(id)
                            "action" -> viewModel.deleteAction(id)
                            "project" -> viewModel.deleteProject(id)
                            "training" -> viewModel.deleteTraining(id)
                            "mentor" -> viewModel.deleteMentorTrainer(id)
                            "volunteer" -> viewModel.deleteVolunteerRegistration(id)
                            "application" -> viewModel.deleteTrainingApplication(id)
                        }
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B))
                ) {
                    Text("Supprimer", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { itemToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }

    // CSV Dialog Preview
    if (csvExportContent != null) {
        Dialog(onDismissRequest = { csvExportContent = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Export des Données (CSV / Texte)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AilForestGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = csvExportContent!!,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("AIL4C Export", csvExportContent)
                                clipboard.setPrimaryClip(clip)
                                viewModel.showToast("Données CSV copiées dans le presse-papier !")
                                csvExportContent = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AilForestGreen)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copier CSV", color = Color.White)
                        }
                        OutlinedButton(
                            onClick = { csvExportContent = null },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Fermer")
                        }
                    }
                }
            }
        }
    }

    // News Form Dialog
    if (showNewsDialog) {
        AdminNewsFormDialog(
            initial = editingNews,
            onDismiss = {
                showNewsDialog = false
                editingNews = null
            },
            onSave = { news ->
                viewModel.saveNews(news) {
                    showNewsDialog = false
                    editingNews = null
                }
            }
        )
    }

    // Action Form Dialog
    if (showActionDialog) {
        AdminActionFormDialog(
            initial = editingAction,
            onDismiss = {
                showActionDialog = false
                editingAction = null
            },
            onSave = { act ->
                viewModel.saveAction(act) {
                    showActionDialog = false
                    editingAction = null
                }
            }
        )
    }

    // Project Form Dialog
    if (showProjectDialog) {
        AdminProjectFormDialog(
            initial = editingProject,
            onDismiss = {
                showProjectDialog = false
                editingProject = null
            },
            onSave = { proj ->
                viewModel.saveProject(proj) {
                    showProjectDialog = false
                    editingProject = null
                }
            }
        )
    }

    // Training Form Dialog
    if (showTrainingDialog) {
        AdminTrainingFormDialog(
            initial = editingTraining,
            onDismiss = {
                showTrainingDialog = false
                editingTraining = null
            },
            onSave = { train ->
                viewModel.saveTraining(train) {
                    showTrainingDialog = false
                    editingTraining = null
                }
            }
        )
    }

    // Mentor / Trainer Form Dialog
    if (showMentorDialog) {
        AdminMentorFormDialog(
            initial = editingMentor,
            onDismiss = {
                showMentorDialog = false
                editingMentor = null
            },
            onSave = { mentor ->
                viewModel.saveMentorTrainer(mentor) {
                    showMentorDialog = false
                    editingMentor = null
                }
            }
        )
    }

    // Impact Metric Edit Dialog
    if (editingMetric != null) {
        AdminMetricEditDialog(
            metric = editingMetric!!,
            onDismiss = { editingMetric = null },
            onSave = { newVal ->
                viewModel.updateImpactMetric(editingMetric!!, newVal)
                editingMetric = null
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Top Admin Header
        item {
            Card(
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = AilForestDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Espace d'Administration",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Gestion intégrale des contenus (CRUD)",
                                style = MaterialTheme.typography.bodySmall,
                                color = AilMintLight
                            )
                        }
                        IconButton(
                            onClick = { viewModel.logoutAdmin() },
                            modifier = Modifier.testTag("admin_logout_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Déconnexion",
                                tint = AilMintLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Cloud Real-Time Sync Status Bar
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(
                                            if (syncStatus.isOnline) Color(0xFF2ECC71) else Color(0xFFE74C3C),
                                            CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (syncStatus.isSyncing) "Synchronisation en cours..." else syncStatus.syncMessage,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = syncStatus.lastSyncFormatted,
                                        fontSize = 10.sp,
                                        color = AilMintLight
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.triggerManualCloudSync() },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("admin_manual_cloud_sync_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Actualiser / Forcer synchronisation Cloud",
                                    tint = if (syncStatus.isSyncing) AilGold else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tab Selector
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(AdminTab.values()) { tab ->
                            val label = when (tab) {
                                AdminTab.HOME_CONFIG -> "Accueil & Hero"
                                AdminTab.ORG_INFO -> "À Propos & Organisation"
                                AdminTab.GLOBAL_TEXTS -> "Toutes les Écritures (App)"
                                AdminTab.NEWS -> "Actualités (${allNews.size})"
                                AdminTab.ACTIONS -> "Actions (${allActions.size})"
                                AdminTab.PROJECTS -> "Projets (${allProjects.size})"
                                AdminTab.TRAININGS -> "Formations (${allTrainings.size})"
                                AdminTab.TRAINERS_MENTORS -> "Mentors & Formateurs (${allMentorsTrainers.size})"
                                AdminTab.VOLUNTEERS -> "Bénévoles (${volunteers.size})"
                                AdminTab.APPLICATIONS -> "Candidats (${applications.size})"
                                AdminTab.METRICS -> "Indicateurs"
                            }
                            FilterChip(
                                selected = selectedTab == tab,
                                onClick = { selectedTab = tab },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AilMint,
                                    selectedLabelColor = AilForestDark
                                )
                            )
                        }
                    }
                }
            }
        }

        // Sub-Tab Content
        when (selectedTab) {
            AdminTab.HOME_CONFIG -> {
                item {
                    AdminHomeConfigTab(viewModel = viewModel)
                }
            }

            AdminTab.ORG_INFO -> {
                item {
                    AdminOrgInfoTab(viewModel = viewModel)
                }
            }

            AdminTab.GLOBAL_TEXTS -> {
                item {
                    AdminGlobalTextsTab(viewModel = viewModel)
                }
            }

            AdminTab.NEWS -> {
                item {
                    AdminSectionBar(
                        title = "Gestion des Actualités",
                        onAddClick = {
                            editingNews = null
                            showNewsDialog = true
                        }
                    )
                }
                items(allNews) { news ->
                    AdminNewsRow(
                        news = news,
                        onEdit = {
                            editingNews = news
                            showNewsDialog = true
                        },
                        onDelete = { itemToDelete = Pair("news", news.id) },
                        onToggleFeatured = {
                            viewModel.saveNews(news.copy(isFeatured = !news.isFeatured)) {}
                        }
                    )
                }
            }

            AdminTab.ACTIONS -> {
                item {
                    AdminSectionBar(
                        title = "Gestion des Actions Terrain",
                        onAddClick = {
                            editingAction = null
                            showActionDialog = true
                        }
                    )
                }
                items(allActions) { act ->
                    AdminActionRow(
                        action = act,
                        onEdit = {
                            editingAction = act
                            showActionDialog = true
                        },
                        onDelete = { itemToDelete = Pair("action", act.id) }
                    )
                }
            }

            AdminTab.PROJECTS -> {
                item {
                    AdminSectionBar(
                        title = "Gestion des Projets & Financements",
                        onAddClick = {
                            editingProject = null
                            showProjectDialog = true
                        }
                    )
                }
                items(allProjects) { proj ->
                    AdminProjectRow(
                        project = proj,
                        onEdit = {
                            editingProject = proj
                            showProjectDialog = true
                        },
                        onDelete = { itemToDelete = Pair("project", proj.id) }
                    )
                }
            }

            AdminTab.TRAININGS -> {
                item {
                    AdminSectionBar(
                        title = "Gestion des Formations",
                        onAddClick = {
                            editingTraining = null
                            showTrainingDialog = true
                        }
                    )
                }
                items(allTrainings) { train ->
                    AdminTrainingRow(
                        training = train,
                        onEdit = {
                            editingTraining = train
                            showTrainingDialog = true
                        },
                        onDelete = { itemToDelete = Pair("training", train.id) }
                    )
                }
            }

            AdminTab.TRAINERS_MENTORS -> {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mentors & Formateurs (${allMentorsTrainers.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AilForestGreen
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (allMentorsTrainers.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.deleteAllMentorsTrainers()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC0392B))
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Tout effacer", fontSize = 11.sp)
                                }
                            }
                            Button(
                                onClick = {
                                    editingMentor = null
                                    showMentorDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AilForestGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ajouter", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
                if (allMentorsTrainers.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = AilEmerald,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Aucun mentor ou formateur enregistré.",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Cliquez sur 'Ajouter' pour enregistrer vos formateurs avec photo, nom, fonction et coordonnées.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(allMentorsTrainers) { mentor ->
                        AdminMentorRow(
                            mentor = mentor,
                            onEdit = {
                                editingMentor = mentor
                                showMentorDialog = true
                            },
                            onDelete = { itemToDelete = Pair("mentor", mentor.id) },
                            onToggleAvailability = {
                                viewModel.saveMentorTrainer(mentor.copy(isAvailableForMentoring = !mentor.isAvailableForMentoring)) {}
                            }
                        )
                    }
                }
            }

            AdminTab.VOLUNTEERS -> {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Inscriptions Bénévoles (${volunteers.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AilForestGreen
                        )
                        Button(
                            onClick = {
                                viewModel.exportRegistrations { csv ->
                                    csvExportContent = csv
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AilMintDarkGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Exporter CSV", fontSize = 12.sp)
                        }
                    }
                }
                if (volunteers.isEmpty()) {
                    item {
                        Text(
                            text = "Aucune inscription de bénévole reçue pour le moment.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(volunteers) { vol ->
                        AdminVolunteerCard(
                            volunteer = vol,
                            onStatusChange = { newStatus ->
                                viewModel.updateVolunteerStatus(vol, newStatus)
                            },
                            onDelete = { itemToDelete = Pair("volunteer", vol.id) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            AdminTab.APPLICATIONS -> {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Candidatures Formations (${applications.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AilTagTraining
                        )
                        Button(
                            onClick = {
                                viewModel.exportApplications { csv ->
                                    csvExportContent = csv
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AilTagTraining),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Exporter CSV", fontSize = 12.sp)
                        }
                    }
                }
                if (applications.isEmpty()) {
                    item {
                        Text(
                            text = "Aucune candidature de formation reçue pour le moment.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(applications) { app ->
                        AdminApplicationCard(
                            application = app,
                            onStatusChange = { newStatus ->
                                viewModel.updateApplicationStatus(app, newStatus)
                            },
                            onDelete = { itemToDelete = Pair("application", app.id) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            AdminTab.METRICS -> {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Gestion des Indicateurs d'Impact",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AilForestGreen
                        )
                        Text(
                            text = "Mettez à jour les compteurs en temps réel affichés sur l'accueil.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                items(impactMetrics) { metric ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = metric.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${java.text.NumberFormat.getNumberInstance(java.util.Locale.FRENCH).format(metric.valueNumber)} ${metric.unit}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AilForestGreen
                                )
                            }
                            IconButton(onClick = { editingMetric = metric }) {
                                Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = AilForestGreen)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSectionBar(
    title: String,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AilForestGreen
        )
        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = AilForestGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Ajouter", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminNewsRow(
    news: NewsArticleEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleFeatured: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EcoCategoryBadge(category = news.category)
                    Spacer(modifier = Modifier.width(6.dp))
                    if (news.isFeatured) {
                        Surface(color = AilSoftYellow, shape = RoundedCornerShape(6.dp)) {
                            Text(
                                text = "À la une",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AilTerracotta,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    text = "${news.dateText} • ${news.author}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onToggleFeatured) {
                Icon(
                    imageVector = if (news.isFeatured) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Une",
                    tint = if (news.isFeatured) AilGold else Color.Gray
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = AilForestGreen)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color(0xFFC0392B))
            }
        }
    }
}

@Composable
fun AdminActionRow(
    action: EcoActionEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EcoCategoryBadge(category = action.category)
                    Spacer(modifier = Modifier.width(6.dp))
                    StatusBadge(status = action.status)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    text = "${action.dateText} • Inscrits: ${action.registeredCount}/${action.maxSpots}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = AilForestGreen)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color(0xFFC0392B))
            }
        }
    }
}

@Composable
fun AdminProjectRow(
    project: ProjectEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val fmt = java.text.NumberFormat.getNumberInstance(java.util.Locale.FRENCH)
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    text = "${fmt.format(project.raisedBudget)} / ${fmt.format(project.targetBudget)} FCFA",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = AilTerracotta
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = AilForestGreen)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color(0xFFC0392B))
            }
        }
    }
}

@Composable
fun AdminTrainingRow(
    training: TrainingEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                EcoCategoryBadge(category = training.domain)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = training.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    text = "${training.duration} • ${training.startDateText}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = AilTagTraining)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color(0xFFC0392B))
            }
        }
    }
}

@Composable
fun AdminVolunteerCard(
    volunteer: VolunteerRegistrationEntity,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = volunteer.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(status = volunteer.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tél : ${volunteer.phone} • Email : ${volunteer.email.ifBlank { "Non renseigné" }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Action : ${volunteer.actionTitle} • Ville : ${volunteer.city}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (volunteer.motivation.isNotBlank()) {
                Text(
                    text = "Motivation : ${volunteer.motivation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AilForestDark,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { onStatusChange("Validée") },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Valider", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { onStatusChange("Confirmée") },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Confirmer", fontSize = 11.sp)
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color(0xFFC0392B))
                }
            }
        }
    }
}

@Composable
fun AdminApplicationCard(
    application: TrainingApplicationEntity,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = application.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(status = application.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Formation : ${application.trainingTitle}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = AilTagTraining
            )
            Text(
                text = "Tél : ${application.phone} • Niveau : ${application.educationLevel}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (application.motivation.isNotBlank()) {
                Text(
                    text = "Motivation : ${application.motivation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { onStatusChange("Acceptée") },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Accepter", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { onStatusChange("Entretien convoqué") },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Convoquer", fontSize = 11.sp)
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color(0xFFC0392B))
                }
            }
        }
    }
}

// Helper for saving local media files (photos / videos)
fun saveMediaLocally(context: Context, uri: Uri, isVideo: Boolean): String? {
    return try {
        val extension = if (isVideo) "mp4" else "jpg"
        val prefix = if (isVideo) "vid_" else "img_"
        val fileName = "${prefix}${System.currentTimeMillis()}.$extension"
        val file = File(context.filesDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun AdminMediaPickerSection(
    currentMedia: String,
    onMediaChanged: (String) -> Unit,
    label: String = "Illustration média (Photo ou Vidéo locale)"
) {
    val context = LocalContext.current
    var uploadStatusMessage by remember { mutableStateOf<String?>(null) }

    // Launcher for Photos / Images
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = saveMediaLocally(context, uri, isVideo = false)
            if (savedPath != null) {
                onMediaChanged(savedPath)
                uploadStatusMessage = "Photo importée avec succès !"
            } else {
                uploadStatusMessage = "Échec de l'importation de l'image."
            }
        }
    }

    // Launcher for Videos
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = saveMediaLocally(context, uri, isVideo = true)
            if (savedPath != null) {
                onMediaChanged(savedPath)
                uploadStatusMessage = "Vidéo importée avec succès !"
            } else {
                uploadStatusMessage = "Échec de l'importation de la vidéo."
            }
        }
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AilForestDark
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Media Preview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                ResolveImage(
                    imageName = currentMedia,
                    contentDescription = "Aperçu de l'illustration",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Upload buttons: Add Photo or Add Video
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { photoPickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp), tint = AilEmeraldDark)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ajouter Photo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AilEmeraldDark)
                }

                OutlinedButton(
                    onClick = { videoPickerLauncher.launch("video/*") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFC0392B))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ajouter Vidéo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC0392B))
                }
            }

            if (uploadStatusMessage != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uploadStatusMessage!!,
                    style = MaterialTheme.typography.labelSmall,
                    color = AilForestGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// Dialog: Add/Edit News
@Composable
fun AdminNewsFormDialog(
    initial: NewsArticleEntity?,
    onDismiss: () -> Unit,
    onSave: (NewsArticleEntity) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var summary by remember { mutableStateOf(initial?.summary ?: "") }
    var content by remember { mutableStateOf(initial?.content ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: "Reboisement") }
    var author by remember { mutableStateOf(initial?.author ?: "Cellule Communication AIL4C") }
    var dateText by remember { mutableStateOf(initial?.dateText ?: "24 Août 2026") }
    var imageResName by remember { mutableStateOf(initial?.imageResName ?: "img_hero_reforestation") }
    var isFeatured by remember { mutableStateOf(initial?.isFeatured ?: false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(20.dp)) {
                item {
                    Text(
                        text = if (initial == null) "Ajouter une Actualité" else "Modifier l'Actualité",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AilForestGreen
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titre de l'actualité *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = { Text("Résumé court *") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Corps de l'article complet *") },
                        maxLines = 6,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Media Picker for photo / video
                    AdminMediaPickerSection(
                        currentMedia = imageResName,
                        onMediaChanged = { imageResName = it },
                        label = "Photo ou Vidéo de l'article"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Catégorie (Reboisement, Jeunesse, Salubrité, etc.)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Auteur / Source") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mettre à la une sur l'accueil :")
                        Switch(
                            checked = isFeatured,
                            onCheckedChange = { isFeatured = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AilForestGreen)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                            Text("Annuler")
                        }
                        Button(
                            onClick = {
                                val item = (initial ?: NewsArticleEntity(
                                    title = "",
                                    summary = "",
                                    content = "",
                                    category = "",
                                    dateText = "",
                                    imageResName = imageResName
                                ))!!.copy(
                                    title = title.trim(),
                                    summary = summary.trim(),
                                    content = content.trim(),
                                    category = category.trim(),
                                    author = author.trim(),
                                    dateText = dateText.trim(),
                                    imageResName = imageResName,
                                    isFeatured = isFeatured
                                )
                                onSave(item)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AilForestGreen)
                        ) {
                            Text("Enregistrer", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// Dialog: Add/Edit Action
@Composable
fun AdminActionFormDialog(
    initial: EcoActionEntity?,
    onDismiss: () -> Unit,
    onSave: (EcoActionEntity) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: "Reboisement") }
    var dateText by remember { mutableStateOf(initial?.dateText ?: "Samedi 12 Septembre 2026") }
    var timeText by remember { mutableStateOf(initial?.timeText ?: "07h30 - 12h00") }
    var location by remember { mutableStateOf(initial?.location ?: "Bouaké") }
    var status by remember { mutableStateOf(initial?.status ?: "À venir") }
    var maxSpots by remember { mutableStateOf((initial?.maxSpots ?: 100).toString()) }
    var coordinatorName by remember { mutableStateOf(initial?.coordinatorName ?: "Kouamé Eric") }
    var coordinatorContact by remember { mutableStateOf(initial?.coordinatorContact ?: "+225 07 00 00 00 00") }
    var recommendedGear by remember { mutableStateOf(initial?.recommendedGear ?: "Gants, gourde d'eau") }
    var imageResName by remember { mutableStateOf(initial?.imageResName ?: "img_waste_cleanup") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(20.dp)) {
                item {
                    Text(
                        text = if (initial == null) "Ajouter une Action / Événement" else "Modifier l'Action / Événement",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AilForestGreen
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titre de l'action / événement *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description complète de l'événement *") },
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Media upload (photo/video illustration)
                    AdminMediaPickerSection(
                        currentMedia = imageResName,
                        onMediaChanged = { imageResName = it },
                        label = "Illustration photo ou vidéo téléchargée localement"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Lieu (ex: Bouaké Bamoro)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = dateText,
                            onValueChange = { dateText = it },
                            label = { Text("Date") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = timeText,
                            onValueChange = { timeText = it },
                            label = { Text("Heure") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Catégorie") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = maxSpots,
                            onValueChange = { maxSpots = it },
                            label = { Text("Places max") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = coordinatorName,
                        onValueChange = { coordinatorName = it },
                        label = { Text("Nom du responsable") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = coordinatorContact,
                        onValueChange = { coordinatorContact = it },
                        label = { Text("Contact responsable") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = recommendedGear,
                        onValueChange = { recommendedGear = it },
                        label = { Text("Équipement recommandé") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                            Text("Annuler")
                        }
                        Button(
                            onClick = {
                                val item = (initial ?: EcoActionEntity(
                                    title = "",
                                    description = "",
                                    category = "",
                                    dateText = "",
                                    timeText = "",
                                    location = "",
                                    status = status,
                                    maxSpots = 100,
                                    coordinatorName = "",
                                    coordinatorContact = "",
                                    recommendedGear = "",
                                    imageResName = imageResName
                                )).copy(
                                    title = title.trim(),
                                    description = description.trim(),
                                    category = category.trim(),
                                    dateText = dateText.trim(),
                                    timeText = timeText.trim(),
                                    location = location.trim(),
                                    status = status.trim(),
                                    maxSpots = maxSpots.toIntOrNull() ?: 100,
                                    coordinatorName = coordinatorName.trim(),
                                    coordinatorContact = coordinatorContact.trim(),
                                    recommendedGear = recommendedGear.trim(),
                                    imageResName = imageResName
                                )
                                onSave(item)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AilForestGreen)
                        ) {
                            Text("Enregistrer", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// Dialog: Add/Edit Project
@Composable
fun AdminProjectFormDialog(
    initial: ProjectEntity?,
    onDismiss: () -> Unit,
    onSave: (ProjectEntity) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var summary by remember { mutableStateOf(initial?.summary ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var targetBudget by remember { mutableStateOf((initial?.targetBudget ?: 5000000L).toString()) }
    var raisedBudget by remember { mutableStateOf((initial?.raisedBudget ?: 0L).toString()) }
    var targetObjective by remember { mutableStateOf(initial?.targetObjective ?: "10 000 plants") }
    var expectedImpact by remember { mutableStateOf(initial?.expectedImpact ?: "Restauration écologique et emplois") }
    var partnerName by remember { mutableStateOf(initial?.partnerName ?: "Partenaires Locaux") }
    var imageResName by remember { mutableStateOf(initial?.imageResName ?: "img_hero_reforestation") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(20.dp)) {
                item {
                    Text(
                        text = if (initial == null) "Ajouter un Projet" else "Modifier le Projet",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AilTerracotta
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titre du projet *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = { Text("Résumé court *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description complète *") },
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Media Picker
                    AdminMediaPickerSection(
                        currentMedia = imageResName,
                        onMediaChanged = { imageResName = it },
                        label = "Illustration photo ou vidéo du projet"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = targetBudget,
                            onValueChange = { targetBudget = it },
                            label = { Text("Budget Cible (FCFA)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = raisedBudget,
                            onValueChange = { raisedBudget = it },
                            label = { Text("Montant Collecté (FCFA)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = targetObjective,
                        onValueChange = { targetObjective = it },
                        label = { Text("Objectif quantitatif (ex: 50 000 arbres)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = partnerName,
                        onValueChange = { partnerName = it },
                        label = { Text("Partenaires") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                            Text("Annuler")
                        }
                        Button(
                            onClick = {
                                val item = (initial ?: ProjectEntity(
                                    title = "",
                                    summary = "",
                                    description = "",
                                    targetBudget = 5000000L,
                                    raisedBudget = 0L,
                                    targetObjective = "",
                                    status = "Actif",
                                    expectedImpact = "",
                                    partnerName = "",
                                    imageResName = imageResName
                                )).copy(
                                    title = title.trim(),
                                    summary = summary.trim(),
                                    description = description.trim(),
                                    targetBudget = targetBudget.toLongOrNull() ?: 5000000L,
                                    raisedBudget = raisedBudget.toLongOrNull() ?: 0L,
                                    targetObjective = targetObjective.trim(),
                                    expectedImpact = expectedImpact.trim(),
                                    partnerName = partnerName.trim(),
                                    imageResName = imageResName
                                )
                                onSave(item)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AilTerracotta)
                        ) {
                            Text("Enregistrer", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// Dialog: Add/Edit Training
@Composable
fun AdminTrainingFormDialog(
    initial: TrainingEntity?,
    onDismiss: () -> Unit,
    onSave: (TrainingEntity) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var domain by remember { mutableStateOf(initial?.domain ?: "Agro-écologie") }
    var duration by remember { mutableStateOf(initial?.duration ?: "4 semaines") }
    var startDateText by remember { mutableStateOf(initial?.startDateText ?: "01 Octobre 2026") }
    var location by remember { mutableStateOf(initial?.location ?: "Centre Pilote AIL4C Bouaké") }
    var prerequisites by remember { mutableStateOf(initial?.prerequisites ?: "Motivation, âge 18-35 ans") }
    var certification by remember { mutableStateOf(initial?.certification ?: "Certificat de Qualification AIL4C") }
    var spotsAvailable by remember { mutableStateOf((initial?.spotsAvailable ?: 25).toString()) }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var isRegistrationOpen by remember { mutableStateOf(initial?.isRegistrationOpen ?: true) }
    var imageResName by remember { mutableStateOf(initial?.imageResName ?: "img_youth_training") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(20.dp)) {
                item {
                    Text(
                        text = if (initial == null) "Ajouter une Formation" else "Modifier la Formation",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AilTagTraining
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titre de la formation *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = domain,
                        onValueChange = { domain = it },
                        label = { Text("Domaine (Agro-écologie, Recyclage, etc.)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Media Picker
                    AdminMediaPickerSection(
                        currentMedia = imageResName,
                        onMediaChanged = { imageResName = it },
                        label = "Illustration photo ou vidéo de la formation"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("Durée") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = spotsAvailable,
                            onValueChange = { spotsAvailable = it },
                            label = { Text("Places") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Programme *") },
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Lieu") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = certification,
                        onValueChange = { certification = it },
                        label = { Text("Certification délivrée") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Inscriptions ouvertes :")
                        Switch(
                            checked = isRegistrationOpen,
                            onCheckedChange = { isRegistrationOpen = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AilTagTraining)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                            Text("Annuler")
                        }
                        Button(
                            onClick = {
                                val item = (initial ?: TrainingEntity(
                                    title = "",
                                    domain = "",
                                    duration = "",
                                    startDateText = "",
                                    location = "",
                                    prerequisites = "",
                                    certification = "",
                                    spotsAvailable = 25,
                                    description = "",
                                    imageResName = imageResName
                                )).copy(
                                    title = title.trim(),
                                    domain = domain.trim(),
                                    duration = duration.trim(),
                                    startDateText = startDateText.trim(),
                                    location = location.trim(),
                                    prerequisites = prerequisites.trim(),
                                    certification = certification.trim(),
                                    spotsAvailable = spotsAvailable.toIntOrNull() ?: 25,
                                    description = description.trim(),
                                    isRegistrationOpen = isRegistrationOpen,
                                    imageResName = imageResName
                                )
                                onSave(item)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AilTagTraining)
                        ) {
                            Text("Enregistrer", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// Dialog: Edit Metric Value
@Composable
fun AdminMetricEditDialog(
    metric: ImpactMetricEntity,
    onDismiss: () -> Unit,
    onSave: (Long) -> Unit
) {
    var valText by remember { mutableStateOf(metric.valueNumber.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Mettre à jour : ${metric.label}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AilForestGreen
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = valText,
                    onValueChange = { valText = it },
                    label = { Text("Nouvelle valeur (${metric.unit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Annuler")
                    }
                    Button(
                        onClick = {
                            val v = valText.toLongOrNull() ?: metric.valueNumber
                            onSave(v)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AilForestGreen)
                    ) {
                        Text("Mettre à jour", color = Color.White)
                    }
                }
            }
        }
    }
}

// Institutional Information Admin Tab
@Composable
fun AdminOrgInfoTab(viewModel: AilViewModel) {
    val orgMap by viewModel.orgInfoMap.collectAsStateWithLifecycle()

    var orgName by remember { mutableStateOf("") }
    var orgAcronym by remember { mutableStateOf("") }
    var president by remember { mutableStateOf("") }
    var founder by remember { mutableStateOf("") }
    var motto by remember { mutableStateOf("") }
    var history by remember { mutableStateOf("") }
    var mission by remember { mutableStateOf("") }
    var vision by remember { mutableStateOf("") }
    var objectives by remember { mutableStateOf("") }
    var headquarters by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone1 by remember { mutableStateOf("") }
    var phone2 by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var websiteUrl by remember { mutableStateOf("") }
    var websiteDomain by remember { mutableStateOf("") }
    var facebookPageName by remember { mutableStateOf("") }
    var facebookUrl by remember { mutableStateOf("") }
    var legalStatus by remember { mutableStateOf("") }
    var creationYear by remember { mutableStateOf("") }

    // Sync state when orgMap emits or changes
    LaunchedEffect(orgMap) {
        if (orgMap.isNotEmpty()) {
            orgName = orgMap["org_name"] ?: "Association Ivoirienne de Lutte contre le Changement Climatique et le Chômage (des Jeunes)"
            orgAcronym = orgMap["org_acronym"] ?: "AIL4C"
            president = orgMap["org_president"] ?: "SENIN Tchoumou Esdras Gemiel"
            founder = orgMap["org_founder"] ?: "Aka Koffi Ezéchiel"
            motto = orgMap["org_motto"] ?: "Agir pour le Climat, Former la Jeunesse, Bâtir l'Avenir"
            history = orgMap["org_about_history"] ?: "Créée en Côte d'Ivoire par des jeunes engagés pour la cause environnementale sous l'impulsion de son Président-Fondateur Aka Koffi Ezéchiel et présidée par SENIN Tchoumou Esdras Gemiel, l'Association Ivoirienne de Lutte contre le Changement Climatique et le Chômage (AIL4C) œuvre activement pour la justice climatique, l'autonomisation de la jeunesse et le développement durable. Basée à Bouaké, l'ONG déploie des actions concrètes de reboisement massif, de salubrité urbaine, d'agroforesterie, de lutte contre les VBG et d'insertion professionnelle aux métiers verts."
            mission = orgMap["org_mission"] ?: "Mobiliser toutes les populations contre les effets néfastes du changement climatique, lutter contre les violences basées sur le genre (VBG) et créer des perspectives concrètes d'emploi et de formation aux métiers verts pour toute la jeunesse sans exception."
            vision = orgMap["org_vision"] ?: "Un environnement durable, vert et propre où chaque citoyen adopte des réflexes écologiques et où la jeunesse trouve dans la transition écologique un vecteur d'émancipation et d'épanouissement socio-économique."
            objectives = orgMap["org_objectives"] ?: "1. Reboisement massif & Création de pépinières communautaires durables.\n2. Formation certifiante aux métiers verts (agro-écologie, recyclage, compostage).\n3. Salubrité urbaine, curage citoyen et prévention des inondations.\n4. Sensibilisation de masse en milieu scolaire et santé reproductive (UNFPA).\n5. Insertion professionnelle et accompagnement des jeunes porteurs d'éco-projets."
            headquarters = orgMap["org_headquarters"] ?: "Bouaké, Région du Gbêkê, Côte d'Ivoire (Siège National)"
            address = orgMap["org_address"] ?: "Siège National : Bouaké - Quartier Tchelekro / Koko / Commerce"
            phone1 = orgMap["org_phone_1"] ?: "+225 07 89 71 02 89"
            phone2 = orgMap["org_phone_2"] ?: "+225 07 89 97 63 23"
            email = orgMap["org_email"] ?: "ongail4c@gmail.com"
            websiteUrl = orgMap["org_website_url"] ?: "https://ongail4csiteweb.netlify.app/"
            websiteDomain = orgMap["org_website_domain"] ?: "ongail4csiteweb.netlify.app"
            facebookPageName = orgMap["org_facebook_page_name"] ?: "ONG AIL4C (Page Facebook Officielle)"
            facebookUrl = orgMap["org_facebook_url"] ?: "https://www.facebook.com/share/1GvChYFAMY/"
            legalStatus = orgMap["org_legal_status"] ?: "Organisation Non Gouvernementale (ONG) à but non lucratif enregistrée en Côte d'Ivoire"
            creationYear = orgMap["org_creation_year"] ?: "2023"
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Gestion 'À Propos' & Informations ONG",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AilForestGreen
                )
                Text(
                    text = "Toutes ces informations sont synchronisées en direct sur la page 'À Propos' de l'application.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = { viewModel.navigateTo(AppScreen.ABOUT) },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Voir l'Écran", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION: Identité & Sigle
        Text("1. Identité de l'Organisation", fontWeight = FontWeight.Bold, color = AilEmeraldDark)
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = orgName,
            onValueChange = { orgName = it },
            label = { Text("Nom complet de l'ONG") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = orgAcronym,
                onValueChange = { orgAcronym = it },
                label = { Text("Sigle / Acronyme") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = creationYear,
                onValueChange = { creationYear = it },
                label = { Text("Année création") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = motto,
            onValueChange = { motto = it },
            label = { Text("Devise / Slogan") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION: Gouvernance
        Text("2. Gouvernance & Présidence", fontWeight = FontWeight.Bold, color = AilEmeraldDark)
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = president,
            onValueChange = { president = it },
            label = { Text("Président Actuel") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = founder,
            onValueChange = { founder = it },
            label = { Text("Président-Fondateur") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION: Présentation, Mission, Vision, Piliers
        Text("3. Contenu 'À Propos' (Historique, Mission, Vision)", fontWeight = FontWeight.Bold, color = AilEmeraldDark)
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = history,
            onValueChange = { history = it },
            label = { Text("Présentation & Historique de l'ONG") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = mission,
            onValueChange = { mission = it },
            label = { Text("Notre Mission") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = vision,
            onValueChange = { vision = it },
            label = { Text("Notre Vision") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = objectives,
            onValueChange = { objectives = it },
            label = { Text("Objectifs & Piliers Stratégiques") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION: Coordonnées & Siège
        Text("4. Coordonnées & Siège National", fontWeight = FontWeight.Bold, color = AilEmeraldDark)
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = headquarters,
            onValueChange = { headquarters = it },
            label = { Text("Siège et localisation") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Quartier & Adresse détaillée") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = phone1,
                onValueChange = { phone1 = it },
                label = { Text("Tél 1 (WhatsApp)") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = phone2,
                onValueChange = { phone2 = it },
                label = { Text("Tél 2") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email officiel") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = websiteDomain,
            onValueChange = { 
                websiteDomain = it
                if (!it.startsWith("http")) {
                    websiteUrl = "https://$it"
                } else {
                    websiteUrl = it
                }
            },
            label = { Text("Site Web Officiel (ex: ongail4csiteweb.netlify.app)") },
            placeholder = { Text("ongail4csiteweb.netlify.app") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = facebookPageName,
            onValueChange = { facebookPageName = it },
            label = { Text("Nom Page Facebook") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = facebookUrl,
            onValueChange = { facebookUrl = it },
            label = { Text("URL Page Facebook") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = legalStatus,
            onValueChange = { legalStatus = it },
            label = { Text("Statut Juridique") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val batch = mapOf(
                    "org_name" to orgName,
                    "org_acronym" to orgAcronym,
                    "org_president" to president,
                    "org_founder" to founder,
                    "org_motto" to motto,
                    "org_about_history" to history,
                    "org_mission" to mission,
                    "org_vision" to vision,
                    "org_objectives" to objectives,
                    "org_headquarters" to headquarters,
                    "org_address" to address,
                    "org_phone_1" to phone1,
                    "org_phone_2" to phone2,
                    "org_email" to email,
                    "org_website_url" to websiteUrl,
                    "org_website_domain" to websiteDomain,
                    "org_facebook_page_name" to facebookPageName,
                    "org_facebook_url" to facebookUrl,
                    "org_legal_status" to legalStatus,
                    "org_creation_year" to creationYear
                )
                viewModel.updateOrgInfoBatch(batch)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("admin_save_org_info_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AilForestGreen)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enregistrer toutes les informations 'À Propos'", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        var showClearConfirmDialog by remember { mutableStateOf(false) }

        if (showClearConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showClearConfirmDialog = false },
                title = { Text("Vider tous les éléments ?") },
                text = { Text("Cette action efface toutes les actualités, événements, projets, formations, médias et indicateurs pour vous laisser un espace 100% vierge à alimenter vous-même.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearAllSampleContent()
                            showClearConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Vider tout", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirmDialog = false }) {
                        Text("Annuler")
                    }
                }
            )
        }

        OutlinedButton(
            onClick = {
                showClearConfirmDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Vider tous les onglets (Remise à zéro)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

// TAB: Admin Home Page Content & App Configuration
@Composable
fun AdminHomeConfigTab(viewModel: AilViewModel) {
    val orgMap by viewModel.orgInfoMap.collectAsStateWithLifecycle()

    // Home Hero Spotlight
    var heroTag by remember(orgMap) { mutableStateOf(orgMap["home_hero_tag"] ?: "Campagne Urgence Reboisement 2026") }
    var heroTitle by remember(orgMap) { mutableStateOf(orgMap["home_hero_title"] ?: "Agir maintenant pour le climat et la biodiversité") }
    var heroDescription by remember(orgMap) {
        mutableStateOf(
            orgMap["home_hero_description"]
                ?: "Rejoignez l'AIL4C dans la création de corridors écologiques et la formation de 5 000 jeunes écocitoyens à travers toute la Côte d'Ivoire."
        )
    }
    var heroImage by remember(orgMap) { mutableStateOf(orgMap["home_hero_image"] ?: "img_hero_reforestation") }
    var heroActionText by remember(orgMap) { mutableStateOf(orgMap["home_hero_action_text"] ?: "Devenir Bénévole") }

    // AI Assistant Banner
    var aiBannerTitle by remember(orgMap) { mutableStateOf(orgMap["home_ai_banner_title"] ?: "Assistant Éco-Intelligence Artificielle") }
    var aiBannerDescription by remember(orgMap) {
        mutableStateOf(
            orgMap["home_ai_banner_description"]
                ?: "Posez toutes vos questions sur les essences d'arbres ivoiriennes, le compostage, les techniques de pépinière et l'agro-foresterie."
        )
    }
    var aiBannerBtn by remember(orgMap) { mutableStateOf(orgMap["home_ai_banner_btn"] ?: "Ouvrir l'Assistant Éco-Conseils") }

    // President Quote Card
    var quoteText by remember(orgMap) {
        mutableStateOf(
            orgMap["home_president_quote"]
                ?: "« La jeunesse ivoirienne est le moteur du changement écologique. Chaque arbre planté est une promesse d'avenir pour nos terroirs. »"
        )
    }
    var quoteAuthor by remember(orgMap) { mutableStateOf(orgMap["home_president_author"] ?: "M. Kouamé Eric") }
    var quoteRole by remember(orgMap) { mutableStateOf(orgMap["home_president_role"] ?: "Président Exécutif AIL4C") }

    // Facebook / Social Section
    var fbBannerTitle by remember(orgMap) { mutableStateOf(orgMap["home_facebook_banner_title"] ?: "Communauté & Direct Facebook") }
    var fbBannerSubtitle by remember(orgMap) {
        mutableStateOf(
            orgMap["home_facebook_banner_subtitle"]
                ?: "Retrouvez nos reportages vidéos sur le terrain, nos directs de reboisement et échangez avec plus de 25 000 membres actifs."
        )
    }
    var fbBtnText by remember(orgMap) { mutableStateOf(orgMap["home_facebook_btn_text"] ?: "Accéder à notre Page Facebook") }

    // Sections visibility & headers
    var sectionNewsTitle by remember(orgMap) { mutableStateOf(orgMap["home_section_news_title"] ?: "Actualités & Reportages") }
    var sectionActionsTitle by remember(orgMap) { mutableStateOf(orgMap["home_section_actions_title"] ?: "Prochaines Actions Terrain") }
    var sectionImpactTitle by remember(orgMap) { mutableStateOf(orgMap["home_section_impact_title"] ?: "Impact Mesurable AIL4C") }

    var saveFeedback by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Personnalisation Globale : Page d'Accueil & Textes",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AilForestDark
        )
        Text(
            text = "Modifiez directement tous les textes, bannières, illustrations médias et messages de la page d'accueil de l'application sans recompiler.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 1: Bannière Vedette (Hero Spotlight)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = AilGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("1. Bannière Vedette (Hero Accueil)", fontWeight = FontWeight.Bold, color = AilForestDark)
                }
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = heroTag,
                    onValueChange = { heroTag = it },
                    label = { Text("Badge / Tag de la bannière") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = heroTitle,
                    onValueChange = { heroTitle = it },
                    label = { Text("Grand Titre Hero") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = heroDescription,
                    onValueChange = { heroDescription = it },
                    label = { Text("Texte Descriptif Hero") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Hero Image / Video Picker
                AdminMediaPickerSection(
                    currentMedia = heroImage,
                    onMediaChanged = { heroImage = it },
                    label = "Illustration photo ou vidéo de la Bannière Hero"
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = heroActionText,
                    onValueChange = { heroActionText = it },
                    label = { Text("Texte du bouton d'action Hero") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 2: Bannière Assistant IA Écologique
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("2. Bannière Assistant IA Écologique", fontWeight = FontWeight.Bold, color = AilForestDark)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = aiBannerTitle,
                    onValueChange = { aiBannerTitle = it },
                    label = { Text("Titre du module IA") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aiBannerDescription,
                    onValueChange = { aiBannerDescription = it },
                    label = { Text("Description du service IA") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aiBannerBtn,
                    onValueChange = { aiBannerBtn = it },
                    label = { Text("Texte du bouton") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 3: Citation & Mot du Président
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("3. Citation & Mot de la Présidence", fontWeight = FontWeight.Bold, color = AilForestDark)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = quoteText,
                    onValueChange = { quoteText = it },
                    label = { Text("Citation / Message clé") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = quoteAuthor,
                    onValueChange = { quoteAuthor = it },
                    label = { Text("Auteur de la citation") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = quoteRole,
                    onValueChange = { quoteRole = it },
                    label = { Text("Titre / Rôle (ex: Président Exécutif AIL4C)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 4: Bloc Facebook & Réseaux Sociaux
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("4. Bloc Facebook & Communauté", fontWeight = FontWeight.Bold, color = AilForestDark)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = fbBannerTitle,
                    onValueChange = { fbBannerTitle = it },
                    label = { Text("Titre du bloc communauté") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fbBannerSubtitle,
                    onValueChange = { fbBannerSubtitle = it },
                    label = { Text("Sous-titre / Explication") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fbBtnText,
                    onValueChange = { fbBtnText = it },
                    label = { Text("Texte du bouton Facebook") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 5: Intitulés des sections de la page d'accueil
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("5. Titres des Sections Accueil", fontWeight = FontWeight.Bold, color = AilForestDark)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = sectionNewsTitle,
                    onValueChange = { sectionNewsTitle = it },
                    label = { Text("Titre section Actualités") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = sectionActionsTitle,
                    onValueChange = { sectionActionsTitle = it },
                    label = { Text("Titre section Actions Terrain") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = sectionImpactTitle,
                    onValueChange = { sectionImpactTitle = it },
                    label = { Text("Titre section Indicateurs d'Impact") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (saveFeedback != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = AilMintLight),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Text(
                    text = saveFeedback!!,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = AilForestDark,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Button(
            onClick = {
                val batch = mapOf(
                    "home_hero_tag" to heroTag.trim(),
                    "home_hero_title" to heroTitle.trim(),
                    "home_hero_description" to heroDescription.trim(),
                    "home_hero_image" to heroImage.trim(),
                    "home_hero_action_text" to heroActionText.trim(),
                    "home_ai_banner_title" to aiBannerTitle.trim(),
                    "home_ai_banner_description" to aiBannerDescription.trim(),
                    "home_ai_banner_btn" to aiBannerBtn.trim(),
                    "home_president_quote" to quoteText.trim(),
                    "home_president_author" to quoteAuthor.trim(),
                    "home_president_role" to quoteRole.trim(),
                    "home_facebook_banner_title" to fbBannerTitle.trim(),
                    "home_facebook_banner_subtitle" to fbBannerSubtitle.trim(),
                    "home_facebook_btn_text" to fbBtnText.trim(),
                    "home_section_news_title" to sectionNewsTitle.trim(),
                    "home_section_actions_title" to sectionActionsTitle.trim(),
                    "home_section_impact_title" to sectionImpactTitle.trim()
                )
                viewModel.updateOrgInfoBatch(batch)
                saveFeedback = "Toutes les modifications de la page d'accueil ont été enregistrées avec succès !"
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AilForestGreen)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enregistrer les Modifications de la Page d'Accueil", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

// Mentor & Trainer Admin Row
@Composable
fun AdminMentorRow(
    mentor: MentorTrainerEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleAvailability: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AilEmeraldLight),
                contentAlignment = Alignment.Center
            ) {
                if (mentor.photoResName.isNotBlank()) {
                    ResolveImage(
                        imageName = mentor.photoResName,
                        contentDescription = mentor.fullName,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = AilEmeraldDark,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = when (mentor.category) {
                            "Mentor", "Mentorat" -> AilMint
                            "Formatrice & Mentore" -> AilEmeraldLight
                            "Expert Climat" -> AilSoftYellow
                            else -> AilMintLight
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = mentor.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AilForestDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (mentor.isAvailableForMentoring) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = Color(0xFFE8F8F0),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Disponible",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E824C),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mentor.fullName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${mentor.roleTitle} • ${mentor.specialty}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AilForestDark,
                    maxLines = 1
                )
                Text(
                    text = "${mentor.experienceYears} ans d'exp. • ${mentor.location} • ${mentor.phone}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onToggleAvailability) {
                Icon(
                    imageVector = if (mentor.isAvailableForMentoring) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = "Disponibilité",
                    tint = if (mentor.isAvailableForMentoring) AilEmerald else Color.Gray
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = AilForestGreen)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color(0xFFC0392B))
            }
        }
    }
}

// Dialog: Create or Edit Mentor / Trainer
@Composable
fun AdminMentorFormDialog(
    initial: MentorTrainerEntity?,
    onDismiss: () -> Unit,
    onSave: (MentorTrainerEntity) -> Unit
) {
    var fullName by remember { mutableStateOf(initial?.fullName ?: "") }
    var roleTitle by remember { mutableStateOf(initial?.roleTitle ?: "Formateur & Expert Climat") }
    var category by remember { mutableStateOf(initial?.category ?: "Formateur") }
    var specialty by remember { mutableStateOf(initial?.specialty ?: "") }
    var bio by remember { mutableStateOf(initial?.bio ?: "") }
    var experienceYears by remember { mutableStateOf(initial?.experienceYears?.toString() ?: "5") }
    var phone by remember { mutableStateOf(initial?.phone ?: "+225 ") }
    var email by remember { mutableStateOf(initial?.email ?: "") }
    var location by remember { mutableStateOf(initial?.location ?: "Bouaké, Côte d'Ivoire") }
    var displayOrder by remember { mutableStateOf(initial?.displayOrder?.toString() ?: "1") }
    var isAvailableForMentoring by remember { mutableStateOf(initial?.isAvailableForMentoring ?: true) }
    var photoResName by remember { mutableStateOf(initial?.photoResName ?: "") }

    val categories = listOf("Formateur", "Mentor", "Formatrice & Mentore", "Expert Climat", "Conseiller Insertion")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(20.dp)) {
                item {
                    Text(
                        text = if (initial == null) "Ajouter un Mentor ou Formateur" else "Modifier Mentor / Formateur",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AilForestGreen
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    AdminMediaPickerSection(
                        currentMedia = photoResName,
                        onMediaChanged = { photoResName = it },
                        label = "Photo de profil (Galerie ou Nom ressource)"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nom complet *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = roleTitle,
                        onValueChange = { roleTitle = it },
                        label = { Text("Titre / Rôle (ex: Expert Agroforesterie)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Catégorie :", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = AilForestDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AilMint,
                                    selectedLabelColor = AilForestDark
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = specialty,
                        onValueChange = { specialty = it },
                        label = { Text("Spécialité technique *") },
                        placeholder = { Text("ex: Pépinières durables, Énergie solaire...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Biographie & Parcours d'accompagnement") },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = experienceYears,
                            onValueChange = { experienceYears = it },
                            label = { Text("Années d'exp.") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = displayOrder,
                            onValueChange = { displayOrder = it },
                            label = { Text("Ordre d'aff.") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Téléphone / WhatsApp") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email professionnel") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Localisation / Ville") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Disponible pour le mentorat des jeunes :", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = isAvailableForMentoring,
                            onCheckedChange = { isAvailableForMentoring = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AilEmerald)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                            Text("Annuler")
                        }
                        Button(
                            onClick = {
                                val item = (initial ?: MentorTrainerEntity(
                                    fullName = "",
                                    roleTitle = "",
                                    category = category,
                                    specialty = specialty,
                                    bio = bio,
                                    experienceYears = experienceYears.toIntOrNull() ?: 5,
                                    phone = phone,
                                    email = email,
                                    location = location,
                                    isAvailableForMentoring = isAvailableForMentoring,
                                    displayOrder = displayOrder.toIntOrNull() ?: 1,
                                    photoResName = photoResName
                                )).copy(
                                    fullName = fullName.trim(),
                                    roleTitle = roleTitle.trim(),
                                    category = category.trim(),
                                    specialty = specialty.trim(),
                                    bio = bio.trim(),
                                    experienceYears = experienceYears.toIntOrNull() ?: 5,
                                    phone = phone.trim(),
                                    email = email.trim(),
                                    location = location.trim(),
                                    isAvailableForMentoring = isAvailableForMentoring,
                                    displayOrder = displayOrder.toIntOrNull() ?: 1,
                                    photoResName = photoResName.trim()
                                )
                                onSave(item)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AilForestGreen)
                        ) {
                            Text("Enregistrer", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// Dialog: Add Custom String / Key
@Composable
fun AdminAddCustomKeyDialog(
    onDismiss: () -> Unit,
    onConfirm: (key: String, value: String) -> Unit
) {
    var newKey by remember { mutableStateOf("") }
    var newValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Ajouter une Nouvelle Écriture / Clé", fontWeight = FontWeight.Bold, color = AilForestDark)
        },
        text = {
            Column {
                Text(
                    "Définissez une clé unique (ex: quiz_bonus_rule, about_partners_tag) et son texte associé.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = newKey,
                    onValueChange = { newKey = it.replace(" ", "_").lowercase() },
                    label = { Text("Clé unique (snake_case)") },
                    placeholder = { Text("ex: my_custom_text_key") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = newValue,
                    onValueChange = { newValue = it },
                    label = { Text("Texte / Valeur") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newKey.isNotBlank()) {
                        onConfirm(newKey.trim(), newValue.trim())
                    }
                },
                enabled = newKey.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AilForestGreen)
            ) {
                Text("Ajouter & Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

// TAB: Admin Global Texts & Universal Config Editor
@Composable
fun AdminGlobalTextsTab(viewModel: AilViewModel) {
    val orgMap by viewModel.orgInfoMap.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var showAddKeyDialog by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // Page titles & subtitles
    var homeWelcomeTitle by remember(orgMap) { mutableStateOf(orgMap["home_welcome_title"] ?: "Bienvenue sur AIL4C") }
    var homeWelcomeSubtitle by remember(orgMap) { mutableStateOf(orgMap["home_welcome_subtitle"] ?: "La plateforme citoyenne d'action pour le climat et l'insertion des jeunes") }
    var homeQuizTitle by remember(orgMap) { mutableStateOf(orgMap["home_quiz_title"] ?: "Quiz Climat Quotidien • 1 Question par Jour") }
    var homeQuizSubtitle by remember(orgMap) { mutableStateOf(orgMap["home_quiz_subtitle"] ?: "Testez vos connaissances chaque jour et remportez +10 Points Éco-Citoyens !") }
    var homeQuizBtn by remember(orgMap) { mutableStateOf(orgMap["home_quiz_btn"] ?: "Participer au Quiz du Jour") }
    var homeFooterCopyright by remember(orgMap) { mutableStateOf(orgMap["home_footer_copyright"] ?: "© 2026 ONG AIL4C • Tous droits réservés") }
    var homeFooterSlogan by remember(orgMap) { mutableStateOf(orgMap["home_footer_slogan"] ?: "Agir pour le Climat, Former la Jeunesse, Bâtir l'Avenir") }

    // About screen texts
    var aboutHeroTag by remember(orgMap) { mutableStateOf(orgMap["about_hero_tag"] ?: "ONG AIL4C • Côte d'Ivoire") }
    var aboutMottoQuote by remember(orgMap) { mutableStateOf(orgMap["about_motto_quote"] ?: "« Agir pour le Climat, Former la Jeunesse, Bâtir l'Avenir »") }
    var aboutHistoryTitle by remember(orgMap) { mutableStateOf(orgMap["about_history_title"] ?: "Présentation & Historique") }
    var aboutMissionTitle by remember(orgMap) { mutableStateOf(orgMap["about_mission_title"] ?: "Notre Mission") }
    var aboutVisionTitle by remember(orgMap) { mutableStateOf(orgMap["about_vision_title"] ?: "Notre Vision") }
    var aboutPillarsTitle by remember(orgMap) { mutableStateOf(orgMap["about_pillars_title"] ?: "Nos Piliers Stratégiques & Objectifs") }
    var aboutGovernanceTitle by remember(orgMap) { mutableStateOf(orgMap["about_governance_title"] ?: "Gouvernance & Présidence") }
    var aboutContactTitle by remember(orgMap) { mutableStateOf(orgMap["about_contact_title"] ?: "Coordonnées & Siège National") }
    var aboutPartnersTitle by remember(orgMap) { mutableStateOf(orgMap["about_partners_title"] ?: "Partenaires & Alliances Stratégiques") }

    // Trainings screen texts
    var trainingsHeaderTitle by remember(orgMap) { mutableStateOf(orgMap["trainings_header_title"] ?: "Pôle Formations & Métiers Verts") }
    var trainingsHeaderSubtitle by remember(orgMap) { mutableStateOf(orgMap["trainings_header_subtitle"] ?: "Développez vos compétences éco-citoyennes et participez à la transition écologique.") }
    var trainingsMentorsTitle by remember(orgMap) { mutableStateOf(orgMap["trainings_mentors_title"] ?: "Formateurs & Mentors Climat") }
    var trainingsNotice by remember(orgMap) { mutableStateOf(orgMap["trainings_application_notice"] ?: "Toutes les formations de l'AIL4C sont gratuites ou subventionnées pour les jeunes vulnérables et passionnés de transition écologique.") }

    // Projects screen texts
    var projectsHeaderTitle by remember(orgMap) { mutableStateOf(orgMap["projects_header_title"] ?: "Grands Projets & Chantiers Climat") }
    var projectsHeaderSubtitle by remember(orgMap) { mutableStateOf(orgMap["projects_header_subtitle"] ?: "Découvrez nos programmes d'impact environnemental, reboisement et économie circulaire en cours de réalisation.") }
    var projectsCallToAction by remember(orgMap) { mutableStateOf(orgMap["projects_call_to_action"] ?: "Contribuer ou Proposer un Partenariat") }

    // News & Actions screen texts
    var newsHeaderTitle by remember(orgMap) { mutableStateOf(orgMap["news_header_title"] ?: "Actualités & Reportages Terrain") }
    var newsHeaderSubtitle by remember(orgMap) { mutableStateOf(orgMap["news_header_subtitle"] ?: "Suivez au quotidien l'avancée de nos actions, nos communiqués officiels et nos succès écologiques.") }
    var actionsHeaderTitle by remember(orgMap) { mutableStateOf(orgMap["actions_header_title"] ?: "Missions & Actions Citoyennes") }
    var actionsHeaderSubtitle by remember(orgMap) { mutableStateOf(orgMap["actions_header_subtitle"] ?: "Engagez-vous sur le terrain : reboisements, curages citoyens, pépinières et sensibilisation.") }

    // Quiz screen texts
    var quizHeaderTitle by remember(orgMap) { mutableStateOf(orgMap["quiz_header_title"] ?: "Quiz Climat & Éco-Savoir") }
    var quizHeaderSubtitle by remember(orgMap) { mutableStateOf(orgMap["quiz_header_subtitle"] ?: "1 question par jour pour renforcer vos connaissances environnementales et remporter +10 points !") }
    var quizRewardText by remember(orgMap) { mutableStateOf(orgMap["quiz_daily_points_reward_text"] ?: "Bravo ! +10 Points Éco-Citoyens remportés !") }
    var quizCongratsMessage by remember(orgMap) { mutableStateOf(orgMap["quiz_congrats_message"] ?: "Excellente réponse ! Vous contribuez activement à la sensibilisation écologique.") }

    // AI Assistant Bot texts
    var aiAssistantName by remember(orgMap) { mutableStateOf(orgMap["ai_assistant_name"] ?: "ÉcoBot IA") }
    var aiWelcomeMessage by remember(orgMap) {
        mutableStateOf(
            orgMap["ai_welcome_message"]
                ?: "Bonjour ! Je suis ÉcoBot IA, l'assistant intelligent de l'ONG AIL4C. Posez-moi toutes vos questions sur le climat, l'agroforesterie, le reboisement, le recyclage et les actions citoyennes !"
        )
    }
    var aiPromptOverride by remember(orgMap) {
        mutableStateOf(
            orgMap["ai_system_instructions_override"]
                ?: "Tu es ÉcoBot IA, l'expert et assistant écologique officiel de l'ONG AIL4C en Côte d'Ivoire. Sois encourageant, concis, pédagogique et valorise les actions sur le terrain."
        )
    }
    var aiPrompt1 by remember(orgMap) { mutableStateOf(orgMap["ai_quick_prompt_1"] ?: "Comment réussir une pépinière durable en Côte d'Ivoire ?") }
    var aiPrompt2 by remember(orgMap) { mutableStateOf(orgMap["ai_quick_prompt_2"] ?: "Quelles sont les meilleures essences d'arbres à planter ?") }
    var aiPrompt3 by remember(orgMap) { mutableStateOf(orgMap["ai_quick_prompt_3"] ?: "Comment transformer les déchets plastiques en éco-pavés ?") }
    var aiPrompt4 by remember(orgMap) { mutableStateOf(orgMap["ai_quick_prompt_4"] ?: "Comment devenir bénévole actif de l'ONG AIL4C ?") }

    // Profile screen texts
    var profileHeaderTitle by remember(orgMap) { mutableStateOf(orgMap["profile_header_title"] ?: "Mon Espace Éco-Citoyen") }
    var profileDailyBonusText by remember(orgMap) { mutableStateOf(orgMap["profile_daily_bonus_text"] ?: "+5 Points Éco-Citoyens offerts à chaque connexion quotidienne !") }
    var profileBadgesTitle by remember(orgMap) { mutableStateOf(orgMap["profile_badges_section_title"] ?: "Mes Badges & Niveaux d'Engagement") }

    if (showAddKeyDialog) {
        AdminAddCustomKeyDialog(
            onDismiss = { showAddKeyDialog = false },
            onConfirm = { k, v ->
                viewModel.updateOrgInfo(k, v)
                showAddKeyDialog = false
                statusMessage = "Clé '$k' ajoutée et enregistrée avec succès !"
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Toutes les Écritures & Textes de l'Application",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AilForestDark
                )
                Text(
                    text = "Modifiez absolument chaque écriture, titre, sous-titre, slogan, consigne IA et message de l'application dans les moindres détails.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = { showAddKeyDialog = true },
                modifier = Modifier
                    .background(AilMint, CircleShape)
                    .size(42.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter une clé", tint = AilForestDark)
            }
        }

        if (statusMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = AilMintLight,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AilEmerald, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(statusMessage!!, style = MaterialTheme.typography.bodySmall, color = AilEmeraldDark, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 1: Titres & Textes Écran Accueil
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = AilForestGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("1. Écritures de la Page d'Accueil & Footer", fontWeight = FontWeight.Bold, color = AilForestDark)
                }
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = homeWelcomeTitle,
                    onValueChange = { homeWelcomeTitle = it },
                    label = { Text("Titre de bienvenue utilisateur") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = homeWelcomeSubtitle,
                    onValueChange = { homeWelcomeSubtitle = it },
                    label = { Text("Sous-titre de bienvenue") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = homeQuizTitle,
                    onValueChange = { homeQuizTitle = it },
                    label = { Text("Titre de la carte Quiz sur l'accueil") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = homeQuizSubtitle,
                    onValueChange = { homeQuizSubtitle = it },
                    label = { Text("Sous-titre de la carte Quiz") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = homeQuizBtn,
                    onValueChange = { homeQuizBtn = it },
                    label = { Text("Texte du bouton Quiz Accueil") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = homeFooterSlogan,
                    onValueChange = { homeFooterSlogan = it },
                    label = { Text("Devise / Slogan du pied de page") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = homeFooterCopyright,
                    onValueChange = { homeFooterCopyright = it },
                    label = { Text("Mention légale / Copyright") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 2: Écritures Écran À Propos
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TextFields, contentDescription = null, tint = AilEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("2. Titres & En-têtes Page 'À Propos'", fontWeight = FontWeight.Bold, color = AilForestDark)
                }
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = aboutHeroTag,
                    onValueChange = { aboutHeroTag = it },
                    label = { Text("Tag héroïque À Propos") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aboutMottoQuote,
                    onValueChange = { aboutMottoQuote = it },
                    label = { Text("Citation / Devise officielle") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aboutHistoryTitle,
                    onValueChange = { aboutHistoryTitle = it },
                    label = { Text("Titre section Historique") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aboutMissionTitle,
                    onValueChange = { aboutMissionTitle = it },
                    label = { Text("Titre section Mission") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aboutVisionTitle,
                    onValueChange = { aboutVisionTitle = it },
                    label = { Text("Titre section Vision") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aboutPillarsTitle,
                    onValueChange = { aboutPillarsTitle = it },
                    label = { Text("Titre section Piliers Stratégiques") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aboutGovernanceTitle,
                    onValueChange = { aboutGovernanceTitle = it },
                    label = { Text("Titre section Gouvernance") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aboutContactTitle,
                    onValueChange = { aboutContactTitle = it },
                    label = { Text("Titre section Coordonnées") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aboutPartnersTitle,
                    onValueChange = { aboutPartnersTitle = it },
                    label = { Text("Titre section Partenaires") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 3: Écritures Formations, Projets, Actualités, Actions
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = null, tint = AilTagTraining)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("3. En-têtes Formations, Projets & Événements", fontWeight = FontWeight.Bold, color = AilForestDark)
                }
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = trainingsHeaderTitle,
                    onValueChange = { trainingsHeaderTitle = it },
                    label = { Text("Titre En-tête Formations") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = trainingsHeaderSubtitle,
                    onValueChange = { trainingsHeaderSubtitle = it },
                    label = { Text("Sous-titre En-tête Formations") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = trainingsMentorsTitle,
                    onValueChange = { trainingsMentorsTitle = it },
                    label = { Text("Titre Section Mentors & Formateurs") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = trainingsNotice,
                    onValueChange = { trainingsNotice = it },
                    label = { Text("Note d'information aux candidats") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = projectsHeaderTitle,
                    onValueChange = { projectsHeaderTitle = it },
                    label = { Text("Titre En-tête Projets") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = projectsHeaderSubtitle,
                    onValueChange = { projectsHeaderSubtitle = it },
                    label = { Text("Sous-titre En-tête Projets") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = newsHeaderTitle,
                    onValueChange = { newsHeaderTitle = it },
                    label = { Text("Titre En-tête Actualités") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newsHeaderSubtitle,
                    onValueChange = { newsHeaderSubtitle = it },
                    label = { Text("Sous-titre En-tête Actualités") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = actionsHeaderTitle,
                    onValueChange = { actionsHeaderTitle = it },
                    label = { Text("Titre En-tête Actions Terrain") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = actionsHeaderSubtitle,
                    onValueChange = { actionsHeaderSubtitle = it },
                    label = { Text("Sous-titre En-tête Actions Terrain") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 4: Assistant ÉcoBot IA & Instructions Personnalisées
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AilEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("4. ÉcoBot IA & Suggestions Rapides", fontWeight = FontWeight.Bold, color = AilForestDark)
                }
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = aiAssistantName,
                    onValueChange = { aiAssistantName = it },
                    label = { Text("Nom affiché de l'Assistant IA") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aiWelcomeMessage,
                    onValueChange = { aiWelcomeMessage = it },
                    label = { Text("Message de premier accueil du Bot") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aiPromptOverride,
                    onValueChange = { aiPromptOverride = it },
                    label = { Text("Directives / Consignes système de l'IA") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aiPrompt1,
                    onValueChange = { aiPrompt1 = it },
                    label = { Text("Suggestion rapide #1") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aiPrompt2,
                    onValueChange = { aiPrompt2 = it },
                    label = { Text("Suggestion rapide #2") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aiPrompt3,
                    onValueChange = { aiPrompt3 = it },
                    label = { Text("Suggestion rapide #3") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aiPrompt4,
                    onValueChange = { aiPrompt4 = it },
                    label = { Text("Suggestion rapide #4") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 5: Quiz Climat, Profil & Gamification
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = AilGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("5. Quiz Climat & Profil Citoyen", fontWeight = FontWeight.Bold, color = AilForestDark)
                }
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = quizHeaderTitle,
                    onValueChange = { quizHeaderTitle = it },
                    label = { Text("Titre Écran Quiz") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = quizHeaderSubtitle,
                    onValueChange = { quizHeaderSubtitle = it },
                    label = { Text("Sous-titre Écran Quiz") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = quizRewardText,
                    onValueChange = { quizRewardText = it },
                    label = { Text("Texte toast de récompense (+10 pts)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = profileHeaderTitle,
                    onValueChange = { profileHeaderTitle = it },
                    label = { Text("Titre Écran Profil") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = profileDailyBonusText,
                    onValueChange = { profileDailyBonusText = it },
                    label = { Text("Texte du bonus de connexion (+5 pts)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton Enregistrer Toutes les Écritures
        Button(
            onClick = {
                val batch = mapOf(
                    "home_welcome_title" to homeWelcomeTitle.trim(),
                    "home_welcome_subtitle" to homeWelcomeSubtitle.trim(),
                    "home_quiz_title" to homeQuizTitle.trim(),
                    "home_quiz_subtitle" to homeQuizSubtitle.trim(),
                    "home_quiz_btn" to homeQuizBtn.trim(),
                    "home_footer_copyright" to homeFooterCopyright.trim(),
                    "home_footer_slogan" to homeFooterSlogan.trim(),
                    "about_hero_tag" to aboutHeroTag.trim(),
                    "about_motto_quote" to aboutMottoQuote.trim(),
                    "about_history_title" to aboutHistoryTitle.trim(),
                    "about_mission_title" to aboutMissionTitle.trim(),
                    "about_vision_title" to aboutVisionTitle.trim(),
                    "about_pillars_title" to aboutPillarsTitle.trim(),
                    "about_governance_title" to aboutGovernanceTitle.trim(),
                    "about_contact_title" to aboutContactTitle.trim(),
                    "about_partners_title" to aboutPartnersTitle.trim(),
                    "trainings_header_title" to trainingsHeaderTitle.trim(),
                    "trainings_header_subtitle" to trainingsHeaderSubtitle.trim(),
                    "trainings_mentors_title" to trainingsMentorsTitle.trim(),
                    "trainings_application_notice" to trainingsNotice.trim(),
                    "projects_header_title" to projectsHeaderTitle.trim(),
                    "projects_header_subtitle" to projectsHeaderSubtitle.trim(),
                    "projects_call_to_action" to projectsCallToAction.trim(),
                    "news_header_title" to newsHeaderTitle.trim(),
                    "news_header_subtitle" to newsHeaderSubtitle.trim(),
                    "actions_header_title" to actionsHeaderTitle.trim(),
                    "actions_header_subtitle" to actionsHeaderSubtitle.trim(),
                    "quiz_header_title" to quizHeaderTitle.trim(),
                    "quiz_header_subtitle" to quizHeaderSubtitle.trim(),
                    "quiz_daily_points_reward_text" to quizRewardText.trim(),
                    "quiz_congrats_message" to quizCongratsMessage.trim(),
                    "ai_assistant_name" to aiAssistantName.trim(),
                    "ai_welcome_message" to aiWelcomeMessage.trim(),
                    "ai_system_instructions_override" to aiPromptOverride.trim(),
                    "ai_quick_prompt_1" to aiPrompt1.trim(),
                    "ai_quick_prompt_2" to aiPrompt2.trim(),
                    "ai_quick_prompt_3" to aiPrompt3.trim(),
                    "ai_quick_prompt_4" to aiPrompt4.trim(),
                    "profile_header_title" to profileHeaderTitle.trim(),
                    "profile_daily_bonus_text" to profileDailyBonusText.trim(),
                    "profile_badges_section_title" to profileBadgesTitle.trim()
                )
                viewModel.updateOrgInfoBatch(batch)
                statusMessage = "Toutes les écritures de l'application ont été enregistrées avec succès !"
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AilForestGreen)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enregistrer Toutes les Écritures Globales", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 6: Éditeur Universel Clé-Valeur en direct avec recherche
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Dictionnaire & Éditeur Universel Clé-Valeur",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AilForestDark
                )
                Text(
                    text = "Recherchez ou modifiez n'importe quel paramètre ou texte stocké (${orgMap.size} clés actives).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = { showAddKeyDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = AilMint),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = AilForestDark, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nouvelle Clé", color = AilForestDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Rechercher par clé ou par texte...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AilEmerald) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Effacer")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        val filteredKeys = orgMap.keys.filter { key ->
            val value = orgMap[key] ?: ""
            searchQuery.isBlank() ||
                    key.contains(searchQuery, ignoreCase = true) ||
                    value.contains(searchQuery, ignoreCase = true)
        }.sorted()

        if (filteredKeys.isEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (searchQuery.isBlank()) "Aucune clé enregistrée pour l'instant." else "Aucun résultat trouvé pour '$searchQuery'.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            filteredKeys.forEach { key ->
                val currentValue = orgMap[key] ?: ""
                var editingVal by remember(currentValue) { mutableStateOf(currentValue) }
                val isModified = editingVal != currentValue

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = AilEmeraldDark
                            )
                            if (isModified) {
                                Surface(
                                    color = AilSoftYellow,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Modifié",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AilForestDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = editingVal,
                            onValueChange = { editingVal = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = if (editingVal.length > 50) 2 else 1,
                            maxLines = 6,
                            shape = RoundedCornerShape(10.dp)
                        )
                        if (isModified) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { editingVal = currentValue }) {
                                    Text("Rétablir")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.updateOrgInfo(key, editingVal.trim())
                                        statusMessage = "Clé '$key' mise à jour !"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AilEmerald),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Enregistrer '$key'", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
