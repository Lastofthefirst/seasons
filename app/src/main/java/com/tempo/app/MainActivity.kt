/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tempo.app.domain.model.EventColor
import com.tempo.app.ui.TempoViewModel
import com.tempo.app.ui.navigation.TempoNavHost
import com.tempo.app.ui.screens.CalendarInfo
import com.tempo.app.ui.theme.TempoDesign
import com.tempo.app.ui.theme.TempoTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            TempoTheme {
                val viewModel: TempoViewModel = viewModel()
                val palette = TempoDesign.palette

                val events by viewModel.events.collectAsState()
                val calendars by viewModel.calendars.collectAsState()
                val selectedDate by viewModel.selectedDate.collectAsState()
                val yearMonth by viewModel.yearMonth.collectAsState()
                val lastSyncTime by viewModel.lastSyncTime.collectAsState()
                val serverUrl by viewModel.serverUrl.collectAsState()
                val syncInterval by viewModel.syncInterval.collectAsState()
                val defaultReminder by viewModel.defaultReminder.collectAsState()
                val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()

                val calendarInfos = calendars.map { cal ->
                    val eventCount = events.count { it.calendarId == cal.id }
                    val color = when (cal.color) {
                        EventColor.ACCENT -> palette.accent
                        EventColor.SECONDARY -> palette.secondary
                        EventColor.TERTIARY -> palette.tertiary
                    }
                    CalendarInfo(
                        id = cal.id,
                        name = cal.name,
                        color = color,
                        eventCount = eventCount,
                        enabled = cal.enabled
                    )
                }

                TempoNavHost(
                    events = events,
                    selectedDate = selectedDate,
                    yearMonth = yearMonth,
                    calendars = calendarInfos,
                    lastSyncText = viewModel.getLastSyncText(lastSyncTime),
                    serverName = if (serverUrl.isNotBlank()) {
                        try {
                            java.net.URI.create(serverUrl).host ?: "Server"
                        } catch (e: Exception) { "Server" }
                    } else "",
                    serverUrl = serverUrl,
                    isSynced = lastSyncTime > 0L,
                    syncInterval = "${syncInterval} min",
                    defaultReminder = "${defaultReminder} min before",
                    allDayTime = "9:00 AM",
                    notificationsEnabled = notificationsEnabled,
                    onDateSelected = { viewModel.selectDate(it) },
                    onMonthChange = { viewModel.changeMonth(it) },
                    onEventTap = { /* handled in nav */ },
                    onCreateEvent = { title, location, calIndex, remIndex, startH, startM, endH, endM ->
                        viewModel.createEvent(title, location, calIndex, remIndex, startH, startM, endH, endM)
                    },
                    onCalendarToggle = { index, enabled ->
                        viewModel.toggleCalendar(index, enabled)
                    },
                    onSync = { viewModel.triggerSync() },
                    onSaveCalDav = { url, user, pass ->
                        viewModel.saveCalDavCredentials(url, user, pass)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                )
            }
        }
    }
}
