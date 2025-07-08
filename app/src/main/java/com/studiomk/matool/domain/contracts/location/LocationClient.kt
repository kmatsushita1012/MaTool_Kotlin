package com.studiomk.matool.domain.contracts.location

import com.studiomk.matool.domain.entities.shared.Coordinate
import kotlinx.coroutines.flow.StateFlow

interface LocationClient {
    fun startTracking()
    fun stopTracking()
    fun isTracking(): Boolean
    fun getLocation(): StateFlow<LocationResult>
}

sealed class LocationResult {
    data class Success(val coordinate: Coordinate) : LocationResult()
    object Loading : LocationResult()
    data class Failure(val error: LocationError) : LocationResult()
}

enum class LocationError {
    AuthorizationDenied,
    ServicesDisabled
}