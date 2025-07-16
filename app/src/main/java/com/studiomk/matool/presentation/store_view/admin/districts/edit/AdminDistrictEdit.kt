package com.studiomk.matool.presentation.store_view.admin.districts.edit

import com.studiomk.matool.domain.contracts.api.ApiError
import com.studiomk.matool.domain.contracts.api.ApiRepository
import com.studiomk.matool.domain.entities.districts.District
import com.studiomk.matool.domain.entities.districts.Performance
import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.domain.entities.districts.Visibility
import com.studiomk.matool.presentation.store_view.admin.districts.edit.area.AdminAreaEdit
import com.studiomk.matool.presentation.store_view.admin.districts.edit.base.AdminBaseEdit
import com.studiomk.matool.presentation.store_view.admin.districts.edit.performance.AdminPerformanceEdit
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert
import com.studiomk.matool.presentation.store_view.shared.confirm_alert.ConfirmAlert
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.annotation.ChildFeature
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.reducer.LetScope
import com.studiomk.matool.core.others.remove
import com.studiomk.matool.domain.entities.districts.DistrictTool
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object AdminDistrictEdit : ReducerOf<AdminDistrictEdit.State, AdminDistrictEdit.Action>, KoinComponent {

    private val apiClient: ApiRepository by inject()
    private val authService: AuthService by inject()

    sealed class Destination {
        @ChildFeature(AdminBaseEdit::class)
        object Base : Destination()
        @ChildFeature(AdminAreaEdit::class)
        object Area : Destination()
        @ChildFeature(AdminPerformanceEdit::class)
        object Performance : Destination()
    }

    data class State(
        var item: District,
        val tool: DistrictTool,
        val image: Any? = null, // PhotosPickerItem相当はUIで管理
        val isLoading: Boolean = false,
        @ChildState val destination: DestinationState? = null,
        @ChildState val alert: NoticeAlert.State? = null
    )

    sealed class Action {
        data class NameChanged(val name: String) : Action()
        data class DescriptionChanged(val description: String) : Action()
        data class VisibilityChanged(val visibility: Visibility) : Action()
        object CancelTapped : Action()
        object SaveTapped : Action()
        object BaseTapped : Action()
        object AreaTapped : Action()
        object PerformanceAddTapped : Action()
        object DestinationDismissed : Action()
        data class PerformanceEditTapped(val item: Performance) : Action()
        data class PostReceived(val result: Result<String, ApiError>) : Action()
        @ChildAction data class Destination(val action: DestinationAction) : Action()
        @ChildAction data class Alert(val action: NoticeAlert.Action) : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        LetScope(
          statePath = destinationKey,
            actionPath = destinationCase,
            reducer = DestinationReducer
        )+
        LetScope(
            statePath = alertKey,
            actionPath = alertCase,
            reducer = NoticeAlert
        ) +
        Reduce { state, action ->
            when (action) {
                is Action.NameChanged -> state.copy(item = state.item.copy(name = action.name)) to Effect.none()
                is Action.DescriptionChanged -> state.copy(item = state.item.copy(description = action.description)) to Effect.none()
                is Action.VisibilityChanged -> state.copy(item = state.item.copy(visibility = action.visibility)) to Effect.none()
                is Action.CancelTapped -> state to Effect.none()
                is Action.SaveTapped -> {
                    state.copy(isLoading = true) to Effect.run { send ->
                        val token = authService.getAccessToken()
                        if (token != null) {
                            val result = apiClient.putDistrict(state.item, token)
                            send(Action.PostReceived(result))
                        } else {
                            send(Action.PostReceived(Result.Failure(ApiError.Unknown("認証に失敗しました。ログインし直してください"))))
                        }
                    }
                }
                is Action.BaseTapped -> {
                    state.copy(
                        destination = DestinationState.Base(
                            AdminBaseEdit.State(
                                base = state.item.base,
                                origin = state.item.base ?: state.tool.base
                            )
                        )
                    ) to Effect.none()
                }
                is Action.AreaTapped -> {
                    state.copy(
                        destination = DestinationState.Area(
                            AdminAreaEdit.State(
                                coordinates = state.item.area,
                                origin = state.item.base ?: state.tool.base
                            )
                        )
                    ) to Effect.none()
                }
                is Action.PerformanceAddTapped -> {
                    state.copy(
                        destination = DestinationState.Performance(
                            AdminPerformanceEdit.State()
                        )
                    ) to Effect.none()
                }
                is Action.PerformanceEditTapped -> {
                    state.copy(
                        destination = DestinationState.Performance(
                            AdminPerformanceEdit.State(item = action.item)
                        )
                    ) to Effect.none()
                }
                is Action.DestinationDismissed -> state.copy(destination = null) to Effect.none()
                is Action.PostReceived -> {
                    state.copy(isLoading = false).let { newState ->
                        if (action.result is Result.Failure) {
                            newState.copy(alert = NoticeAlert.State.error("保存に失敗しました。${action.result.error.localizedDescription}")) to Effect.none()
                        } else {
                            newState to Effect.none()
                        }
                    }
                }
                is Action.Destination -> {
                    when (val destAction = action.action) {
                        is DestinationAction.Base -> when (destAction.action) {
                            is AdminBaseEdit.Action.DoneTapped -> {
                                val baseState = (destinationKey + Destination.Base.key).get(state)
                                if (baseState != null) {
                                    state.item = state.item.copy(base = baseState.base)
                                }
                                state.copy(destination = null) to Effect.none()
                            }
                            is AdminBaseEdit.Action.DismissTapped -> state.copy(destination = null) to Effect.none()
                            else -> state to Effect.none()
                        }
                        is DestinationAction.Area -> when (destAction.action) {
                            is AdminAreaEdit.Action.DoneTapped -> {
                                val areaState = (destinationKey + Destination.Area.key).get(state)
                                if (areaState != null) {
                                    state.item = state.item.copy(area = areaState.coordinates)
                                }
                                state.copy(destination = null) to Effect.none()
                            }
                            is AdminAreaEdit.Action.DismissTapped -> state.copy(destination = null) to Effect.none()
                            else -> state to Effect.none()
                        }
                        is DestinationAction.Performance -> when (val perfAction = destAction.action) {
                            is AdminPerformanceEdit.Action.DoneTapped -> {
                                val perfState = (destinationKey + Destination.Performance.key).get(state)
                                if (perfState != null) {
                                    // upsert: 既存なら更新、なければ追加
                                    val performances = state.item.performances.toMutableList()
                                    val idx = performances.indexOfFirst { it.id == perfState.item.id }
                                    if (idx >= 0) {
                                        performances[idx] = perfState.item
                                    } else {
                                        performances.add(perfState.item)
                                    }
                                    val item = state.item.copy(performances = performances)
                                    state.copy(item = item, destination = null) to Effect.none()
                                }else {
                                    state.copy(destination = null) to Effect.none()
                                }
                            }
                            is AdminPerformanceEdit.Action.CancelTapped -> state.copy(destination = null) to Effect.none()
                            is AdminPerformanceEdit.Action.DeleteAlert ->{
                                when(val action = perfAction.action){
                                    is ConfirmAlert.Action.OkTapped -> {
                                        val perfState = (destinationKey + Destination.Performance.key).get(state)
                                        if (perfState != null) {
                                            val item= state.item.copy(
                                                performances = state.item.performances.remove(perfState.item)
                                            )
                                            state.copy(item = item, destination = null) to Effect.none()
                                        }else{
                                            state to Effect.none()
                                        }
                                    }
                                    else -> state.copy(destination = null) to Effect.none()
                                }
                            }
                            else -> state to Effect.none()
                        }
                    }
                }
                is Action.Alert -> {
                    when (action.action) {
                        is NoticeAlert.Action.OkTapped -> state.copy(alert = null) to Effect.none()
                    }
                }
            }
        }
}