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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tempo.app.domain.model.EventColor
import com.tempo.app.ui.components.TempoBottomSheet
import com.tempo.app.ui.theme.TempoDesign
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("MMM d")
private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

@Composable
fun NewEventSheet(
    visible: Boolean,
    date: LocalDate,
    onDismiss: () -> Unit,
    onCreateEvent: (title: String, location: String, calendarIndex: Int, reminderIndex: Int) -> Unit
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedCalendar by remember { mutableIntStateOf(0) }
    var selectedReminder by remember { mutableIntStateOf(2) } // Default 15 min

    val calendarNames = listOf("Personal", "Work", "Family")
    val calendarColors = listOf(palette.accent, palette.secondary, palette.tertiary)
    val reminderOptions = listOf("None", "5 min", "15 min", "1 hour")

    TempoBottomSheet(visible = visible, onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = "New Event",
                    style = typography.sheetTitle.copy(color = palette.text)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.surface)
                        .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = "✕",
                        style = typography.body.copy(color = palette.textSec)
                    )
                }
            }

            // Title input
            GlassInput(
                value = title,
                onValueChange = { title = it },
                placeholder = "Event title",
                textSize = 16
            )

            // Start / End time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    BasicText(
                        text = "START",
                        style = typography.label.copy(color = palette.textMuted),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(palette.glass)
                            .border(1.5.dp, palette.border, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        BasicText(
                            text = "${date.format(dateFormatter)}, ${LocalTime.of(9, 0).format(timeFormatter)}",
                            style = typography.body.copy(color = palette.text)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    BasicText(
                        text = "END",
                        style = typography.label.copy(color = palette.textMuted),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(palette.glass)
                            .border(1.5.dp, palette.border, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        BasicText(
                            text = "${date.format(dateFormatter)}, ${LocalTime.of(10, 0).format(timeFormatter)}",
                            style = typography.body.copy(color = palette.text)
                        )
                    }
                }
            }

            // Location input
            GlassInput(
                value = location,
                onValueChange = { location = it },
                placeholder = "Add location",
                textSize = 14
            )

            // Calendar selector
            Column {
                BasicText(
                    text = "CALENDAR",
                    style = typography.label.copy(color = palette.textMuted),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    calendarNames.forEachIndexed { index, name ->
                        val isSelected = selectedCalendar == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) palette.accentSoft else palette.glass)
                                .border(
                                    1.5.dp,
                                    if (isSelected) palette.accent.copy(alpha = 0.25f) else palette.border,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedCalendar = index }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(calendarColors[index])
                                )
                                BasicText(
                                    text = name,
                                    style = typography.eventTime.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = if (isSelected) palette.accent else palette.textSec
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Reminder selector
            Column {
                BasicText(
                    text = "REMINDER",
                    style = typography.label.copy(color = palette.textMuted),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    reminderOptions.forEachIndexed { index, label ->
                        val isSelected = selectedReminder == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) palette.accentSoft else palette.glass)
                                .border(
                                    1.5.dp,
                                    if (isSelected) palette.accent.copy(alpha = 0.25f) else palette.border,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedReminder = index }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            BasicText(
                                text = label,
                                style = typography.eventTime.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) palette.accent else palette.textSec
                                )
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Create button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(18.dp), ambientColor = palette.fabGlow, spotColor = palette.fabGlow)
                    .clip(RoundedCornerShape(18.dp))
                    .background(palette.accent)
                    .clickable {
                        if (title.isNotBlank()) {
                            onCreateEvent(title, location, selectedCalendar, selectedReminder)
                            title = ""
                            location = ""
                        }
                    }
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    text = "Create Event",
                    style = typography.button.copy(color = palette.accentContrast)
                )
            }
        }
    }
}

@Composable
private fun GlassInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    textSize: Int = 14
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(palette.glass)
            .border(1.5.dp, palette.border, RoundedCornerShape(18.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        if (value.isEmpty()) {
            BasicText(
                text = placeholder,
                style = typography.body.copy(
                    color = palette.textMuted,
                    fontSize = textSize.sp
                )
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = typography.body.copy(
                color = palette.text,
                fontSize = textSize.sp,
                fontWeight = if (textSize > 14) FontWeight.Medium else FontWeight.Normal
            ),
            cursorBrush = SolidColor(palette.accent),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
