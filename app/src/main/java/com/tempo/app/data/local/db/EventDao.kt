/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.data.local.db

import androidx.room.*
import com.tempo.app.data.local.db.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events WHERE syncStatus != 'deleted' ORDER BY startTime ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query(
        "SELECT * FROM events WHERE startTime >= :dayStart AND startTime < :dayEnd " +
        "AND syncStatus != 'deleted' ORDER BY startTime ASC"
    )
    fun getEventsForDay(dayStart: Long, dayEnd: Long): Flow<List<EventEntity>>

    @Query(
        "SELECT * FROM events WHERE startTime >= :rangeStart AND startTime < :rangeEnd " +
        "AND syncStatus != 'deleted' ORDER BY startTime ASC"
    )
    fun getEventsInRange(rangeStart: Long, rangeEnd: Long): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: String): EventEntity?

    @Query("SELECT * FROM events WHERE icsUid = :uid")
    suspend fun getEventByIcsUid(uid: String): EventEntity?

    @Query("SELECT * FROM events WHERE syncStatus IN ('local', 'modified', 'deleted')")
    suspend fun getUnsyncedEvents(): List<EventEntity>

    @Query("SELECT * FROM events WHERE calendarId = :calendarId AND syncStatus != 'deleted' ORDER BY startTime ASC")
    fun getEventsByCalendar(calendarId: String): Flow<List<EventEntity>>

    @Query("SELECT COUNT(*) FROM events WHERE calendarId = :calendarId AND syncStatus != 'deleted'")
    fun getEventCountForCalendar(calendarId: String): Flow<Int>

    @Query(
        "SELECT * FROM events WHERE reminderMinutesBefore IS NOT NULL " +
        "AND startTime > :now AND syncStatus != 'deleted'"
    )
    suspend fun getEventsWithUpcomingReminders(now: Long): List<EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Query("UPDATE events SET syncStatus = :status WHERE id = :eventId")
    suspend fun updateSyncStatus(eventId: String, status: String)

    @Query("UPDATE events SET syncStatus = :status, etag = :etag WHERE id = :eventId")
    suspend fun updateSyncStatusAndEtag(eventId: String, status: String, etag: String)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteEventById(eventId: String)

    @Query("DELETE FROM events WHERE calendarId = :calendarId")
    suspend fun deleteEventsByCalendar(calendarId: String)
}
