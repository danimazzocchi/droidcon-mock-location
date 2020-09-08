package com.droidcon.mocktest.support

import com.droidcon.mocktest.app.data.db.MockLocationEntity
import com.droidcon.mocktest.app.domain.MockLocation

/**
 * Converts the [MockLocationEntity] db object to a [MockLocation] object.
 */
fun MockLocationEntity.toMockLocation(): MockLocation {
    return MockLocation(
        id,
        provider,
        latitude,
        longitude,
        speed,
        accuracy,
        creationDate
    )
}

/**
 * Converts a list of [MockLocationEntity] db object to a ist [MockLocation] object.
 */
fun List<MockLocationEntity>.toMockLocations(): List<MockLocation> {
    val ret = arrayListOf<MockLocation>()
    forEach {
        ret.add(it.toMockLocation())
    }
    return ret
}

/**
 * Converts the [MockLocation] to a [MockLocationEntity] db object.
 */
fun MockLocation.toDbEntity(): MockLocationEntity {
    return MockLocationEntity(
        id,
        provider,
        latitude,
        longitude,
        speed,
        accuracy,
        creationDate
    )
}
