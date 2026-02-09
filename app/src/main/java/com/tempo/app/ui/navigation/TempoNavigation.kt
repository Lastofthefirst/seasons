/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.tempo.app.domain.model.CalendarEvent
import com.tempo.app.ui.components.BottomNavBar
import com.tempo.app.ui.components.NavTab
import com.tempo.app.ui.components.TempoFAB
import com.tempo.app.ui.screens.*
import com.tempo.app.ui.theme.TempoDesign
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun TempoNavHost(
    events: List<CalendarEvent>,
    selectedDate: LocalDate,
    yearMonth: YearMonth,
    calendars: List<CalendarInfo>,
    lastSyncText: String,
    serverName: String,
    serverUrl: String,
    isSynced: Boolean,
    syncInterval: String,
    defaultReminder: String,
    allDayTime: String,
    notificationsEnabled: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChange: (Int) -> Unit,
    onEventTap: (CalendarEvent) -> Unit,
    onCreateEvent: (title: String, location: String, calendarIndex: Int, reminderIndex: Int, startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) -> Unit,
    onCalendarToggle: (Int, Boolean) -> Unit,
    onSync: () -> Unit,
    onSaveCalDav: (serverUrl: String, username: String, password: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = TempoDesign.palette

    var activeTab by remember { mutableStateOf(NavTab.TODAY) }
    var showNewEvent by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<CalendarEvent?>(null) }

    // (#12) Pre-compute filtered collections outside the Crossfade so that
    // O(n) work only re-runs when `events` or the relevant key changes,
    // not on every recomposition of the Crossfade content lambda.
    val today = LocalDate.now()

    val dayEvents = remember(events, selectedDate) {
        events.filter { it.startDateTime.toLocalDate() == selectedDate }
    }

    // (#4) Use Set<LocalDate> instead of Set<Int> to avoid month-boundary ambiguity.
    val eventDays: Set<LocalDate> = remember(events, selectedDate) {
        events.filter {
            val eventDate = it.startDateTime.toLocalDate()
            eventDate.month == selectedDate.month && eventDate.year == selectedDate.year
        }.map { it.startDateTime.toLocalDate() }.toSet()
    }

    val monthEventDots = remember(events, yearMonth) {
        events.filter {
            val eventDate = it.startDateTime.toLocalDate()
            eventDate.month == yearMonth.month && eventDate.year == yearMonth.year
        }.groupBy { it.startDateTime.toLocalDate().dayOfMonth }
            .mapValues { (_, evts) -> evts.map { it.color } }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = palette.background)
            .drawBehind {
                // Ambient glow 1 - top right
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(palette.glow1Color, palette.glow1Color.copy(alpha = 0f)),
                        center = Offset(size.width * 0.8f, size.height * 0.1f),
                        radius = 200.dp.toPx()
                    ),
                    radius = 200.dp.toPx(),
                    center = Offset(size.width * 0.8f, size.height * 0.1f)
                )
                // Ambient glow 2 - bottom left
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(palette.glow2Color, palette.glow2Color.copy(alpha = 0f)),
                        center = Offset(size.width * 0.15f, size.height * 0.75f),
                        radius = 180.dp.toPx()
                    ),
                    radius = 180.dp.toPx(),
                    center = Offset(size.width * 0.15f, size.height * 0.75f)
                )
            }
    ) {
        // Content area
        Crossfade(
            targetState = activeTab,
            animationSpec = tween(250),
            label = "screenTransition"
        ) { tab ->
            when (tab) {
                NavTab.TODAY -> TodayScreen(
                    date = selectedDate,
                    events = dayEvents,
                    lastSyncText = lastSyncText,
                    onEventTap = { event ->
                        selectedEvent = event
                        onEventTap(event)
                    },
                    onSync = onSync
                )
                NavTab.WEEK -> WeekScreen(
                    selectedDate = selectedDate,
                    today = today,
                    events = dayEvents,
                    eventDays = eventDays,
                    onDaySelected = onDateSelected,
                    onEventTap = { event ->
                        selectedEvent = event
                        onEventTap(event)
                    }
                )
                NavTab.MONTH -> MonthScreen(
                    yearMonth = yearMonth,
                    today = today,
                    eventDots = monthEventDots,
                    onDateSelected = { date ->
                        onDateSelected(date)
                        activeTab = NavTab.TODAY
                    },
                    onMonthChange = onMonthChange
                )
                NavTab.SETTINGS -> SettingsScreen(
                    serverName = serverName,
                    serverUrl = serverUrl,
                    isSynced = isSynced,
                    syncInterval = syncInterval,
                    calendars = calendars,
                    defaultReminder = defaultReminder,
                    allDayTime = allDayTime,
                    notificationsEnabled = notificationsEnabled,
                    onCalendarToggle = onCalendarToggle,
                    onSaveCalDav = onSaveCalDav,
                    onSync = onSync
                )
            }
        }

        // FAB
        if (activeTab != NavTab.SETTINGS) {
            TempoFAB(
                onClick = { showNewEvent = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 100.dp)
            )
        }

        // Bottom nav
        BottomNavBar(
            activeTab = activeTab,
            onTabSelected = { activeTab = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // New Event Sheet
        NewEventSheet(
            visible = showNewEvent,
            date = selectedDate,
            onDismiss = { showNewEvent = false },
            onCreateEvent = { title, location, calIndex, remIndex, startH, startM, endH, endM ->
                onCreateEvent(title, location, calIndex, remIndex, startH, startM, endH, endM)
                showNewEvent = false
            }
        )

        // Event Detail Sheet
        // (#15) Look up calendar name by matching event.calendarId to the
        // calendars list instead of using the fragile EventColor ordinal index.
        EventDetailSheet(
            visible = selectedEvent != null,
            event = selectedEvent,
            calendarName = selectedEvent?.let { evt ->
                calendars.find { it.id == evt.calendarId }?.name ?: "Personal"
            } ?: "Personal",
            onDismiss = { selectedEvent = null },
            onEdit = { selectedEvent = null },
            onShare = { selectedEvent = null }
        )
    }
}
