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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.TrainingEntity
import com.example.ui.components.CategoryPillList
import com.example.ui.components.EcoCategoryBadge
import com.example.ui.components.ModernSearchBar
import com.example.ui.components.ResolveImage
import com.example.ui.components.TrainingApplicationDialog
import com.example.ui.theme.AilEmerald
import com.example.ui.theme.AilMintLight
import com.example.ui.theme.AilMintPillBg
import com.example.ui.theme.AilTagTraining
import com.example.ui.theme.AilTerracotta
import com.example.ui.viewmodel.AilViewModel

@Composable
fun TrainingsScreen(
    viewModel: AilViewModel,
    modifier: Modifier = Modifier
) {
    val allTrainings by viewModel.allTrainings.collectAsStateWithLifecycle()
    val selectedTraining by viewModel.selectedTraining.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedDomain by remember { mutableStateOf("Tous") }
    var trainingToApplyFor by remember { mutableStateOf<TrainingEntity?>(null) }

    val domains = listOf(
        "Tous" to Icons.Default.Widgets,
        "Agroforesterie" to Icons.Default.Forest,
        "Recyclage" to Icons.Default.CleaningServices,
        "Compostage" to Icons.Default.Eco,
        "Énergie Solaire" to Icons.Default.School
    )

    val filteredTrainings = allTrainings.filter { training ->
        val matchSearch = searchQuery.isBlank() ||
                training.title.contains(searchQuery, ignoreCase = true) ||
                training.description.contains(searchQuery, ignoreCase = true) ||
                training.location.contains(searchQuery, ignoreCase = true)
        val matchDomain = selectedDomain == "Tous" || training.domain.contains(selectedDomain, ignoreCase = true)
        matchSearch && matchDomain
    }

    if (trainingToApplyFor != null) {
        val t = trainingToApplyFor!!
        TrainingApplicationDialog(
            trainingTitle = t.title,
            trainingId = t.id,
            onDismiss = { trainingToApplyFor = null },
            onSubmit = { name, phone, email, edu, mot ->
                viewModel.submitTrainingApplication(
                    trainingId = t.id,
                    trainingTitle = t.title,
                    fullName = name,
                    phone = phone,
                    email = email,
                    educationLevel = edu,
                    motivation = mot
                ) {
                    trainingToApplyFor = null
                }
            }
        )
    }

    if (selectedTraining != null) {
        TrainingDetailDialog(
            training = selectedTraining!!,
            onDismiss = { viewModel.selectTraining(null) },
            onApplyClick = {
                val t = selectedTraining
                viewModel.selectTraining(null)
                trainingToApplyFor = t
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("trainings_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            ModernSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Rechercher une formation aux métiers verts..."
            )
        }

        item {
            CategoryPillList(
                categories = domains,
                selectedCategory = selectedDomain,
                onCategorySelected = { selectedDomain = it }
            )
        }

        if (filteredTrainings.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune session de formation ne correspond à votre recherche.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredTrainings) { training ->
                DetailedTrainingCard(
                    training = training,
                    onClick = { viewModel.selectTraining(training) },
                    onApplyClick = { trainingToApplyFor = training },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun DetailedTrainingCard(
    training: TrainingEntity,
    onClick: () -> Unit,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("training_card_${training.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                ResolveImage(
                    imageName = training.imageResName,
                    contentDescription = training.title,
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
                        EcoCategoryBadge(category = training.domain)
                        Surface(
                            color = if (training.isRegistrationOpen) AilMintPillBg else Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (training.isRegistrationOpen) "Inscriptions Ouvertes" else "Complet / Clôturé",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (training.isRegistrationOpen) AilEmerald else Color(0xFFC62828),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = training.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = AilEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Durée : ${training.duration} • Début : ${training.startDateText}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = AilTerracotta,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = training.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CardMembership,
                        contentDescription = null,
                        tint = AilEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = training.certification,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onApplyClick,
                    enabled = training.isRegistrationOpen,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AilEmerald),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("training_apply_btn_${training.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (training.isRegistrationOpen) "Candidater (${training.spotsAvailable} places)" else "Session Complète",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun TrainingDetailDialog(
    training: TrainingEntity,
    onDismiss: () -> Unit,
    onApplyClick: () -> Unit
) {
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
                        EcoCategoryBadge(category = training.domain)
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = training.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AilTagTraining
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ResolveImage(
                        imageName = training.imageResName,
                        contentDescription = training.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Training details card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Durée : ${training.duration}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = AilTagTraining
                            )
                            Text(
                                text = "Début : ${training.startDateText} • Lieu : ${training.location}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Certification : ${training.certification}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Programme & Objectifs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AilEmerald
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = training.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Prérequis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AilEmerald
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = training.prerequisites,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onApplyClick,
                        enabled = training.isRegistrationOpen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AilTagTraining)
                    ) {
                        Text(
                            text = "Soumettre ma candidature",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
