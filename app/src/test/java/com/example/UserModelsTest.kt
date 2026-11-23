package com.example

import com.example.data.RegistrationDraft
import com.example.data.UserProfile
import com.google.firebase.firestore.FieldValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserModelsTest {

    @Test
    fun registrationDraft_toCreateMap_excludesIdAndNullOptionalFields() {
        val draft = RegistrationDraft(
            fullName = "  Zalfa Qoiqisty  ",
            phoneNumber = "",
            city = "Jakarta",
            bio = "  "
        )
        val map = draft.toCreateMap(
            userId = "uid_123",
            resolvedEmail = "zalfa@example.com",
            photoUrl = null
        )

        assertFalse(map.containsKey("id"))
        assertEquals("uid_123", map["userId"])
        assertEquals("Zalfa Qoiqisty", map["fullName"])
        assertEquals("zalfa@example.com", map["email"])
        assertEquals("Jakarta", map["city"])
        assertFalse(map.containsKey("phoneNumber"))
        assertFalse(map.containsKey("bio"))
        assertFalse(map.containsKey("photoUrl"))
        assertTrue(map["createdAt"] is FieldValue)
        assertTrue(map["updatedAt"] is FieldValue)
    }

    @Test
    fun userProfile_completionPercent_reflectsFilledFields() {
        val partial = UserProfile(
            userId = "uid_123",
            fullName = "Zalfa",
            email = "zalfa@example.com"
        )
        assertEquals(28, partial.completionPercent())

        val complete = partial.copy(
            phoneNumber = "+62812345678",
            birthDate = "14 Mei 2000",
            city = "Jakarta",
            occupation = "Engineer",
            bio = "Halo dunia"
        )
        assertEquals(100, complete.completionPercent())
    }
}
