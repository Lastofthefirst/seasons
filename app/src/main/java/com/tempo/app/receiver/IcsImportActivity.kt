/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.receiver

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tempo.app.data.local.db.TempoDatabase
import com.tempo.app.data.remote.caldav.IcsParser
import com.tempo.app.data.repository.EventRepository
import com.tempo.app.domain.model.CalendarEvent
import com.tempo.app.domain.usecase.ImportIcsUseCase
import com.tempo.app.ui.screens.EventDetailSheet
import com.tempo.app.ui.theme.TempoDesign
import com.tempo.app.ui.theme.TempoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IcsImportActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = TempoDatabase.getInstance(this)
        val eventRepository = EventRepository(database.eventDao())
        val icsParser = IcsParser()
        val importUseCase = ImportIcsUseCase(icsParser, eventRepository)

        setContent {
            TempoTheme {
                val palette = TempoDesign.palette
                var importedEvents by remember { mutableStateOf<List<CalendarEvent>>(emptyList()) }
                var importing by remember { mutableStateOf(true) }
                var error by remember { mutableStateOf<String?>(null) }
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    scope.launch(Dispatchers.IO) {
                        try {
                            val uri = intent?.data
                            if (uri != null) {
                                val inputStream = contentResolver.openInputStream(uri)
                                if (inputStream != null) {
                                    val events = importUseCase.fromStream(inputStream, "default")
                                    importedEvents = events
                                    inputStream.close()
                                } else {
                                    error = "Could not read file"
                                }
                            } else {
                                error = "No file provided"
                            }
                        } catch (e: Exception) {
                            error = e.message ?: "Import failed"
                        } finally {
                            importing = false
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(brush = palette.background),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        importing -> {
                            BasicText(
                                text = "Importing...",
                                style = TempoDesign.typography.sectionHeader.copy(color = palette.text)
                            )
                        }
                        error != null -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                BasicText(
                                    text = "Import failed",
                                    style = TempoDesign.typography.sectionHeader.copy(color = palette.text)
                                )
                                BasicText(
                                    text = error ?: "",
                                    style = TempoDesign.typography.body.copy(color = palette.textSec)
                                )
                            }
                        }
                        importedEvents.isNotEmpty() -> {
                            val event = importedEvents.first()
                            EventDetailSheet(
                                visible = true,
                                event = event,
                                calendarName = "Imported",
                                onDismiss = { finish() },
                                onEdit = { finish() },
                                onShare = { finish() }
                            )
                        }
                        else -> {
                            BasicText(
                                text = "No events found in file",
                                style = TempoDesign.typography.body.copy(color = palette.textSec)
                            )
                        }
                    }
                }
            }
        }
    }
}
