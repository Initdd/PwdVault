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

    fun reencryptAll(oldMasterPasswordDO: MasterPasswordDO, newMasterPasswordDO: MasterPasswordDO): Boolean {
        val credentials = storage.retrieveAll()
        val updatedCredentials = try {
            credentials.map {
                CredentialMapper.toDomain(
                    it.copy(
                        password = EncryptionManager.decrypt(
                            oldMasterPasswordDO.password,
                            it.password
                        )
                    )
                )
            }
        } catch (e: Exception) {
            return false
        }
        return updateAll(updatedCredentials, newMasterPasswordDO)
    }

    fun updateAll(list: List<CredentialDO>, masterPasswordDO: MasterPasswordDO): Boolean {
        storage.deleteAll()
        return list.all { add(it, masterPasswordDO) }
    }

    fun saveCredToFile() {
        saveToFile<CredentialDT>(file, storage.retrieveAll())
    }
}