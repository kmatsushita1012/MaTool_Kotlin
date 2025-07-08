package com.studiomk.matool.data.location.live

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.studiomk.matool.domain.contracts.location.LocationClient
import com.studiomk.matool.domain.contracts.location.LocationError
import com.studiomk.matool.domain.contracts.location.LocationResult
import com.studiomk.matool.domain.entities.shared.Coordinate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LiveLocationClient(
    private val context: Context
) : LocationClient {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 3000L
    ).setMinUpdateIntervalMillis(1000L).build()

    private val _location: MutableStateFlow<LocationResult> = MutableStateFlow(LocationResult.Loading)
    override fun getLocation(): StateFlow<LocationResult> = _location

    private var isTracking = false

    private val callback = object : LocationCallback() {
        override fun onLocationResult(p0: com.google.android.gms.location.LocationResult) {
            val loc = p0.lastLocation
            if (loc != null) {
                _location.value = LocationResult.Success(
                    Coordinate(
                        latitude = loc.latitude,
                        longitude = loc.longitude
                    )
                )
            }
        }

        override fun onLocationAvailability(availability: LocationAvailability) {
            if (!availability.isLocationAvailable) {
                _location.value = LocationResult.Failure(LocationError.ServicesDisabled)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun startTracking() {
        if (isTracking) return
        // 権限チェックは呼び出し側で行う前提
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )
        isTracking = true
    }

    override fun stopTracking() {
        if (!isTracking) return
        fusedLocationClient.removeLocationUpdates(callback)
        isTracking = false
    }

    override fun isTracking(): Boolean = isTracking
}