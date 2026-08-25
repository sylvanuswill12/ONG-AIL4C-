package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MediaTestimonialEntity
import com.example.ui.components.CategoryPillList
import com.example.ui.components.EcoCategoryBadge
import com.example.ui.components.ResolveImage
import com.example.ui.components.SectionHeader
import com.example.ui.theme.AilEmerald
import com.example.ui.theme.AilGold
import com.example.ui.theme.AilMintLight
import com.example.ui.theme.AilMintPillBg
import com.example.ui.theme.AilSoftYellow
import com.example.ui.theme.AilTerracotta
import com.example.ui.viewmodel.AilViewModel

@Composable
fun MediaScreen(
    viewModel: AilViewModel,
    modifier: Modifier = Modifier
) {
    val allMedia by viewModel.mediaTestimonials.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf("Tous") }
    val tabs = listOf(
        "Tous" to Icons.Default.Widgets,
        "Témoignages" to Icons.Default.RecordVoiceOver,
        "Photos Terrain" to Icons.Default.PhotoLibrary,
        "Vidéos" to Icons.Default.PlayCircle
    )

    val filteredMedia = allMedia.filter { item ->
        when (selectedTab) {
            "Témoignages" -> item.mediaType.equals("Témoignage", ignoreCase = true)
            "Photos Terrain" -> item.mediaType.equals("Photo", ignoreCase = true)
            "Vidéos" -> item.mediaType.equals("Vidéo", ignoreCase = true)
            else -> true
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("media_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            CategoryPillList(
                categories = tabs,
                selectedCategory = selectedTab,
                onCategorySelected = { selectedTab = it }
            )
        }

        // Volunteer Engagement Badges Showcase
        item {
            SectionHeader(
                title = "Badges & Reconnaissance",
                subtitle = "Impact et engagement citoyen des jeunes bénévoles"
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BadgeCard(
                    title = "Éco-Novice",
                    description = "1ère action terrain",
                    icon = Icons.Default.Eco,
                    iconTint = AilEmerald,
                    containerColor = AilMintPillBg,
                    modifier = Modifier.weight(1f)
                )
                BadgeCard(
                    title = "Gardien Vert",
                    description = "5 reboisements",
                    icon = Icons.Default.MilitaryTech,
                    iconTint = AilTerracotta,
                    containerColor = Color(0xFFFBECE2),
                    modifier = Modifier.weight(1f)
                )
                BadgeCard(
                    title = "Champion",
                    description = "Leader engagé",
                    icon = Icons.Default.Star,
                    iconTint = AilGold,
                    containerColor = AilSoftYellow,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Media & Testimonials Feed
        if (filteredMedia.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun contenu multimédia dans cette catégorie.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredMedia) { media ->
                MediaItemCard(
                    media = media,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun BadgeCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MediaItemCard(
    media: MediaTestimonialEntity,
    modifier: Modifier = Modifier
) {
    val isTestimonial = media.mediaType.equals("Témoignage", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                ResolveImage(
                    imageName = media.imageResName,
                    contentDescription = media.title,
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
                                text = media.mediaType.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = media.tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = media.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isTestimonial) Icons.Default.Person else Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = AilTerracotta,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = media.authorOrLocation,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (isTestimonial) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = AilEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = media.descriptionOrQuote,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    Text(
                        text = media.descriptionOrQuote,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
