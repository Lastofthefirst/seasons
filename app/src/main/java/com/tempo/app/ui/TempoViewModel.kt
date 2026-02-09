/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tempo.app.data.local.db.TempoDatabase
import com.tempo.app.data.local.preferences.TempoPreferences
import com.tempo.app.data.repository.CalendarRepository
import com.tempo.app.data.repository.EventRepository
import com.tempo.app.domain.model.Calendar
import com.tempo.app.domain.model.CalendarEvent
import com.tempo.app.domain.model.EventColor
import com.tempo.app.domain.model.SyncStatus
import com.tempo.app.worker.CalDavSyncWorker
import com.tempo.app.worker.NotificationScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.util.UUID

class TempoViewModel(application: Application) : AndroidViewModel(application) {

    private val database = TempoDatabase.getInstance(application)
    private val eventRepository = EventRepository(database.eventDao())
    private val calendarRepository = CalendarRepository(database.calendarDao())
    val preferences = TempoPreferences(application)
    private val notificationScheduler = NotificationScheduler(application)

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _yearMonth = MutableStateFlow(YearMonth.now())
    val yearMonth: StateFlow<YearMonth> = _yearMonth.asStateFlow()

    val allEvents: StateFlow<List<CalendarEvent>> = eventRepository.getAllEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val calendars: StateFlow<List<Calendar>> = calendarRepository.getAllCalendars()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter events by enabled calendars (#20)
    val events: StateFlow<List<CalendarEvent>> = combine(allEvents, calendars) { events, cals ->
        val enabledIds = cals.filter { it.enabled }.map { it.id }.toSet()
        events.filter { it.calendarId in enabledIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lastSyncTime: StateFlow<Long> = preferences.lastSyncTime
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val serverUrl: StateFlow<String> = preferences.calDavServerUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val syncInterval: StateFlow<Int> = preferences.syncIntervalMinutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 15)

    val defaultReminder: StateFlow<Int> = preferences.defaultReminderMinutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 15)

    val notificationsEnabled: StateFlow<Boolean> = preferences.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    init {
        notificationScheduler.createNotificationChannel()
        ensureDefaultCalendars()
    }

    private fun ensureDefaultCalendars() {
        viewModelScope.launch {
            val existing = calendarRepository.getAllCalendars().first()
            if (existing.isEmpty()) {
                val defaults = listOf(
                    Calendar(
                        id = UUID.randomUUID().toString(),
                        name = "Personal",
                        color = EventColor.ACCENT,
                        enabled = true
                    ),
                    Calendar(
                        id = UUID.randomUUID().toString(),
                        name = "Work",
                        color = EventColor.SECONDARY,
                        enabled = true
                    ),
                    Calendar(
                        id = UUID.randomUUID().toString(),
                        name = "Family",
                        color = EventColor.TERTIARY,
                        enabled = true
                    )
                )
                calendarRepository.insertCalendars(defaults)
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        _yearMonth.value = YearMonth.of(date.year, date.month)
    }

    fun changeMonth(direction: Int) {
        _yearMonth.value = _yearMonth.value.plusMonths(direction.toLong())
    }

    fun createEvent(
        title: String,
        location: String,
        calendarIndex: Int,
        reminderIndex: Int,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int
    ) {
        viewModelScope.launch {
            val cals = calendars.value
            val cal = cals.getOrNull(calendarIndex) ?: cals.firstOrNull() ?: return@launch
            val calendarId = cal.id
            val color = cal.color
            val reminderMinutes = when (reminderIndex) {
                0 -> null
                1 -> 5
                2 -> 15
                3 -> 60
                else -> 15
            }

            val date = _selectedDate.value
            val startTime = date.atTime(startHour, startMinute).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endTime = date.atTime(endHour, endMinute).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val event = CalendarEvent(
                id = UUID.randomUUID().toString(),
                calendarId = calendarId,
                title = title,
                location = location.ifBlank { null },
                startTime = startTime,
                endTime = endTime,
                color = color,
                reminderMinutesBefore = reminderMinutes,
                syncStatus = SyncStatus.LOCAL
            )

            val created = eventRepository.createEvent(event)
            notificationScheduler.scheduleNotification(created)
        }
    }

    fun toggleCalendar(index: Int, enabled: Boolean) {
        viewModelScope.launch {
            val cal = calendars.value.getOrNull(index) ?: return@launch
            calendarRepository.setCalendarEnabled(cal.id, enabled)
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            notificationScheduler.cancelNotification(eventId)
            eventRepository.deleteEvent(eventId)
        }
    }

    fun triggerSync() {
        CalDavSyncWorker.enqueueOneTimeSync(getApplication())
    }

    fun saveCalDavCredentials(serverUrl: String, username: String, password: String) {
        viewModelScope.launch {
            preferences.setCalDavCredentials(serverUrl, username, password)
        }
    }

    fun getLastSyncText(lastSync: Long): String {
        if (lastSync == 0L) return "Not synced yet"
        val diff = System.currentTimeMillis() - lastSync
        val minutes = diff / (1000 * 60)
        return when {
            minutes < 1 -> "CalDAV synced · just now"
            minutes < 60 -> "CalDAV synced · ${minutes} min ago"
            else -> {
                val hours = minutes / 60
                "CalDAV synced · ${hours}h ago"
            }
        }
    }
}
