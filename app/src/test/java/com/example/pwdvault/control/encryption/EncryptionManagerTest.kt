package com.example.pwdvault.control.encryption

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class EncryptionManagerTest {

    @Test
    fun `encrypt should return encrypted string when valid key and data are provided`() {
        val key = "testKey"
        val data = "testData"
        val result = EncryptionManager.encrypt(key, data)
        assertNotEquals(data, result)
    }

    @Test
    fun `decrypt should return original string when valid key and encrypted data are provided`() {
        val key = "testKey"
        val data = "testData"
        val encryptedData = EncryptionManager.encrypt(key, data)
        val result = EncryptionManager.decrypt(key, encryptedData)
        assertEquals(data, result)
    }

    @Test
    fun `encrypt should throw IllegalArgumentException when key is empty`() {
        val key = ""
        val data = "testData"
        assertThrows(IllegalArgumentException::class.java) {
            EncryptionManager.encrypt(key, data)
        }
    }

    @Test
    fun `decrypt should throw IllegalArgumentException when key is empty`() {
        val key = ""
        val data = "testData"
        assertThrows(IllegalArgumentException::class.java) {
            EncryptionManager.decrypt(key, data)
        }
    }

    @Test
    fun `encrypt should throw IllegalArgumentException when data is empty`() {
        val key = "testKey"
        val data = ""
        assertThrows(IllegalArgumentException::class.java) {
            EncryptionManager.encrypt(key, data)
        }
    }
}