package com.example.passmanager.control

import com.example.passmanager.dal.Storage
import com.example.passmanager.dal.domain.CredentialDO
import com.example.passmanager.dal.dto.CredentialDT
import com.example.passmanager.dal.mapper.CredentialMapper

class CredentialsManager (
    private val storage: Storage<CredentialDT>
) {

    fun add(platform: String, email: String, password: String): Boolean {
        return storage.store(CredentialMapper.toDto(CredentialDO(platform, email, password)))
    }

    fun get(platform: String, email: String): CredentialDO? {
        return CredentialMapper.toDomain(storage.retrieve(CredentialDT(platform, email, "")) ?: return null)
    }

    fun getAll(): List<CredentialDO> {
        return storage.retrieveAll().map { CredentialMapper.toDomain(it) }
    }

    fun remove(platform: String, email: String): Boolean {
        return storage.delete(CredentialDT(platform, email, ""))
    }

    fun deleteAll() {
        storage.deleteAll()
    }

    fun updatePassword(platform: String, email: String, newPassword: String): Boolean {
        return storage.update(CredentialDT(platform, email, newPassword))
    }
}