package com.example.passmanager.dal.domain

data class CredentialDO(
    val platform: String,
    val email: String,
    val password: String,
)

fun compareCredentialDO(cred1: CredentialDO, cred2: CredentialDO): Boolean {
    return cred1.platform == cred2.platform && cred1.email == cred2.email
}