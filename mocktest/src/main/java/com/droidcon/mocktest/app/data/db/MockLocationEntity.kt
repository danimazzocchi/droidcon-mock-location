package com.droidcon.mocktest.app.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MOCK_LOCATIONS_TABLE")
data class MockLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "provider") val provider: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "speed") var speed: Int = 0,
    @ColumnInfo(name = "accuracy") var accuracy: Int = 0,
    @ColumnInfo(name = "creation_date") var creationDate: Long = 0
)