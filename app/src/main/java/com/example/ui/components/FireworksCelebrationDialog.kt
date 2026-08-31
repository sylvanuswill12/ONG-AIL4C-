package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WbSunny
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserBadgeEntity
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class FireworkParticle(
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float,
    var alpha: Float = 1.0f,
    val maxLife: Float = 1.0f,
    var age: Float = 0f
)

private data class ConfettiRibbon(
    var x: Float,
    var y: Float,
    val speedY: Float,
    val speedX: Float,
    val color: Color,
    val width: Float,
    val height: Float,
    var angle: Float = 0f
)

@Composable
fun FireworksCelebrationDialog(
    badge: UserBadgeEntity,
    onDismiss: () -> Unit,
    onShare: (() -> Unit)? = null
) {
    val scaleAnim = remember { Animatable(0.2f) }
    val infiniteTransition = rememberInfiniteTransition(label = "badge_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val particles = remember { mutableStateListOf<FireworkParticle>() }
    val confettis = remember { mutableStateListOf<ConfettiRibbon>() }

    // Particle Colors
    val celebrationColors = remember {
        listOf(
            Color(0xFF10B981), // Emerald
            Color(0xFFF59E0B), // Gold Amber
            Color(0xFF3B82F6), // Sky Blue
            Color(0xFFEC4899), // Pink
            Color(0xFF8B5CF6), // Purple
            Color(0xFFF97316), // Orange
            Color(0xFF14B8A6), // Teal
            Color(0xFFFFFFFF)  // White star
        )
    }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
    }

    // Launch Fireworks bursts loop
    LaunchedEffect(Unit) {
        // Initial Confettis
        for (i in 0 until 40) {
            confettis.add(
                ConfettiRibbon(
                    x = Random.nextFloat() * 1000f,
                    y = -Random.nextFloat() * 500f,
                    speedY = Random.nextFloat() * 4f + 2.5f,
                    speedX = (Random.nextFloat() - 0.5f) * 2f,
                    color = celebrationColors.random(),
                    width = Random.nextFloat() * 10f + 8f,
                    height = Random.nextFloat() * 16f + 12f,
                    angle = Random.nextFloat() * 360f
                )
            )
        }

        // Continual bursts
        while (true) {
            val burstX = Random.nextFloat() * 800f + 100f
            val burstY = Random.nextFloat() * 600f + 150f
            val burstColor = celebrationColors.random()

            val newParticles = List(35) {
                val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
                val speed = Random.nextFloat() * 6.5f + 2.5f
                FireworkParticle(
                    x = burstX,
                    y = burstY,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = if (Random.nextBoolean()) burstColor else celebrationColors.random(),
                    size = Random.nextFloat() * 5f + 3f,
                    alpha = 1.0f
                )
            }
            particles.addAll(newParticles)

            delay(380)
        }
    }

    // Physics update frame loop
    LaunchedEffect(Unit) {
        while (true) {
            // Update particles
            val iterator = particles.iterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                p.x += p.vx
                p.y += p.vy + 0.15f // gravity
                p.age += 0.03f
                p.alpha = (1.0f - p.age / p.maxLife).coerceIn(0f, 1f)
                if (p.age >= p.maxLife || p.alpha <= 0.05f) {
                    iterator.remove()
                }
            }

            // Update confettis
            for (c in confettis) {
                c.y += c.speedY
                c.x += c.speedX
                c.angle += 3f
                if (c.y > 2000f) {
                    c.y = -50f
                    c.x = Random.nextFloat() * 1000f
                }
            }

            delay(20)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xCC062519)),
            contentAlignment = Alignment.Center
        ) {
            // Fireworks Canvas Overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw confettis
                confettis.forEach { c ->
                    drawRect(
                        color = c.color.copy(alpha = 0.85f),
                        topLeft = Offset(c.x, c.y),
                        size = androidx.compose.ui.geometry.Size(c.width, c.height)
                    )
                }

                // Draw firework spark particles
                particles.forEach { p ->
                    drawCircle(
                        color = p.color.copy(alpha = p.alpha),
                        radius = p.size,
                        center = Offset(p.x, p.y)
                    )
                }
            }

            // Central Celebration Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .scale(scaleAnim.value)
                    .shadow(24.dp, RoundedCornerShape(28.dp))
                    .testTag("fireworks_celebration_dialog"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Top Celebration Pill
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Celebration,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "NOUVEAU BADGE DÉBLOQUÉ !",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color(0xFF047857)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Pulsing Glowing Badge Container
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        getTierGlowColor(badge.tierLevel),
                                        getTierColor(badge.tierLevel).copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(86.dp)
                                .shadow(12.dp, CircleShape),
                            shape = CircleShape,
                            color = getTierColor(badge.tierLevel),
                            border = androidx.compose.foundation.BorderStroke(3.dp, Color.White)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getBadgeIconVector(badge.iconKey),
                                    contentDescription = badge.title,
                                    tint = Color.White,
                                    modifier = Modifier.size(46.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Badge Title
                    Text(
                        text = badge.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Tier Level Tag
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = getTierColor(badge.tierLevel).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Niveau ${badge.tierLevel} • +${badge.requiredPoints} pts d'éco-citoyenneté",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = getTierColor(badge.tierLevel),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Badge Description
                    Text(
                        text = badge.description,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("collect_badge_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🎉 Super ! Collecter mon badge",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    if (onShare != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = onShare,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Partager ma réussite")
                        }
                    }
                }
            }
        }
    }
}

fun getTierColor(tier: String): Color {
    return when (tier.lowercase()) {
        "bronze" -> Color(0xFFCD7F32)
        "argent" -> Color(0xFF64748B)
        "or" -> Color(0xFFF59E0B)
        "platine" -> Color(0xFF06B6D4)
        "diamant" -> Color(0xFF8B5CF6)
        else -> Color(0xFF10B981)
    }
}

fun getTierGlowColor(tier: String): Color {
    return when (tier.lowercase()) {
        "bronze" -> Color(0x66CD7F32)
        "argent" -> Color(0x6694A3B8)
        "or" -> Color(0x66F59E0B)
        "platine" -> Color(0x6606B6D4)
        "diamant" -> Color(0x668B5CF6)
        else -> Color(0x6610B981)
    }
}

fun getBadgeIconVector(key: String): ImageVector {
    return when (key.lowercase()) {
        "seed", "graine" -> Icons.Default.Spa
        "guardian", "gardien" -> Icons.Default.Grade
        "planter", "planteur", "tree" -> Icons.Default.Park
        "recycle", "recyclage" -> Icons.Default.Recycling
        "solar", "soleil" -> Icons.Default.WbSunny
        "scholar", "expert" -> Icons.Default.School
        "ambassador", "ambassadeur" -> Icons.Default.Public
        "hero", "heros" -> Icons.Default.Forest
        else -> Icons.Default.EmojiEvents
    }
}
