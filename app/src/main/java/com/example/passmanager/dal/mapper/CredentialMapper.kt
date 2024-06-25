package com.example.passmanager.dal.mapper

import com.example.passmanager.dal.domain.CredentialDO
import com.example.passmanager.dal.dto.CredentialDT

object CredentialMapper {

    private const val DELIMITER = "\n"

    fun toDomain(dto: CredentialDT): CredentialDO {
        return CredentialDO(
            platform = dto.platform,
            emailUsername = dto.emailUsername,
            password = dto.password,
            otherInfo = dto.otherInfo.trim().split(DELIMITER).filter { it.isNotEmpty() }
        )
    }

    fun toDTO(domain: CredentialDO): CredentialDT {
        return CredentialDT(
            platform = domain.platform,
            emailUsername = domain.emailUsername,
            password = domain.password,
            otherInfo = domain.otherInfo.joinToString(DELIMITER)
        )
    }
}