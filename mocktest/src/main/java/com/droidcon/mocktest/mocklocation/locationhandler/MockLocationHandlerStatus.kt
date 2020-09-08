package com.droidcon.mocktest.mocklocation.locationhandler

/**
 * Status of the simulation of the location.
 * <ul>
 *     <li>ACTIVATED if the simulation is running</li>
 *     <li>STOPPED if the simulation is not running</li>
 *     <li>PAUSED if the simulation is running but is paused by the user to certain position</li>
 * </ul>
 */
enum class MockLocationHandlerStatus {ACTIVATED, PAUSED, STOPPED}