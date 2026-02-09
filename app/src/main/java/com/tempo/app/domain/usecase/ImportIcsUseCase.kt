/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.domain.usecase

import com.tempo.app.data.remote.caldav.IcsParser
import com.tempo.app.data.repository.EventRepository
import com.tempo.app.domain.model.CalendarEvent
import java.io.InputStream

class ImportIcsUseCase(
    private val icsParser: IcsParser,
    private val eventRepository: EventRepository
) {
    suspend fun fromStream(inputStream: InputStream, calendarId: String): List<CalendarEvent> {
        val events = icsParser.parseIcsStream(inputStream)
        val importedEvents = mutableListOf<CalendarEvent>()
        for (event in events) {
            val imported = eventRepository.createEvent(event.copy(calendarId = calendarId))
            importedEvents.add(imported)
        }
        return importedEvents
    }

    suspend fun fromString(icsData: String, calendarId: String): List<CalendarEvent> {
        val events = icsParser.parseIcsString(icsData)
        val importedEvents = mutableListOf<CalendarEvent>()
        for (event in events) {
            val imported = eventRepository.createEvent(event.copy(calendarId = calendarId))
            importedEvents.add(imported)
        }
        return importedEvents
    }
}
