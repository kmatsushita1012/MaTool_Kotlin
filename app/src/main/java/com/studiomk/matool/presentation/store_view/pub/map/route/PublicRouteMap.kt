package com.studiomk.matool.presentation.store_view.pub.map.route

import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.matool.domain.contracts.api.ApiError
import com.studiomk.matool.domain.contracts.api.ApiRepository
import com.studiomk.matool.domain.entities.routes.PublicRoute
import com.studiomk.matool.domain.entities.routes.RouteSummary
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


object PublicRouteMap: ReducerOf<PublicRouteMap.State, PublicRouteMap.Action>, KoinComponent {

    data class State(
        val districtId: String,
        val routes: List<RouteSummary> = listOf(),
        val selectedRoute: PublicRoute? = null,
        val isMenuPresented: Boolean = false
    )

    sealed class Action {
        class OnAppear() : Action()
        class MenuTapped() : Action()
        data class RouteSelected(val value: RouteSummary) : Action()
        data class RoutesReceived(val result: Result<List<RouteSummary>, ApiError>) : Action()
        data class RouteReceived(val result: Result<PublicRoute, ApiError>) : Action()
    }

    private val apiRepository: ApiRepository by inject()
    private val authService: AuthService by inject()

    override fun body(): ReducerOf<State, Action> =
        Reduce { state, action ->
            when (action) {
                is Action.OnAppear -> {
                    state to Effect.merge(
                        Effect.run { send ->
                            val accessToken = authService.getAccessToken()
                            val result = apiRepository.getRoutes(state.districtId, accessToken)
                            send(Action.RoutesReceived(result))
                        },
                        Effect.run { send ->
                            val accessToken = authService.getAccessToken()
                            val result = apiRepository.getCurrentRoute(state.districtId, accessToken)
                            send(Action.RouteReceived(result))
                        }
                    )
                }
                is Action.MenuTapped -> {
                    state.copy(isMenuPresented = true) to Effect.none()
                }
                is Action.RouteSelected -> {
                    state.copy(
                        isMenuPresented = false
                    ) to Effect.run { send ->
                        val result = apiRepository.getRoute(action.value.id, authService.getAccessToken())
                        send(Action.RouteReceived(result))
                    }
                }
                is Action.RoutesReceived -> {
                    when (val result = action.result) {
                        is Result.Success -> {
                            state.copy(routes = result.value) to Effect.none()
                        }
                        is Result.Failure -> {
                            state to Effect.none()
                        }
                    }
                }
                is Action.RouteReceived -> {
                    when (val result = action.result) {
                        is Result.Success -> {
                            state.copy(selectedRoute = result.value) to Effect.none()
                        }

                        is Result.Failure -> {
                            state to Effect.none()
                        }
                    }
                }
            }
        }
}