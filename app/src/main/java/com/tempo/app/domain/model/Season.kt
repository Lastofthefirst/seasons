/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.domain.model

import java.time.LocalDate

enum class Season(
    val label: String,
    val greeting: String,
    val emoji: String
) {
    SPRING("Spring", "Season of bloom", "\uD83C\uDF38"),
    SUMMER("Summer", "Golden hour", "☀\uFE0F"),
    AUTUMN("Autumn", "Amber light", "\uD83C\uDF42"),
    WINTER("Winter", "Crystal clear", "❄\uFE0F");

    companion object {
        fun current(): Season {
            val month = LocalDate.now().monthValue
            return when (month) {
                in 3..5 -> SPRING
                in 6..8 -> SUMMER
                in 9..11 -> AUTUMN
                else -> WINTER
            }
        }
    }
}
