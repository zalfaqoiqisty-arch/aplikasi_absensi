package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.StrokeCap
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import coil.compose.AsyncImage
import com.example.R
import com.example.auth.signOutUser
import com.example.data.ActivityLog
import com.example.data.RegistrationDraft
import com.example.data.UserProfile
import com.example.ui.ActionBannerState
import com.example.ui.UiState
import com.example.ui.components.AuroraGlassBackground
import com.example.ui.components.GlassBirthDatePickerDialog
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPrimaryButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.GlassStatusBanner
import com.example.ui.components.GlassTextField
import com.example.ui.theme.AuroraIndigo
import com.example.ui.theme.AuroraMint
import com.example.ui.theme.CoralError
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorderHighlight
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.MidnightCanvas
import com.example.ui.theme.MidnightSurface
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.Locale

private data class NavDestinationItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
)

@Composable
fun MainDashboardScreen(
    firebaseUser: FirebaseUser,
    profile: UserProfile,
    activityLogsState: UiState<List<ActivityLog>>,
    actionState: ActionBannerState,
    onDismissBanner: () -> Unit,
    onUpdateProfile: (RegistrationDraft) -> Unit,
    onDeleteProfile: () -> Unit,
    onSignedOut: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var isEditingProfile by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = selectedTab != 0) {
        selectedTab = 0
    }

    val navItems = listOf(
        NavDestinationItem(
            label = stringResource(R.string.nav_home),
            selectedIcon = Icons.Rounded.Home,
            unselectedIcon = Icons.Outlined.Home,
            tag = "nav_tab_home"
        ),
        NavDestinationItem(
            label = stringResource(R.string.nav_profile),
            selectedIcon = Icons.Rounded.Badge,
            unselectedIcon = Icons.Outlined.Badge,
            tag = "nav_tab_profile"
        ),
        NavDestinationItem(
            label = stringResource(R.string.nav_account),
            selectedIcon = Icons.Rounded.Security,
            unselectedIcon = Icons.Outlined.Security,
            tag = "nav_tab_account"
        )
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = MidnightSurface,
            title = {
                Text(
                    text = "Hapus Data Profil?",
                    color = GlassTextPrimary
                )
            },
            text = {
                Text(
                    text = "Data diri Anda di Firestore akan dihapus. Anda dapat mendaftarkan ulang kapan saja setelah masuk kembali.",
                    color = GlassTextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteProfile()
                    }
                ) {
                    Text("Hapus", color = CoralError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = GlassTextSecondary)
                }
            }
        )
    }

    AuroraGlassBackground {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideScreen = maxWidth >= 600.dp

            Row(modifier = Modifier.fillMaxSize()) {
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = Color.White.copy(alpha = 0.06f),
                        modifier = Modifier
                            .fillMaxHeight()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        navItems.forEachIndexed { index, item ->
                            val selected = selectedTab == index
                            NavigationRailItem(
                                selected = selected,
                                onClick = { selectedTab = index },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = ElectricCyan,
                                    selectedTextColor = ElectricCyan,
                                    unselectedIconColor = GlassTextMuted,
                                    unselectedTextColor = GlassTextMuted,
                                    indicatorColor = ElectricCyan.copy(alpha = 0.16f)
                                ),
                                modifier = Modifier.testTag(item.tag)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 680.dp)
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .imePadding()
                            .verticalScroll(rememberScrollState())
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = 16.dp,
                                bottom = if (isWideScreen) 24.dp else 110.dp
                            ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (!actionState.message.isNullOrBlank()) {
                            GlassStatusBanner(
                                message = actionState.message,
                                isError = actionState.isError,
                                onDismiss = onDismissBanner
                            )
                        }

                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "dashboardTab"
                        ) { tab ->
                            when (tab) {
                                0 -> HomeOverviewTab(
                                    profile = profile,
                                    onEditProfile = {
                                        isEditingProfile = true
                                        selectedTab = 1
                                    },
                                    onOpenActivity = { selectedTab = 2 }
                                )

                                1 -> PersonalDataTab(
                                    profile = profile,
                                    isEditing = isEditingProfile,
                                    isSubmitting = actionState.isSubmitting,
                                    onToggleEdit = { isEditingProfile = it },
                                    onSave = { draft ->
                                        onUpdateProfile(draft)
                                        isEditingProfile = false
                                    }
                                )

                                else -> AccountSecurityTab(
                                    firebaseUser = firebaseUser,
                                    activityLogsState = activityLogsState,
                                    onRequestDeleteProfile = { showDeleteDialog = true },
                                    onSignOut = {
                                        signOutUser(
                                            credentialManager = credentialManager,
                                            scope = scope,
                                            onSignOutComplete = onSignedOut
                                        )
                                    }
                                )
                            }
                        }
                    }

                    if (!isWideScreen) {
                        GlassFloatingNavBar(
                            items = navItems,
                            selectedIndex = selectedTab,
                            onSelect = { selectedTab = it },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .padding(horizontal = 20.dp, vertical = 14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeOverviewTab(
    profile: UserProfile,
    onEditProfile: () -> Unit,
    onOpenActivity: () -> Unit
) {
    val completion = profile.completionPercent()
    val joinedLabel = formatTimestamp(profile.createdAt ?: profile.updatedAt)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Header greeting
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Halo, ${profile.fullName.substringBefore(" ").ifBlank { profile.fullName }}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = GlassTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Kartu identitas digital aktif Anda",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GlassTextSecondary
                )
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = AuroraMint.copy(alpha = 0.16f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AuroraMint.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Verified,
                        contentDescription = "Terverifikasi",
                        tint = AuroraMint,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Aktif",
                        style = MaterialTheme.typography.labelMedium,
                        color = AuroraMint
                    )
                }
            }
        }

        // Digital Glass ID Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.06f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            GlassBorderHighlight,
                            ElectricCyan.copy(alpha = 0.35f),
                            GlassBorderSubtle
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
        ) {
            Column {
                // Hero strip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(104.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_glass_hero_1795408723236),
                        contentDescription = "Latar kartu identitas",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MidnightCanvas.copy(alpha = 0.65f)
                                    )
                                )
                            )
                    )
                    Text(
                        text = "AURA DIGITAL ID",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ProfileAvatar(
                            fullName = profile.fullName,
                            photoUrl = profile.photoUrl
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.fullName,
                                style = MaterialTheme.typography.titleLarge,
                                color = GlassTextPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = profile.occupation.ifBlank { "Anggota Terdaftar AuraID" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = ElectricCyan
                            )
                            Text(
                                text = profile.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = GlassTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (profile.bio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = profile.bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GlassTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Info Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GlassMiniInfoChip(
                            icon = Icons.Rounded.LocationCity,
                            label = profile.city.ifBlank { "Belum diisi" },
                            modifier = Modifier.weight(1f)
                        )
                        GlassMiniInfoChip(
                            icon = Icons.Rounded.Phone,
                            label = profile.phoneNumber.ifBlank { "Belum diisi" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Completion indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Kelengkapan Data Diri",
                            style = MaterialTheme.typography.labelMedium,
                            color = GlassTextSecondary
                        )
                        Text(
                            text = "$completion%",
                            style = MaterialTheme.typography.labelMedium,
                            color = ElectricCyan
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { completion / 100f },
                        color = ElectricCyan,
                        trackColor = Color.White.copy(alpha = 0.12f),
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Terdaftar sejak $joinedLabel",
                        style = MaterialTheme.typography.bodySmall,
                        color = GlassTextMuted
                    )
                }
            }
        }

        // Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassPrimaryButton(
                text = "Edit Data Diri",
                icon = Icons.Rounded.Edit,
                onClick = onEditProfile,
                modifier = Modifier
                    .weight(1f)
                    .testTag("home_edit_profile_button")
            )
            GlassSecondaryButton(
                text = "Riwayat Login",
                icon = Icons.Rounded.History,
                onClick = onOpenActivity,
                modifier = Modifier
                    .weight(1f)
                    .testTag("home_activity_button")
            )
        }
    }
}

