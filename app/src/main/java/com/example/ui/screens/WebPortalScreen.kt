package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.remote.CloudSyncEngine
import com.example.ui.components.AilWebPortalGenerator
import com.example.ui.theme.AilEmerald
import com.example.ui.theme.AilEmeraldDark
import com.example.ui.theme.AilEmeraldLight
import com.example.ui.theme.AilForestDark
import com.example.ui.theme.AilGold
import com.example.ui.theme.AilMintLight
import com.example.ui.theme.AilMintPillBg
import com.example.ui.viewmodel.AilViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPortalScreen(
    viewModel: AilViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val syncStatus by viewModel.cloudSyncStatus.collectAsStateWithLifecycle()
    val orgMap by viewModel.orgInfoMap.collectAsStateWithLifecycle()
    val allNews by viewModel.publishedNews.collectAsStateWithLifecycle()
    val allActions by viewModel.allActions.collectAsStateWithLifecycle()
    val allProjects by viewModel.allProjects.collectAsStateWithLifecycle()
    val allTrainings by viewModel.allTrainings.collectAsStateWithLifecycle()
    val impactMetrics by viewModel.impactMetrics.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Portail Web Officiel, 1: Liens & Partage Public
    var webLoadingProgress by remember { mutableFloatStateOf(0f) }
    var isWebLoading by remember { mutableStateOf(false) }
    var isRemoteMode by remember { mutableStateOf(false) } // false: Local Rich Portal, true: Remote URL
    var remoteLoadFailed by remember { mutableStateOf(false) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    val portalUrl = orgMap["org_website_url"] ?: CloudSyncEngine.WEB_PORTAL_URL
    val domainUrl = orgMap["org_website_domain"] ?: CloudSyncEngine.OFFICIAL_DOMAIN

    // Generate full HTML content dynamically with current state
    val htmlContent = remember(orgMap, allNews, allActions, allProjects, allTrainings, impactMetrics) {
        AilWebPortalGenerator.generatePortalHtml(
            orgMap = orgMap,
            newsList = allNews,
            actionsList = allActions,
            projectsList = allProjects,
            trainingsList = allTrainings,
            metricsList = impactMetrics
        )
    }

    // Function to load the active view
    fun reloadWeb() {
        webViewInstance?.let { webView ->
            if (isRemoteMode) {
                remoteLoadFailed = false
                webView.loadUrl(portalUrl)
            } else {
                webView.loadDataWithBaseURL("https://www.ongail4c.com", htmlContent, "text/html", "UTF-8", null)
            }
        }
    }

    LaunchedEffect(isRemoteMode, htmlContent) {
        reloadWeb()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // En-tête du Portail Web
        Surface(
            color = AilForestDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                            .testTag("web_portal_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = AilMintLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Site Web AIL4C en Ligne",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = if (isRemoteMode) "Mode : Serveur distant ($domainUrl)" else "Mode : Portail Web Officiel Intégré 24h/24",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    // Bouton Partager
                    IconButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "🌱 Visitez le portail Web officiel de l'ONG AIL4C (Association Ivoirienne de Lutte contre le Changement Climatique) accessible 24h/24 :\n$portalUrl"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Partager le site web AIL4C"))
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Partager",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Bouton Ouvrir dans le navigateur externe
                    IconButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(portalUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                viewModel.showToast("Lien Web : $portalUrl")
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(AilEmerald, CircleShape)
                            .testTag("web_portal_open_browser_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = "Ouvrir dans le navigateur",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Onglets principaux
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White.copy(alpha = 0.12f),
                    contentColor = Color.White,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Navigateur & Site Web",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) AilMintLight else Color.White.copy(alpha = 0.7f)
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Partage & Liens Publics",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) AilMintLight else Color.White.copy(alpha = 0.7f)
                            )
                        }
                    )
                }
            }
        }

        if (selectedTab == 0) {
            // Onglet 1: Navigateur Web interactif
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    // Mode switch and status bar
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(if (syncStatus.isOnline) Color(0xFF2ECC71) else Color(0xFFE74C3C), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isRemoteMode) portalUrl else "Portail Web Officiel AIL4C (Actif)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Mode Toggle Button
                                Surface(
                                    color = if (isRemoteMode) AilEmeraldLight else AilMintPillBg,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.clickable {
                                        isRemoteMode = !isRemoteMode
                                        if (isRemoteMode) {
                                            viewModel.showToast("Connexion au serveur distant...")
                                        } else {
                                            viewModel.showToast("Affichage du portail officiel local...")
                                        }
                                    }
                                ) {
                                    Text(
                                        text = if (isRemoteMode) "Basculer vers Portail Local" else "Tester Serveur Distant",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AilEmeraldDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { reloadWeb() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Actualiser",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Alert if remote load failed
                    AnimatedVisibility(visible = remoteLoadFailed) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFB45309),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Serveur distant non joignable. Basculement automatique sur le portail Web officiel local interactif.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF92400E),
                                    lineHeight = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    if (isWebLoading) {
                        LinearProgressIndicator(
                            progress = { webLoadingProgress },
                            modifier = Modifier.fillMaxWidth(),
                            color = AilEmerald,
                            trackColor = AilMintPillBg
                        )
                    }

                    // Web View hosting the full portal
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.loadWithOverviewMode = true
                                settings.useWideViewPort = true
                                settings.setSupportZoom(true)
                                settings.builtInZoomControls = true
                                settings.displayZoomControls = false

                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                        super.onPageStarted(view, url, favicon)
                                        isWebLoading = true
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        super.onPageFinished(view, url)
                                        isWebLoading = false
                                    }

                                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                        super.onReceivedError(view, request, error)
                                        if (request?.isForMainFrame == true && isRemoteMode) {
                                            remoteLoadFailed = true
                                            isRemoteMode = false
                                            // Fallback to rich built-in interactive portal
                                            loadDataWithBaseURL("https://www.facebook.com/share/1GvChYFAMY/", htmlContent, "text/html", "UTF-8", null)
                                        }
                                    }

                                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                        val url = request?.url?.toString() ?: return false
                                        return if (url.startsWith("tel:") || url.startsWith("mailto:") || url.startsWith("https://wa.me") || url.startsWith("whatsapp://")) {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                ctx.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(ctx, "Lien : $url", Toast.LENGTH_SHORT).show()
                                            }
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                }

                                webChromeClient = object : WebChromeClient() {
                                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                        webLoadingProgress = newProgress / 100f
                                        if (newProgress >= 100) isWebLoading = false
                                    }
                                }

                                loadDataWithBaseURL("https://www.ongail4c.com", htmlContent, "text/html", "UTF-8", null)
                                webViewInstance = this
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("embedded_web_portal_view")
                    )
                }
            }
        } else {
            // Onglet 2: Liens Web & Partage Public pour les utilisateurs externes
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Card explicative
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AilMintPillBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = AilEmerald,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Accès Web Sans Installation",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AilForestDark
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Toutes les personnes n'ayant pas installé l'application peuvent accéder exactement aux mêmes contenus, actualités, formations, projets et soumissions en temps réel depuis n'importe quel navigateur Web (Chrome, Safari, Firefox, Edge sur PC, Mac, iPhone ou Android).",
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = AilEmeraldDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Carte du Lien Principal
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Lien Officiel Actif (Gratuit & Accessible à tous)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ce lien officiel fonctionne sur tous les téléphones et ordinateurs sans frais :",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = portalUrl,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AilEmerald,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("AIL4C Web Link", portalUrl)
                                        clipboard.setPrimaryClip(clip)
                                        viewModel.showToast("📋 Lien officiel copié dans le presse-papier !")
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copier le lien",
                                        tint = AilEmerald,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "🌱 Découvrez les actions, projets et formations de l'ONG AIL4C (Lutte contre le Changement Climatique et le Chômage) :\n👉 $portalUrl\n📞 Contact officiel : +225 07 89 71 02 89"
                                        )
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Partager la plateforme AIL4C")
                                    context.startActivity(shareIntent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AilEmerald),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("share_web_portal_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Partager", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(portalUrl))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        viewModel.showToast("Lien : $portalUrl")
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInBrowser,
                                    contentDescription = null,
                                    tint = AilEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ouvrir", color = AilEmerald, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Bouton WhatsApp direct gratuit
                        Button(
                            onClick = {
                                try {
                                    val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/2250789710289?text=Bonjour%20ONG%20AIL4C%2C%20je%20souhaite%20des%20informations"))
                                    context.startActivity(waIntent)
                                } catch (e: Exception) {
                                    viewModel.showToast("WhatsApp : +225 07 89 71 02 89")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Assistance & Informations WhatsApp Direct", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Information de synchronisation 24h/24
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = AilGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Fonctionnement Réseau & Sync 24h/24",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2ECC71),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Connexion internet permanente pour la mise à jour des données",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2ECC71),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Synchronisation bidirectionnelle instantanée (Web <-> App)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2ECC71),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mises à jour administrateur diffusées en temps réel 24h/24",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
