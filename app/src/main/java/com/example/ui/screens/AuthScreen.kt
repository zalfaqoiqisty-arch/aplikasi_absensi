package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.auth.onGoogleSignInClicked
import com.example.data.RegistrationDraft
import com.example.ui.components.AuroraGlassBackground
import com.example.ui.components.GlassBirthDatePickerDialog
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPrimaryButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.GlassSegmentedControl
import com.example.ui.components.GlassStatusBanner
import com.example.ui.components.GlassTextField
import com.example.ui.theme.AuroraIndigo
import com.example.ui.theme.AuroraMint
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.MidnightCanvas

@Composable
fun AuthScreen(
    onPrepareRegistrationDraft: (RegistrationDraft) -> Unit,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var selectedTab by rememberSaveable { mutableIntStateOf(0) } // 0 = Masuk, 1 = Registrasi
    var isLoading by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    // Registration personal data fields
    var fullName by rememberSaveable { mutableStateOf("") }
    var emailHint by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var birthDate by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var occupation by rememberSaveable { mutableStateOf("") }
    var bio by rememberSaveable { mutableStateOf("") }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        GlassBirthDatePickerDialog(
            onDateSelected = { selected -> birthDate = selected },
            onDismiss = { showDatePicker = false }
        )
    }

    AuroraGlassBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Hero Glass Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(158.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.28f),
                            shape = RoundedCornerShape(28.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_glass_hero_1795408723236),
                        contentDescription = "Ilustrasi latar glassmorphism AuraID",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MidnightCanvas.copy(alpha = 0.25f),
                                        MidnightCanvas.copy(alpha = 0.78f)
                                    )
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color.White.copy(alpha = 0.16f))
                                .border(
                                    1.dp,
                                    ElectricCyan.copy(alpha = 0.65f),
                                    RoundedCornerShape(18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shield,
                                contentDescription = "Logo AuraID",
                                tint = ElectricCyan,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.headlineMedium,
                                color = GlassTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.tagline_brand),
                                style = MaterialTheme.typography.bodySmall,
                                color = GlassTextSecondary
                            )
                        }
                    }
                }

                // Segmented Switcher: Masuk | Registrasi
                GlassSegmentedControl(
                    options = listOf(
                        stringResource(R.string.tab_login),
                        stringResource(R.string.tab_register)
                    ),
                    selectedIndex = selectedTab,
                    onSelect = { index ->
                        selectedTab = index
                        authError = null
                        fullNameError = null
                    },
                    modifier = Modifier.testTag("auth_mode_switcher")
                )

                if (!authError.isNullOrBlank()) {
                    GlassStatusBanner(
                        message = authError!!,
                        isError = true,
                        onDismiss = { authError = null }
                    )
                }

                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "authTabContent"
                ) { tabIndex ->
                    if (tabIndex == 0) {
                        // MENU LOGIN
                        GlassCard(
                            contentPadding = PaddingValues(22.dp)
                        ) {
                            Text(
                                text = "Menu Masuk",
                                style = MaterialTheme.typography.headlineSmall,
                                color = GlassTextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Masuk secara instan dan aman ke akun Anda menggunakan Email Google yang terhubung dengan Firebase.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GlassTextSecondary
                            )
                            Spacer(modifier = Modifier.height(18.dp))

                            AuthFeatureRow(
                                icon = Icons.Rounded.VerifiedUser,
                                title = "Autentikasi Email Terverifikasi",
                                subtitle = "Dilindungi Firebase Auth & Credential Manager"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            AuthFeatureRow(
                                icon = Icons.Rounded.CloudDone,
                                title = "Profil Data Diri Tersinkronisasi",
                                subtitle = "Data pribadi tersimpan aman di Cloud Firestore"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            AuthFeatureRow(
                                icon = Icons.Rounded.Badge,
                                title = "Kartu Identitas Digital Modern",
                                subtitle = "Akses dan perbarui biodata kapan saja"
                            )

                            Spacer(modifier = Modifier.height(22.dp))

                            GlassPrimaryButton(
                                text = stringResource(R.string.auth_google_login_btn),
                                icon = Icons.AutoMirrored.Rounded.Login,
                                isLoading = isLoading,
                                onClick = {
                                    authError = null
                                    isLoading = true
                                    onPrepareRegistrationDraft(RegistrationDraft())
                                    onGoogleSignInClicked(
                                        context = context,
                                        credentialManager = credentialManager,
                                        onAuthSuccess = {
                                            isLoading = false
                                            onAuthSuccess()
                                        },
                                        onAuthError = { msg ->
                                            isLoading = false
                                            authError = msg
                                        },
                                        scope = scope,
                                        onAuthCancelled = { isLoading = false }
                                    )
                                },
                                modifier = Modifier.testTag("login_google_button")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            GlassSecondaryButton(
                                text = "Pengguna Baru? Isi Form Registrasi",
                                icon = Icons.Rounded.PersonAdd,
                                tint = ElectricCyan,
                                onClick = {
                                    selectedTab = 1
                                    authError = null
                                },
                                modifier = Modifier.testTag("switch_to_register_button")
                            )
                        }
                    } else {
                        // MENU REGISTRASI + DATA DIRI
                        GlassCard(
                            contentPadding = PaddingValues(22.dp)
                        ) {
                            Text(
                                text = "Registrasi Data Diri",
                                style = MaterialTheme.typography.headlineSmall,
                                color = GlassTextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tambahkan data diri yang akan didaftarkan, kemudian hubungkan dengan akun Email Google Anda.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GlassTextSecondary
                            )
                            Spacer(modifier = Modifier.height(18.dp))

                            GlassTextField(
                                value = fullName,
                                onValueChange = {
                                    fullName = it
                                    if (fullNameError != null) fullNameError = null
                                },
                                label = "Nama Lengkap *",
                                placeholder = "Contoh: Zalfa Qoiqisty",
                                leadingIcon = Icons.Rounded.Person,
                                isError = fullNameError != null,
                                errorText = fullNameError,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier.testTag("reg_fullname_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            GlassTextField(
                                value = emailHint,
                                onValueChange = { emailHint = it },
                                label = "Email Kontak (Disinkronkan dg Google)",
                                placeholder = "nama@gmail.com",
                                leadingIcon = Icons.Rounded.AlternateEmail,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier.testTag("reg_email_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            GlassTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = "Nomor Telepon",
                                placeholder = "+62 812-3456-7890",
                                leadingIcon = Icons.Rounded.Phone,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier.testTag("reg_phone_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            GlassTextField(
                                value = birthDate,
                                onValueChange = { birthDate = it },
                                label = "Tanggal Lahir",
                                placeholder = "Pilih atau ketik tanggal lahir",
                                leadingIcon = Icons.Rounded.CalendarMonth,
                                trailingIcon = {
                                    IconButton(
                                        onClick = { showDatePicker = true },
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .testTag("reg_birthdate_picker")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.CalendarMonth,
                                            contentDescription = "Pilih tanggal lahir",
                                            tint = ElectricCyan
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                modifier = Modifier.testTag("reg_birthdate_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            GlassTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = "Kota / Domisili",
                                placeholder = "Contoh: Jakarta Selatan",
                                leadingIcon = Icons.Rounded.LocationCity,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier.testTag("reg_city_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            GlassTextField(
                                value = occupation,
                                onValueChange = { occupation = it },
                                label = "Pekerjaan / Profesi",
                                placeholder = "Contoh: UI/UX Designer",
                                leadingIcon = Icons.Rounded.Work,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier.testTag("reg_occupation_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            GlassTextField(
                                value = bio,
                                onValueChange = { bio = it.take(240) },
                                label = "Bio Singkat",
                                placeholder = "Ceritakan sedikit tentang diri Anda...",
                                leadingIcon = Icons.Rounded.EditNote,
                                singleLine = false,
                                maxLines = 3,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier.testTag("reg_bio_input")
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            GlassPrimaryButton(
                                text = stringResource(R.string.auth_google_register_btn),
                                icon = Icons.Rounded.PersonAdd,
                                isLoading = isLoading,
                                onClick = {
                                    val cleanName = fullName.trim()
                                    if (cleanName.length !in 2..80) {
                                        fullNameError = context.getString(R.string.error_invalid_fullname)
                                        return@GlassPrimaryButton
                                    }
                                    authError = null
                                    isLoading = true
                                    onPrepareRegistrationDraft(
                                        RegistrationDraft(
                                            fullName = cleanName,
                                            emailHint = emailHint,
                                            phoneNumber = phoneNumber,
                                            birthDate = birthDate,
                                            city = city,
                                            occupation = occupation,
                                            bio = bio
                                        )
                                    )
                                    onGoogleSignInClicked(
                                        context = context,
                                        credentialManager = credentialManager,
                                        onAuthSuccess = {
                                            isLoading = false
                                            onAuthSuccess()
                                        },
                                        onAuthError = { msg ->
                                            isLoading = false
                                            authError = msg
                                        },
                                        scope = scope,
                                        onAuthCancelled = { isLoading = false }
                                    )
                                },
                                modifier = Modifier.testTag("register_google_button")
                            )
                        }
                    }
                }

                // Footer note
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.05f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.auth_email_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = GlassTextMuted,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AuthFeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            ElectricCyan.copy(alpha = 0.25f),
                            AuroraIndigo.copy(alpha = 0.25f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AuroraMint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = GlassTextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = GlassTextSecondary
            )
        }
    }
}