@Composable
private fun PersonalDataTab(
    profile: UserProfile,
    isEditing: Boolean,
    isSubmitting: Boolean,
    onToggleEdit: (Boolean) -> Unit,
    onSave: (RegistrationDraft) -> Unit
) {
    var fullName by rememberSaveable(profile.fullName) { mutableStateOf(profile.fullName) }
    var phoneNumber by rememberSaveable(profile.phoneNumber) { mutableStateOf(profile.phoneNumber) }
    var birthDate by rememberSaveable(profile.birthDate) { mutableStateOf(profile.birthDate) }
    var city by rememberSaveable(profile.city) { mutableStateOf(profile.city) }
    var occupation by rememberSaveable(profile.occupation) { mutableStateOf(profile.occupation) }
    var bio by rememberSaveable(profile.bio) { mutableStateOf(profile.bio) }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(profile, isEditing) {
        if (!isEditing) {
            fullName = profile.fullName
            phoneNumber = profile.phoneNumber
            birthDate = profile.birthDate
            city = profile.city
            occupation = profile.occupation
            bio = profile.bio
        }
    }

    if (showDatePicker) {
        GlassBirthDatePickerDialog(
            onDateSelected = { selected -> birthDate = selected },
            onDismiss = { showDatePicker = false }
        )
    }

    GlassCard(contentPadding = PaddingValues(22.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Data Diri Terdaftar",
                    style = MaterialTheme.typography.headlineSmall,
                    color = GlassTextPrimary
                )
                Text(
                    text = if (isEditing) "Perbarui biodata Anda di bawah ini" else "Informasi profil tersimpan di Cloud Firestore",
                    style = MaterialTheme.typography.bodySmall,
                    color = GlassTextSecondary
                )
            }
            if (!isEditing) {
                IconButton(
                    onClick = { onToggleEdit(true) },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(CircleShape)
                        .background(ElectricCyan.copy(alpha = 0.16f))
                        .testTag("toggle_edit_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Ubah data diri",
                        tint = ElectricCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (!isEditing) {
            ProfileFieldItem(Icons.Rounded.Person, "Nama Lengkap", profile.fullName)
            ProfileFieldItem(Icons.Rounded.AlternateEmail, "Email Terdaftar", profile.email)
            ProfileFieldItem(Icons.Rounded.Phone, "Nomor Telepon", profile.phoneNumber.ifBlank { "-" })
            ProfileFieldItem(Icons.Rounded.CalendarMonth, "Tanggal Lahir", profile.birthDate.ifBlank { "-" })
            ProfileFieldItem(Icons.Rounded.LocationCity, "Kota / Domisili", profile.city.ifBlank { "-" })
            ProfileFieldItem(Icons.Rounded.Work, "Pekerjaan / Profesi", profile.occupation.ifBlank { "-" })
            ProfileFieldItem(Icons.Rounded.EditNote, "Bio Singkat", profile.bio.ifBlank { "-" })
        } else {
            GlassTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    if (fullNameError != null) fullNameError = null
                },
                label = "Nama Lengkap *",
                leadingIcon = Icons.Rounded.Person,
                isError = fullNameError != null,
                errorText = fullNameError,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.testTag("edit_fullname_input")
            )
            Spacer(modifier = Modifier.height(12.dp))
            GlassTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = "Nomor Telepon",
                leadingIcon = Icons.Rounded.Phone,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.testTag("edit_phone_input")
            )
            Spacer(modifier = Modifier.height(12.dp))
            GlassTextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = "Tanggal Lahir",
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
                modifier = Modifier.testTag("edit_birthdate_input")
            )
            Spacer(modifier = Modifier.height(12.dp))
            GlassTextField(
                value = city,
                onValueChange = { city = it },
                label = "Kota / Domisili",
                leadingIcon = Icons.Rounded.LocationCity,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.testTag("edit_city_input")
            )
            Spacer(modifier = Modifier.height(12.dp))
            GlassTextField(
                value = occupation,
                onValueChange = { occupation = it },
                label = "Pekerjaan / Profesi",
                leadingIcon = Icons.Rounded.Work,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.testTag("edit_occupation_input")
            )
            Spacer(modifier = Modifier.height(12.dp))
            GlassTextField(
                value = bio,
                onValueChange = { bio = it.take(240) },
                label = "Bio Singkat",
                leadingIcon = Icons.Rounded.EditNote,
                singleLine = false,
                maxLines = 3,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.testTag("edit_bio_input")
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassSecondaryButton(
                    text = "Batal",
                    onClick = { onToggleEdit(false) },
                    modifier = Modifier.weight(1f)
                )
                GlassPrimaryButton(
                    text = "Simpan",
                    icon = Icons.Rounded.Save,
                    isLoading = isSubmitting,
                    onClick = {
                        val cleanName = fullName.trim()
                        if (cleanName.length !in 2..80) {
                            fullNameError = "Nama lengkap minimal 2 karakter."
                            return@GlassPrimaryButton
                        }
                        onSave(
                            RegistrationDraft(
                                fullName = cleanName,
                                phoneNumber = phoneNumber,
                                birthDate = birthDate,
                                city = city,
                                occupation = occupation,
                                bio = bio
                            )
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_profile_changes_button")
                )
            }
        }
    }
}

