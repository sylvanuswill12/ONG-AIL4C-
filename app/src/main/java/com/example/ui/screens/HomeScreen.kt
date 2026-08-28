package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Person
import com.example.ui.theme.AilEmeraldDark
import com.example.ui.theme.AilEmeraldLight
import com.example.ui.theme.AilLeafGreen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EcoActionEntity
import com.example.data.model.NewsArticleEntity
import com.example.ui.components.CategoryPillList
import com.example.ui.components.EcoCategoryBadge
import com.example.ui.components.ImpactMetricCard
import com.example.ui.components.MentorProfileCard
import com.example.ui.components.ModernHorizontalCard
import com.example.ui.components.ModernSearchBar
import com.example.ui.components.ResolveImage
import com.example.ui.components.SectionHeader
import com.example.ui.components.SpotlightHeroCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.VolunteerDialog
import com.example.ui.components.WeeklyStreakWidget
import com.example.ui.theme.AilAmber
import com.example.ui.theme.AilEmerald
import com.example.ui.theme.AilMintLight
import com.example.ui.theme.AilMintPillBg
import com.example.ui.theme.AilSoftYellow
import com.example.ui.theme.AilTagReforest
import com.example.ui.theme.AilTagTraining
import com.example.ui.theme.AilTagWaste
import com.example.ui.theme.AilTerracotta
import com.example.ui.viewmodel.AilViewModel
import com.example.ui.viewmodel.AppScreen

