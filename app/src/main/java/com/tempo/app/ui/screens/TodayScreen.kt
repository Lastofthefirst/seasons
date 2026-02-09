/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.tempo.app.domain.model.CalendarEvent
import com.tempo.app.ui.components.EventCard
import com.tempo.app.ui.components.NowIndicator
import com.tempo.app.ui.components.SeasonBadge
import com.tempo.app.ui.theme.TempoDesign
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val DAYS_SHORT = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
private val dateFormatter = DateTimeFormatter.ofPattern("MMMM d")

@Composable
fun TodayScreen(
    date: LocalDate,
    events: List<CalendarEvent>,
    lastSyncText: String,
    onEventTap: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    val greeting = remember {
        val hour = LocalTime.now().hour
        when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    val dayOfWeek = DAYS_SHORT[date.dayOfWeek.value - 1]

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 100.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BasicText(
                        text = greeting,
                        style = typography.greeting.copy(color = palette.textSec)
                    )
                    SeasonBadge()
                }

                BasicText(
                    text = date.format(dateFormatter),
                    style = typography.screenTitle.copy(color = palette.text),
                    modifier = Modifier.padding(top = 2.dp)
                )

                BasicText(
                    text = "$dayOfWeek · ${events.size} events",
                    style = typography.greeting.copy(color = palette.textMuted),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Now indicator
        item {
            NowIndicator()
        }

        // Event cards with staggered animation
        itemsIndexed(events, key = { _, event -> event.id }) { index, event ->
            val animatedAlpha = remember { androidx.compose.animation.core.Animatable(0f) }
            val animatedOffset = remember { androidx.compose.animation.core.Animatable(16f) }

            LaunchedEffect(event.id) {
                kotlinx.coroutines.delay(index * 60L)
                kotlinx.coroutines.launch {
                    animatedAlpha.animateTo(1f, tween(400))
                }
                animatedOffset.animateTo(0f, tween(400))
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

        // Sync status footer
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(palette.secondary)
                )
                Spacer(Modifier.width(8.dp))
                BasicText(
                    text = lastSyncText,
                    style = typography.label.copy(color = palette.textMuted)
                )
            }
        }
    }
}
