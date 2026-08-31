package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ProjectEntity
import com.example.ui.components.CategoryPillList
import com.example.ui.components.DonationDialog
import com.example.ui.components.ModernSearchBar
import com.example.ui.components.ResolveImage
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AilEmerald
import com.example.ui.theme.AilMintLight
import com.example.ui.theme.AilMintPillBg
import com.example.ui.theme.AilSoftYellow
import com.example.ui.theme.AilTerracotta
import com.example.ui.viewmodel.AilViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProjectsScreen(
    viewModel: AilViewModel,
    modifier: Modifier = Modifier
) {
    val allProjects by viewModel.allProjects.collectAsStateWithLifecycle()
    val selectedProject by viewModel.selectedProject.collectAsStateWithLifecycle()
    val orgMap by viewModel.orgInfoMap.collectAsStateWithLifecycle()

    val headerTitle = orgMap["projects_header_title"] ?: "Grands Projets & Chantiers Climat"
    val headerSubtitle = orgMap["projects_header_subtitle"] ?: "Découvrez nos programmes d'impact environnemental, reboisement et économie circulaire."

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Tous") }
    var projectToDonateTo by remember { mutableStateOf<ProjectEntity?>(null) }

    val categories = listOf(
        "Tous" to Icons.Default.Widgets,
        "Reboisement" to Icons.Default.Forest,
        "Recyclage" to Icons.Default.CleaningServices,
        "Formation" to Icons.Default.School,
        "Agriculture" to Icons.Default.Eco
    )

    val filteredProjects = allProjects.filter { project ->
        val matchSearch = searchQuery.isBlank() ||
                project.title.contains(searchQuery, ignoreCase = true) ||
                project.summary.contains(searchQuery, ignoreCase = true) ||
                project.partnerName.contains(searchQuery, ignoreCase = true)
        val matchCat = selectedCategory == "Tous" ||
                project.title.contains(selectedCategory, ignoreCase = true) ||
                project.targetObjective.contains(selectedCategory, ignoreCase = true)
        matchSearch && matchCat
    }

    if (projectToDonateTo != null) {
        val proj = projectToDonateTo!!
        DonationDialog(
            projectTitle = proj.title,
            projectId = proj.id,
            onDismiss = { projectToDonateTo = null },
            onConfirm = { amount, donorName ->
                viewModel.makeDonation(proj.id, amount, donorName) {
                    projectToDonateTo = null
                }
            }
        )
    }

    if (selectedProject != null) {
        ProjectDetailDialog(
            project = selectedProject!!,
            onDismiss = { viewModel.selectProject(null) },
            onDonateClick = {
                val proj = selectedProject
                viewModel.selectProject(null)
                projectToDonateTo = proj
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("projects_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            ModernSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Rechercher un projet de reboisement, recyclage..."
            )
        }

        item {
            CategoryPillList(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
        }

        if (searchQuery.isBlank() && selectedCategory == "Tous") {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = headerTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = headerSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (filteredProjects.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun projet ne correspond à vos critères.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredProjects) { project ->
                DetailedProjectCard(
                    project = project,
                    onClick = { viewModel.selectProject(project) },
                    onDonateClick = { projectToDonateTo = project },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun DetailedProjectCard(
    project: ProjectEntity,
    onClick: () -> Unit,
    onDonateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (project.targetBudget > 0) {
        (project.raisedBudget.toFloat() / project.targetBudget.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val percent = (progress * 100).toInt()
    val fmt = NumberFormat.getNumberInstance(Locale.FRENCH)

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("project_card_${project.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                ResolveImage(
                    imageName = project.imageResName,
                    contentDescription = project.title,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = AilEmerald,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = project.status,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        StatusBadge(status = project.status)
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = project.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${fmt.format(project.raisedBudget)} FCFA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = AilEmerald
                        )
                        Text(
                            text = "sur objectif de ${fmt.format(project.targetBudget)} FCFA",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = AilEmerald
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = AilEmerald,
                    trackColor = AilMintPillBg
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDonateClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AilEmerald),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("project_donate_btn_${project.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Soutenir ce projet",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectDetailDialog(
    project: ProjectEntity,
    onDismiss: () -> Unit,
    onDonateClick: () -> Unit
) {
    val fmt = NumberFormat.getNumberInstance(Locale.FRENCH)
    val progress = if (project.targetBudget > 0) {
        (project.raisedBudget.toFloat() / project.targetBudget.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val percent = (progress * 100).toInt()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(20.dp)) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(status = project.status)
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AilEmerald
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ResolveImage(
                        imageName = project.imageResName,
                        contentDescription = project.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Budget Progress Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AilSoftYellow),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Collecté : ${fmt.format(project.raisedBudget)} FCFA",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AilTerracotta
                                )
                                Text(
                                    text = "$percent%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AilEmerald
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = AilTerracotta,
                                trackColor = Color(0xFFFBECE2)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Objectif : ${fmt.format(project.targetBudget)} FCFA • Cible : ${project.targetObjective}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Présentation du projet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AilEmerald
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Impacts attendus",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AilEmerald
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = project.expectedImpact,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Partenaire associé : ${project.partnerName}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onDonateClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AilTerracotta)
                    ) {
                        Text(
                            text = "Faire un Don pour ce projet",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
