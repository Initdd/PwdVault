package com.example.passmanager.dal.domain

data class CredentialDO(
    val platform: String,
    val emailUsername: String,
    val password: String,
    val otherInfo: List<String>,
)

fun compareCredentialDO(cred1: CredentialDO, cred2: CredentialDO): Boolean {
    return cred1.platform == cred2.platform && cred1.emailUsername == cred2.emailUsername
}