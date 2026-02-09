/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.data.remote.caldav

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class CalDavClient(
    private val baseUrl: String,
    private val username: String,
    private val password: String
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .authenticator { _, response ->
            if (response.request.header("Authorization") != null) {
                return@authenticator null
            }
            val credential = Credentials.basic(username, password)
            response.request.newBuilder()
                .header("Authorization", credential)
                .build()
        }
        .build()

    private val xmlMediaType = "application/xml; charset=utf-8".toMediaType()
    private val icsMediaType = "text/calendar; charset=utf-8".toMediaType()

    fun propfind(url: String, body: String, depth: Int = 0): CalDavResponse {
        val request = Request.Builder()
            .url(url)
            .method("PROPFIND", body.toRequestBody(xmlMediaType))
            .header("Depth", depth.toString())
            .header("Content-Type", "application/xml; charset=utf-8")
            .build()

        return executeRequest(request)
    }

    fun report(url: String, body: String): CalDavResponse {
        val request = Request.Builder()
            .url(url)
            .method("REPORT", body.toRequestBody(xmlMediaType))
            .header("Depth", "1")
            .header("Content-Type", "application/xml; charset=utf-8")
            .build()

        return executeRequest(request)
    }

    fun put(url: String, icsData: String, etag: String? = null): CalDavResponse {
        val builder = Request.Builder()
            .url(url)
            .put(icsData.toRequestBody(icsMediaType))
            .header("Content-Type", "text/calendar; charset=utf-8")

        if (etag != null) {
            builder.header("If-Match", etag)
        } else {
            builder.header("If-None-Match", "*")
        }

        return executeRequest(builder.build())
    }

    fun delete(url: String, etag: String? = null): CalDavResponse {
        val builder = Request.Builder()
            .url(url)
            .delete()

        if (etag != null) {
            builder.header("If-Match", etag)
        }

        return executeRequest(builder.build())
    }

    fun get(url: String): CalDavResponse {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return executeRequest(request)
    }

    private fun executeRequest(request: Request): CalDavResponse {
        return try {
            val response = client.newCall(request).execute()
            CalDavResponse(
                statusCode = response.code,
                body = response.body?.string() ?: "",
                headers = response.headers.toMap(),
                isSuccess = response.isSuccessful || response.code == 207
            )
        } catch (e: IOException) {
            CalDavResponse(
                statusCode = -1,
                body = e.message ?: "Network error",
                headers = emptyMap(),
                isSuccess = false
            )
        }
    }

    private fun Headers.toMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (i in 0 until size) {
            map[name(i)] = value(i)
        }
        return map
    }
}

data class CalDavResponse(
    val statusCode: Int,
    val body: String,
    val headers: Map<String, String>,
    val isSuccess: Boolean
)
