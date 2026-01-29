package com.example.pwdvault.dal

import android.content.Context
import android.content.SharedPreferences
import com.example.pwdvault.dal.dto.CredentialDT
import com.example.pwdvault.dal.dto.compareCredentialDT
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlinx.serialization.builtins.ListSerializer

class StorageSPTest {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var storage: StorageSP<CredentialDT>

    @Before
    fun setup() {
        context = mockk()
        sharedPreferences = mockk()
        editor = mockk(relaxed = true)

        every { context.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.getString(any(), any()) } returns null
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any<String>(), any<String>()) } returns editor

        storage = StorageSP(context, "test_prefs", ListSerializer(CredentialDT.serializer())) { a, b -> compareCredentialDT(a, b) }
    }

    @Test
    fun `store adds item and persists`() {
        val credential = CredentialDT("Platform", "Email", "Password", "Info")
        val result = storage.store(credential)

        assertTrue(result)
        assertEquals(1, storage.retrieveAll().size)
        verify { editor.putString(any(), any()) }
        verify { editor.apply() }
    }

    @Test
    fun `store returns false for duplicate item`() {
        val credential = CredentialDT("Platform", "Email", "Password", "Info")
        storage.store(credential)
        val result = storage.store(credential)

        assertFalse(result)
        assertEquals(1, storage.retrieveAll().size)
    }

    @Test
    fun `delete removes item and persists`() {
        val credential = CredentialDT("Platform", "Email", "Password", "Info")
        storage.store(credential)
        val result = storage.delete(0)

        assertTrue(result)
        assertEquals(0, storage.retrieveAll().size)
        verify(exactly = 2) { editor.putString(any(), any()) }
        verify(exactly = 2) { editor.apply() }
    }

    @Test
    fun `update modifies item and persists`() {
        val credential = CredentialDT("Platform", "Email", "Password", "Info")
        storage.store(credential)
        val updated = credential.copy(password = "NewPassword")
        val result = storage.update(0, updated)

        assertTrue(result)
        assertEquals("NewPassword", storage.retrieve(0)?.password)
        verify(exactly = 2) { editor.putString(any(), any()) }
        verify(exactly = 2) { editor.apply() }
    }
}
