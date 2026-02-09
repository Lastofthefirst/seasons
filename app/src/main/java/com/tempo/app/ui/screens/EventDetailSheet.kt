/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tempo.app.domain.model.CalendarEvent
import com.tempo.app.domain.model.EventColor
import com.tempo.app.ui.components.TempoBottomSheet
import com.tempo.app.ui.theme.TempoDesign
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

@Composable
fun EventDetailSheet(
    visible: Boolean,
    event: CalendarEvent?,
    calendarName: String,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    if (event == null) return

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

    TempoBottomSheet(visible = visible, onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 8.dp)
                .padding(bottom = 40.dp)
        ) {
            // Title with color bar
            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(fg)
                )
                Column {
                    BasicText(
                        text = event.title,
                        style = typography.detailTitle.copy(color = palette.text)
                    )
                    BasicText(
                        text = calendarName,
                        style = typography.body.copy(color = palette.textSec),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Detail rows
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 28.dp)
            ) {
                // Time
                DetailRow(
                    icon = "\uD83D\uDD50",
                    bg = bg,
                    content = {
                        BasicText(
                            text = "${event.startDateTime.format(timeFormatter)} — ${event.endDateTime.format(timeFormatter)}",
                            style = typography.bodySemiBold.copy(color = palette.text)
                        )
                        val durationMin = event.durationMinutes
                        BasicText(
                            text = if (durationMin < 60) "${durationMin} min" else "${durationMin / 60}h${if (durationMin % 60 > 0) " ${durationMin % 60}m" else ""}",
                            style = typography.eventTime.copy(color = palette.textMuted)
                        )
                    }
                )

                // Location
                if (!event.location.isNullOrBlank()) {
                    DetailRow(
                        icon = "\uD83D\uDCCD",
                        bg = bg,
                        content = {
                            BasicText(
                                text = event.location,
                                style = typography.bodySemiBold.copy(color = palette.text)
                            )
                        }
                    )
                }

                // Reminder
                DetailRow(
                    icon = "\uD83D\uDD14",
                    bg = bg,
                    content = {
                        BasicText(
                            text = when (event.reminderMinutesBefore) {
                                null, 0 -> "No reminder"
                                5 -> "5 min before"
                                15 -> "15 min before"
                                60 -> "1 hour before"
                                else -> "${event.reminderMinutesBefore} min before"
                            },
                            style = typography.bodySemiBold.copy(color = palette.text)
                        )
                    }
                )
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Edit button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(palette.glass)
                        .border(1.5.dp, palette.border, RoundedCornerShape(16.dp))
                        .clickable(onClick = onEdit)
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = "Edit",
                        style = typography.bodySemiBold.copy(color = palette.text)
                    )
                }

                // Share button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(8.dp, RoundedCornerShape(16.dp), ambientColor = palette.fabGlow, spotColor = palette.fabGlow)
                        .clip(RoundedCornerShape(16.dp))
                        .background(palette.accent)
                        .clickable(onClick = onShare)
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = "Share",
                        style = typography.bodySemiBold.copy(color = palette.accentContrast)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: String,
    bg: androidx.compose.ui.graphics.Color,
    content: @Composable ColumnScope.() -> Unit
) {
    val typography = TempoDesign.typography

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                text = icon,
                style = typography.sheetTitle.copy(fontSize = 20.sp)
            )
        }
        Column(content = content)
    }
}
