/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tempo.app.data.local.db.TempoDatabase
import com.tempo.app.data.repository.EventRepository
import com.tempo.app.worker.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = TempoDatabase.getInstance(context)
                val eventRepository = EventRepository(database.eventDao())
                val scheduler = NotificationScheduler(context)

                val upcomingEvents = eventRepository.getEventsWithUpcomingReminders()
                scheduler.rescheduleAll(upcomingEvents)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
