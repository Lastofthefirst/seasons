/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tempo.app.domain.model.CalendarEvent
import com.tempo.app.domain.model.EventColor
import com.tempo.app.ui.theme.TempoDesign
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

@Composable
fun EventCard(
    event: CalendarEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    val fg = when (event.color) {
        EventColor.ACCENT -> palette.accent
        EventColor.SECONDARY -> palette.secondary
        EventColor.TERTIARY -> palette.tertiary
    }
    val bg = when (event.color) {
        EventColor.ACCENT -> palette.accentSoft
        EventColor.SECONDARY -> palette.secondarySoft
        EventColor.TERTIARY -> palette.tertiarySoft
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(18.dp, 18.dp, 20.dp, 18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Color bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .defaultMinSize(minHeight = 44.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(fg)
            )

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BasicText(
                    text = event.title,
                    style = typography.eventTitle.copy(color = palette.text)
                )
                BasicText(
                    text = "${event.startDateTime.format(timeFormatter)} — ${event.endDateTime.format(timeFormatter)}",
                    style = typography.eventTime.copy(color = palette.textSec)
                )
                if (!event.location.isNullOrBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(bg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        BasicText(
                            text = "\uD83D\uDCCD ${event.location}",
                            style = typography.locationChip.copy(color = fg)
                        )
                    }
                }
            }

            // Color dot
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(bg)
            )
        }
    }
}
