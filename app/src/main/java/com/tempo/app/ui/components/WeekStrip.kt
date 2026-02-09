/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tempo.app.ui.theme.TempoDesign
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

private val DAY_LETTERS = listOf("M", "T", "W", "T", "F", "S", "S")

@Composable
fun WeekStrip(
    selectedDate: LocalDate,
    today: LocalDate,
    eventDays: Set<LocalDate>,
    onDaySelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    val weekStart = selectedDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
    val weekDates = (0..6).map { weekStart.plusDays(it.toLong()) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        weekDates.forEachIndexed { index, date ->
            val isSelected = date == selectedDate
            val isToday = date == today
            val hasEvents = eventDays.contains(date)

            val bgColor by animateColorAsState(
                targetValue = if (isSelected) palette.accent else Color.Transparent,
                animationSpec = tween(250),
                label = "weekStripBg"
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgColor)
                    .clickable { onDaySelected(date) }
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BasicText(
                    text = DAY_LETTERS[index],
                    style = typography.weekDayLetter.copy(
                        color = if (isSelected) Color.White.copy(alpha = 0.7f) else palette.textMuted
                    )
                )
                BasicText(
                    text = date.dayOfMonth.toString(),
                    style = typography.weekDayLetter.copy(
                        fontSize = 18.sp,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                        color = when {
                            isSelected -> Color.White
                            isToday -> palette.accent
                            else -> palette.text
                        }
                    )
                )
                if (hasEvents) {
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color.White.copy(alpha = 0.55f) else palette.accent
                            )
                    )
                } else {
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}
