/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.data.repository

import com.tempo.app.data.local.db.EventDao
import com.tempo.app.data.local.db.entity.EventEntity
import com.tempo.app.domain.model.CalendarEvent
import com.tempo.app.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

class EventRepository(private val eventDao: EventDao) {

    fun getAllEvents(): Flow<List<CalendarEvent>> =
        eventDao.getAllEvents().map { entities -> entities.map { it.toDomain() } }

    fun getEventsForDay(date: LocalDate): Flow<List<CalendarEvent>> {
        val zone = ZoneId.systemDefault()
        val dayStart = date.atStartOfDay(zone).toInstant().toEpochMilli()
        val dayEnd = date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        return eventDao.getEventsForDay(dayStart, dayEnd).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getEventsInRange(start: LocalDate, end: LocalDate): Flow<List<CalendarEvent>> {
        val zone = ZoneId.systemDefault()
        val rangeStart = start.atStartOfDay(zone).toInstant().toEpochMilli()
        val rangeEnd = end.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        return eventDao.getEventsInRange(rangeStart, rangeEnd).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getEventsByCalendar(calendarId: String): Flow<List<CalendarEvent>> =
        eventDao.getEventsByCalendar(calendarId).map { entities -> entities.map { it.toDomain() } }

    fun getEventCountForCalendar(calendarId: String): Flow<Int> =
        eventDao.getEventCountForCalendar(calendarId)

    suspend fun getEventById(id: String): CalendarEvent? =
        eventDao.getEventById(id)?.toDomain()

    suspend fun getEventByIcsUid(uid: String): CalendarEvent? =
        eventDao.getEventByIcsUid(uid)?.toDomain()

    suspend fun createEvent(event: CalendarEvent): CalendarEvent {
        val newEvent = event.copy(
            id = if (event.id.isBlank()) UUID.randomUUID().toString() else event.id,
            syncStatus = SyncStatus.LOCAL,
            lastModified = System.currentTimeMillis()
        )
        eventDao.insertEvent(EventEntity.fromDomain(newEvent))
        return newEvent
    }

    suspend fun updateEvent(event: CalendarEvent) {
        val updated = event.copy(
            syncStatus = if (event.syncStatus == SyncStatus.SYNCED) SyncStatus.MODIFIED else event.syncStatus,
            lastModified = System.currentTimeMillis()
        )
        eventDao.updateEvent(EventEntity.fromDomain(updated))
    }

    suspend fun deleteEvent(eventId: String) {
        val event = eventDao.getEventById(eventId) ?: return
        if (event.calDavUrl != null) {
            eventDao.updateSyncStatus(eventId, SyncStatus.DELETED.key)
        } else {
            eventDao.deleteEventById(eventId)
        }
    }

    suspend fun getUnsyncedEvents(): List<CalendarEvent> =
        eventDao.getUnsyncedEvents().map { it.toDomain() }

    suspend fun getEventsWithUpcomingReminders(): List<CalendarEvent> =
        eventDao.getEventsWithUpcomingReminders(System.currentTimeMillis()).map { it.toDomain() }

    suspend fun markSynced(eventId: String, etag: String) {
        eventDao.updateSyncStatusAndEtag(eventId, SyncStatus.SYNCED.key, etag)
    }

    suspend fun insertSyncedEvent(event: CalendarEvent) {
        eventDao.insertEvent(EventEntity.fromDomain(event.copy(syncStatus = SyncStatus.SYNCED)))
    }

    suspend fun permanentlyDelete(eventId: String) {
        eventDao.deleteEventById(eventId)
    }
}
