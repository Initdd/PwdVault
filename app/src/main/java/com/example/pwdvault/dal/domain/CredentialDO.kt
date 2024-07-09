package com.example.pwdvault.dal.domain

data class CredentialDO(
    val platform: String,
    val emailUsername: String,
    val password: String,
    val otherInfo: List<String>,
)