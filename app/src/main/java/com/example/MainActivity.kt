package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.auth.attemptAutoSignIn
import com.example.auth.signOutUser
import com.example.data.RegistrationDraft
import com.example.data.UserRepository
import com.example.ui.ProfileViewModel
import com.example.ui.UiState
import com.example.ui.components.AuroraGlassBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CompleteRegistrationScreen
import com.example.ui.screens.MainDashboardScreen
import com.example.ui.theme.AuraIdTheme
import com.example.ui.theme.CoralError
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuraIdTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var isCheckingAutoSignIn by remember { mutableStateOf(Firebase.auth.currentUser == null) }
    var pendingRegistrationDraft by remember { mutableStateOf(RegistrationDraft()) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose { Firebase.auth.removeAuthStateListener(listener) }
    }

    LaunchedEffect(Unit) {
        if (Firebase.auth.currentUser == null) {
            attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = {
                    currentUser = Firebase.auth.currentUser
                    isCheckingAutoSignIn = false
                },
                onUnauthenticated = {
                    isCheckingAutoSignIn = false
                },
                scope = scope
            )
        } else {
            isCheckingAutoSignIn = false
        }
    }

    val user = currentUser
    if (user == null) {
        if (isCheckingAutoSignIn) {
            AuroraGlassBackground {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = ElectricCyan)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Memeriksa sesi AuraID...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GlassTextSecondary
                        )
                    }
                }
            }
        } else {
            AuthScreen(
                onPrepareRegistrationDraft = { draft ->
                    pendingRegistrationDraft = draft
                },
                onAuthSuccess = {
                    currentUser = Firebase.auth.currentUser
                }
            )
        }
    } else {
        val uid = user.uid
        key(uid) {
            AuthenticatedFlow(
                firebaseUser = user,
                initialDraft = pendingRegistrationDraft,
                onClearDraft = { pendingRegistrationDraft = RegistrationDraft() },
                onSignedOut = {
                    pendingRegistrationDraft = RegistrationDraft()
                    currentUser = null
                }
            )
        }
    }
}

@Composable
private fun AuthenticatedFlow(
    firebaseUser: FirebaseUser,
    initialDraft: RegistrationDraft,
    onClearDraft: () -> Unit,
    onSignedOut: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val currentUserId = firebaseUser.uid

    val profileViewModel: ProfileViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                ProfileViewModel(UserRepository(db), currentUserId)
            }
        }
    )

    val profileState by profileViewModel.profileState.collectAsStateWithLifecycle()
    val activityLogsState by profileViewModel.activityLogsState.collectAsStateWithLifecycle()
    val actionState by profileViewModel.actionState.collectAsStateWithLifecycle()

    when (val state = profileState) {
        is UiState.Loading -> {
            AuroraGlassBackground {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = ElectricCyan)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Memuat profil data diri...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GlassTextSecondary
                        )
                    }
                }
            }
        }

        is UiState.Error -> {
            AuroraGlassBackground {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(
                        modifier = Modifier.widthIn(max = 460.dp),
                        contentPadding = PaddingValues(24.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Gagal Memuat Profil",
                                style = MaterialTheme.typography.titleLarge,
                                color = CoralError
                            )
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = GlassTextPrimary
                            )
                            GlassSecondaryButton(
                                text = "Keluar & Coba Lagi",
                                tint = ElectricCyan,
                                onClick = {
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
            }
        }

        is UiState.Success -> {
            val profile = state.data
            if (profile == null) {
                CompleteRegistrationScreen(
                    initialDraft = initialDraft,
                    googleEmail = firebaseUser.email.orEmpty(),
                    googleDisplayName = firebaseUser.displayName.orEmpty(),
                    googlePhotoUrl = firebaseUser.photoUrl?.toString(),
                    actionState = actionState,
                    onDismissBanner = profileViewModel::clearBanner,
                    onSubmitRegistration = { draft, email, photoUrl ->
                        profileViewModel.createProfile(
                            draft = draft,
                            email = email,
                            photoUrl = photoUrl,
                            onSuccess = onClearDraft
                        )
                    },
                    onSignedOut = onSignedOut
                )
            } else {
                MainDashboardScreen(
                    firebaseUser = firebaseUser,
                    profile = profile,
                    activityLogsState = activityLogsState,
                    actionState = actionState,
                    onDismissBanner = profileViewModel::clearBanner,
                    onUpdateProfile = { draft ->
                        profileViewModel.updateProfile(
                            draft = draft,
                            email = firebaseUser.email
                        )
                    },
                    onDeleteProfile = {
                        profileViewModel.deleteProfile()
                    },
                    onSignedOut = onSignedOut
                )
            }
        }
    }
}
