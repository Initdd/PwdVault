package com.example.pwdvault.dal.dto

import kotlinx.serialization.Serializable

@Serializable
data class MasterPasswordDT(val password: String)

fun compareMasterPasswordDT(masterPasswordDT1: MasterPasswordDT, masterPasswordDT2: MasterPasswordDT): Boolean {
    return masterPasswordDT1.password == masterPasswordDT2.password
}