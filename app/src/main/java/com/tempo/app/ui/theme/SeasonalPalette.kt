/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class TempoPalette(
    val background: Brush,
    val glass: Color,
    val glassFallback: Color,
    val text: Color,
    val textSec: Color,
    val textMuted: Color,
    val accent: Color,
    val accentSoft: Color,
    val secondary: Color,
    val secondarySoft: Color,
    val tertiary: Color,
    val tertiarySoft: Color,
    val border: Color,
    val nowColor: Color,
    val fabGlow: Color,
    val greeting: String,
    val seasonLabel: String,
    val seasonEmoji: String,
    val glow1Color: Color,
    val glow2Color: Color,
    val surface: Color,
    val accentContrast: Color
)

val SpringPalette = TempoPalette(
    background = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFDE8EF),
            Color(0xFFFCF0E8),
            Color(0xFFEDF4E8),
            Color(0xFFE4F0EC)
        )
    ),
    glass = Color(0x80FFFFFF),           // rgba(255,255,255,0.5)
    glassFallback = Color(0xC0FFFFFF),   // higher opacity fallback
    text = Color(0xFF2D3028),
    textSec = Color(0xFF6B705C),
    textMuted = Color(0xFFA3A898),
    accent = Color(0xFFD4728C),
    accentSoft = Color(0x1AD4728C),      // rgba(212,114,140,0.1)
    secondary = Color(0xFF5B9E6F),
    secondarySoft = Color(0x1A5B9E6F),
    tertiary = Color(0xFFC49B5A),
    tertiarySoft = Color(0x1AC49B5A),
    border = Color(0x8CFFFFFF),          // rgba(255,255,255,0.55)
    nowColor = Color(0xFFD4728C),
    fabGlow = Color(0x4DD4728C),
    greeting = "Season of bloom",
    seasonLabel = "Spring",
    seasonEmoji = "\uD83C\uDF38",
    glow1Color = Color(0x1FD4728C),      // 12% opacity
    glow2Color = Color(0x1A5B9E6F),      // 10% opacity
    surface = Color(0x80FFFFFF),
    accentContrast = Color.White
)

val SummerPalette = TempoPalette(
    background = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFEF3E2),
            Color(0xFFFDE8C8),
            Color(0xFFF0E0D0),
            Color(0xFFE4D8F0)
        )
    ),
    glass = Color(0x80FFFFFF),
    glassFallback = Color(0xC0FFFFFF),
    text = Color(0xFF2A2440),
    textSec = Color(0xFF6E6588),
    textMuted = Color(0xFF9E96B2),
    accent = Color(0xFFE8883C),
    accentSoft = Color(0x1FE8883C),      // 12% opacity
    secondary = Color(0xFF7B6BE0),
    secondarySoft = Color(0x1A7B6BE0),
    tertiary = Color(0xFFE06880),
    tertiarySoft = Color(0x1AE06880),
    border = Color(0x80FFFFFF),
    nowColor = Color(0xFFE8883C),
    fabGlow = Color(0x4DE8883C),
    greeting = "Golden hour",
    seasonLabel = "Summer",
    seasonEmoji = "☀\uFE0F",
    glow1Color = Color(0x1AE8883C),
    glow2Color = Color(0x147B6BE0),
    surface = Color(0x80FFFFFF),
    accentContrast = Color.White
)

val AutumnPalette = TempoPalette(
    background = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF0E6DC),
            Color(0xFFEAD8C8),
            Color(0xFFE4D4CC),
            Color(0xFFDCD0D8)
        )
    ),
    glass = Color(0x7AFFFFFF),           // rgba(255,255,255,0.48)
    glassFallback = Color(0xBFFFFFFF),
    text = Color(0xFF2C2420),
    textSec = Color(0xFF7A6A5C),
    textMuted = Color(0xFFA89888),
    accent = Color(0xFFC4623A),
    accentSoft = Color(0x1AC4623A),
    secondary = Color(0xFF8B6E4E),
    secondarySoft = Color(0x1A8B6E4E),
    tertiary = Color(0xFFA85858),
    tertiarySoft = Color(0x1AA85858),
    border = Color(0x80FFFFFF),
    nowColor = Color(0xFFC4623A),
    fabGlow = Color(0x4DC4623A),
    greeting = "Amber light",
    seasonLabel = "Autumn",
    seasonEmoji = "\uD83C\uDF42",
    glow1Color = Color(0x1AC4623A),
    glow2Color = Color(0x14A85858),
    surface = Color(0x7AFFFFFF),
    accentContrast = Color.White
)

val WinterPalette = TempoPalette(
    background = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE6EFF8),
            Color(0xFFD8E8F4),
            Color(0xFFE4E0F2),
            Color(0xFFF0ECF6)
        )
    ),
    glass = Color(0x94FFFFFF),           // rgba(255,255,255,0.58)
    glassFallback = Color(0xD9FFFFFF),
    text = Color(0xFF1E2A3A),
    textSec = Color(0xFF5E7088),
    textMuted = Color(0xFF94A3B8),
    accent = Color(0xFF3B82D4),
    accentSoft = Color(0x1A3B82D4),
    secondary = Color(0xFFA78BDA),
    secondarySoft = Color(0x1AA78BDA),
    tertiary = Color(0xFF5EA8A0),
    tertiarySoft = Color(0x1A5EA8A0),
    border = Color(0xA6FFFFFF),          // rgba(255,255,255,0.65)
    nowColor = Color(0xFF3B82D4),
    fabGlow = Color(0x403B82D4),
    greeting = "Crystal clear",
    seasonLabel = "Winter",
    seasonEmoji = "❄\uFE0F",
    glow1Color = Color(0x143B82D4),
    glow2Color = Color(0x14A78BDA),
    surface = Color(0x94FFFFFF),
    accentContrast = Color.White
)

val LocalTempoPalette = staticCompositionLocalOf { WinterPalette }
