/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tempo.app.ui.theme.TempoDesign
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun NowIndicator(
    modifier: Modifier = Modifier
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography
    val currentTime = LocalTime.now()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // NOW badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(palette.accentSoft)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            BasicText(
                text = "NOW",
                style = typography.nowBadge.copy(color = palette.nowColor)
            )
        }

        // Line
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.5.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(palette.nowColor.copy(alpha = 0.35f))
        )

        // Time
        BasicText(
            text = currentTime.format(timeFormat),
            style = typography.nowBadge.copy(
                color = palette.nowColor,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
