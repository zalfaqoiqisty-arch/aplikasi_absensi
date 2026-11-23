package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.auth.signOutUser
import com.example.data.RegistrationDraft
import com.example.ui.ActionBannerState
import com.example.ui.components.AuroraGlassBackground
import com.example.ui.components.GlassBirthDatePickerDialog
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPrimaryButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.GlassStatusBanner
import com.example.ui.components.GlassTextField
import com.example.ui.theme.AuroraMint
import com.example.ui.theme.CoralError
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary

@Composable
fun CompleteRegistrationScreen(
    initialDraft: RegistrationDraft,
    googleEmail: String,
    googleDisplayName: String,
    googlePhotoUrl: String?,
    actionState: ActionBannerState,
    onDismissBanner: () -> Unit,
    onSubmitRegistration: (RegistrationDraft, String, String?) -> Unit,
    onSignedOut: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var fullName by rememberSaveable {
        mutableStateOf(initialDraft.fullName.ifBlank { googleDisplayName })
    }
    var phoneNumber by rememberSaveable { mutableStateOf(initialDraft.phoneNumber) }
    var birthDate by rememberSaveable { mutableStateOf(initialDraft.birthDate) }
    var city by rememberSaveable { mutableStateOf(initialDraft.city) }
    var occupation by rememberSaveable { mutableStateOf(initialDraft.occupation) }
    var bio by rememberSaveable { mutableStateOf(initialDraft.bio) }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val resolvedEmail = googleEmail.ifBlank { initialDraft.emailHint }

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
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Verified Google Account Header Pill
                GlassCard(
                    cornerRadius = 22.dp,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AuroraMint.copy(alpha = 0.18f))
                                .border(1.dp, AuroraMint.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Verified,
                                contentDescription = "Akun Google terhubung",
                                tint = AuroraMint,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Email Google Terhubung",
                                style = MaterialTheme.typography.labelLarge,
                                color = AuroraMint
                            )
                            Text(
                                text = resolvedEmail.ifBlank { "Akun Google Aktif" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = GlassTextPrimary
                            )
                        }
                    }
                }

                if (!actionState.message.isNullOrBlank()) {
                    GlassStatusBanner(
                        message = actionState.message,
                        isError = actionState.isError,
                        onDismiss = onDismissBanner
                    )
                }

                GlassCard(contentPadding = PaddingValues(22.dp)) {
                    Text(
                        text = "Lengkapi Data Diri",
                        style = MaterialTheme.typography.headlineSmall,
                        color = GlassTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Konfirmasi data diri Anda untuk disimpan ke profil Cloud Firestore AuraID.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GlassTextSecondary
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    GlassTextField(
                        value = resolvedEmail,
                        onValueChange = {},
                        label = "Email Terdaftar",
                        leadingIcon = Icons.Rounded.AlternateEmail,
                        readOnly = true,
                        modifier = Modifier.testTag("complete_reg_email_field")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            if (fullNameError != null) fullNameError = null
                        },
                        label = "Nama Lengkap *",
                        placeholder = "Masukkan nama lengkap Anda",
                        leadingIcon = Icons.Rounded.Person,
                        isError = fullNameError != null,
                        errorText = fullNameError,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.testTag("complete_reg_fullname_input")
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
                        modifier = Modifier.testTag("complete_reg_phone_input")
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
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CalendarMonth,
                                    contentDescription = "Pilih tanggal lahir",
                                    tint = ElectricCyan
                                )
                            }
                        },
                        modifier = Modifier.testTag("complete_reg_birthdate_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = "Kota / Domisili",
                        placeholder = "Contoh: Bandung",
                        leadingIcon = Icons.Rounded.LocationCity,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.testTag("complete_reg_city_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = occupation,
                        onValueChange = { occupation = it },
                        label = "Pekerjaan / Profesi",
                        placeholder = "Contoh: Software Engineer",
                        leadingIcon = Icons.Rounded.Work,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.testTag("complete_reg_occupation_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = bio,
                        onValueChange = { bio = it.take(240) },
                        label = "Bio Singkat",
                        placeholder = "Deskripsi singkat tentang Anda...",
                        leadingIcon = Icons.Rounded.EditNote,
                        singleLine = false,
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.testTag("complete_reg_bio_input")
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    GlassPrimaryButton(
                        text = "Simpan & Aktifkan Profil",
                        icon = Icons.Rounded.CheckCircle,
                        isLoading = actionState.isSubmitting,
                        onClick = {
                            val cleanName = fullName.trim()
                            if (cleanName.length !in 2..80) {
                                fullNameError = "Nama lengkap minimal 2 karakter (maks. 80)."
                                return@GlassPrimaryButton
                            }
                            onSubmitRegistration(
                                RegistrationDraft(
                                    fullName = cleanName,
                                    emailHint = resolvedEmail,
                                    phoneNumber = phoneNumber,
                                    birthDate = birthDate,
                                    city = city,
                                    occupation = occupation,
                                    bio = bio
                                ),
                                resolvedEmail,
                                googlePhotoUrl
                            )
                        },
                        modifier = Modifier.testTag("complete_registration_submit")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassSecondaryButton(
                        text = "Ganti Akun Google",
                        icon = Icons.AutoMirrored.Rounded.Logout,
                        tint = CoralError,
                        onClick = {
                            signOutUser(
                                credentialManager = credentialManager,
                                scope = scope,
                                onSignOutComplete = onSignedOut
                            )
                        },
                        modifier = Modifier.testTag("registration_sign_out_button")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
