/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app

import android.app.Application
import com.tempo.app.worker.CalDavSyncWorker
import com.tempo.app.worker.NotificationScheduler

class TempoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Create notification channel
        val scheduler = NotificationScheduler(this)
        scheduler.createNotificationChannel()

        // Enqueue periodic CalDAV sync
        CalDavSyncWorker.enqueuePeriodicSync(this, 15L)
    }
}
