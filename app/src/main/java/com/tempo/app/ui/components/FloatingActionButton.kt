/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tempo.app.ui.theme.TempoDesign

@Composable
fun TempoFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = TempoDesign.palette
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .size(58.dp)
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = palette.fabGlow,
                spotColor = palette.fabGlow
            )
            .clip(shape)
            .background(palette.accent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = "+",
            style = TextStyle(
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Light
            )
        )
    }
}
