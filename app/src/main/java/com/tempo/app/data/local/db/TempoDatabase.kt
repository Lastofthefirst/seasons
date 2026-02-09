/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tempo.app.data.local.db.entity.CalendarEntity
import com.tempo.app.data.local.db.entity.EventEntity

@Database(
    entities = [EventEntity::class, CalendarEntity::class],
    version = 1,
    exportSchema = true
)
abstract class TempoDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
    abstract fun calendarDao(): CalendarDao

    companion object {
        @Volatile
        private var INSTANCE: TempoDatabase? = null

        fun getInstance(context: Context): TempoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TempoDatabase::class.java,
                    "tempo_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
