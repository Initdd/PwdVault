package com.example.passmanager.control

import com.example.passmanager.control.encryption.EncryptionManager
import com.example.passmanager.dal.Storage
import com.example.passmanager.dal.domain.CredentialDO
import com.example.passmanager.dal.domain.MasterPasswordDO
import com.example.passmanager.dal.dto.CredentialDT
import com.example.passmanager.dal.mapper.CredentialMapper
import com.example.passmanager.dal.saveToFile
import java.io.File


class CredentialsManager (
    private val storage: Storage<Int, CredentialDT>,
    private val file: File,
) {

    fun add(credentialDO: CredentialDO, masterPasswordDO: MasterPasswordDO): Boolean {
        // check if the platform or the email are empty
        if (credentialDO.platform.isEmpty() || credentialDO.emailUsername.isEmpty()) {
            return false
        }
        // check if the platform and email already exist
        storage.retrieveAll().forEach {
            if (it.platform == credentialDO.platform && it.emailUsername == credentialDO.emailUsername) {
                return false
            }
        }
        return storage.store(
            CredentialMapper.toDTO(
                credentialDO.copy(
                    password = EncryptionManager.encrypt(masterPasswordDO.password, credentialDO.password)
                )
            )
        )
    }

    fun getAll(masterPasswordDO: MasterPasswordDO?): List<CredentialDO> {
        return storage.retrieveAll().map {
            if (masterPasswordDO == null) {
                println("masterPasswordDO is null")
                return@map CredentialMapper.toDomain(it)
            }
            try {
                CredentialMapper.toDomain(
                    it.copy(
                        password = EncryptionManager.decrypt(masterPasswordDO.password, it.password)
                    )
                )
            } catch (e: Exception) {
                // If the password cannot be decrypted, return the original credential list not decrypted
                println("Error decrypting password")
                CredentialMapper.toDomain(it)
            }
        }
    }

    fun get(platform: String, email: String): CredentialDO? {
        return storage.retrieveAll()
            .filter { it.platform == platform && it.emailUsername == email }
            .map { CredentialMapper.toDomain(it) }
            .firstOrNull()
    }

    fun getWithPwd(platform: String, email: String, masterPasswordDO: MasterPasswordDO): CredentialDO? {
        val credential = get(platform, email) ?: return null
        return try {
            credential.copy(
                password = EncryptionManager.decrypt(masterPasswordDO.password, credential.password)
            )
        } catch (e: Exception) {
            throw SecurityException("Invalid master password")
        }
    }

    fun remove(platform: String, email: String): Boolean {
        // Remove all credentials with the same platform and email
        val keysToRemove = mutableListOf<Int>()
        storage.retrieveAll().forEachIndexed { k, v ->
            if (v.platform == platform && v.emailUsername == email) {
                keysToRemove.add(k)
            }
        }
        if (keysToRemove.isEmpty()) return false
        keysToRemove.forEach { key ->
            storage.delete(key)
        }
        return true
    }

    fun deleteAll() {
        storage.deleteAll()
    }

    fun update(oldPlatform: String, oldEmail: String, newCredential: CredentialDO, masterPasswordDO: MasterPasswordDO): Boolean {
        val credentials = storage.retrieveAll()
        val idx = credentials.indexOfFirst { it.platform == oldPlatform && it.emailUsername == oldEmail }
        if (idx == -1) {
            return false
        }
        return storage.update(
            idx,
            CredentialMapper.toDTO(
                newCredential.copy(
                    password = EncryptionManager.encrypt(masterPasswordDO.password, newCredential.password)
                )
            )
        )
    }

    fun saveCredToFile() {
        saveToFile<CredentialDT>(file, storage.retrieveAll())
    }
}