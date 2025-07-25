package com.studiomk.matool.presentation.store_view.pub.map.root

import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.annotation.ChildFeature
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.reducer.LetScope
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.domain.contracts.api.ApiError
import com.studiomk.matool.domain.contracts.api.ApiRepository
import com.studiomk.matool.domain.entities.locations.PublicLocation
import com.studiomk.matool.domain.entities.routes.PublicRoute
import com.studiomk.matool.domain.entities.routes.RouteSummary
import com.studiomk.matool.presentation.store_view.pub.map.location.PublicLocationMap
import com.studiomk.matool.presentation.store_view.pub.map.route.PublicRouteMap
import com.studiomk.matool.domain.entities.shared.Result
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PublicMap: ReducerOf<PublicMap.State, PublicMap.Action>, KoinComponent {

    sealed class Tab {
        class Location(): Tab()
        data class Route(val id: String, val name: String): Tab()
    }

    sealed class Destination {
        @ChildFeature(PublicLocationMap::class)
        object Location: Destination()
        @ChildFeature(PublicRouteMap::class)
        object Route: Destination()
    }

    private val apiRepository: ApiRepository by inject()
    private val authService: AuthService by inject()

    data class State(
        val regionId: String,
        val tabItems: List<Tab>,
        val selectedTab: Tab?,
        @ChildState val destination: DestinationState? =  null
    )

    sealed class Action {
        class OnAppear(): Action()
        data class TabSelected(val value: Tab): Action()
        class DismissTapped(): Action()
        data class RoutePrepared(
            val districtId: String,
            val routes: Result<List<RouteSummary>, ApiError>,
            val current: Result<PublicRoute, ApiError>
        ): Action()
        data class LocationPrepared(
            val regionId: String,
            val locations: Result<List<PublicLocation>, ApiError>
        ): Action()
        @ChildAction data class Destination(val action: DestinationAction): Action()
    }

    override fun body(): ReducerOf<State, Action> =
        LetScope(
            statePath = destinationKey,
            actionPath = destinationCase,
            reducer = DestinationReducer
        ) +
        Reduce { state, action ->
            when(action){
                is Action.OnAppear -> switchTab(
                    state = state,
                    selected = state.selectedTab ?: state.tabItems.first()
                )
                is Action.TabSelected -> {
                    val selected = action.value
                    switchTab(
                        state = state,
                        selected = selected
                    )
                }
                is Action.DismissTapped -> state to Effect.none()
                is Action.RoutePrepared -> state.copy(
                    destination =  DestinationState.Route(
                        PublicRouteMap.State(
                            districtId = action.districtId,
                            items = action.routes.value?.sorted() ?: listOf(),
                            selectedItem = action.current.value?.let{ RouteSummary(it) },
                            selectedRoute = action.current.value
                        )
                    )
                ) to Effect.none()
                is Action.LocationPrepared -> state to Effect.none()
                is Action.Destination -> {
                    state to Effect.none()
                }
            }
        }

    fun switchTab(state: State, selected: Tab): Pair<State, Effect<Action>> {
        return state.copy(
            selectedTab = selected,
        ) to Effect.run{ send ->
            val accessToken = authService.getAccessToken()
            when(selected){
                is Tab.Location -> send(Action.LocationPrepared(
                    regionId = state.regionId,
                    locations = apiRepository.getLocations(state.regionId, accessToken)
                ))
                is Tab.Route -> send(Action.RoutePrepared(
                    districtId = selected.id,
                    routes = apiRepository.getRoutes(selected.id, accessToken),
                    current = apiRepository.getCurrentRoute(selected.id, accessToken)
                ))
            }
        }
    }
}

val PublicMap.Tab.text: String
    get() = when (this) {
        is PublicMap.Tab.Location -> "準備中"
        is PublicMap.Tab.Route -> name
    }
