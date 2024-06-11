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
        val encryptedPasswordList: List<MasterPasswordDT> = storageManager.retrieveAll()
        // If the list is empty, return an empty password
        val encryptedPassword = if (encryptedPasswordList.isEmpty()) MasterPasswordMapper.toDTO(MasterPasswordDO(""))
        // Otherwise, return the first element
        else encryptedPasswordList.first()
        return MasterPasswordMapper.toDomain(encryptedPassword)
    }

    fun check(password: MasterPasswordDO): Boolean {
        val encryptedPassword = get()
        println("Encrypted password: ${encryptedPassword.password}")
        println("Password: ${password.password}")
        return password == encryptedPassword//hash(password.password) == encryptedPassword.password
    }

    private fun hash(password: String): String {
        return password.hashCode().toString()
    }

    fun saveMPToFile() {
        saveToFile(file, storageManager.retrieveAll())
    }
}