package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Sync
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
import com.example.data.model.NewsArticleEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.TrainingApplicationEntity
import com.example.data.model.TrainingEntity
import com.example.data.model.VolunteerRegistrationEntity
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.EcoCategoryBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AilEmerald
import com.example.ui.theme.AilEmeraldDark
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

enum class AdminTab {
    NEWS,
    ACTIONS,
    PROJECTS,
    TRAININGS,
    VOLUNTEERS,
    APPLICATIONS,
    METRICS,
    ORG_INFO
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
                                AdminTab.NEWS -> "Actualités (${allNews.size})"
                                AdminTab.ACTIONS -> "Actions (${allActions.size})"
                                AdminTab.PROJECTS -> "Projets (${allProjects.size})"
                                AdminTab.TRAININGS -> "Formations (${allTrainings.size})"
                                AdminTab.VOLUNTEERS -> "Bénévoles (${volunteers.size})"
                                AdminTab.APPLICATIONS -> "Candidats (${applications.size})"
                                AdminTab.METRICS -> "Indicateurs"
                                AdminTab.ORG_INFO -> "Infos ONG"
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

            AdminTab.ORG_INFO -> {
                item {
                    AdminOrgInfoTab(viewModel = viewModel)
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
                                )).copy(
                                    title = title.trim(),
                                    summary = summary.trim(),
                                    content = content.trim(),
                                    category = category.trim(),
                                    author = author.trim(),
                                    dateText = dateText.trim(),
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
                        text = if (initial == null) "Ajouter une Action Terrain" else "Modifier l'Action",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AilForestGreen
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titre de l'action *") },
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
                                    imageResName = "img_waste_cleanup"
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
                                    recommendedGear = recommendedGear.trim()
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
                                    imageResName = "img_hero_reforestation"
                                )).copy(
                                    title = title.trim(),
                                    summary = summary.trim(),
                                    description = description.trim(),
                                    targetBudget = targetBudget.toLongOrNull() ?: 5000000L,
                                    raisedBudget = raisedBudget.toLongOrNull() ?: 0L,
                                    targetObjective = targetObjective.trim(),
                                    expectedImpact = expectedImpact.trim(),
                                    partnerName = partnerName.trim()
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
                                    imageResName = "img_youth_training"
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
                                    isRegistrationOpen = isRegistrationOpen
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
            websiteUrl = orgMap["org_website_url"] ?: "https://www.ongail4c.com"
            websiteDomain = orgMap["org_website_domain"] ?: "www.ongail4c.com"
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
            label = { Text("Site Web Officiel (ex: www.ongail4c.com)") },
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

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                viewModel.navigateTo(AppScreen.WEB_PORTAL)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AilEmerald)
        ) {
            Icon(Icons.Default.Language, contentDescription = null, tint = AilEmerald)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ouvrir et Tester le Portail Web en Direct", fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
