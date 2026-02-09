/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tempo.app.domain.model.CalendarEvent
import com.tempo.app.domain.model.EventColor
import com.tempo.app.domain.model.SyncStatus

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val calendarId: String,
    val title: String,
    val description: String?,
    val location: String?,
    val startTime: Long,
    val endTime: Long,
    val allDay: Boolean,
    val reminderMinutesBefore: Int?,
    val color: String,
    val icsUid: String?,
    val etag: String?,
    val calDavUrl: String?,
    val lastModified: Long,
    val syncStatus: String
) {
    fun toDomain(): CalendarEvent = CalendarEvent(
        id = id,
        calendarId = calendarId,
        title = title,
        description = description,
        location = location,
        startTime = startTime,
        endTime = endTime,
        allDay = allDay,
        reminderMinutesBefore = reminderMinutesBefore,
        color = EventColor.fromKey(color),
        icsUid = icsUid,
        etag = etag,
        calDavUrl = calDavUrl,
        lastModified = lastModified,
        syncStatus = SyncStatus.fromKey(syncStatus)
    )

    companion object {
        fun fromDomain(event: CalendarEvent): EventEntity = EventEntity(
            id = event.id,
            calendarId = event.calendarId,
            title = event.title,
            description = event.description,
            location = event.location,
            startTime = event.startTime,
            endTime = event.endTime,
            allDay = event.allDay,
            reminderMinutesBefore = event.reminderMinutesBefore,
            color = event.color.key,
            icsUid = event.icsUid,
            etag = event.etag,
            calDavUrl = event.calDavUrl,
            lastModified = event.lastModified,
            syncStatus = event.syncStatus.key
        )
    }
}
