/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.data.remote.caldav

import com.tempo.app.domain.model.CalendarEvent
import com.tempo.app.domain.model.EventColor
import com.tempo.app.domain.model.SyncStatus
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Component
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VAlarm
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.*
import java.io.InputStream
import java.io.StringReader
import java.time.Duration
import java.util.UUID

class IcsParser {

    fun parseIcsString(icsData: String): List<CalendarEvent> {
        return try {
            val builder = CalendarBuilder()
            val calendar = builder.build(StringReader(icsData))
            extractEvents(calendar)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseIcsStream(inputStream: InputStream): List<CalendarEvent> {
        return try {
            val builder = CalendarBuilder()
            val calendar = builder.build(inputStream)
            extractEvents(calendar)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun extractEvents(calendar: Calendar): List<CalendarEvent> {
        val events = mutableListOf<CalendarEvent>()

        for (component in calendar.components) {
            if (component is VEvent) {
                val event = parseVEvent(component)
                if (event != null) {
                    events.add(event)
                }
            }
        }

        return events
    }

    private fun parseVEvent(vEvent: VEvent): CalendarEvent? {
        val uid = vEvent.getProperty<Uid>(Property.UID)?.value ?: return null
        val summary = vEvent.getProperty<Summary>(Property.SUMMARY)?.value ?: "Untitled Event"
        val description = vEvent.getProperty<Description>(Property.DESCRIPTION)?.value
        val location = vEvent.getProperty<Location>(Property.LOCATION)?.value

        val dtStart = vEvent.getProperty<DtStart<*>>(Property.DTSTART) ?: return null
        val dtEnd = vEvent.getProperty<DtEnd<*>>(Property.DTEND)

        val startMillis = try {
            dtStart.date.toInstant().toEpochMilli()
        } catch (e: Exception) {
            return null
        }

        val endMillis = try {
            dtEnd?.date?.toInstant()?.toEpochMilli() ?: (startMillis + 3600000L)
        } catch (e: Exception) {
            startMillis + 3600000L
        }

        val allDay = dtStart.value?.contains("T") != true

        val reminderMinutes = extractReminderMinutes(vEvent)

        return CalendarEvent(
            id = UUID.randomUUID().toString(),
            calendarId = "",
            title = summary,
            description = description,
            location = location,
            startTime = startMillis,
            endTime = endMillis,
            allDay = allDay,
            reminderMinutesBefore = reminderMinutes,
            color = EventColor.ACCENT,
            icsUid = uid,
            syncStatus = SyncStatus.LOCAL
        )
    }

    private fun extractReminderMinutes(vEvent: VEvent): Int? {
        for (component in vEvent.components) {
            if (component is VAlarm) {
                val trigger = component.getProperty<Trigger>(Property.TRIGGER)
                if (trigger != null) {
                    return try {
                        val duration = trigger.duration
                        if (duration != null) {
                            val totalSeconds = Duration.parse(duration.toString()).abs().seconds
                            (totalSeconds / 60).toInt()
                        } else {
                            15
                        }
                    } catch (e: Exception) {
                        15
                    }
                }
            }
        }
        return null
    }

    fun serializeToIcs(event: CalendarEvent): String {
        val uid = event.icsUid ?: UUID.randomUUID().toString()
        val sb = StringBuilder()
        sb.appendLine("BEGIN:VCALENDAR")
        sb.appendLine("VERSION:2.0")
        sb.appendLine("PRODID:-//Tempo//Calendar//EN")
        sb.appendLine("BEGIN:VEVENT")
        sb.appendLine("UID:$uid")
        sb.appendLine("SUMMARY:${escapeIcsText(event.title)}")

        if (!event.description.isNullOrBlank()) {
            sb.appendLine("DESCRIPTION:${escapeIcsText(event.description)}")
        }
        if (!event.location.isNullOrBlank()) {
            sb.appendLine("LOCATION:${escapeIcsText(event.location)}")
        }

        val startFormatted = formatIcsDateTime(event.startTime)
        val endFormatted = formatIcsDateTime(event.endTime)

        if (event.allDay) {
            sb.appendLine("DTSTART;VALUE=DATE:${formatIcsDate(event.startTime)}")
            sb.appendLine("DTEND;VALUE=DATE:${formatIcsDate(event.endTime)}")
        } else {
            sb.appendLine("DTSTART:$startFormatted")
            sb.appendLine("DTEND:$endFormatted")
        }

        if (event.reminderMinutesBefore != null && event.reminderMinutesBefore > 0) {
            sb.appendLine("BEGIN:VALARM")
            sb.appendLine("TRIGGER:-PT${event.reminderMinutesBefore}M")
            sb.appendLine("ACTION:DISPLAY")
            sb.appendLine("DESCRIPTION:Reminder")
            sb.appendLine("END:VALARM")
        }

        sb.appendLine("END:VEVENT")
        sb.appendLine("END:VCALENDAR")
        return sb.toString()
    }

    private fun escapeIcsText(text: String): String =
        text.replace("\\", "\\\\").replace(",", "\\,").replace(";", "\\;").replace("\n", "\\n")

    private fun formatIcsDateTime(epochMillis: Long): String {
        val instant = java.time.Instant.ofEpochMilli(epochMillis)
        val zdt = instant.atZone(java.time.ZoneOffset.UTC)
        return java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").format(zdt)
    }

    private fun formatIcsDate(epochMillis: Long): String {
        val instant = java.time.Instant.ofEpochMilli(epochMillis)
        val zdt = instant.atZone(java.time.ZoneOffset.UTC)
        return java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd").format(zdt)
    }
}
