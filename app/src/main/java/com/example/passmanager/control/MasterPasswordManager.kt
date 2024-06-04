package com.example.passmanager.control

import com.example.passmanager.dal.Storage
import com.example.passmanager.dal.domain.MasterPasswordDO
import com.example.passmanager.dal.dto.MasterPasswordDT
import com.example.passmanager.dal.mapper.MasterPasswordMapper
import com.example.passmanager.dal.saveToFile
import java.io.File

class MasterPasswordManager (
    private val storageManager: Storage<Int, MasterPasswordDT>,
    private val file: File
) {

    fun set(masterPasswordDO: MasterPasswordDO) {
        // Hash the password before storing it
        // ? Currently mapping from domain to dto and then storing the dto. Can change that later
        val encryptedPassword = masterPasswordDO.copy(password = hash(masterPasswordDO.password))
        storageManager.update(0, MasterPasswordMapper.toDTO(encryptedPassword))
    }

    fun get(): MasterPasswordDO {
        // ? Currently mapping from dto to domain and then returning the domain. Can change that later
        val encryptedPassword = storageManager.retrieveAll().first()
        return MasterPasswordMapper.toDomain(encryptedPassword)
    }

    private fun hash(password: String): String {
        return password.hashCode().toString()
    }

    fun saveMPToFile() {
        saveToFile(file, storageManager.retrieveAll())
    }
}