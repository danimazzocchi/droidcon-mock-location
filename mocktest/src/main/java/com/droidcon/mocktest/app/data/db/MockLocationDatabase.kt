package com.droidcon.mocktest.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MockLocationEntity::class], version = 1)
abstract class MockLocationDatabase : RoomDatabase() {
    abstract fun mockLocationDao(): MockLocationDao
}