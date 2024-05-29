package com.example.passmanager.dal.domain

data class MasterPasswordDO(
    val password: String
)

fun compareMasterPasswordDO(mp1: MasterPasswordDO, mp2: MasterPasswordDO): Boolean {
    return mp1.password == mp2.password
}