package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfileEntity
import com.example.ui.theme.AilEmerald
import com.example.ui.theme.AilEmeraldDark
import com.example.ui.theme.AilEmeraldLight
import com.example.ui.theme.AilGold
import com.example.ui.theme.AilLeafGreen
import com.example.ui.theme.AilMintBackground
import com.example.ui.viewmodel.AilViewModel
import com.example.ui.viewmodel.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AilViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.currentUserProfile.collectAsState()
    val isUserAdminAuthorized by viewModel.isUserAdminAuthorized.collectAsState()
    val scrollState = rememberScrollState()
    var isEditDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AilMintBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mon Profil Éco-Citoyen",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("profile_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isEditDialogOpen = true },
                        modifier = Modifier.testTag("profile_edit_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifier le profil",
                            tint = AilEmeraldDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val user = userProfile ?: UserProfileEntity(
                fullName = "Visiteur Découverte",
                identifier = "invite@ail4c-ci.org",
                authType = "GUEST",
                city = "Bouaké",
                quartier = "Centre",
                ecoPoints = 20,
                volunteerLevel = "Éco-Volontaire Débutant"
            )

            // User Hero Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(AilEmerald, AilLeafGreen)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = user.fullName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = AilEmeraldLight,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = null,
                                tint = AilEmeraldDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = user.volunteerLevel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AilEmeraldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Contact & Location badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (user.authType == "PHONE") Icons.Default.Phone else Icons.Default.Email,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = user.identifier,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.DarkGray
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${user.quartier}, ${user.city}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Admin Management shortcut (VISIBLE ONLY FOR AUTHORIZED EMAILS)
            if (isUserAdminAuthorized) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(AppScreen.ADMIN) }
                        .testTag("profile_admin_panel_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AilEmeraldLight),
                    border = BorderStroke(1.5.dp, AilEmerald.copy(alpha = 0.6f)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(AilEmerald),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Panneau Administrateur",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AilEmeraldDark
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = AilEmerald,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "AUTORISÉ",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Gérer les projets, actualités, actions et candidatures",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray,
                                fontSize = 11.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = AilEmeraldDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Eco Points and Impact Score
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Points d'Éco-Citoyenneté",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${user.ecoPoints}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = AilEmerald
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "pts AIL4C",
                                style = MaterialTheme.typography.bodySmall,
                                color = AilEmeraldDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(AilGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = AilGold,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Badges & Achievements
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Mes Badges Débloqués",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProfileBadgeItem(
                            title = "Éco-Novice",
                            desc = "Compte créé",
                            isUnlocked = true
                        )

                        ProfileBadgeItem(
                            title = "Planteur Gbêkê",
                            desc = "Reboisement",
                            isUnlocked = user.ecoPoints >= 50
                        )

                        ProfileBadgeItem(
                            title = "Champion Vert",
                            desc = "Leader Climat",
                            isUnlocked = user.ecoPoints >= 100
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fast Actions
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Raccourcis & Assistance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ProfileQuickActionRow(
                        icon = Icons.Default.AutoAwesome,
                        title = "Parler à l'Assistante IA AWA",
                        subtitle = "Obtenir des réponses sur les actions et formations",
                        onClick = { viewModel.navigateTo(AppScreen.AI_ASSISTANT) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.3f))

                    ProfileQuickActionRow(
                        icon = Icons.Default.VolunteerActivism,
                        title = "Participer aux Projets Climat",
                        subtitle = "Faire un don ou soutenir la pépinière",
                        onClick = { viewModel.navigateTo(AppScreen.PROJECTS) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.3f))

                    ProfileQuickActionRow(
                        icon = Icons.Default.School,
                        title = "Mes Candidatures Formations",
                        subtitle = "Agro-écologie, Métiers Verts & Recyclage",
                        onClick = { viewModel.navigateTo(AppScreen.TRAININGS) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.3f))

                    ProfileQuickActionRow(
                        icon = Icons.Default.Info,
                        title = "À Propos de l'ONG AIL4C",
                        subtitle = "Histoire, gouvernance, mission, vision & contacts",
                        onClick = { viewModel.navigateTo(AppScreen.ABOUT) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Logout or Switch Account
            OutlinedButton(
                onClick = {
                    viewModel.logoutUser()
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("profile_logout_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Red
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.4f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Changer de compte / Se déconnecter",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Edit Profile Dialog
    if (isEditDialogOpen) {
        var editName by remember { mutableStateOf(userProfile?.fullName ?: "") }
        var editCity by remember { mutableStateOf(userProfile?.city ?: "Bouaké") }
        var editQuartier by remember { mutableStateOf(userProfile?.quartier ?: "Commerce") }

        AlertDialog(
            onDismissRequest = { isEditDialogOpen = false },
            title = {
                Text(
                    text = "Modifier mon profil",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nom et Prénoms") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editCity,
                        onValueChange = { editCity = it },
                        label = { Text("Ville") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editQuartier,
                        onValueChange = { editQuartier = it },
                        label = { Text("Quartier") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val current = userProfile ?: UserProfileEntity(
                            fullName = editName,
                            identifier = "bénévole@ail4c.org",
                            authType = "PHONE"
                        )
                        viewModel.updateUserProfile(
                            current.copy(
                                fullName = editName.ifBlank { current.fullName },
                                city = editCity.ifBlank { current.city },
                                quartier = editQuartier.ifBlank { current.quartier }
                            )
                        )
                        isEditDialogOpen = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AilEmerald)
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditDialogOpen = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun ProfileBadgeItem(
    title: String,
    desc: String,
    isUnlocked: Boolean
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isUnlocked) AilEmeraldLight else Color.LightGray.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isUnlocked) AilEmerald.copy(alpha = 0.4f) else Color.LightGray
        ),
        modifier = Modifier.width(96.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isUnlocked) Icons.Default.CheckCircle else Icons.Default.MilitaryTech,
                contentDescription = null,
                tint = if (isUnlocked) AilEmerald else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) AilEmeraldDark else Color.Gray,
                fontSize = 11.sp
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.labelSmall,
                color = Color.DarkGray,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun ProfileQuickActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(AilEmeraldLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AilEmeraldDark,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 11.sp
            )
        }
    }
}
