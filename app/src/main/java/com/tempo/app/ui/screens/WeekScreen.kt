/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tempo.app.domain.model.CalendarEvent
import com.tempo.app.ui.components.EventCard
import com.tempo.app.ui.components.WeekStrip
import com.tempo.app.ui.theme.TempoDesign
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val monthFormatter = DateTimeFormatter.ofPattern("MMMM")

@Composable
fun WeekScreen(
    selectedDate: LocalDate,
    today: LocalDate,
    events: List<CalendarEvent>,
    eventDays: Set<LocalDate>,
    onDaySelected: (LocalDate) -> Unit,
    onEventTap: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Month header
        BasicText(
            text = selectedDate.format(monthFormatter),
            style = typography.sectionHeader.copy(color = palette.text),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )

        // Week strip
        WeekStrip(
            selectedDate = selectedDate,
            today = today,
            eventDays = eventDays,
            onDaySelected = onDaySelected,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Events or empty state
        if (events.isEmpty()) {
            EmptyDayState(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 100.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                itemsIndexed(events, key = { _, event -> event.id }) { index, event ->
                    val animatedAlpha = remember { Animatable(0f) }
                    val animatedOffset = remember { Animatable(12f) }

                    LaunchedEffect(event.id) {
                        kotlinx.coroutines.delay(index * 50L)
                        kotlinx.coroutines.launch {
                            animatedAlpha.animateTo(1f, tween(350))
                        }
                        animatedOffset.animateTo(0f, tween(350))
                    }

                    EventCard(
                        event = event,
                        onClick = { onEventTap(event) },
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 5.dp)
                            .graphicsLayer {
                                alpha = animatedAlpha.value
                                translationY = animatedOffset.value
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyDayState(modifier: Modifier = Modifier) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(palette.accentSoft),
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                text = "☀\uFE0F",
                style = typography.sectionHeader.copy(fontSize = 28.sp)
            )
        }
        Spacer(Modifier.height(16.dp))
        BasicText(
            text = "Nothing planned",
            style = typography.eventTitle.copy(color = palette.text)
        )
        Spacer(Modifier.height(4.dp))
        BasicText(
            text = "Enjoy your free day, or tap + to add something",
            style = typography.eventTime.copy(color = palette.textMuted)
        )
    }
}
