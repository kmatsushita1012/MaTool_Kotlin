package com.studiomk.matool.presentation.store_view.pub.map.route

import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.matool.domain.contracts.api.ApiError
import com.studiomk.matool.domain.contracts.api.ApiRepository
import com.studiomk.matool.domain.entities.locations.PublicLocation
import com.studiomk.matool.domain.entities.routes.Point
import com.studiomk.matool.domain.entities.routes.PublicRoute
import com.studiomk.matool.domain.entities.routes.RouteSummary
import com.studiomk.matool.domain.entities.routes.Segment
import com.studiomk.matool.presentation.utils.CoordinateRegion
import com.studiomk.matool.presentation.utils.makeRegion
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


object PublicRouteMap: ReducerOf<PublicRouteMap.State, PublicRouteMap.Action>, KoinComponent {

    data class State(
        val districtId: String,
        val items: List<RouteSummary>,
        val selectedItem: RouteSummary? = null,
        val selectedRoute: PublicRoute?,
        val location: PublicLocation? = null,
        val isMenuPresented: Boolean = false,
        val selectedPoint: Point? = null,
        val coordinateRegion: CoordinateRegion? = selectedRoute?.let{ makeRegion(it.points.map { it.coordinate }) },
    ){
        val points:List<Point>? = selectedRoute?.let { filterPoints(it) }
        val segments:List<Segment>? = selectedRoute?.segments
    }

    sealed class Action {
        data class ToggleChanged(val value: Boolean) : Action()
        data class ItemSelected(val value: RouteSummary?) : Action()
        data class RouteReceived(val result: Result<PublicRoute, ApiError>) : Action()
        data class PointSelected(val value: Point?) : Action()
        object PointClosed : Action()
    }

    private val apiRepository: ApiRepository by inject()
    private val authService: AuthService by inject()

    override fun body(): ReducerOf<State, Action> =
        Reduce { state, action ->
            when (action) {
                is Action.ToggleChanged -> {
                    state.copy(isMenuPresented = action.value) to Effect.none()
                }
                is Action.ItemSelected -> {
                    state.copy(
                        isMenuPresented = false,
                        selectedItem = action.value
                    ) to Effect.run { send ->
                        action.value?.let{
                            val result = apiRepository.getRoute(it.id, authService.getAccessToken())
                            send(Action.RouteReceived(result))
                        }
                    }
                }
                is Action.RouteReceived -> {
                    when (val result = action.result) {
                        is Result.Success -> {
                            state.copy(
                                selectedItem = RouteSummary(result.value),
                                selectedRoute = result.value,
                                isMenuPresented = false,
                                coordinateRegion = makeRegion(result.value.points.map { it.coordinate })
                            ) to Effect.none()
                        }
                        is Result.Failure -> {
                            state to Effect.none()
                        }
                    }
                }
                is Action.PointSelected -> {
                    state.copy(selectedPoint = action.value) to Effect.none()
                }
                is Action.PointClosed -> {
                    state.copy(selectedPoint = null) to Effect.none()
                }
            }
        }
    
    fun filterPoints(route: PublicRoute): List<Point> {
        val newPoints = mutableListOf<Point>()
        val points = route.points
        if(points.isEmpty()){
            return newPoints
        }
        if (points.firstOrNull()?.title == null) {
            val first = points.first()
            val tempFirst = first.copy(
                title = "出発",
                time = route.start
            )
            newPoints.add(tempFirst)
        }
        newPoints.addAll(points.filter { it.title != null })
        if (points.size >= 2 && points.lastOrNull()?.title == null) {
            val last = points.last()
            val tempLast = last.copy(
                title = "到着",
                time = route.goal
            )
            newPoints.add(tempLast)
        }
        return newPoints
    }
}
