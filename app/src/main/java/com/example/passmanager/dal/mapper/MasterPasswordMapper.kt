package com.example.passmanager.dal.mapper

import com.example.passmanager.dal.dto.MasterPasswordDT
import com.example.passmanager.dal.domain.MasterPasswordDO

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