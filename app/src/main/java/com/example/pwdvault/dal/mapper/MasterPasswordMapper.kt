package com.example.pwdvault.dal.mapper

import com.example.pwdvault.dal.dto.MasterPasswordDT
import com.example.pwdvault.dal.domain.MasterPasswordDO

object MasterPasswordMapper {
    fun toDomain(dto: MasterPasswordDT): MasterPasswordDO {
        return MasterPasswordDO(
            password = dto.password
        )
    }

    fun toDTO(domain: MasterPasswordDO): MasterPasswordDT {
        return MasterPasswordDT(
            password = domain.password
        )
    }
}