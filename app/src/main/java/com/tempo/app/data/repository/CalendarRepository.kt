/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.data.repository

import com.tempo.app.data.local.db.CalendarDao
import com.tempo.app.data.local.db.entity.CalendarEntity
import com.tempo.app.domain.model.Calendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class CalendarRepository(private val calendarDao: CalendarDao) {

    fun getAllCalendars(): Flow<List<Calendar>> =
        calendarDao.getAllCalendars().map { entities -> entities.map { it.toDomain() } }

    fun getEnabledCalendars(): Flow<List<Calendar>> =
        calendarDao.getEnabledCalendars().map { entities -> entities.map { it.toDomain() } }

    suspend fun getCalendarById(id: String): Calendar? =
        calendarDao.getCalendarById(id)?.toDomain()

    suspend fun getCalendarByUrl(url: String): Calendar? =
        calendarDao.getCalendarByUrl(url)?.toDomain()

    suspend fun createCalendar(calendar: Calendar): Calendar {
        val newCalendar = calendar.copy(
            id = if (calendar.id.isBlank()) UUID.randomUUID().toString() else calendar.id
        )
        calendarDao.insertCalendar(CalendarEntity.fromDomain(newCalendar))
        return newCalendar
    }

    suspend fun updateCalendar(calendar: Calendar) {
        calendarDao.updateCalendar(CalendarEntity.fromDomain(calendar))
    }

    suspend fun setCalendarEnabled(calendarId: String, enabled: Boolean) {
        calendarDao.setCalendarEnabled(calendarId, enabled)
    }

    suspend fun updateCtag(calendarId: String, ctag: String) {
        calendarDao.updateCtag(calendarId, ctag)
    }

    suspend fun deleteCalendar(calendarId: String) {
        calendarDao.deleteCalendarById(calendarId)
    }

    suspend fun insertCalendars(calendars: List<Calendar>) {
        calendarDao.insertCalendars(calendars.map { CalendarEntity.fromDomain(it) })
    }
}
