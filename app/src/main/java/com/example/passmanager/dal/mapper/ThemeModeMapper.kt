package com.example.passmanager.dal.mapper

import com.example.passmanager.dal.dto.ThemeModeDT
import com.example.passmanager.dal.domain.ThemeModeDO

object ThemeModeMapper {
    fun toDomain(dto: ThemeModeDT): ThemeModeDO {
        return when (dto.theme) {
            "LIGHT" -> ThemeModeDO.LIGHT
            "DARK" -> ThemeModeDO.DARK
            else -> throw IllegalArgumentException("Invalid theme mode")
        }
    }

    fun toDto(domain: ThemeModeDO): ThemeModeDT {
        return ThemeModeDT(
            theme = when (domain) {
                ThemeModeDO.LIGHT -> "LIGHT"
                ThemeModeDO.DARK -> "DARK"
            }
        )
    }
}