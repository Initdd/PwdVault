package com.example.pwdvault.control

import com.example.pwdvault.control.encryption.HashingManager
import com.example.pwdvault.dal.Storage
import com.example.pwdvault.dal.domain.MasterPasswordDO
import com.example.pwdvault.dal.dto.MasterPasswordDT
import com.example.pwdvault.dal.mapper.MasterPasswordMapper
import com.example.pwdvault.dal.saveToFile
import java.io.File

class MasterPasswordManager (
    private val storageManager: Storage<Int, MasterPasswordDT>,
    private val file: File
) {

    private val defaultMasterPwd = "123"

    init {
        // Check if the file exists and create it if it doesn't
        if (!file.exists()) file.createNewFile()
        // Check if the master password is empty
        if (storageManager.retrieveAll().isEmpty()) {
            storageManager.store(MasterPasswordMapper.toDTO(
                MasterPasswordDO(password = hash(defaultMasterPwd))
            ))
        }
    }

    fun set(masterPasswordDO: MasterPasswordDO) {
        // Hash the password before storing it
        val encryptedPassword = masterPasswordDO.copy(password = hash(masterPasswordDO.password))
        storageManager.update(0, MasterPasswordMapper.toDTO(encryptedPassword))
    }

    fun get(): MasterPasswordDO {
        // ? Currently mapping from dto to domain and then returning the domain. Can change that later
        val encryptedPasswordList: List<MasterPasswordDT> = storageManager.retrieveAll()
        // If the list is empty, save and return an empty password
        val encryptedPassword = encryptedPasswordList.first()
        return MasterPasswordMapper.toDomain(encryptedPassword)
    }

    fun check(password: MasterPasswordDO): Boolean {
        val encryptedPassword = get()
        return hash(password.password) == encryptedPassword.password
    }

    private fun hash(password: String): String {
        return HashingManager.hashString(password)
    }

    fun saveMPToFile() {
        saveToFile(file, storageManager.retrieveAll())
    }
}