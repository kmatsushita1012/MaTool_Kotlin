package com.studiomk.matool.presentation.store_view.pub.map.location

import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.domain.contracts.api.ApiError
import com.studiomk.matool.domain.contracts.api.ApiRepository
import com.studiomk.matool.domain.entities.locations.PublicLocation
import com.studiomk.matool.presentation.utils.CoordinateRegion
import org.koin.core.component.inject
import kotlin.getValue
import org.koin.core.component.KoinComponent


object PublicLocationMap: ReducerOf<PublicLocationMap.State, PublicLocationMap.Action>, KoinComponent {

    data class State(
        val regionId: String,
        val locations: List<PublicLocation> = listOf(),
        val coordinateRegion: CoordinateRegion? = null,
    )

    sealed class Action {
        class OnAppear() : Action()
        data class LocationsReceived(val result: Result<List<PublicLocation>, ApiError>) : Action()
    }

    private val apiRepository: ApiRepository by inject()
    private val authService: AuthService by inject()

    override fun body(): ReducerOf<State, Action> =
        Reduce { state, action ->
            when (action) {
                is Action.OnAppear -> {
                    state to Effect.run { send ->
                        val result = apiRepository.getLocations(state.regionId, authService.getAccessToken())
                        send(Action.LocationsReceived(result))
                    }
                }
                is Action.LocationsReceived -> {
                    when (val result = action.result) {
                        is Result.Success -> {
                            state.copy(locations = result.value) to Effect.none()
                        }
                        is Result.Failure -> {
                            state to Effect.none()
                        }
                    }
                }
            }
        }
}