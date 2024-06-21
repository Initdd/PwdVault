package com.example.passmanager.control

import com.example.passmanager.control.encryption.HashingManager
import com.example.passmanager.dal.StorageJSON
import com.example.passmanager.dal.domain.MasterPasswordDO
import com.example.passmanager.dal.dto.MasterPasswordDT
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class MasterPasswordManagerTest {

    private lateinit var masterPasswordManager: MasterPasswordManager
    private lateinit var storageManager: StorageJSON<MasterPasswordDT>
    private lateinit var file: File
    private val defaultPwd = "123"

    @Before
    fun setup() {
        file = File.createTempFile("temp", null)
        storageManager = StorageJSON { a, b -> a.password == b.password }
        masterPasswordManager = MasterPasswordManager(storageManager, file)
    }

    @Test
    fun `set should store hashed password when no password is stored`() {
        val password = MasterPasswordDO("password")
        masterPasswordManager.set(password)
        val storedPassword = masterPasswordManager.get()
        Assert.assertNotEquals(password.password, storedPassword.password)
        // cleanup
        masterPasswordManager.set(MasterPasswordDO(defaultPwd))
    }

    @Test
    fun `set should update stored password`() {
        val password = MasterPasswordDO("password")
        masterPasswordManager.set(password)
        val newPassword = MasterPasswordDO("newPassword")
        masterPasswordManager.set(newPassword)
        val storedPassword = masterPasswordManager.get()
        Assert.assertEquals(storedPassword.password, HashingManager.hashString(newPassword.password))
        // cleanup
        masterPasswordManager.set(MasterPasswordDO(defaultPwd))
    }

    @Test
    fun `get should return empty password when no password is stored`() {
        val password = masterPasswordManager.get()
        Assert.assertEquals(password.password, HashingManager.hashString(defaultPwd))
    }

    @Test
    fun `get should return stored password`() {
        val password = MasterPasswordDO("password")
        masterPasswordManager.set(password)
        val storedPassword = masterPasswordManager.get()
        Assert.assertEquals(storedPassword.password, HashingManager.hashString(password.password))
        // cleanup
        masterPasswordManager.set(MasterPasswordDO(defaultPwd))
    }

    @Test
    fun `check should return true when password matches stored password`() {
        val password = MasterPasswordDO("password")
        masterPasswordManager.set(password)
        val result = masterPasswordManager.check(password)
        Assert.assertTrue(result)
        // cleanup
        masterPasswordManager.set(MasterPasswordDO(defaultPwd))
    }

    @Test
    fun `check should return false when password does not match stored password`() {
        val password = MasterPasswordDO("password")
        masterPasswordManager.set(password)
        val wrongPassword = MasterPasswordDO("wrongPassword")
        val result = masterPasswordManager.check(wrongPassword)
        Assert.assertFalse(result)
        // cleanup
        masterPasswordManager.set(MasterPasswordDO(defaultPwd))
    }

}