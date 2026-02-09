/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tempo.app.ui.theme.TempoDesign

@Composable
fun SeasonBadge(
    modifier: Modifier = Modifier
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(palette.accentSoft)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        BasicText(
            text = palette.greeting,
            style = typography.seasonTag.copy(color = palette.accent)
        )
    }
}
