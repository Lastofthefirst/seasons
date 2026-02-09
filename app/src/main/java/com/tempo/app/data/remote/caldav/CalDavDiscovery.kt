/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.data.remote.caldav

import com.tempo.app.domain.model.Calendar
import com.tempo.app.domain.model.EventColor
import java.util.UUID

class CalDavDiscovery(private val client: CalDavClient, private val baseUrl: String) {

    data class DiscoveryResult(
        val calendars: List<Calendar>,
        val error: String? = null
    )

    suspend fun discoverCalendars(): DiscoveryResult {
        return try {
            val principalUrl = findPrincipal()
                ?: return DiscoveryResult(emptyList(), "Could not find principal URL")

            val homeSetUrl = findCalendarHomeSet(principalUrl)
                ?: return DiscoveryResult(emptyList(), "Could not find calendar home set")

            val calendars = listCalendars(homeSetUrl)
            DiscoveryResult(calendars)
        } catch (e: Exception) {
            DiscoveryResult(emptyList(), e.message ?: "Discovery failed")
        }
    }

    private fun findPrincipal(): String? {
        val wellKnownUrl = resolveUrl(baseUrl, "/.well-known/caldav")
        val response = client.get(wellKnownUrl)

        val targetUrl = if (response.statusCode in 300..399) {
            response.headers["Location"] ?: baseUrl
        } else {
            baseUrl
        }

        val propfindBody = """
            <?xml version="1.0" encoding="utf-8"?>
            <d:propfind xmlns:d="DAV:" xmlns:c="urn:ietf:params:xml:ns:caldav">
              <d:prop>
                <d:current-user-principal />
              </d:prop>
            </d:propfind>
        """.trimIndent()

        val propfindResponse = client.propfind(targetUrl, propfindBody, 0)
        if (!propfindResponse.isSuccess) return null

        return extractHref(propfindResponse.body, "current-user-principal")?.let {
            resolveUrl(baseUrl, it)
        }
    }

    private fun findCalendarHomeSet(principalUrl: String): String? {
        val propfindBody = """
            <?xml version="1.0" encoding="utf-8"?>
            <d:propfind xmlns:d="DAV:" xmlns:c="urn:ietf:params:xml:ns:caldav">
              <d:prop>
                <c:calendar-home-set />
              </d:prop>
            </d:propfind>
        """.trimIndent()

        val response = client.propfind(principalUrl, propfindBody, 0)
        if (!response.isSuccess) return null

        return extractHref(response.body, "calendar-home-set")?.let {
            resolveUrl(baseUrl, it)
        }
    }

    private fun listCalendars(homeSetUrl: String): List<Calendar> {
        val propfindBody = """
            <?xml version="1.0" encoding="utf-8"?>
            <d:propfind xmlns:d="DAV:" xmlns:c="urn:ietf:params:xml:ns:caldav" xmlns:cs="http://calendarserver.org/ns/" xmlns:ic="http://apple.com/ns/ical/">
              <d:prop>
                <d:displayname />
                <d:resourcetype />
                <cs:getctag />
                <ic:calendar-color />
              </d:prop>
            </d:propfind>
        """.trimIndent()

        val response = client.propfind(homeSetUrl, propfindBody, 1)
        if (!response.isSuccess) return emptyList()

        return parseCalendarList(response.body, homeSetUrl)
    }

    private fun parseCalendarList(xml: String, homeSetUrl: String): List<Calendar> {
        val calendars = mutableListOf<Calendar>()
        val colors = listOf(EventColor.ACCENT, EventColor.SECONDARY, EventColor.TERTIARY)
        var colorIndex = 0

        val responses = xml.split("<d:response>", "<D:response>")
            .drop(1)

        for (responseBlock in responses) {
            val href = extractSimpleTag(responseBlock, "href") ?: continue

            val isCalendar = responseBlock.contains("calendar") &&
                    responseBlock.contains("resourcetype") &&
                    !responseBlock.contains("inbox") &&
                    !responseBlock.contains("outbox")

            if (!isCalendar) continue

            val displayName = extractSimpleTag(responseBlock, "displayname") ?: "Calendar"
            val ctag = extractSimpleTag(responseBlock, "getctag")
            val calendarUrl = resolveUrl(homeSetUrl, href)

            calendars.add(
                Calendar(
                    id = UUID.randomUUID().toString(),
                    name = displayName,
                    color = colors[colorIndex % colors.size],
                    calDavUrl = calendarUrl,
                    ctag = ctag,
                    enabled = true,
                    accountUrl = baseUrl
                )
            )
            colorIndex++
        }

        return calendars
    }

    private fun extractHref(xml: String, parentTag: String): String? {
        val parentPattern = Regex("""<[^>]*$parentTag[^>]*>(.*?)</[^>]*$parentTag>""", RegexOption.DOT_MATCHES_ALL)
        val parentMatch = parentPattern.find(xml) ?: return null
        val hrefPattern = Regex("""<[^>]*href[^>]*>([^<]+)</[^>]*href>""", RegexOption.IGNORE_CASE)
        val hrefMatch = hrefPattern.find(parentMatch.groupValues[1]) ?: return null
        return hrefMatch.groupValues[1].trim()
    }

    private fun extractSimpleTag(xml: String, tagName: String): String? {
        val pattern = Regex("""<[^>]*$tagName[^>]*>([^<]*)</[^>]*$tagName>""", RegexOption.IGNORE_CASE)
        return pattern.find(xml)?.groupValues?.get(1)?.trim()?.ifBlank { null }
    }

    private fun resolveUrl(base: String, path: String): String {
        if (path.startsWith("http://") || path.startsWith("https://")) return path
        val baseUri = java.net.URI.create(base.trimEnd('/'))
        return "${baseUri.scheme}://${baseUri.host}${if (baseUri.port > 0) ":${baseUri.port}" else ""}${path}"
    }
}
