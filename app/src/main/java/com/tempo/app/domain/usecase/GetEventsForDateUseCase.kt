/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.domain.usecase

import com.tempo.app.data.repository.EventRepository
import com.tempo.app.domain.model.CalendarEvent
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GetEventsForDateUseCase(private val eventRepository: EventRepository) {
    operator fun invoke(date: LocalDate): Flow<List<CalendarEvent>> =
        eventRepository.getEventsForDay(date)
}
