/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.worker

import android.content.Context
import androidx.work.*
import com.tempo.app.data.local.db.TempoDatabase
import com.tempo.app.data.local.preferences.TempoPreferences
import com.tempo.app.data.remote.caldav.CalDavClient
import com.tempo.app.data.remote.caldav.CalDavSync
import com.tempo.app.data.remote.caldav.IcsParser
import com.tempo.app.data.repository.CalendarRepository
import com.tempo.app.data.repository.EventRepository
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class CalDavSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "tempo_caldav_sync"

        fun enqueuePeriodicSync(context: Context, intervalMinutes: Long = 15L) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<CalDavSyncWorker>(
                intervalMinutes, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }

        fun enqueueOneTimeSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<CalDavSyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueue(request)
        }

        fun cancelSync(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }

    override suspend fun doWork(): Result {
        return try {
            val preferences = TempoPreferences(applicationContext)
            val serverUrl = preferences.calDavServerUrl.first()
            val username = preferences.calDavUsername.first()
            val password = preferences.calDavPassword.first()

            if (serverUrl.isBlank() || username.isBlank()) {
                return Result.success()
            }

            val database = TempoDatabase.getInstance(applicationContext)
            val eventRepository = EventRepository(database.eventDao())
            val calendarRepository = CalendarRepository(database.calendarDao())
            val client = CalDavClient(serverUrl, username, password)
            val icsParser = IcsParser()

            val sync = CalDavSync(client, icsParser, eventRepository, calendarRepository)
            val result = sync.syncAll()

            preferences.setLastSyncTime(System.currentTimeMillis())

            // Reschedule notifications for newly synced events
            val scheduler = NotificationScheduler(applicationContext)
            val upcomingEvents = eventRepository.getEventsWithUpcomingReminders()
            scheduler.rescheduleAll(upcomingEvents)

            if (result.errors.isEmpty()) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
