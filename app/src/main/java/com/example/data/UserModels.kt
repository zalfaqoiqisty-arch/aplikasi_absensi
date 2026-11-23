package com.example.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val birthDate: String = "",
    val city: String = "",
    val occupation: String = "",
    val bio: String = "",
    val photoUrl: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun completionPercent(): Int {
        val fields = listOf(fullName, email, phoneNumber, birthDate, city, occupation, bio)
        val filled = fields.count { it.isNotBlank() }
        return ((filled.toFloat() / fields.size.toFloat()) * 100f).toInt().coerceIn(0, 100)
    }
}

@Keep
@IgnoreExtraProperties
data class ActivityLog(
    @DocumentId val id: String = "",
    val userId: String = "",
    val action: String = "",
    val description: String = "",
    val deviceLabel: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

data class RegistrationDraft(
    val fullName: String = "",
    val emailHint: String = "",
    val phoneNumber: String = "",
    val birthDate: String = "",
    val city: String = "",
    val occupation: String = "",
    val bio: String = ""
) {
    fun normalized(): RegistrationDraft = copy(
        fullName = fullName.trim().take(80),
        emailHint = emailHint.trim().take(120),
        phoneNumber = phoneNumber.trim().take(25),
        birthDate = birthDate.trim().take(20),
        city = city.trim().take(60),
        occupation = occupation.trim().take(60),
        bio = bio.trim().take(240)
    )

    fun toCreateMap(userId: String, resolvedEmail: String, photoUrl: String?): Map<String, Any> {
        val clean = normalized()
        val raw = mutableMapOf<String, Any?>(
            "userId" to userId,
            "fullName" to clean.fullName,
            "email" to resolvedEmail.trim().take(120),
            "phoneNumber" to clean.phoneNumber.ifBlank { null },
            "birthDate" to clean.birthDate.ifBlank { null },
            "city" to clean.city.ifBlank { null },
            "occupation" to clean.occupation.ifBlank { null },
            "bio" to clean.bio.ifBlank { null },
            "photoUrl" to photoUrl?.trim()?.take(500)?.ifBlank { null },
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        @Suppress("UNCHECKED_CAST")
        return raw.filterValues { it != null } as Map<String, Any>
    }

    fun toUpdateMap(resolvedEmail: String? = null): Map<String, Any> {
        val clean = normalized()
        val raw = mutableMapOf<String, Any>(
            "fullName" to clean.fullName,
            "phoneNumber" to clean.phoneNumber,
            "birthDate" to clean.birthDate,
            "city" to clean.city,
            "occupation" to clean.occupation,
            "bio" to clean.bio,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (!resolvedEmail.isNullOrBlank()) {
            raw["email"] = resolvedEmail.trim().take(120)
        }
        return raw
    }
}
