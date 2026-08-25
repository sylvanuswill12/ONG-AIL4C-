package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiChatMessageEntity
import com.example.ui.theme.AilEmerald
import com.example.ui.theme.AilEmeraldDark
import com.example.ui.theme.AilEmeraldLight
import com.example.ui.theme.AilGold
import com.example.ui.theme.AilLeafGreen
import com.example.ui.theme.AilMintBackground
import com.example.ui.theme.AilSoftCardBg
import com.example.ui.viewmodel.AilViewModel
import com.example.ui.viewmodel.AppScreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    viewModel: AilViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.allAiMessages.collectAsState()
    val isThinking by viewModel.isAiThinking.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickPrompts = listOf(
        "🌿 Qui est l'ONG AIL4C ?",
        "🌳 Participer au reboisement",
        "🎓 Formations gratuites métiers verts",
        "📍 Adresse du siège de l'ONG",
        "💚 Comment faire un don Mobile Money ?",
        "🤝 Devenir bénévole éco-citoyen"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AilMintBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(AilEmerald, AilLeafGreen)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "IA AWA",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AWA • Éco-Assistante",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = AilEmerald.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "IA AIL4C",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AilEmeraldDark,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            Text(
                                text = if (isThinking) "En train de vous répondre..." else "En ligne • Côte d'Ivoire",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isThinking) AilEmeraldDark else Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("ai_back_button")
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
                        onClick = { viewModel.clearAiChat() },
                        modifier = Modifier.testTag("clear_ai_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Effacer la discussion",
                            tint = Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    // Quick Prompts row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(quickPrompts) { prompt ->
                            Surface(
                                modifier = Modifier
                                    .clickable {
                                        viewModel.sendAiMessage(prompt)
                                    }
                                    .testTag("quick_prompt_${prompt.take(10)}"),
                                shape = RoundedCornerShape(20.dp),
                                color = AilEmeraldLight,
                                border = androidx.compose.foundation.BorderStroke(1.dp, AilEmerald.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = prompt,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AilEmeraldDark,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Input Field and Send Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Posez votre question à AWA...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ai_input_field"),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AilEmerald,
                                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.6f),
                                focusedContainerColor = AilMintBackground,
                                unfocusedContainerColor = AilMintBackground
                            ),
                            maxLines = 3,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendAiMessage(inputText)
                                    inputText = ""
                                }
                            })
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        FloatingActionButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendAiMessage(inputText)
                                    inputText = ""
                                }
                            },
                            containerColor = AilEmerald,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("ai_send_button")
                        ) {
                            if (isThinking) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Envoyer",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Welcome Header Card
            item {
                WelcomeAiHeaderCard(
                    onNavigateToActions = { viewModel.navigateTo(AppScreen.ACTIONS) },
                    onNavigateToTrainings = { viewModel.navigateTo(AppScreen.TRAININGS) }
                )
            }

            // Chat Messages
            items(messages) { message ->
                ChatMessageBubble(
                    message = message,
                    onActionClick = { actionType ->
                        when (actionType) {
                            "ACTIONS" -> viewModel.navigateTo(AppScreen.ACTIONS)
                            "TRAININGS" -> viewModel.navigateTo(AppScreen.TRAININGS)
                            "PROJECTS" -> viewModel.navigateTo(AppScreen.PROJECTS)
                            "NEWS" -> viewModel.navigateTo(AppScreen.NEWS)
                        }
                    }
                )
            }

            // Thinking indicator
            if (isThinking) {
                item {
                    AiThinkingBubble()
                }
            }
        }
    }
}

@Composable
fun WelcomeAiHeaderCard(
    onNavigateToActions: () -> Unit,
    onNavigateToTrainings: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(AilEmerald, AilLeafGreen)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Bienvenue sur l'IA AWA !",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Votre guide intelligent pour le climat, l'écologie & la jeunesse",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "💡 **Exemples de demandes à tester :**",
                style = MaterialTheme.typography.bodySmall,
                color = AilEmeraldDark,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "• « Explique-moi les bienfaits de l'agro-écologie »\n" +
                        "• « Comment puis-ci m'inscrire au prochain reboisement ? »\n" +
                        "• « Quelles formations de recyclage plastique sont disponibles ? »\n" +
                        "• « Quels sont les numéros pour faire un don Wave ou Orange ? »",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: AiChatMessageEntity,
    onActionClick: (String) -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.FRENCH)
    val timeStr = timeFormat.format(Date(message.timestamp))

    if (message.isFromUser) {
        // User Message (Right-aligned, Emerald Bubble)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.widthIn(max = 290.dp)
            ) {
                Surface(
                    color = AilEmerald,
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 4.dp
                    ),
                    shadowElevation = 1.dp
                ) {
                    Text(
                        text = message.messageText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 2.dp, end = 4.dp)
                )
            }
        }
    } else {
        // AI Model Message (Left-aligned, Clean White Card with Mint borders)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(AilEmerald, AilLeafGreen)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.widthIn(max = 310.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 18.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 18.dp
                    ),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = message.messageText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            lineHeight = 20.sp
                        )

                        // If the message contains keywords, show shortcut pills
                        if (message.messageText.contains("reboisement", ignoreCase = true) ||
                            message.messageText.contains("action", ignoreCase = true)
                        ) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                modifier = Modifier
                                    .clickable { onActionClick("ACTIONS") },
                                color = AilEmeraldLight,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Eco,
                                        contentDescription = null,
                                        tint = AilEmeraldDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Voir les actions de reboisement",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AilEmeraldDark
                                    )
                                }
                            }
                        }

                        if (message.messageText.contains("formation", ignoreCase = true) ||
                            message.messageText.contains("métier", ignoreCase = true)
                        ) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                modifier = Modifier
                                    .clickable { onActionClick("TRAININGS") },
                                color = AilEmeraldLight,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = AilEmeraldDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Découvrir les formations gratuites",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AilEmeraldDark
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    text = "AWA • $timeStr",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 2.dp, start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AiThinkingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(AilEmerald),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = AilEmerald
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AWA réfléchit à votre réponse...",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}
