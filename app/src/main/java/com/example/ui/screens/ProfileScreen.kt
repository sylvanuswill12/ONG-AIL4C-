package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Today
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.model.EcoActivityPreset
import com.example.data.model.EcoActivityRecordEntity
import com.example.data.model.QuizBank
import com.example.data.model.UserBadgeEntity
import com.example.data.model.UserProfileEntity
import com.example.ui.components.getBadgeIconVector
import com.example.ui.components.getTierColor
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
    val allBadges by viewModel.allBadges.collectAsState()
    val allActivities by viewModel.allEcoActivities.collectAsState()
    val orgMap by viewModel.orgInfoMap.collectAsState()

    val profileTitle = orgMap["profile_header_title"] ?: "Mon Profil Éco-Citoyen"

    val scrollState = rememberScrollState()
    var isEditDialogOpen by remember { mutableStateOf(false) }
    var isActivitySheetOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AilMintBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = profileTitle,
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
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                Spacer(modifier = Modifier.width(6.dp))
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress towards next badge threshold
                    val nextBadge = allBadges.firstOrNull { !it.isUnlocked }
                    val currentPoints = user.ecoPoints
                    val targetPoints = nextBadge?.requiredPoints ?: 500
                    val progressToNext = (currentPoints.toFloat() / targetPoints.toFloat()).coerceIn(0f, 1f)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (nextBadge != null) "Prochain badge : ${nextBadge.title}" else "Grade Maximal Atteint !",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = Color.DarkGray
                        )
                        Text(
                            text = "$currentPoints / $targetPoints pts",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = AilEmeraldDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progressToNext },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = AilEmerald,
                        trackColor = Color.LightGray.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.QUIZ) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("profile_quiz_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF047857),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Quiz du Jour", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { isActivitySheetOpen = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("profile_record_eco_action_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AilEmerald,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Éco-Geste 🌱", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Badges & Achievements (Dynamic Room List)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mes Badges Débloqués",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AilEmeraldLight
                        ) {
                            Text(
                                text = "${allBadges.count { it.isUnlocked }} / ${allBadges.size}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AilEmeraldDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        allBadges.forEach { badge ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (badge.isUnlocked) getTierColor(badge.tierLevel).copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.15f),
                                border = BorderStroke(
                                    1.dp,
                                    if (badge.isUnlocked) getTierColor(badge.tierLevel).copy(alpha = 0.5f) else Color.LightGray.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(42.dp),
                                        shape = CircleShape,
                                        color = if (badge.isUnlocked) getTierColor(badge.tierLevel) else Color.Gray.copy(alpha = 0.3f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (badge.isUnlocked) getBadgeIconVector(badge.iconKey) else Icons.Default.Lock,
                                                contentDescription = badge.title,
                                                tint = if (badge.isUnlocked) Color.White else Color.Gray,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = badge.title,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = if (badge.isUnlocked) Color.Black else Color.Gray
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = getTierColor(badge.tierLevel).copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = badge.tierLevel,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = getTierColor(badge.tierLevel),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                        Text(
                                            text = badge.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.DarkGray,
                                            fontSize = 11.sp
                                        )
                                    }

                                    if (badge.isUnlocked) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Débloqué",
                                            tint = AilEmerald,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${badge.requiredPoints} pts",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Eco Activities & Connection Calendar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_eco_calendar_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = AilEmeraldDark,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Calendrier d'Activité Éco-Citoyenne",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                val currentMonthYearStr = remember {
                                    SimpleDateFormat("MMMM yyyy", Locale.FRENCH).format(Date()).replaceFirstChar { it.uppercase() }
                                }
                                Text(
                                    text = "$currentMonthYearStr • Détection auto (+5 pts/jour)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF047857),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Actif",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF047857)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Calendar Calculation
                    val cal = remember { Calendar.getInstance() }
                    val currentDayOfMonth = remember { cal.get(Calendar.DAY_OF_MONTH) }
                    val currentMonth = remember { cal.get(Calendar.MONTH) }
                    val currentYear = remember { cal.get(Calendar.YEAR) }
                    val maxDaysInMonth = remember { cal.getActualMaximum(Calendar.DAY_OF_MONTH) }

                    // First day of week offset (Monday = 0, Sunday = 6)
                    val firstDayOffset = remember {
                        val tempCal = Calendar.getInstance()
                        tempCal.set(Calendar.DAY_OF_MONTH, 1)
                        val dayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK)
                        // In Java Calendar, Sunday is 1, Monday is 2 ... Saturday is 7
                        (dayOfWeek + 5) % 7
                    }

                    // Map days of month to activities
                    val daysWithActivityMap = remember(allActivities, currentMonth, currentYear) {
                        val map = mutableMapOf<Int, MutableList<EcoActivityRecordEntity>>()
                        val activityCal = Calendar.getInstance()
                        allActivities.forEach { act ->
                            activityCal.timeInMillis = act.completedTimestamp
                            if (activityCal.get(Calendar.MONTH) == currentMonth &&
                                activityCal.get(Calendar.YEAR) == currentYear
                            ) {
                                val day = activityCal.get(Calendar.DAY_OF_MONTH)
                                map.getOrPut(day) { mutableListOf() }.add(act)
                            }
                        }
                        map
                    }

                    // Weekdays Header Row
                    val weekDays = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        weekDays.forEach { dayName ->
                            Text(
                                text = dayName,
                                modifier = Modifier.weight(1f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calendar Days Grid (Rows of 7 days)
                    val totalSlots = firstDayOffset + maxDaysInMonth
                    val rowsCount = (totalSlots + 6) / 7

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (row in 0 until rowsCount) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (col in 0 until 7) {
                                    val slotIndex = row * 7 + col
                                    val dayNumber = slotIndex - firstDayOffset + 1

                                    if (dayNumber in 1..maxDaysInMonth) {
                                        val isToday = dayNumber == currentDayOfMonth
                                        val dayActivities = daysWithActivityMap[dayNumber] ?: emptyList()
                                        val isCheckedGreen = dayActivities.isNotEmpty() || isToday
                                        val totalDayPoints = dayActivities.sumOf { it.pointsAwarded }.let { if (it == 0 && isToday) 5 else it }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    when {
                                                        isCheckedGreen -> Color(0xFFD1FAE5)
                                                        else -> Color(0xFFF3F4F6)
                                                    }
                                                )
                                                .then(
                                                    if (isToday) Modifier.border(1.5.dp, Color(0xFF047857), RoundedCornerShape(8.dp))
                                                    else Modifier
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Text(
                                                        text = dayNumber.toString(),
                                                        style = MaterialTheme.typography.labelMedium.copy(
                                                            fontWeight = if (isCheckedGreen || isToday) FontWeight.ExtraBold else FontWeight.Normal
                                                        ),
                                                        color = if (isCheckedGreen) Color(0xFF065F46) else Color.DarkGray,
                                                        fontSize = 12.sp
                                                    )
                                                    if (isCheckedGreen) {
                                                        Spacer(modifier = Modifier.width(2.dp))
                                                        Icon(
                                                            imageVector = Icons.Default.CheckCircle,
                                                            contentDescription = "Coché vert",
                                                            tint = Color(0xFF10B981),
                                                            modifier = Modifier.size(10.dp)
                                                        )
                                                    }
                                                }
                                                if (isCheckedGreen) {
                                                    Text(
                                                        text = "+${if (totalDayPoints > 0) totalDayPoints else 5}",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = Color(0xFF047857),
                                                        fontSize = 9.sp
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        // Empty slot before day 1 or after end of month
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Calendar Legend & Rules
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Règles d'attribution automatique des points :",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF064E3B)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• 🟢 Connexion quotidienne : jour coché en vert (+5 pts)\n• 🎯 Quiz Climat du jour : 1 question (+10 pts)\n• 🌿 Participation Action Terrain (Événements) : (+10 pts)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF047857),
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Activities History Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Historique des Activités",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "${allActivities.size} enregistrement(s)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (allActivities.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Votre connexion du jour est validée (+5 pts).\nParticipez aux quiz et actions terrain pour accumuler plus de points !",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            allActivities.take(8).forEach { act ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(34.dp),
                                        shape = CircleShape,
                                        color = AilEmeraldLight
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = when (act.iconKey.lowercase()) {
                                                    "calendar" -> Icons.Default.CalendarMonth
                                                    "volunteer" -> Icons.Default.VolunteerActivism
                                                    "tree" -> Icons.Default.Park
                                                    "recycle" -> Icons.Default.Recycling
                                                    "solar" -> Icons.Default.WbSunny
                                                    "scholar" -> Icons.Default.School
                                                    else -> Icons.Default.Spa
                                                },
                                                contentDescription = null,
                                                tint = AilEmeraldDark,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = act.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = Color.Black
                                        )
                                        val formattedDate = remember(act.completedTimestamp) {
                                            try {
                                                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(act.completedTimestamp))
                                            } catch (e: Exception) {
                                                ""
                                            }
                                        }
                                        Text(
                                            text = "${act.category} • $formattedDate",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray,
                                            fontSize = 10.sp
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF10B981).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "+${act.pointsAwarded} pts",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFF047857),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
                            }
                        }
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

    if (isActivitySheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isActivitySheetOpen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Valider une Action Éco-Citoyenne",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AilEmeraldLight
                    ) {
                        Text(
                            text = "+Points & Badges",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AilEmeraldDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Choisissez l'éco-geste ou l'action civique que vous avez réalisé(e) aujourd'hui à Bouaké ou dans votre quartier :",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                QuizBank.AVAILABLE_ACTIVITIES.forEach { preset ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = AilMintBackground,
                        border = BorderStroke(1.dp, AilEmerald.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(38.dp),
                                    shape = CircleShape,
                                    color = AilEmerald
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = when (preset.iconKey.lowercase()) {
                                                "tree" -> Icons.Default.Park
                                                "recycle" -> Icons.Default.Recycling
                                                "solar" -> Icons.Default.WbSunny
                                                "scholar" -> Icons.Default.School
                                                else -> Icons.Default.Spa
                                            },
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = preset.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.Black
                                    )
                                    Text(
                                        text = preset.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AilEmeraldDark
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "+${preset.points} pts",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF047857),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = preset.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    viewModel.recordEcoActivity(preset)
                                    isActivitySheetOpen = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .testTag("record_preset_${preset.key}"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AilEmerald)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Valider cette action (+${preset.points} pts)", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
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
