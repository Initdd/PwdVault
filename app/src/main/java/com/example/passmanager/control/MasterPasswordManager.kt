package com.example.passmanager.control

import com.example.passmanager.control.encryption.HashingManager
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

    init {
        // Check if the file exists and create it if it doesn't
        if (!file.exists()) file.createNewFile()
        get()
    }

    fun set(masterPasswordDO: MasterPasswordDO) {
        // Hash the password before storing it
        val encryptedPassword = masterPasswordDO.copy(password = hash(masterPasswordDO.password))
        // check if there is no password stored, if so, store the password
        if (storageManager.retrieveAll().isEmpty()) {
            storageManager.store(MasterPasswordMapper.toDTO(encryptedPassword))
        }
        storageManager.update(0, MasterPasswordMapper.toDTO(encryptedPassword))
    }

    fun get(): MasterPasswordDO {
        // ? Currently mapping from dto to domain and then returning the domain. Can change that later
        val encryptedPasswordList: List<MasterPasswordDT> = storageManager.retrieveAll()
        // If the list is empty, save and return an empty password
        val encryptedPassword = if (encryptedPasswordList.isEmpty()) {
            val emptyPassword = MasterPasswordDO("")
            set(emptyPassword)
            MasterPasswordMapper.toDTO(emptyPassword)
        }
        // Otherwise, return the first element
        else encryptedPasswordList.first()
        return MasterPasswordMapper.toDomain(encryptedPassword)
    }

    fun check(password: MasterPasswordDO): Boolean {
        val encryptedPassword = get()
        println("Encrypted password: ${encryptedPassword.password}")
        println("Password: ${password.password}")
        return hash(password.password) == encryptedPassword.password
    }

    private fun hash(password: String): String {
        return HashingManager.hashString(password)
    }

    fun saveMPToFile() {
        saveToFile(file, storageManager.retrieveAll())
    }
}