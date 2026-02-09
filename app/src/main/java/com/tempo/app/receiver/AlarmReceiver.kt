/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.tempo.app.MainActivity
import com.tempo.app.R
import com.tempo.app.worker.NotificationScheduler

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getStringExtra(NotificationScheduler.EXTRA_EVENT_ID) ?: return
        val title = intent.getStringExtra(NotificationScheduler.EXTRA_EVENT_TITLE) ?: "Event"
        val time = intent.getStringExtra(NotificationScheduler.EXTRA_EVENT_TIME) ?: ""
        val location = intent.getStringExtra(NotificationScheduler.EXTRA_EVENT_LOCATION) ?: ""

        val bodyParts = mutableListOf<String>()
        if (time.isNotBlank()) bodyParts.add(time)
        if (location.isNotBlank()) bodyParts.add(location)
        val body = bodyParts.joinToString(" · ")

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("event_id", eventId)
        }

        val tapPendingIntent = PendingIntent.getActivity(
            context,
            eventId.hashCode(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationScheduler.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(tapPendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(eventId.hashCode(), notification)
    }
}
