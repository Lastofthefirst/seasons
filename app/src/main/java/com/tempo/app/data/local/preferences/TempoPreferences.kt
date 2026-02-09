/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tempo_prefs")

class TempoPreferences(private val context: Context) {

    companion object {
        val CALDAV_SERVER_URL = stringPreferencesKey("caldav_server_url")
        val CALDAV_USERNAME = stringPreferencesKey("caldav_username")
        val SYNC_INTERVAL_MINUTES = intPreferencesKey("sync_interval_minutes")
        val DEFAULT_REMINDER_MINUTES = intPreferencesKey("default_reminder_minutes")
        val ALL_DAY_REMINDER_HOUR = intPreferencesKey("all_day_reminder_hour")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
        private const val ENCRYPTED_PREFS_FILE = "tempo_secure_prefs"
        private const val KEY_PASSWORD = "caldav_password"
    }

    private val encryptedPrefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    val calDavServerUrl: Flow<String> = context.dataStore.data.map { it[CALDAV_SERVER_URL] ?: "" }
    val calDavUsername: Flow<String> = context.dataStore.data.map { it[CALDAV_USERNAME] ?: "" }

    private val _calDavPassword = MutableStateFlow(encryptedPrefs.getString(KEY_PASSWORD, "") ?: "")
    val calDavPassword: Flow<String> = _calDavPassword

    val syncIntervalMinutes: Flow<Int> = context.dataStore.data.map { it[SYNC_INTERVAL_MINUTES] ?: 15 }
    val defaultReminderMinutes: Flow<Int> = context.dataStore.data.map { it[DEFAULT_REMINDER_MINUTES] ?: 15 }
    val allDayReminderHour: Flow<Int> = context.dataStore.data.map { it[ALL_DAY_REMINDER_HOUR] ?: 9 }
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { it[NOTIFICATIONS_ENABLED] ?: true }
    val lastSyncTime: Flow<Long> = context.dataStore.data.map { it[LAST_SYNC_TIME] ?: 0L }

    suspend fun setCalDavCredentials(serverUrl: String, username: String, password: String) {
        context.dataStore.edit { prefs ->
            prefs[CALDAV_SERVER_URL] = serverUrl
            prefs[CALDAV_USERNAME] = username
        }
        encryptedPrefs.edit().putString(KEY_PASSWORD, password).apply()
        _calDavPassword.value = password
    }

    suspend fun setSyncInterval(minutes: Int) {
        context.dataStore.edit { it[SYNC_INTERVAL_MINUTES] = minutes }
    }

    suspend fun setDefaultReminder(minutes: Int) {
        context.dataStore.edit { it[DEFAULT_REMINDER_MINUTES] = minutes }
    }

    suspend fun setAllDayReminderHour(hour: Int) {
        context.dataStore.edit { it[ALL_DAY_REMINDER_HOUR] = hour }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setLastSyncTime(time: Long) {
        context.dataStore.edit { it[LAST_SYNC_TIME] = time }
    }
}
