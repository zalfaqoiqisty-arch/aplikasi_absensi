package com.example.data

import android.content.Context
import android.os.Build
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("Pengguna harus masuk dengan Google sebelum mengakses data.")
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val uid = requireUserId()
        require(uid == userId) { "UID sesi tidak cocok." }
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            UserProfile::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else {
                        null
                    }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(exceptionToLog, OperationType.GET, path)
                    }
                    throw error
                }
        )
    }

    fun observeActivityLogs(userId: String): Flow<List<ActivityLog>> = flow {
        val uid = requireUserId()
        require(uid == userId) { "UID sesi tidak cocok." }
        val path = "users/$userId/activity_logs"
        emitAll(
            db.collection("users").document(userId)
                .collection("activity_logs")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(20)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        ActivityLog::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createUserProfile(
        draft: RegistrationDraft,
        email: String,
        photoUrl: String?
    ): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
        return try {
            val uid = requireUserId()
            val clean = draft.normalized()
            require(clean.fullName.length in 2..80) {
                "Nama lengkap harus antara 2 hingga 80 karakter."
            }
            val cleanEmail = email.trim().ifBlank { clean.emailHint }
            require(cleanEmail.length in 3..120) {
                "Email tidak valid atau kosong."
            }

            val docRef = db.collection("users").document(uid)
            docPath = docRef.path
            val payload = clean.toCreateMap(
                userId = uid,
                resolvedEmail = cleanEmail,
                photoUrl = photoUrl
            )
            docRef.set(payload).await()
            writeActivityLogInternal(
                uid = uid,
                action = "REGISTER",
                description = "Registrasi profil data diri untuk ${clean.fullName}"
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(
        draft: RegistrationDraft,
        email: String? = null
    ): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
        return try {
            val uid = requireUserId()
            val clean = draft.normalized()
            require(clean.fullName.length in 2..80) {
                "Nama lengkap harus antara 2 hingga 80 karakter."
            }
            val docRef = db.collection("users").document(uid)
            docPath = docRef.path
            docRef.update(clean.toUpdateMap(email)).await()
            writeActivityLogInternal(
                uid = uid,
                action = "UPDATE_PROFILE",
                description = "Memperbarui data diri (${clean.fullName})"
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun recordLoginActivity(): Result<Unit> {
        var logPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/activity_logs"
        return try {
            val uid = requireUserId()
            val email = auth.currentUser?.email ?: "Akun Google"
            val docRef = db.collection("users").document(uid)
                .collection("activity_logs").document()
            logPath = docRef.path
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "action" to "LOGIN",
                    "description" to "Masuk menggunakan $email",
                    "deviceLabel" to deviceSummary(),
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, logPath)
            Result.failure(e)
        }
    }

    suspend fun deleteUserProfile(): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid)
            docPath = docRef.path
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    private suspend fun writeActivityLogInternal(
        uid: String,
        action: String,
        description: String
    ) {
        val logRef = db.collection("users").document(uid)
            .collection("activity_logs").document()
        logRef.set(
            mapOf(
                "userId" to uid,
                "action" to action,
                "description" to description.take(160),
                "deviceLabel" to deviceSummary(),
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    private fun deviceSummary(): String {
        val model = "${Build.MANUFACTURER} ${Build.MODEL}".trim()
        return model.ifBlank { "Android Device" }.take(80)
    }
}
