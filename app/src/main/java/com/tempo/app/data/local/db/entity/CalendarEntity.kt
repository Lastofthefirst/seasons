/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tempo.app.domain.model.Calendar
import com.tempo.app.domain.model.EventColor

@Entity(tableName = "calendars")
data class CalendarEntity(
    @PrimaryKey val id: String,
    val name: String,
    val color: String,
    val calDavUrl: String?,
    val ctag: String?,
    val enabled: Boolean,
    val accountUrl: String?
) {
    fun toDomain(): Calendar = Calendar(
        id = id,
        name = name,
        color = EventColor.fromKey(color),
        calDavUrl = calDavUrl,
        ctag = ctag,
        enabled = enabled,
        accountUrl = accountUrl
    )

    companion object {
        fun fromDomain(calendar: Calendar): CalendarEntity = CalendarEntity(
            id = calendar.id,
            name = calendar.name,
            color = calendar.color.key,
            calDavUrl = calendar.calDavUrl,
            ctag = calendar.ctag,
            enabled = calendar.enabled,
            accountUrl = calendar.accountUrl
        )
    }
}
