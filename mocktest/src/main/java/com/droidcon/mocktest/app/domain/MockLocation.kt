package com.droidcon.mocktest.app.domain

/**
 * The MockLocation data class contains all information related to the location to mock.
 */
data class MockLocation(
    val id: Long? = null,
    val provider: String,
    val latitude: Double,
    val longitude: Double,
    var speed: Int = 0,
    var accuracy: Int = 0,
    var creationDate: Long = 0
)