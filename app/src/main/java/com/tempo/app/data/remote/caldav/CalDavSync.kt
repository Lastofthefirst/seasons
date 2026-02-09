/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.data.remote.caldav

import com.tempo.app.data.repository.CalendarRepository
import com.tempo.app.data.repository.EventRepository
import com.tempo.app.domain.model.SyncStatus
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class CalDavSync(
    private val client: CalDavClient,
    private val icsParser: IcsParser,
    private val eventRepository: EventRepository,
    private val calendarRepository: CalendarRepository
) {
    data class SyncResult(
        val pulled: Int = 0,
        val pushed: Int = 0,
        val deleted: Int = 0,
        val errors: List<String> = emptyList()
    )

    suspend fun syncAll(): SyncResult {
        var totalPulled = 0
        var totalPushed = 0
        var totalDeleted = 0
        val errors = mutableListOf<String>()

        // Push local changes first
        try {
            val pushResult = pushLocalChanges()
            totalPushed = pushResult.pushed
            totalDeleted = pushResult.deleted
            errors.addAll(pushResult.errors)
        } catch (e: Exception) {
            errors.add("Push failed: ${e.message}")
        }

        // Pull remote changes for each calendar
        try {
            val calendars = calendarRepository.getAllCalendarsList()
            for (cal in calendars) {
                val calDavUrl = cal.calDavUrl ?: continue
                val pullResult = pullCalendar(calDavUrl, cal.id)
                totalPulled += pullResult.pulled
                errors.addAll(pullResult.errors)
            }
        } catch (e: Exception) {
            errors.add("Pull failed: ${e.message}")
        }

        return SyncResult(totalPulled, totalPushed, totalDeleted, errors)
    }

    private suspend fun pushLocalChanges(): SyncResult {
        val unsyncedEvents = eventRepository.getUnsyncedEvents()
        var pushed = 0
        var deleted = 0
        val errors = mutableListOf<String>()

        for (event in unsyncedEvents) {
            try {
                when (event.syncStatus) {
                    SyncStatus.LOCAL, SyncStatus.MODIFIED -> {
                        val calDavUrl = event.calDavUrl ?: continue
                        val icsData = icsParser.serializeToIcs(event)
                        val response = client.put(calDavUrl, icsData, event.etag)
                        if (response.isSuccess) {
                            val newEtag = response.headers["ETag"] ?: ""
                            eventRepository.markSynced(event.id, newEtag)
                            pushed++
                        } else {
                            errors.add("Failed to push ${event.title}: ${response.statusCode}")
                        }
                    }
                    SyncStatus.DELETED -> {
                        val calDavUrl = event.calDavUrl
                        if (calDavUrl != null) {
                            val response = client.delete(calDavUrl, event.etag)
                            if (response.isSuccess || response.statusCode == 404) {
                                eventRepository.permanentlyDelete(event.id)
                                deleted++
                            } else {
                                errors.add("Failed to delete ${event.title}: ${response.statusCode}")
                            }
                        } else {
                            eventRepository.permanentlyDelete(event.id)
                            deleted++
                        }
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                errors.add("Error syncing ${event.title}: ${e.message}")
            }
        }

        return SyncResult(pushed = pushed, deleted = deleted, errors = errors)
    }

    suspend fun pullCalendar(calendarUrl: String, calendarId: String): SyncResult {
        var pulled = 0
        val errors = mutableListOf<String>()

        val now = LocalDate.now()
        val startDate = now.minusMonths(1).withDayOfMonth(1)
        val endDate = now.plusMonths(3).withDayOfMonth(1)

        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
        val startStr = startDate.atStartOfDay().atOffset(ZoneOffset.UTC).format(formatter)
        val endStr = endDate.atStartOfDay().atOffset(ZoneOffset.UTC).format(formatter)

        val reportBody = """
            <?xml version="1.0" encoding="utf-8"?>
            <c:calendar-query xmlns:d="DAV:" xmlns:c="urn:ietf:params:xml:ns:caldav">
              <d:prop>
                <d:getetag />
                <c:calendar-data />
              </d:prop>
              <c:filter>
                <c:comp-filter name="VCALENDAR">
                  <c:comp-filter name="VEVENT">
                    <c:time-range start="$startStr" end="$endStr"/>
                  </c:comp-filter>
                </c:comp-filter>
              </c:filter>
            </c:calendar-query>
        """.trimIndent()

        try {
            val response = client.report(calendarUrl, reportBody)
            if (!response.isSuccess) {
                return SyncResult(errors = listOf("REPORT failed: ${response.statusCode}"))
            }

            val calendarDataBlocks = extractCalendarData(response.body)

            for ((icsData, etag, href) in calendarDataBlocks) {
                try {
                    val events = icsParser.parseIcsString(icsData)
                    for (event in events) {
                        val existingEvent = event.icsUid?.let {
                            eventRepository.getEventByIcsUid(it)
                        }
                        if (existingEvent != null) {
                            if (existingEvent.etag != etag) {
                                eventRepository.updateEvent(
                                    event.copy(
                                        id = existingEvent.id,
                                        calendarId = calendarId,
                                        etag = etag,
                                        calDavUrl = resolveUrl(calendarUrl, href),
                                        syncStatus = SyncStatus.SYNCED
                                    )
                                )
                                pulled++
                            }
                        } else {
                            eventRepository.insertSyncedEvent(
                                event.copy(
                                    calendarId = calendarId,
                                    etag = etag,
                                    calDavUrl = resolveUrl(calendarUrl, href),
                                    syncStatus = SyncStatus.SYNCED
                                )
                            )
                            pulled++
                        }
                    }
                } catch (e: Exception) {
                    errors.add("Failed to parse event: ${e.message}")
                }
            }
        } catch (e: Exception) {
            errors.add("Pull failed: ${e.message}")
        }

        return SyncResult(pulled = pulled, errors = errors)
    }

    private fun extractCalendarData(xml: String): List<Triple<String, String, String>> {
        val results = mutableListOf<Triple<String, String, String>>()

        val responseBlocks = xml.split(Regex("<[dD]:response>")).drop(1)
        for (block in responseBlocks) {
            val href = Regex("""<[dD]:href>([^<]+)</[dD]:href>""")
                .find(block)?.groupValues?.get(1) ?: continue
            val etag = Regex("""<[dD]:getetag>"?([^"<]+)"?</[dD]:getetag>""")
                .find(block)?.groupValues?.get(1) ?: ""
            val calData = Regex("""<[cC]:calendar-data[^>]*>(.*?)</[cC]:calendar-data>""", RegexOption.DOT_MATCHES_ALL)
                .find(block)?.groupValues?.get(1)?.trim() ?: continue

            val decodedData = calData
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")

            results.add(Triple(decodedData, etag, href))
        }

        return results
    }

    private fun resolveUrl(base: String, path: String): String {
        if (path.startsWith("http://") || path.startsWith("https://")) return path
        val baseUri = java.net.URI.create(base.trimEnd('/'))
        return "${baseUri.scheme}://${baseUri.host}${if (baseUri.port > 0) ":${baseUri.port}" else ""}${path}"
    }
}
