package com.example.passmanager.control

import com.example.passmanager.dal.Storage
import com.example.passmanager.dal.domain.CredentialDO
import com.example.passmanager.dal.dto.CredentialDT
import com.example.passmanager.dal.mapper.CredentialMapper
import com.example.passmanager.dal.saveToFile
import java.io.File


class CredentialsManager (
    private val storage: Storage<Int, CredentialDT>,
    private val file: File
) {

    fun add(platform: String, email: String, password: String): Boolean {
        return storage.store(CredentialMapper.toDTO(CredentialDO(platform, email, password)))
    }

    fun get(idx: Int): CredentialDO? {
        return storage.retrieve(idx)?.let { CredentialMapper.toDomain(it) }
    }

    fun getAll(): List<CredentialDO> {
        return storage.retrieveAll().map { CredentialMapper.toDomain(it) }
    }

    fun remove(idx: Int): Boolean {
        return storage.delete(idx)
    }

    fun deleteAll() {
        storage.deleteAll()
    }

    fun updatePassword(idx: Int, newPassword: String): Boolean {
        val credential = storage.retrieve(idx) ?: return false
        return storage.update(idx, credential.copy(password = newPassword))
    }

    fun saveCredToFile() {
        saveToFile<CredentialDT>(file, storage.retrieveAll())
    }
}