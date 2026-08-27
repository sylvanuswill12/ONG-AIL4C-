package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AilBlueAccent
import com.example.ui.theme.AilEmerald
import com.example.ui.theme.AilEmeraldDark
import com.example.ui.theme.AilEmeraldLight
import com.example.ui.theme.AilForestDark
import com.example.ui.theme.AilGold
import com.example.ui.theme.AilLeafGreen
import com.example.ui.theme.AilMintLight

const val AIL_WEB_PORTAL_URL = "https://www.facebook.com/share/1GvChYFAMY/"
const val AIL_WEB_DOMAIN = "facebook.com/share/1GvChYFAMY"

/**
 * Card for displaying the Web Browser / 24h Cloud access prompt
 */
@Composable
fun WebAccessBannerCard(
    modifier: Modifier = Modifier,
    onOpenDialog: () -> Unit,
    onOpenWebPortal: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (onOpenWebPortal != null) {
                    onOpenWebPortal()
                } else {
                    onOpenDialog()
                }
            }
            .testTag("web_access_banner_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, AilEmerald.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(AilEmerald, AilBlueAccent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Site Web 24h/24 & Sans App",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AilForestDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AilEmeraldLight
                        ) {
                            Text(
                                text = "En direct",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AilEmeraldDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Visitez et utilisez l'application directement dans votre navigateur web.",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // URL badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AilMintLight.copy(alpha = 0.5f),
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
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = AilEmeraldDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AIL_WEB_PORTAL_URL,
                            fontSize = 11.sp,
                            color = AilEmeraldDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Ouvrir options",
                        tint = AilEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("AIL4C Web Portal", AIL_WEB_PORTAL_URL)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Lien web copié dans le presse-papier !", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AilEmerald)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = AilEmeraldDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copier", fontSize = 12.sp, color = AilEmeraldDark, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        if (onOpenWebPortal != null) {
                            onOpenWebPortal()
                        } else {
                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(AIL_WEB_PORTAL_URL))
                                context.startActivity(browserIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Lien Web : $AIL_WEB_PORTAL_URL", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AilEmerald)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ouvrir Web", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Detailed Modal Dialog explaining Web Access, 24/7 synchronization, and Sharing
 */
@Composable
fun WebAccessDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(AilEmerald),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Accès Web & Sync 24h/24",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AilForestDark
                    )
                    Text(
                        text = "Accessible sans installer l'application",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "L'application AIL4C est entièrement connectée et fonctionne 24h sur 24. Les utilisateurs peuvent y accéder depuis leur navigateur web sans aucun téléchargement, tout en partageant les mêmes données en temps réel que l'application installée.",
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Feature Highlights
                FeatureItem(
                    icon = Icons.Default.Public,
                    title = "Accès universel depuis tout navigateur",
                    description = "Accessible sur PC, Mac, iPhone, iPad, Linux et Android via Chrome, Safari, Firefox, Edge."
                )

                Spacer(modifier = Modifier.height(8.dp))

                FeatureItem(
                    icon = Icons.Default.Sync,
                    title = "Synchronisation temps réel 24h/24",
                    description = "Toutes les actions créées, actualités, formations, candidatures et données d'impact sont instantanément mises à jour sur le Web et sur l'App mobile."
                )

                Spacer(modifier = Modifier.height(8.dp))

                FeatureItem(
                    icon = Icons.Default.CloudDone,
                    title = "Connexion permanente requise",
                    description = "L'application utilise la connexion Internet pour garantir la fiabilité des données et permettre aux bénévoles d'interagir en direct."
                )

                Spacer(modifier = Modifier.height(14.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Lien du Site Web / Application en ligne :",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AilEmeraldDark
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AilMintLight,
                    border = BorderStroke(1.dp, AilEmerald.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = AIL_WEB_PORTAL_URL,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AilForestDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("AIL4C Web Portal", AIL_WEB_PORTAL_URL)
                                    clipboard.setPrimaryClip(clip)
                                    copied = true
                                    Toast.makeText(context, "Lien copié !", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = if (copied) AilEmeraldDark else AilEmerald),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (copied) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (copied) "Copié !" else "Copier le lien", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Visitez l'application AIL4C en ligne 24h/24")
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Découvrez et rejoignez les actions citoyennes et écologiques d'AIL4C Bouaké en ligne sans installer d'application : $AIL_WEB_PORTAL_URL"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Partager le lien du site Web"))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = AilEmeraldDark)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Partager", fontSize = 12.sp, color = AilEmeraldDark, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(AIL_WEB_PORTAL_URL))
                                context.startActivity(browserIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Impossible d'ouvrir le navigateur", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AilEmerald)
                    ) {
                        Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Visiter", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AilForestDark)
            ) {
                Text("Fermer", color = Color.White, fontSize = 13.sp)
            }
        }
    )
}

@Composable
private fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(AilEmeraldLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AilEmeraldDark,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AilForestDark
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color.DarkGray,
                lineHeight = 15.sp
            )
        }
    }
}
