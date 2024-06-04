package com.example.passmanager.dal.mapper

import com.example.passmanager.dal.domain.CredentialDO
import com.example.passmanager.dal.dto.CredentialDT

object CredentialMapper {
    fun toDomain(dto: CredentialDT): CredentialDO {
        return CredentialDO(
            platform = dto.platform,
            email = dto.email,
            password = dto.password
        )
    }

    fun toDTO(domain: CredentialDO): CredentialDT {
        return CredentialDT(
            platform = domain.platform,
            email = domain.email,
            password = domain.password
        )
    }
}