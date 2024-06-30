package com.example.pwdvault.control

import com.example.pwdvault.dal.StorageJSON
import com.example.pwdvault.dal.domain.CredentialDO
import com.example.pwdvault.dal.domain.MasterPasswordDO
import com.example.pwdvault.dal.dto.CredentialDT
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class CredentialsManagerTest {

    private lateinit var credentialsManager: CredentialsManager
    private lateinit var storage: StorageJSON<CredentialDT>
    private lateinit var masterPassword: MasterPasswordDO
    private lateinit var credential: CredentialDO

    @Before
    fun setup() {
        storage = StorageJSON { a, b -> a.platform == b.platform && a.emailUsername == b.emailUsername }
        masterPassword = MasterPasswordDO("masterPassword")
        credential = CredentialDO("platform", "emailUsername", "password", listOf())
        credentialsManager = CredentialsManager(storage, File("testFile"))
    }

    @Test
    fun `add credential successfully`() {
        val result = credentialsManager.add(credential, masterPassword)
        assertEquals(true, result)
        // cleanup
        credentialsManager.remove(credential.platform, credential.emailUsername)
    }

    @Test
    fun `add credential fails when credential already exists`() {
        val result = credentialsManager.add(credential, masterPassword)
        assertEquals(true, result)
        val result2 = credentialsManager.add(credential, masterPassword)
        assertEquals(false, result2)
        // cleanup
        credentialsManager.remove(credential.platform, credential.emailUsername)
    }

    @Test
    fun `add credential fails when platform is empty`() {
        val result = credentialsManager.add(credential.copy(platform = ""), masterPassword)
        assertEquals(false, result)
    }

    @Test
    fun `add credential fails when emailUsername is empty`() {
        val result = credentialsManager.add(credential.copy(emailUsername = ""), masterPassword)
        assertEquals(false, result)
    }

    @Test
    fun `get all credentials successfully`() {
        credentialsManager.add(credential, masterPassword)
        val result = credentialsManager.getAll(masterPassword)
        assertEquals(1, result.size)
        assertEquals(credential.platform, result[0].platform)
        assertEquals(credential.emailUsername, result[0].emailUsername)
        assertEquals(credential.password, result[0].password)
        // cleanup
        credentialsManager.remove(credential.platform, credential.emailUsername)
    }

    @Test
    fun `get all credentials successfully without master password`() {
        credentialsManager.add(credential, masterPassword)
        val result = credentialsManager.getAll(null)
        assertEquals(1, result.size)
        assertNotEquals(credential.password, result[0].password)
        // cleanup
        credentialsManager.remove(credential.platform, credential.emailUsername)
    }

    @Test
    fun `get all credentials unsuccessfully with wrong master password`() {
        credentialsManager.add(credential, masterPassword)
        val wrongMasterPassword = MasterPasswordDO("wrongMasterPassword")
        val allEncrypted = credentialsManager.getAll(wrongMasterPassword)
        assertTrue(allEncrypted.isNotEmpty())
        assertTrue(credential.password != allEncrypted[0].password)
        // cleanup
        credentialsManager.remove(credential.platform, credential.emailUsername)
    }

    @Test
    fun `get credential by platform and emailUsername successfully`() {
        credentialsManager.add(credential, masterPassword)
        val result = credentialsManager.get(credential.platform, credential.emailUsername)
        assertEquals(credential.platform, result?.platform)
        assertEquals(credential.emailUsername, result?.emailUsername)
        // cleanup
        credentialsManager.remove(credential.platform, credential.emailUsername)
    }

    @Test
    fun `get credential by platform and emailUsername returns null when credential does not exist`() {
        val result = credentialsManager.get("nonexistent", "nonexistent")
        assertEquals(null, result)
    }

    @Test
    fun `remove credential successfully`() {
        credentialsManager.add(credential, masterPassword)
        val result = credentialsManager.remove(credential.platform, credential.emailUsername)
        assertEquals(true, result)
    }

    @Test
    fun `remove credential fails when credential does not exist`() {
        val result = credentialsManager.remove("nonexistent", "nonexistent")
        assertEquals(false, result)
    }

    @Test
    fun `update credential successfully`() {
        credentialsManager.add(credential, masterPassword)
        val newCredential = CredentialDO("platform", "emailUsername", "newPassword", listOf())
        val result = credentialsManager.update("platform", "emailUsername", newCredential, masterPassword)
        assertEquals(true, result)
        // cleanup
        credentialsManager.remove(newCredential.platform, newCredential.emailUsername)
    }

    @Test
    fun `update credential fails when credential does not exist`() {
        val newCredential = CredentialDO("platform", "emailUsername", "newPassword", listOf())
        val result = credentialsManager.update("nonexistent", "nonexistent", newCredential, masterPassword)
        assertEquals(false, result)
    }

    @Test
    fun `reencrypt credentials successfully`() {
        credentialsManager.add(credential, masterPassword)
        val newMasterPassword = MasterPasswordDO("newMasterPassword")
        val result = credentialsManager.reencryptAll(masterPassword, newMasterPassword)
        assertEquals(true, result)
        // cleanup
        credentialsManager.remove(credential.platform, credential.emailUsername)
    }

    @Test
    fun `reencrypt credentials fails when old master password is wrong`() {
        credentialsManager.add(credential, masterPassword)
        val wrongMasterPassword = MasterPasswordDO("wrongMasterPassword")
        val result = credentialsManager.reencryptAll(wrongMasterPassword, masterPassword)
        assertEquals(false, result)
        // cleanup
        credentialsManager.remove(credential.platform, credential.emailUsername)
    }

    @Test
    fun `update all credentials successfully`() {
        credentialsManager.add(credential, masterPassword)
        val newCredential = CredentialDO("platform", "emailUsername", "newPassword", listOf())
        val result = credentialsManager.updateAll(listOf(newCredential), masterPassword)
        assertEquals(true, result)
        // cleanup
        credentialsManager.remove(newCredential.platform, newCredential.emailUsername)
    }
}