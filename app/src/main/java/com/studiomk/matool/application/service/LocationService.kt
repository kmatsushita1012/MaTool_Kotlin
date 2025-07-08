package com.studiomk.matool.application.service

import com.studiomk.matool.domain.contracts.api.ApiRepository
import com.studiomk.matool.domain.entities.shared.*
import com.studiomk.matool.domain.contracts.location.LocationClient
import com.studiomk.matool.domain.contracts.location.LocationResult
import com.studiomk.matool.domain.entities.locations.Interval
import com.studiomk.matool.domain.entities.locations.Location
import com.studiomk.matool.domain.entities.locations.Status
import java.time.LocalDateTime
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

class LocationService : KoinComponent {

    private val apiClient: ApiRepository by inject()
    private val authService: AuthService by inject()
    private val locationClient: LocationClient by inject()

    private var timerJob: Job? = null
    private val _locationHistory = MutableStateFlow<List<Status>>(emptyList())
    val locationHistory: List<Status> get() = _locationHistory.value
    val historyStream: StateFlow<List<Status>> get() = _locationHistory.asStateFlow()

    @Volatile
    var isTracking: Boolean = false
        private set

    @Volatile
    var interval: Interval = Interval.sample
        private set

    fun startTracking(id: String, interval: Interval) {
        if (isTracking) return
        isTracking = true
        locationClient.startTracking()
        this.interval = interval
        startTimer(id, interval.value.toLong())
    }

    fun stopTracking(id: String) {
        if (!isTracking) return
        locationClient.stopTracking()
        stopTimer()
        isTracking = false

        CoroutineScope(Dispatchers.IO).launch {
            val token = authService.getAccessToken()
            if (token != null) {
                deleteLocation(id, token)
            }
        }
    }

    private fun startTimer(id: String, intervalSec: Long) {
        stopTimer()
        timerJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val token = authService.getAccessToken()
                if (token != null) {
                    fetchLocationAndSend(id, token)
                }
                delay(intervalSec * 1000)
            }
        }
        // 最初の即時送信
        CoroutineScope(Dispatchers.IO).launch {
            val token = authService.getAccessToken()
            if (token != null) {
                fetchLocationAndSend(id, token)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun appendHistory(status: Status) {
        _locationHistory.update { it + status }
    }

    private suspend fun fetchLocationAndSend(id: String, accessToken: String) {
        val locationResultFlow = locationClient.getLocation()
        when (val locationResult = locationResultFlow.value) {
            is LocationResult.Loading -> appendHistory(Status.Loading(LocalDateTime.now()))
            is LocationResult.Failure -> appendHistory(Status.LocationError(LocalDateTime.now()))
            is LocationResult.Success -> {
                val location = Location(
                    districtId = id,
                    coordinate = locationResult.coordinate,
                    timestamp = LocalDateTime.now()
                )
                when (val result = apiClient.putLocation(location, accessToken)) {
                    is Result.Success -> appendHistory(Status.Update(location))
                    is Result.Failure -> appendHistory(Status.ApiError(LocalDateTime.now()))
                }
            }
        }
    }

    private suspend fun deleteLocation(id: String, accessToken: String) {
        when (val result = apiClient.deleteLocation(id, accessToken)) {
            is Result.Success -> appendHistory(Status.Delete(LocalDateTime.now()))
            is Result.Failure -> appendHistory(Status.ApiError(LocalDateTime.now()))
        }
    }
}

val locationServiceModule = module {
    single { LocationService() }
}