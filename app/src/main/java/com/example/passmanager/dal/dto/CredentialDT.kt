package com.example.passmanager.dal.dto

import kotlinx.serialization.Serializable

@Serializable
data class CredentialDT(val platform: String, val email: String, val password: String)

fun compareCredentialDT(credentialDT1: CredentialDT, credentialDT2: CredentialDT): Boolean {
    return credentialDT1.platform == credentialDT2.platform && credentialDT1.email == credentialDT2.email
}