package com.studiomk.matool.presentation.store_view.admin.districts.location

import com.studiomk.matool.application.service.LocationService
import com.studiomk.matool.domain.entities.locations.Interval
import com.studiomk.matool.domain.entities.locations.Status
import com.studiomk.matool.domain.entities.locations.Location
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.reducer.LetScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object AdminLocation : ReducerOf<AdminLocation.State, AdminLocation.Action>, KoinComponent {

    private val locationService: LocationService by inject()

    data class State(
        val id: String,
        var location: Location? = null,
        var isTracking: Boolean,
        var isLoading: Boolean = false,
        var history: List<Status> = emptyList(),
        var selectedInterval: Interval = Interval.sample,
        val intervals: List<Interval> = Interval.options,
        @ChildState var alert: NoticeAlert.State? = null
    )

    sealed class Action {
        object OnAppear : Action()
        object OnDisappear : Action()
        data class ToggleChanged(val value: Boolean) : Action()
        data class HistoryUpdated(val history: List<Status>) : Action()
        data class IntervalChanged(val interval: Interval) : Action()
        object DismissTapped : Action()
        @ChildAction data class Alert(val action: NoticeAlert.Action) : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        LetScope(
            statePath = alertKey,
            actionPath = alertCase,
            reducer = NoticeAlert
        ) +
        Reduce { state, action ->
            when (action) {
                is Action.OnAppear -> {

                    state.copy(
                        selectedInterval = locationService.interval,
                        history = locationService.locationHistory
                    ) to Effect.run(id = "HistoryStream") { send ->
                        locationService.historyStream.collect { history ->
                            send(Action.HistoryUpdated(history))
                        }
                    }
                }
                is Action.OnDisappear -> {
                    state to Effect.cancel("HistoryStream")
                }
                is Action.ToggleChanged -> {
                    if (action.value) {
                        locationService.startTracking(id = state.id, interval = state.selectedInterval)
                    } else {
                        locationService.stopTracking(id = state.id)
                    }
                    state.copy(
                        isTracking = action.value
                    ) to Effect.none()
                }
                is Action.HistoryUpdated -> {
                    state.copy(
                        history = action.history
                    ) to Effect.none()
                }
                is Action.IntervalChanged -> {
                    state.copy(
                        selectedInterval = action.interval
                    ) to Effect.none()
                }
                is Action.DismissTapped -> {
                    state to Effect.none()
                }
                is Action.Alert -> {
                    when (action.action) {
                        is NoticeAlert.Action.OkTapped -> state.copy(alert = null) to Effect.none()
                    }
                }
            }
        }
}