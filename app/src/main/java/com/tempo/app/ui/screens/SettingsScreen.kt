/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tempo.app.ui.components.GlassCard
import com.tempo.app.ui.components.TempoBottomSheet
import com.tempo.app.ui.theme.TempoDesign

data class CalendarInfo(
    val id: String,
    val name: String,
    val color: Color,
    val eventCount: Int,
    val enabled: Boolean
)

@Composable
fun SettingsScreen(
    serverName: String,
    serverUrl: String,
    isSynced: Boolean,
    syncInterval: String,
    calendars: List<CalendarInfo>,
    defaultReminder: String,
    allDayTime: String,
    notificationsEnabled: Boolean,
    onCalendarToggle: (Int, Boolean) -> Unit,
    onSaveCalDav: (serverUrl: String, username: String, password: String) -> Unit,
    onSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography
    val scrollState = rememberScrollState()

    var showCalDavSetup by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 100.dp)
    ) {
        // Title
        BasicText(
            text = "Settings",
            style = typography.sectionHeader.copy(color = palette.text),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )

        // Season indicator card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(palette.accentSoft),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = palette.seasonEmoji,
                        style = typography.sectionHeader.copy(fontSize = 22.sp)
                    )
                }
                Column {
                    BasicText(
                        text = "Current theme: ${palette.seasonLabel}",
                        style = typography.bodySemiBold.copy(color = palette.text)
                    )
                    BasicText(
                        text = "Palette shifts with the seasons",
                        style = typography.locationChip.copy(color = palette.textSec)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // CalDAV Account
        SectionLabel("CALDAV ACCOUNT")
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clickable { showCalDavSetup = true }
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.accentSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(text = "☁\uFE0F", style = typography.eventTitle.copy(fontSize = 18.sp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        BasicText(
                            text = serverName.ifBlank { "Not configured" },
                            style = typography.bodySemiBold.copy(color = palette.text)
                        )
                        BasicText(
                            text = serverUrl.ifBlank { "Tap to set up CalDAV" },
                            style = typography.locationChip.copy(color = palette.textSec),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (isSynced) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(palette.secondary)
                            )
                            BasicText(
                                text = "Synced",
                                style = typography.locationChip.copy(
                                    color = palette.secondary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(palette.border)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicText(
                        text = "Auto-sync interval",
                        style = typography.body.copy(color = palette.text)
                    )
                    BasicText(
                        text = syncInterval,
                        style = typography.eventTime.copy(
                            color = palette.accent,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                if (serverUrl.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(palette.border)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSync() }
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = "Sync Now",
                            style = typography.bodyMedium.copy(
                                color = palette.accent,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Calendars
        SectionLabel("CALENDARS")
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Column {
                calendars.forEachIndexed { index, cal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp)
                            .semantics { contentDescription = "${cal.name} calendar, ${cal.eventCount} events" },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(cal.color)
                        )
                        BasicText(
                            text = cal.name,
                            style = typography.bodyMedium.copy(color = palette.text),
                            modifier = Modifier.weight(1f)
                        )
                        BasicText(
                            text = cal.eventCount.toString(),
                            style = typography.locationChip.copy(color = palette.textMuted)
                        )
                        ToggleSwitch(
                            checked = cal.enabled,
                            onCheckedChange = { onCalendarToggle(index, it) }
                        )
                    }

                    if (index < calendars.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(palette.border)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Notifications
        SectionLabel("NOTIFICATIONS")
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Column {
                SettingsRow(label = "Default reminder", value = defaultReminder)
                SettingsDivider()
                SettingsRow(label = "All-day events", value = allDayTime)
                SettingsDivider()
                SettingsRow(
                    label = "Offline notifications",
                    value = if (notificationsEnabled) "Enabled" else "Disabled",
                    isAccent = notificationsEnabled
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Import
        SectionLabel("IMPORT")
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = "Handle .ics files",
                    style = typography.body.copy(color = palette.text)
                )
                BasicText(
                    text = "Default app",
                    style = typography.eventTime.copy(
                        color = palette.secondary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    // CalDAV Setup Sheet
    CalDavSetupSheet(
        visible = showCalDavSetup,
        currentUrl = serverUrl,
        onDismiss = { showCalDavSetup = false },
        onSave = { url, user, pass ->
            onSaveCalDav(url, user, pass)
            showCalDavSetup = false
        }
    )
}

@Composable
private fun CalDavSetupSheet(
    visible: Boolean,
    currentUrl: String,
    onDismiss: () -> Unit,
    onSave: (serverUrl: String, username: String, password: String) -> Unit
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    var serverUrl by remember { mutableStateOf(currentUrl) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(visible) {
        if (visible) {
            serverUrl = currentUrl
        }
    }

    TempoBottomSheet(visible = visible, onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BasicText(
                text = "CalDAV Setup",
                style = typography.sheetTitle.copy(color = palette.text)
            )

            SetupField(value = serverUrl, onValueChange = { serverUrl = it }, placeholder = "Server URL (e.g. https://...)")
            SetupField(value = username, onValueChange = { username = it }, placeholder = "Username")
            SetupField(value = password, onValueChange = { password = it }, placeholder = "Password")

            Spacer(Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(palette.accent)
                    .clickable {
                        if (serverUrl.isNotBlank() && username.isNotBlank()) {
                            onSave(serverUrl.trim(), username.trim(), password)
                        }
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    text = "Save & Connect",
                    style = typography.button.copy(color = palette.accentContrast)
                )
            }
        }
    }
}

@Composable
private fun SetupField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(palette.glass)
            .border(1.5.dp, palette.border, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        if (value.isEmpty()) {
            BasicText(
                text = placeholder,
                style = typography.body.copy(color = palette.textMuted)
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = typography.body.copy(color = palette.text),
            cursorBrush = SolidColor(palette.accent),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    BasicText(
        text = text,
        style = typography.sectionLabel.copy(color = palette.textMuted),
        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsRow(
    label: String,
    value: String,
    isAccent: Boolean = false
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicText(
            text = label,
            style = typography.body.copy(color = palette.text)
        )
        BasicText(
            text = value,
            style = typography.eventTime.copy(
                color = if (isAccent) palette.accent else palette.textSec,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun SettingsDivider() {
    val palette = TempoDesign.palette
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(palette.border)
    )
}

@Composable
private fun ToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val palette = TempoDesign.palette

    Box(
        modifier = Modifier
            .width(44.dp)
            .height(26.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(if (checked) palette.accent else palette.textMuted.copy(alpha = 0.3f))
            .clickable { onCheckedChange(!checked) }
            .semantics { contentDescription = if (checked) "Enabled" else "Disabled" }
    ) {
        Box(
            modifier = Modifier
                .padding(2.dp)
                .size(22.dp)
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
