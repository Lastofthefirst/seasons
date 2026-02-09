/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.domain.model

data class Calendar(
    val id: String,
    val name: String,
    val color: EventColor = EventColor.ACCENT,
    val calDavUrl: String? = null,
    val ctag: String? = null,
    val enabled: Boolean = true,
    val accountUrl: String? = null
)
