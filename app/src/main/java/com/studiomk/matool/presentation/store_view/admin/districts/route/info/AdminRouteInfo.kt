package com.studiomk.matool.presentation.store_view.admin.districts.route.info

import android.util.Log
import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.domain.contracts.api.ApiError
import com.studiomk.matool.domain.contracts.api.ApiRepository
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.matool.domain.entities.routes.Route
import com.studiomk.matool.domain.entities.shared.Coordinate
import com.studiomk.matool.domain.entities.shared.Information
import com.studiomk.matool.domain.entities.shared.SimpleDate
import com.studiomk.matool.domain.entities.shared.SimpleTime
import com.studiomk.matool.domain.entities.shared.Span
import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.matool.domain.entities.shared.fromDate
import com.studiomk.matool.presentation.store_view.admin.districts.route.map.AdminRouteMap
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert

import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.annotation.ChildFeature
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.reducer.LetScope
import com.studiomk.matool.presentation.store_view.shared.confirm_alert.ConfirmAlert
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object AdminRouteInfo : ReducerOf<AdminRouteInfo.State, AdminRouteInfo.Action>, KoinComponent{

    private val apiClient: ApiRepository by inject()
    private val authService: AuthService by inject()

    sealed class Destination {
        @ChildFeature(AdminRouteMap::class)
        object Map : Destination()
    }

    sealed class AlertDestination {
        @ChildFeature(NoticeAlert::class)
        object Notice : AlertDestination()
        @ChildFeature(ConfirmAlert::class)
        object Delete : AlertDestination()
    }

    sealed class Mode {
        data class Create(val id: String, val span: Span) : Mode()
        data class Edit(val route: Route) : Mode()
        val isCreate: Boolean get() = this is Create
    }

    data class State(
        val mode: Mode,
        val route: Route = when (mode) {
            is Mode.Create -> Route(
                id = java.util.UUID.randomUUID().toString(),
                districtId = mode.id,
                date = SimpleDate.fromDate(mode.span.start),
                start = SimpleTime(hour = 12, minute = 0),
                goal = SimpleTime(hour = 12, minute = 0)
            )
            is Mode.Edit -> mode.route
        },
        val events: List<Information>,
        val origin: Coordinate?,
        val isLoading: Boolean = false,
        @ChildState val destination: DestinationState? = null,
        @ChildState val alert: AlertDestinationState? = null,
    )

    sealed class Action {
        data class TitleChanged(val title: String) : Action()
        data class DateChanged(val date: SimpleDate) : Action()
        data class DescriptionChanged(val description: String) : Action()
        data class StartChanged(val start: SimpleTime) : Action()
        data class GoalChanged(val goal: SimpleTime) : Action()

        object MapTapped : Action()
        object SaveTapped : Action()
        object CancelTapped : Action()
        object DeleteTapped : Action()
        data class PostReceived(val result: Result<String, ApiError>) : Action()
        data class DeleteReceived(val result: Result<String, ApiError>) : Action()
        @ChildAction data class Destination(val action: DestinationAction) : Action()
        @ChildAction data class Alert(val action: AlertDestinationAction) : Action()
    }


    override fun body(): ReducerOf<State, Action> =
        LetScope(
            statePath = destinationKey,
            actionPath = destinationCase,
            reducer = DestinationReducer
        ) +
        LetScope(
            statePath = alertKey,
            actionPath = alertCase,
            reducer = AlertDestinationReducer
        ) +
        Reduce { state, action ->
            Log.d("AdminRouteInfo1", "action: $action")
            Log.d("AdminRouteInfo1", "action: ${state.route.title}")
            when (action) {
                is Action.TitleChanged -> state.copy(route = state.route.copy(title = action.title)) to Effect.none()
                is Action.DateChanged -> state.copy(route = state.route.copy(date = action.date)) to Effect.none()
                is Action.DescriptionChanged -> state.copy(route = state.route.copy(description = action.description)) to Effect.none()
                is Action.StartChanged -> state.copy(route = state.route.copy(start = action.start)) to Effect.none()
                is Action.GoalChanged -> state.copy(route = state.route.copy(goal = action.goal)) to Effect.none()
                is Action.MapTapped -> {
                    state.copy(
                        destination = DestinationState.Map(
                            AdminRouteMap.State(
                                route = state.route,
                                events = state.events,
                                origin = state.origin
                            )
                        )
                    ) to Effect.none()
                }
                is Action.SaveTapped -> {
                    if (state.route.title.isEmpty()) {
                        state.copy(alert =
                            AlertDestinationState.Notice(
                                NoticeAlert.State.error("タイトルは1文字以上を指定してください。")
                            )
                        ) to Effect.none()
                    } else if (state.route.title.contains("/")) {
                        state.copy(alert =
                            AlertDestinationState.Notice(
                                NoticeAlert.State.error(
                                    "タイトルに\"/\"を含むことはできません"
                                )
                            )
                        ) to Effect.none()
                    } else if (state.route.start >= state.route.goal) {
                        state.copy(alert =
                            AlertDestinationState.Notice(
                                NoticeAlert.State.error(
                                    "終了時刻は開始時刻より後に設定してください"
                                )
                            )
                        ) to Effect.none()
                    } else {
                        val isCreate = state.mode.isCreate
                        state.copy(isLoading = true) to Effect.run { send ->
                            val token = authService.getAccessToken()
                            if (token != null) {
                                val result = if (isCreate) {
                                    apiClient.postRoute(state.route, token)
                                } else {
                                    apiClient.putRoute(state.route, token)
                                }
                                send(Action.PostReceived(result))
                            } else {
                                send(Action.PostReceived(Result.Failure(ApiError.Unknown("認証に失敗しました。ログインし直してください。"))))
                            }
                        }
                    }
                }
                is Action.CancelTapped -> state to Effect.none()
                is Action.DeleteTapped -> {
                    state.copy(alert =
                        AlertDestinationState.Delete(
                            ConfirmAlert.State.delete()
                        )
                    ) to Effect.none()
                }
                is Action.PostReceived -> {
                    state.copy(isLoading = false).let { newState ->
                        if (action.result is Result.Failure) {
                            newState.copy(alert =
                                AlertDestinationState.Notice(
                                    NoticeAlert.State.error("情報の取得に失敗しました。 ${action.result.error.localizedDescription}")
                                )
                            ) to Effect.none()
                        } else {
                            newState to Effect.none()
                        }
                    }
                }
                is Action.DeleteReceived -> {
                    state.copy(isLoading = false).let { newState ->
                        if (action.result is Result.Failure) {
                            newState.copy(alert =
                                AlertDestinationState.Notice(
                                    NoticeAlert.State.error("情報の取得に失敗しました。 ${(action.result as Result.Failure).error.localizedDescription}")
                                )
                            ) to Effect.none()
                        } else {
                            newState to Effect.none()
                        }
                    }
                }
                is Action.Destination -> {
                    when (val action = action.action) {
                        is DestinationAction.Map -> {
                            when (action.action) {
                                is AdminRouteMap.Action.DoneTapped -> {
                                    val mapState = (destinationKey + Destination.Map.key).get(state)
                                    if (mapState != null) {
                                        state.copy(route = mapState.manager.value, destination = null) to Effect.none()
                                    } else {
                                        state.copy(destination = null) to Effect.none()
                                    }
                                }
                                is AdminRouteMap.Action.CancelTapped -> state.copy(destination = null) to Effect.none()
                                else -> state to Effect.none()
                            }
                        }
                    }
                }
                is Action.Alert -> {
                    when (val action = action.action) {
                        is AlertDestinationAction.Notice -> {
                            when (action.action) {
                                is NoticeAlert.Action.OkTapped -> state.copy(alert = null) to Effect.none()
                            }
                        }
                        is AlertDestinationAction.Delete -> {
                            when (action.action) {
                                is ConfirmAlert.Action.OkTapped -> {
                                    state.copy(
                                        alert = null,
                                        isLoading = true
                                    ) to Effect.run { send ->
                                        val token = authService.getAccessToken()
                                        if (token != null) {
                                            val result = apiClient.deleteRoute(state.route.id, token)
                                            send(Action.DeleteReceived(result))
                                        } else {
                                            send(Action.PostReceived(Result.Failure(ApiError.Unknown("認証に失敗しました。ログインし直してください。"))))
                                        }
                                    }
                                }
                                is ConfirmAlert.Action.CancelTapped -> state.copy(alert = null) to Effect.none()
                            }
                        }
                    }
                }
            }
        } +
        Reduce { state, action ->
            Log.d("AdminRouteInfo2", "action: ${state.route.title}")
            state to Effect.none()
        }
}