@Composable
private fun AccountSecurityTab(
    firebaseUser: FirebaseUser,
    activityLogsState: UiState<List<ActivityLog>>,
    onRequestDeleteProfile: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        GlassCard(contentPadding = PaddingValues(22.dp)) {
            Text(
                text = "Sesi & Autentikasi Firebase",
                style = MaterialTheme.typography.titleLarge,
                color = GlassTextPrimary
            )
            Spacer(modifier = Modifier.height(14.dp))
            ProfileFieldItem(
                icon = Icons.Rounded.AlternateEmail,
                label = "Email Akun",
                value = firebaseUser.email ?: "Tidak tersedia"
            )
            ProfileFieldItem(
                icon = Icons.Rounded.CheckCircle,
                label = "Status Verifikasi",
                value = if (firebaseUser.isEmailVerified) "Email Terverifikasi (Google)" else "Aktif"
            )
            ProfileFieldItem(
                icon = Icons.Rounded.Security,
                label = "ID Pengguna (UID)",
                value = firebaseUser.uid.take(14) + "..."
            )

            Spacer(modifier = Modifier.height(14.dp))

            GlassSecondaryButton(
                text = stringResource(R.string.sign_out_btn),
                icon = Icons.AutoMirrored.Rounded.Logout,
                tint = ElectricCyan,
                onClick = onSignOut,
                modifier = Modifier.testTag("sign_out_button")
            )

            Spacer(modifier = Modifier.height(10.dp))

            GlassSecondaryButton(
                text = "Hapus Data Profil Firestore",
                icon = Icons.Rounded.DeleteForever,
                tint = CoralError,
                onClick = onRequestDeleteProfile,
                modifier = Modifier.testTag("delete_profile_button")
            )
        }

        GlassCard(contentPadding = PaddingValues(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Riwayat Login & Aktivitas",
                    style = MaterialTheme.typography.titleLarge,
                    color = GlassTextPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            when (activityLogsState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ElectricCyan)
                    }
                }

                is UiState.Error -> {
                    Text(
                        text = activityLogsState.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = CoralError
                    )
                }

                is UiState.Success -> {
                    val logs = activityLogsState.data
                    if (logs.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.History,
                                contentDescription = null,
                                tint = GlassTextMuted,
                                modifier = Modifier.size(34.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Belum ada riwayat aktivitas tercatat.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GlassTextMuted
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            logs.forEach { log ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White.copy(alpha = 0.05f))
                                        .border(1.dp, GlassBorderSubtle, RoundedCornerShape(16.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (log.action) {
                                                    "REGISTER" -> AuroraMint
                                                    "UPDATE_PROFILE" -> AuroraIndigo
                                                    else -> ElectricCyan
                                                }
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = log.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = GlassTextPrimary
                                        )
                                        Text(
                                            text = "${log.deviceLabel} • ${formatTimestamp(log.createdAt)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = GlassTextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassFloatingNavBar(
    items: List<NavDestinationItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val barShape = RoundedCornerShape(26.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 460.dp)
            .clip(barShape)
            .background(MidnightSurface.copy(alpha = 0.88f))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(GlassBorderHighlight, ElectricCyan.copy(alpha = 0.4f), GlassBorderHighlight)
                ),
                shape = barShape
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            val selected = selectedIndex == index
            val pillShape = RoundedCornerShape(20.dp)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .clip(pillShape)
                    .background(if (selected) ElectricCyan.copy(alpha = 0.18f) else Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .testTag(item.tag),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.label,
                    tint = if (selected) ElectricCyan else GlassTextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) GlassTextPrimary else GlassTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ProfileAvatar(
    fullName: String,
    photoUrl: String
) {
    val initials = fullName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifBlank { "A" }

    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(ElectricCyan.copy(alpha = 0.35f), AuroraIndigo.copy(alpha = 0.45f))
                )
            )
            .border(1.5.dp, ElectricCyan.copy(alpha = 0.75f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (photoUrl.isNotBlank()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Foto profil $fullName",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.headlineSmall,
                color = GlassTextPrimary
            )
        }
    }
}

@Composable
private fun GlassMiniInfoChip(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ElectricCyan,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = GlassTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ProfileFieldItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.07f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ElectricCyan,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = GlassTextMuted
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = GlassTextPrimary
            )
        }
    }
}

private fun formatTimestamp(timestamp: Timestamp?): String {
    val safeDate = (timestamp ?: Timestamp.now()).toDate()
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    return formatter.format(safeDate)
}
