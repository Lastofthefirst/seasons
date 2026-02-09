/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.domain.usecase

import com.tempo.app.data.repository.EventRepository
import com.tempo.app.domain.model.CalendarEvent

class CreateEventUseCase(private val eventRepository: EventRepository) {
    suspend operator fun invoke(event: CalendarEvent): CalendarEvent =
        eventRepository.createEvent(event)
}
