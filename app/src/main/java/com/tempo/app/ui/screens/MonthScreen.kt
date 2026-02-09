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
import androidx.compose.ui.unit.dp
import com.tempo.app.domain.model.EventColor
import com.tempo.app.ui.components.MonthGrid
import com.tempo.app.ui.theme.TempoDesign
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private val monthFormatter = DateTimeFormatter.ofPattern("MMMM")

@Composable
fun MonthScreen(
    yearMonth: YearMonth,
    today: LocalDate,
    eventDots: Map<Int, List<EventColor>>,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 100.dp)
    ) {
        // Header with month nav
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                BasicText(
                    text = yearMonth.format(monthFormatter),
                    style = typography.sectionHeader.copy(color = palette.text)
                )
                BasicText(
                    text = yearMonth.year.toString(),
                    style = typography.greeting.copy(color = palette.textMuted)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                NavArrowButton(text = "‹") { onMonthChange(-1) }
                NavArrowButton(text = "›") { onMonthChange(1) }
            }
        }

        // Month grid
        MonthGrid(
            yearMonth = yearMonth,
            today = today,
            eventDots = eventDots,
            onDateSelected = onDateSelected,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun NavArrowButton(
    text: String,
    onClick: () -> Unit
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(palette.glass)
            .border(1.dp, palette.border, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = text,
            style = typography.sectionHeader.copy(color = palette.textSec)
        )
    }
}
