/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.data.remote.caldav

import com.tempo.app.domain.model.CalendarEvent
import com.tempo.app.domain.model.EventColor
import com.tempo.app.domain.model.SyncStatus
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringReader
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

class IcsParser {

    fun parseIcsString(icsData: String): List<CalendarEvent> {
        return try {
            val lines = unfoldLines(StringReader(icsData).readLines())
            extractEvents(lines)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseIcsStream(inputStream: InputStream): List<CalendarEvent> {
        return try {
            val lines = unfoldLines(BufferedReader(InputStreamReader(inputStream)).readLines())
            extractEvents(lines)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun unfoldLines(rawLines: List<String>): List<String> {
        val result = mutableListOf<String>()
        for (line in rawLines) {
            if (line.startsWith(" ") || line.startsWith("\t")) {
                if (result.isNotEmpty()) {
                    result[result.lastIndex] = result.last() + line.substring(1)
                }
            } else {
                result.add(line)
            }
        }
        return result
    }

    private fun extractEvents(lines: List<String>): List<CalendarEvent> {
        val events = mutableListOf<CalendarEvent>()
        var inEvent = false
        var inAlarm = false
        var eventLines = mutableListOf<String>()
        var alarmLines = mutableListOf<String>()

        for (line in lines) {
            when {
                line.equals("BEGIN:VEVENT", ignoreCase = true) -> {
                    inEvent = true
                    eventLines = mutableListOf()
                    alarmLines = mutableListOf()
                }
                line.equals("END:VEVENT", ignoreCase = true) -> {
                    inEvent = false
                    val event = parseVEvent(eventLines, alarmLines)
                    if (event != null) events.add(event)
                }
                line.equals("BEGIN:VALARM", ignoreCase = true) && inEvent -> {
                    inAlarm = true
                }
                line.equals("END:VALARM", ignoreCase = true) && inEvent -> {
                    inAlarm = false
                }
                inEvent && inAlarm -> alarmLines.add(line)
                inEvent -> eventLines.add(line)
            }
        }
        return events
    }

    private fun parseVEvent(lines: List<String>, alarmLines: List<String>): CalendarEvent? {
        val props = mutableMapOf<String, String>()
        for (line in lines) {
            val colonIndex = line.indexOf(':')
            if (colonIndex > 0) {
                val key = line.substring(0, colonIndex).uppercase()
                val value = line.substring(colonIndex + 1)
                props[key] = value
            }
        }

        val uid = props.entries.firstOrNull { it.key.startsWith("UID") }?.value ?: return null
        val summary = props.entries.firstOrNull { it.key.startsWith("SUMMARY") }?.value ?: "Untitled Event"
        val description = props.entries.firstOrNull { it.key.startsWith("DESCRIPTION") }?.value?.unescapeIcs()
        val location = props.entries.firstOrNull { it.key.startsWith("LOCATION") }?.value?.unescapeIcs()

        val dtStartEntry = props.entries.firstOrNull { it.key.startsWith("DTSTART") } ?: return null
        val dtEndEntry = props.entries.firstOrNull { it.key.startsWith("DTEND") }

        val allDay = dtStartEntry.key.contains("VALUE=DATE", ignoreCase = true) ||
                (!dtStartEntry.value.contains("T"))

        val startMillis = parseDateTime(dtStartEntry.key, dtStartEntry.value) ?: return null
        val endMillis = dtEndEntry?.let { parseDateTime(it.key, it.value) } ?: (startMillis + 3600000L)

        val reminderMinutes = extractReminderMinutes(alarmLines)

        return CalendarEvent(
            id = UUID.randomUUID().toString(),
            calendarId = "",
            title = summary.unescapeIcs(),
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

    private fun parseDateTime(key: String, value: String): Long? {
        return try {
            val cleanValue = value.trim()
            when {
                // Date-only: 20260115
                cleanValue.length == 8 && !cleanValue.contains("T") -> {
                    val date = LocalDate.parse(cleanValue, DateTimeFormatter.BASIC_ISO_DATE)
                    date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                }
                // UTC datetime: 20260115T090000Z
                cleanValue.endsWith("Z") -> {
                    val formatted = cleanValue.removeSuffix("Z")
                    val ldt = LocalDateTime.parse(formatted, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
                    ldt.toInstant(ZoneOffset.UTC).toEpochMilli()
                }
                // Local datetime with TZID
                key.contains("TZID=", ignoreCase = true) -> {
                    val tzid = Regex("TZID=([^;:]+)", RegexOption.IGNORE_CASE).find(key)?.groupValues?.get(1)
                    val zone = if (tzid != null) {
                        try { ZoneId.of(tzid) } catch (_: Exception) { ZoneId.systemDefault() }
                    } else ZoneId.systemDefault()
                    val ldt = LocalDateTime.parse(cleanValue, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
                    ldt.atZone(zone).toInstant().toEpochMilli()
                }
                // Local datetime without timezone
                cleanValue.contains("T") -> {
                    val ldt = LocalDateTime.parse(cleanValue, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
                    ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun extractReminderMinutes(alarmLines: List<String>): Int? {
        for (line in alarmLines) {
            if (line.uppercase().startsWith("TRIGGER")) {
                val value = line.substringAfter(':').trim()
                return try {
                    val cleaned = value.removePrefix("-")
                    val duration = Duration.parse(cleaned)
                    duration.toMinutes().toInt()
                } catch (e: Exception) {
                    15
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

        if (event.allDay) {
            sb.appendLine("DTSTART;VALUE=DATE:${formatIcsDate(event.startTime)}")
            sb.appendLine("DTEND;VALUE=DATE:${formatIcsDate(event.endTime)}")
        } else {
            sb.appendLine("DTSTART:${formatIcsDateTime(event.startTime)}")
            sb.appendLine("DTEND:${formatIcsDateTime(event.endTime)}")
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

    private fun String.unescapeIcs(): String =
        replace("\\n", "\n").replace("\\,", ",").replace("\\;", ";").replace("\\\\", "\\")

    private fun escapeIcsText(text: String): String =
        text.replace("\\", "\\\\").replace(",", "\\,").replace(";", "\\;").replace("\n", "\\n")

    private fun formatIcsDateTime(epochMillis: Long): String {
        val instant = Instant.ofEpochMilli(epochMillis)
        val zdt = instant.atZone(ZoneOffset.UTC)
        return DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").format(zdt)
    }

    private fun formatIcsDate(epochMillis: Long): String {
        val instant = Instant.ofEpochMilli(epochMillis)
        val zdt = instant.atZone(ZoneOffset.UTC)
        return DateTimeFormatter.ofPattern("yyyyMMdd").format(zdt)
    }
}
