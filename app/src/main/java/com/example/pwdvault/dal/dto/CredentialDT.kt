package com.example.pwdvault.dal.dto

import kotlinx.serialization.Serializable

@Serializable
data class CredentialDT(val platform: String, val emailUsername: String, val password: String, val otherInfo: String)

fun compareCredentialDT(credentialDT1: CredentialDT, credentialDT2: CredentialDT): Boolean {
    return credentialDT1.platform == credentialDT2.platform && credentialDT1.emailUsername == credentialDT2.emailUsername
}