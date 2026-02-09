/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.data.local.db

import androidx.room.*
import com.tempo.app.data.local.db.entity.CalendarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {

    @Query("SELECT * FROM calendars ORDER BY name ASC")
    fun getAllCalendars(): Flow<List<CalendarEntity>>

    @Query("SELECT * FROM calendars WHERE enabled = 1 ORDER BY name ASC")
    fun getEnabledCalendars(): Flow<List<CalendarEntity>>

    @Query("SELECT * FROM calendars WHERE id = :calendarId")
    suspend fun getCalendarById(calendarId: String): CalendarEntity?

    @Query("SELECT * FROM calendars WHERE calDavUrl = :url")
    suspend fun getCalendarByUrl(url: String): CalendarEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendar(calendar: CalendarEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendars(calendars: List<CalendarEntity>)

    @Update
    suspend fun updateCalendar(calendar: CalendarEntity)

    @Query("UPDATE calendars SET enabled = :enabled WHERE id = :calendarId")
    suspend fun setCalendarEnabled(calendarId: String, enabled: Boolean)

    @Query("UPDATE calendars SET ctag = :ctag WHERE id = :calendarId")
    suspend fun updateCtag(calendarId: String, ctag: String)

    @Delete
    suspend fun deleteCalendar(calendar: CalendarEntity)

    @Query("DELETE FROM calendars WHERE id = :calendarId")
    suspend fun deleteCalendarById(calendarId: String)
}
