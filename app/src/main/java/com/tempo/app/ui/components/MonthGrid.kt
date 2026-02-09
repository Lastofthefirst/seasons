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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tempo.app.domain.model.EventColor
import com.tempo.app.ui.theme.TempoDesign
import java.time.LocalDate
import java.time.YearMonth

private val DAY_LETTERS = listOf("M", "T", "W", "T", "F", "S", "S")

@Composable
fun MonthGrid(
    yearMonth: YearMonth,
    today: LocalDate,
    eventDots: Map<Int, List<EventColor>>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value // 1=Monday

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Day name headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DAY_LETTERS.forEach { letter ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = letter,
                        style = typography.locationChip.copy(color = palette.textMuted)
                    )
                }
            }
        }

        // Calendar grid
        val cells = mutableListOf<Int?>()
        for (i in 1 until firstDayOfWeek) cells.add(null)
        for (d in 1..daysInMonth) cells.add(d)
        while (cells.size % 7 != 0) cells.add(null)

        val weeks = cells.chunked(7)
        for (week in weeks) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (day in week) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .then(
                                if (day != null) {
                                    Modifier.clickable {
                                        onDateSelected(yearMonth.atDay(day))
                                    }
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (day != null) {
                            val isToday = day == today.dayOfMonth &&
                                    yearMonth.month == today.month &&
                                    yearMonth.year == today.year
                            val dots = eventDots[day] ?: emptyList()

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isToday) palette.accent else Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    BasicText(
                                        text = day.toString(),
                                        style = typography.body.copy(
                                            fontSize = 15.sp,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isToday) Color.White else palette.text
                                        )
                                    )
                                }
                                if (dots.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier.padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        dots.take(3).forEach { color ->
                                            val dotColor = when (color) {
                                                EventColor.ACCENT -> palette.accent
                                                EventColor.SECONDARY -> palette.secondary
                                                EventColor.TERTIARY -> palette.tertiary
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .clip(CircleShape)
                                                    .background(dotColor)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
