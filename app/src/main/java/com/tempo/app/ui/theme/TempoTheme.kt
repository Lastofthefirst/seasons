/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.tempo.app.domain.model.Season

@Composable
fun TempoTheme(
    season: Season = Season.current(),
    content: @Composable () -> Unit
) {
    val palette = remember(season) {
        when (season) {
            Season.SPRING -> SpringPalette
            Season.SUMMER -> SummerPalette
            Season.AUTUMN -> AutumnPalette
            Season.WINTER -> WinterPalette
        }
    }

    val typography = remember { TempoTypography() }
    val shapes = remember { TempoShapes() }

    CompositionLocalProvider(
        LocalTempoPalette provides palette,
        LocalTempoTypography provides typography,
        LocalTempoShapes provides shapes,
        content = content
    )
}

object TempoDesign {
    val palette: TempoPalette
        @Composable get() = LocalTempoPalette.current

    val typography: TempoTypography
        @Composable get() = LocalTempoTypography.current

    val shapes: TempoShapes
        @Composable get() = LocalTempoShapes.current
}
