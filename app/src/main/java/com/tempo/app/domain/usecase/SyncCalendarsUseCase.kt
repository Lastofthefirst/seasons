/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.domain.usecase

import com.tempo.app.data.remote.caldav.CalDavSync

class SyncCalendarsUseCase(private val calDavSync: CalDavSync) {
    suspend operator fun invoke(): CalDavSync.SyncResult = calDavSync.syncAll()
}
