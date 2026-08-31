package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AilEmerald
import com.example.ui.theme.AilEmeraldDark
import com.example.ui.theme.AilEmeraldLight
import com.example.ui.theme.AilLeafGreen
import com.example.ui.theme.AilMintBackground
import com.example.ui.viewmodel.AilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AilViewModel,
    onBack: (() -> Unit)? = null,
    initialRegisterMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    var isRegisterMode by remember { mutableStateOf(initialRegisterMode) }
    var selectedMethodTab by remember { mutableIntStateOf(0) } // 0 = Phone, 1 = Email

    // Form inputs
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Bouaké") }
    var quartier by remember { mutableStateOf("Commerce") }

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AilMintBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isRegisterMode) "Créer un compte Éco-Citoyen" else "Authentification Obligatoire",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("auth_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour",
                                tint = Color.Black
                            )
                        }
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Logo & Mission
            Box(
                modifier = Modifier
                    .size(68.dp)
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
                    contentDescription = "AIL4C Logo",
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ONG AIL4C CÔTE D'IVOIRE",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = AilEmeraldDark
            )

            Text(
                text = "Pour accéder à l'application, l'authentification est obligatoire. Les nouveaux utilisateurs doivent impérativement s'inscrire.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Cloud Database Badge Indicator
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = AilEmeraldLight.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AilEmerald.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = AilEmeraldDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Google Cloud & Firebase Database • Sécurité Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = AilEmeraldDark,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mode Selector: Inscription vs Connexion
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    onClick = { isRegisterMode = true },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("auth_mode_register_tab"),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isRegisterMode) AilEmerald else Color.Transparent
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "S'inscrire (Nouveau)",
                            fontWeight = if (isRegisterMode) FontWeight.Bold else FontWeight.Medium,
                            color = if (isRegisterMode) Color.White else Color.DarkGray,
                            fontSize = 13.sp
                        )
                    }
                }

                Surface(
                    onClick = { isRegisterMode = false },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("auth_mode_login_tab"),
                    shape = RoundedCornerShape(12.dp),
                    color = if (!isRegisterMode) AilEmerald else Color.Transparent
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Se connecter",
                            fontWeight = if (!isRegisterMode) FontWeight.Bold else FontWeight.Medium,
                            color = if (!isRegisterMode) Color.White else Color.DarkGray,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Auth Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Method Tabs (Phone vs Email)
                    TabRow(
                        selectedTabIndex = selectedMethodTab,
                        containerColor = AilMintBackground,
                        contentColor = AilEmerald,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedMethodTab]),
                                color = AilEmerald,
                                height = 3.dp
                            )
                        },
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedMethodTab == 0,
                            onClick = { selectedMethodTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (selectedMethodTab == 0) AilEmerald else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Téléphone 🇨🇮",
                                        fontWeight = if (selectedMethodTab == 0) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedMethodTab == 0) AilEmerald else Color.Gray
                                    )
                                }
                            }
                        )

                        Tab(
                            selected = selectedMethodTab == 1,
                            onClick = { selectedMethodTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (selectedMethodTab == 1) AilEmerald else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Email ✉️",
                                        fontWeight = if (selectedMethodTab == 1) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedMethodTab == 1) AilEmerald else Color.Gray
                                    )
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Registration extra fields (Full Name, City, Quartier)
                    AnimatedVisibility(visible = isRegisterMode) {
                        Column {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Nom et Prénoms complets *") },
                                placeholder = { Text("Ex: Kouamé Sylvain") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = AilEmerald)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_name_input"),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AilEmerald,
                                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.8f)
                                ),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = city,
                                    onValueChange = { city = it },
                                    label = { Text("Ville *") },
                                    leadingIcon = {
                                        Icon(Icons.Default.LocationCity, contentDescription = null, tint = AilEmerald)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("auth_city_input"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AilEmerald,
                                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.8f)
                                    ),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                OutlinedTextField(
                                    value = quartier,
                                    onValueChange = { quartier = it },
                                    label = { Text("Quartier") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("auth_quartier_input"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AilEmerald,
                                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.8f)
                                    ),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // Main input field (Phone or Email)
                    if (selectedMethodTab == 0) {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Numéro de téléphone (+225) *") },
                            placeholder = { Text("Ex: 07 89 71 02 89") },
                            leadingIcon = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 8.dp, end = 4.dp)
                                ) {
                                    Text(
                                        text = "+225",
                                        fontWeight = FontWeight.Bold,
                                        color = AilEmeraldDark,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(20.dp)
                                            .background(Color.LightGray)
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_phone_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AilEmerald,
                                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.8f)
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                    } else {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Adresse Email *") },
                            placeholder = { Text("Ex: membre@ongail4c.com") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = AilEmerald)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AilEmerald,
                                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.8f)
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(if (isRegisterMode) "Créer un mot de passe *" else "Mot de passe *") },
                        placeholder = { Text("4 caractères minimum") },
                        leadingIcon = {
                            Icon(Icons.Default.Key, contentDescription = null, tint = AilEmerald)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Afficher mot de passe",
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AilEmerald,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.8f)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )

                    // Confirm password for registration
                    if (isRegisterMode) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirmer le mot de passe *") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = AilEmerald)
                            },
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Afficher mot de passe",
                                        tint = Color.Gray
                                    )
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_confirm_password_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AilEmerald,
                                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.8f)
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            val authType = if (selectedMethodTab == 0) "PHONE" else "EMAIL"
                            val identifier = if (selectedMethodTab == 0) phoneNumber.trim() else email.trim()

                            if (isRegisterMode) {
                                if (fullName.isBlank()) {
                                    viewModel.showToast("Veuillez renseigner votre nom et prénoms.")
                                } else if (identifier.isBlank()) {
                                    viewModel.showToast(if (selectedMethodTab == 0) "Veuillez renseigner votre numéro de téléphone." else "Veuillez renseigner votre email.")
                                } else if (password.length < 4) {
                                    viewModel.showToast("Le mot de passe doit contenir au moins 4 caractères.")
                                } else if (password != confirmPassword) {
                                    viewModel.showToast("Les deux mots de passe ne correspondent pas.")
                                } else {
                                    viewModel.registerUser(
                                        fullName = fullName,
                                        identifier = identifier,
                                        authType = authType,
                                        password = password,
                                        city = city,
                                        quartier = quartier,
                                        onSuccess = { onBack?.invoke() }
                                    )
                                }
                            } else {
                                if (identifier.isBlank()) {
                                    viewModel.showToast(if (selectedMethodTab == 0) "Veuillez saisir votre numéro de téléphone." else "Veuillez saisir votre email.")
                                } else {
                                    viewModel.loginUser(
                                        identifier = identifier,
                                        authType = authType,
                                        password = password,
                                        onSuccess = { onBack?.invoke() }
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_submit_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AilEmerald)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRegisterMode) "Créer mon compte éco-citoyen" else "Se connecter à mon compte",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Toggle Register vs Login
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isRegisterMode) "Vous avez déjà un compte ?" else "Nouveau sur AIL4C ?",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isRegisterMode) "Se connecter" else "S'inscrire impérativement",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = AilEmeraldDark,
                            modifier = Modifier
                                .clickable { isRegisterMode = !isRegisterMode }
                                .padding(4.dp)
                                .testTag("toggle_register_mode_button")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Security & Privacy Info
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AilEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Authentification chiffrée et synchronisée avec la base de données Google Cloud & Firebase AIL4C.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.DarkGray,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
