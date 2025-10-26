package com.example.pwdvault.dal.mapper

import com.example.pwdvault.dal.dto.ThemeModeDT
import com.example.pwdvault.dal.domain.ThemeModeDO

object ThemeModeMapper {
    fun toDomain(dto: ThemeModeDT): ThemeModeDO {
        return when (dto.theme) {
            "LIGHT" -> ThemeModeDO.LIGHT
            "DARK" -> ThemeModeDO.DARK
            else -> throw IllegalArgumentException("Invalid theme mode")
        }
    }

    fun toDTO(domain: ThemeModeDO): ThemeModeDT {
        return ThemeModeDT(
            theme = when (domain) {
                ThemeModeDO.LIGHT -> "LIGHT"
                ThemeModeDO.DARK -> "DARK"
            }
        )
    }
}