@Composable
fun HomeScreen(
    viewModel: AilViewModel,
    modifier: Modifier = Modifier
) {
    val impactMetrics by viewModel.impactMetrics.collectAsStateWithLifecycle()
    val allNews by viewModel.publishedNews.collectAsStateWithLifecycle()
    val allActions by viewModel.allActions.collectAsStateWithLifecycle()
    val allProjects by viewModel.allProjects.collectAsStateWithLifecycle()
    val userProfile by viewModel.currentUserProfile.collectAsStateWithLifecycle()
    val orgMap by viewModel.orgInfoMap.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Tous") }
    var showGeneralVolunteerDialog by remember { mutableStateOf(false) }

    val categories = listOf(
        "Tous" to Icons.Default.Eco,
        "Reboisement" to Icons.Default.Forest,
        "Salubrité" to Icons.Default.CleaningServices,
        "Formations" to Icons.Default.School,
        "Projets" to Icons.Default.Handshake,
        "Sensibilisation" to Icons.Default.Eco
    )

    if (showGeneralVolunteerDialog) {
        VolunteerDialog(
            actionTitle = "Volontariat Général AIL4C Bouaké",
            actionId = null,
            onDismiss = { showGeneralVolunteerDialog = false },
            onSubmit = { name, phone, email, city, avail, mot ->
                viewModel.registerVolunteer(name, phone, email, city, null, "Volontariat Général", avail, mot) {
                    showGeneralVolunteerDialog = false
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 0. User Welcome Header & Connection Button
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable {
                        if (userProfile != null) {
                            viewModel.navigateTo(AppScreen.PROFILE)
                        } else {
                            viewModel.openAuthDialog()
                        }
                    }
                    .testTag("home_user_badge_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(AilEmerald, AilLeafGreen))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (userProfile != null) "Bonjour, ${userProfile?.fullName}" else "Bienvenue sur AIL4C !",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = if (userProfile != null) "${userProfile?.volunteerLevel} • ${userProfile?.ecoPoints} pts" else "Connectez-vous avec numéro ou email",
                            style = MaterialTheme.typography.bodySmall,
                            color = AilEmeraldDark,
                            fontSize = 11.sp
                        )
                    }

                    Surface(
                        color = AilEmeraldLight,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (userProfile != null) "Mon Profil" else "Connexion",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AilEmeraldDark,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 0.1 AI Assistant Warm Welcome Banner
        item {
            val aiWelcomeTitle = orgMap["home_ai_title"] ?: "ÉcoBot IA • Assistant Connecté en Direct"
            val aiWelcomeSubtitle = orgMap["home_ai_subtitle"] ?: "Posez vos questions sur le climat, l'agroforesterie, les formations et l'ONG AIL4C."
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable {
                        viewModel.navigateTo(AppScreen.AI_ASSISTANT)
                    }
                    .testTag("home_ai_welcome_banner"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AilEmeraldLight),
                border = BorderStroke(1.dp, AilEmerald.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AilEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "ÉcoBot IA",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = aiWelcomeTitle,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = AilEmeraldDark
                            )
                        }
                        Text(
                            text = aiWelcomeSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray,
                            fontSize = 11.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Ouvrir IA",
                        tint = AilEmeraldDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 1. Search Bar
        item {
            ModernSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Rechercher une action, reboisement, formation..."
            )
        }

        // 2. Category Filter Pills
        item {
            CategoryPillList(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
        }

        // 3. Spotlight Hero Card
        item {
            val heroTitle = orgMap["home_hero_title"] ?: "Grande Campagne : 25 000 Arbres pour le Climat"
            val heroSubtitle = orgMap["home_hero_subtitle"] ?: "Avec la jeunesse citoyenne & l'appui de l'UNFPA"
            val heroCategory = orgMap["home_hero_category"] ?: "Reboisement"
            val heroImage = orgMap["home_hero_image"] ?: "img_hero_reforestation"
            val heroProgressLabel = orgMap["home_hero_progress_label"] ?: "12 500 / 25 000 Arbres plantés"
            val heroProgressRatio = (orgMap["home_hero_progress_percent"]?.toFloatOrNull() ?: 50f) / 100f

            Spacer(modifier = Modifier.height(4.dp))
            SpotlightHeroCard(
                title = heroTitle,
                subtitle = heroSubtitle,
                category = heroCategory,
                imageName = heroImage,
                progress = heroProgressRatio.coerceIn(0f, 1f),
                progressLabel = heroProgressLabel,
                onPlayActionClick = {
                    viewModel.navigateTo(AppScreen.ACTIONS)
                }
            )
        }

        // 4. Weekly Eco-Streak Widget
        item {
            WeeklyStreakWidget(
                streakDays = 5,
                treesCount = 12,
                actionsCount = 4
            )
        }

        // 5. Recommended Actions & Projects (Horizontal Carousel)
        item {
            Spacer(modifier = Modifier.height(12.dp))
            SectionHeader(
                title = "Actions & Projets Phares",
                subtitle = "Recommandés pour votre engagement",
                actionLabel = "Voir tout",
                onActionClick = { viewModel.navigateTo(AppScreen.PROJECTS) }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val featuredProjects = allProjects.filter {
                    if (selectedCategory == "Tous") true
                    else it.title.contains(selectedCategory, ignoreCase = true) || it.targetObjective.contains(selectedCategory, ignoreCase = true)
                }
                items(featuredProjects) { project ->
                    val percent = if (project.targetBudget > 0) {
                        ((project.raisedBudget.toFloat() / project.targetBudget.toFloat()) * 100).toInt()
                    } else 0
                    ModernHorizontalCard(
                        title = project.title,
                        category = project.status,
                        imageName = project.imageResName,
                        ratingOrMetric = "$percent% financé",
                        author = project.partnerName,
                        onClick = {
                            viewModel.selectProject(project)
                            viewModel.navigateTo(AppScreen.PROJECTS)
                        }
                    )
                }
            }
        }

        // 6. Impact Counters (Shown when indicators are defined)
        if (impactMetrics.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = "Notre Impact Écologique & Social",
                    subtitle = "Des résultats concrets mesurés sur le terrain"
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(impactMetrics) { metric ->
                        ImpactMetricCard(
                            label = metric.label,
                            value = metric.valueNumber,
                            unit = metric.unit,
                            iconKey = metric.iconKey,
                            modifier = Modifier.width(170.dp)
                        )
                    }
                }
            }
        }

        // 7. Mentors & Formateurs AIL4C Carousel
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "Mentors & Formateurs AIL4C",
                subtitle = "Des experts mobilisés pour former la jeunesse",
                actionLabel = "Formations",
                onActionClick = { viewModel.navigateTo(AppScreen.TRAININGS) }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val trainers = listOf(
                    Triple("SENIN Tchoumou Esdras Gemiel", "Président Actuel", "Gouvernance & Climat"),
                    Triple("Aka Koffi Ezéchiel", "Président-Fondateur", "Agroforesterie & Vision"),
                    Triple("Kouamé Jean-Marc", "Formateur Senior", "Recyclage & Compost"),
                    Triple("Konan Adjoua Célestine", "Coordonnatrice Jeunesse", "Éco-Citoyenneté"),
                    Triple("Bamba Souleymane", "Ingénieur Écologue", "Restauration Sols")
                )
                items(trainers) { (name, role, specialty) ->
                    MentorProfileCard(
                        name = name,
                        role = role,
                        imageName = "img_youth_training",
                        specialty = specialty,
                        onProfileClick = { viewModel.navigateTo(AppScreen.TRAININGS) }
                    )
                }
            }
        }

        // 8. Upcoming Field Events
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "Événements à Venir",
                subtitle = "Participez aux prochaines mobilisations citoyennes",
                actionLabel = "Voir tout",
                onActionClick = { viewModel.navigateTo(AppScreen.ACTIONS) }
            )
        }

        val upcomingActions = allActions.filter {
            if (selectedCategory == "Tous") true
            else it.category.contains(selectedCategory, ignoreCase = true)
        }.take(3)

        if (upcomingActions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = AilEmerald,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Aucun événement pour le moment. Ajoutez vos actions via l'espace Administration.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(upcomingActions) { action ->
                HomeActionCard(
                    action = action,
                    onClick = {
                        viewModel.selectAction(action)
                        viewModel.navigateTo(AppScreen.ACTIONS)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        // 9. Dernières Actualités
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "Dernières Actualités",
                subtitle = "Les avancées de l'ONG et de nos partenaires",
                actionLabel = "Voir tout",
                onActionClick = { viewModel.navigateTo(AppScreen.NEWS) }
            )
        }

        val latestNews = allNews.take(3)
        if (latestNews.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Newspaper,
                            contentDescription = null,
                            tint = AilEmerald,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Aucune actualité publiée pour le moment. Ajoutez vos articles via l'espace Administration.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(latestNews) { news ->
                HomeNewsCard(
                    news = news,
                    onClick = {
                        viewModel.selectNews(news)
                        viewModel.navigateTo(AppScreen.NEWS)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        // 10. Facebook Community Connect Card
        item {
            val context = LocalContext.current
            val fbTitle = orgMap["home_fb_title"] ?: "Communauté Facebook AIL4C"
            val fbSubtitle = orgMap["home_fb_subtitle"] ?: "Suivez nos directs & actions citoyennes"
            val fbDesc = orgMap["home_fb_desc"] ?: "Rejoignez plus de 15 000 sympathisants et suivez au quotidien nos opérations de salubrité et nos reboisements avec l'UNFPA."
            val fbUrl = orgMap["org_facebook_url"] ?: "https://www.facebook.com/share/1GvChYFAMY/"

            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1877F2).copy(alpha = 0.08f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1877F2),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("f", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = fbTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1877F2)
                            )
                            Text(
                                text = fbSubtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = fbDesc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    viewModel.showToast("Lien Facebook : $fbUrl")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Page Facebook", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Suivez l'ONG AIL4C sur Facebook : $fbUrl")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Partager la page AIL4C"))
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Partager", tint = Color(0xFF1877F2), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // 11. President & Founder Quote Box
        item {
            val context = LocalContext.current
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                val presidentName = orgMap["org_president"] ?: "SENIN Tchoumou Esdras Gemiel"
                val founderName = orgMap["org_founder"] ?: "Aka Koffi Ezéchiel"
                val phone1Number = orgMap["org_phone_1"] ?: "+225 07 89 71 02 89"
                val quoteText = orgMap["home_president_quote"] ?: "« Notre combat est double : redonner vie à nos forêts ivoiriennes et offrir à chaque jeune une qualification et un travail digne au service de notre environnement. »"
                val quoteAuthor = orgMap["home_president_quote_author"] ?: "— Mot de la Présidence de l'ONG AIL4C"
                val quoteImage = orgMap["org_president_image"] ?: "img_founder_portrait"

                Column(modifier = Modifier.padding(18.dp)) {
                    // President & Founder Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ResolveImage(
                            imageName = quoteImage,
                            contentDescription = "$presidentName - Président Actuel AIL4C",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = presidentName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Président Actuel de l'AIL4C",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = AilEmerald
                            )
                            Text(
                                text = "Initiateur & Fondateur : $founderName",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = quoteText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = quoteAuthor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AilEmeraldDark,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                try {
                                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone1Number.replace(" ", "")}"))
                                    context.startActivity(callIntent)
                                } catch (e: Exception) {
                                    viewModel.showToast("Téléphone : $phone1Number")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AilEmerald),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Appeler Siège", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.navigateTo(AppScreen.ABOUT)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Eco, contentDescription = null, tint = AilEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("À Propos", color = AilEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeActionCard(
    action: EcoActionEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ResolveImage(
                imageName = action.imageResName,
                contentDescription = action.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EcoCategoryBadge(category = action.category)
                    StatusBadge(status = action.status)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = AilEmerald,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = action.dateText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun HomeNewsCard(
    news: NewsArticleEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ResolveImage(
                imageName = news.imageResName,
                contentDescription = news.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EcoCategoryBadge(category = news.category)
                    Text(
                        text = news.dateText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
