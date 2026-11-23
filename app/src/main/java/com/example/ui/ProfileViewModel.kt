package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ActivityLog
import com.example.data.RegistrationDraft
import com.example.data.UserProfile
import com.example.data.UserRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class ActionBannerState(
    val isSubmitting: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false
)

class ProfileViewModel(
    private val repository: UserRepository,
    private val currentUserId: String
) : ViewModel() {

    private var hasRecordedSessionLogin = false

    val profileState: StateFlow<UiState<UserProfile?>> = repository.observeUserProfile(currentUserId)
        .map<UserProfile?, UiState<UserProfile?>> { profile ->
            if (profile != null && !hasRecordedSessionLogin) {
                hasRecordedSessionLogin = true
                viewModelScope.launch {
                    repository.recordLoginActivity()
                }
            }
            UiState.Success(profile)
        }
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            val rootError = firestoreError ?: error
            emit(UiState.Error(rootError.localizedMessage ?: "Gagal memuat profil pengguna."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val activityLogsState: StateFlow<UiState<List<ActivityLog>>> = repository.observeActivityLogs(currentUserId)
        .map<List<ActivityLog>, UiState<List<ActivityLog>>> { logs ->
            UiState.Success(logs)
        }
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            val rootError = firestoreError ?: error
            emit(UiState.Error(rootError.localizedMessage ?: "Gagal memuat riwayat aktivitas."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    private val _actionState = MutableStateFlow(ActionBannerState())
    val actionState: StateFlow<ActionBannerState> = _actionState.asStateFlow()

    fun createProfile(
        draft: RegistrationDraft,
        email: String,
        photoUrl: String?,
        onSuccess: () -> Unit = {}
    ) {
        val cleanName = draft.fullName.trim()
        if (cleanName.length !in 2..80) {
            _actionState.value = ActionBannerState(
                isSubmitting = false,
                message = "Nama lengkap minimal 2 karakter dan maksimal 80 karakter.",
                isError = true
            )
            return
        }
        viewModelScope.launch {
            _actionState.value = ActionBannerState(isSubmitting = true)
            val result = repository.createUserProfile(draft, email, photoUrl)
            result.fold(
                onSuccess = {
                    hasRecordedSessionLogin = true
                    _actionState.value = ActionBannerState(
                        isSubmitting = false,
                        message = "Data diri berhasil didaftarkan ke Cloud Firestore.",
                        isError = false
                    )
                    onSuccess()
                },
                onFailure = { err ->
                    _actionState.value = ActionBannerState(
                        isSubmitting = false,
                        message = err.localizedMessage ?: "Gagal mendaftarkan profil.",
                        isError = true
                    )
                }
            )
        }
    }

    fun updateProfile(
        draft: RegistrationDraft,
        email: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        val cleanName = draft.fullName.trim()
        if (cleanName.length !in 2..80) {
            _actionState.value = ActionBannerState(
                isSubmitting = false,
                message = "Nama lengkap minimal 2 karakter dan maksimal 80 karakter.",
                isError = true
            )
            return
        }
        viewModelScope.launch {
            _actionState.value = ActionBannerState(isSubmitting = true)
            val result = repository.updateUserProfile(draft, email)
            result.fold(
                onSuccess = {
                    _actionState.value = ActionBannerState(
                        isSubmitting = false,
                        message = "Perubahan data diri berhasil disimpan.",
                        isError = false
                    )
                    onSuccess()
                },
                onFailure = { err ->
                    _actionState.value = ActionBannerState(
                        isSubmitting = false,
                        message = err.localizedMessage ?: "Gagal memperbarui data diri.",
                        isError = true
                    )
                }
            )
        }
    }

    fun deleteProfile(onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            _actionState.value = ActionBannerState(isSubmitting = true)
            val result = repository.deleteUserProfile()
            result.fold(
                onSuccess = {
                    _actionState.value = ActionBannerState(
                        isSubmitting = false,
                        message = "Data profil berhasil dihapus dari Firestore.",
                        isError = false
                    )
                    onDeleted()
                },
                onFailure = { err ->
                    _actionState.value = ActionBannerState(
                        isSubmitting = false,
                        message = err.localizedMessage ?: "Gagal menghapus profil.",
                        isError = true
                    )
                }
            )
        }
    }

    fun clearBanner() {
        _actionState.value = _actionState.value.copy(message = null)
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
