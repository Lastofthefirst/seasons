/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

@Immutable
data class TempoShapes(
    val card: RoundedCornerShape = RoundedCornerShape(22.dp),
    val cardSmall: RoundedCornerShape = RoundedCornerShape(16.dp),
    val pill: RoundedCornerShape = RoundedCornerShape(16.dp),
    val fab: RoundedCornerShape = RoundedCornerShape(20.dp),
    val bottomSheet: RoundedCornerShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    val input: RoundedCornerShape = RoundedCornerShape(18.dp),
    val inputSmall: RoundedCornerShape = RoundedCornerShape(14.dp),
    val badge: RoundedCornerShape = RoundedCornerShape(8.dp),
    val locationChip: RoundedCornerShape = RoundedCornerShape(10.dp),
    val iconContainer: RoundedCornerShape = RoundedCornerShape(14.dp),
    val toggle: RoundedCornerShape = RoundedCornerShape(13.dp),
    val monthDay: RoundedCornerShape = RoundedCornerShape(12.dp),
    val navButton: RoundedCornerShape = RoundedCornerShape(14.dp),
    val colorDot: RoundedCornerShape = RoundedCornerShape(5.dp),
    val closeButton: RoundedCornerShape = RoundedCornerShape(10.dp)
)

val LocalTempoShapes = staticCompositionLocalOf { TempoShapes() }
