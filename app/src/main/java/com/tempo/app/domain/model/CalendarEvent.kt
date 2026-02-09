/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.domain.model

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class CalendarEvent(
    val id: String,
    val calendarId: String,
    val title: String,
    val description: String? = null,
    val location: String? = null,
    val startTime: Long,
    val endTime: Long,
    val allDay: Boolean = false,
    val reminderMinutesBefore: Int? = 15,
    val color: EventColor = EventColor.ACCENT,
    val icsUid: String? = null,
    val etag: String? = null,
    val calDavUrl: String? = null,
    val lastModified: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.LOCAL
) {
    val startDateTime: LocalDateTime
        get() = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault())

    val endDateTime: LocalDateTime
        get() = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault())

    val durationMinutes: Long
        get() = (endTime - startTime) / (1000 * 60)
}

enum class EventColor(val key: String) {
    ACCENT("accent"),
    SECONDARY("secondary"),
    TERTIARY("tertiary");

    companion object {
        fun fromKey(key: String): EventColor = entries.firstOrNull { it.key == key } ?: ACCENT
    }
}

enum class SyncStatus(val key: String) {
    SYNCED("synced"),
    LOCAL("local"),
    MODIFIED("modified"),
    DELETED("deleted");

    companion object {
        fun fromKey(key: String): SyncStatus = entries.firstOrNull { it.key == key } ?: LOCAL
    }
}
