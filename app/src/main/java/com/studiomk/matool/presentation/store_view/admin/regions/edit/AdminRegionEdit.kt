package com.studiomk.matool.presentation.store_view.admin.regions.edit

import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.annotation.ChildFeature
import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.reducer.LetScope
import com.studiomk.ktca.core.effect.Effect
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.studiomk.matool.domain.contracts.api.ApiRepository
import com.studiomk.matool.domain.contracts.api.ApiError
import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.domain.entities.regions.Region
import com.studiomk.matool.domain.entities.shared.Span
import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.matool.domain.entities.shared.Information
import com.studiomk.matool.presentation.store_view.admin.regions.edit.span.AdminSpanEdit
import com.studiomk.matool.presentation.store_view.admin.shared.information.InformationEdit
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert
import com.studiomk.matool.core.others.*

object AdminRegionEdit : ReducerOf<AdminRegionEdit.State, AdminRegionEdit.Action>, KoinComponent {

    private val apiRepository: ApiRepository by inject()
    private val authService: AuthService by inject()

    sealed class Destination {
        @ChildFeature(AdminSpanEdit::class)
        object Span : Destination()
        @ChildFeature(InformationEdit::class)
        object Milestone : Destination()
    }

    data class State(
        var item: Region,
        val isLoading: Boolean = false,
        @ChildState val destination: DestinationState? = null,
        @ChildState val alert: NoticeAlert.State? = null
    )

    sealed class Action {
        object SaveTapped : Action()
        object CancelTapped : Action()
        data class PutReceived(val result: Result<String, ApiError>) : Action()
        data class OnSpanEdit(val item: Span) : Action()
        object OnSpanAdd : Action()
        data class OnMilestoneEdit(val item: Information) : Action()
        data class OnMilestoneDelete(val item: Information) : Action()
        object OnMilestoneAdd : Action()
        @ChildAction data class Destination(val action: DestinationAction) : Action()
        @ChildAction data class Alert(val action: NoticeAlert.Action) : Action()
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
            reducer = NoticeAlert
        ) +
        Reduce { state, action ->
            when (action) {
                is Action.SaveTapped -> {
                    state.copy(
                        isLoading = true,
                    ) to Effect.run { send ->
                        val token = authService.getAccessToken()
                        if (token != null) {
                            val result = apiRepository.putRegion(state.item, token)
                            send(Action.PutReceived(result))
                        } else {
                            send(
                                Action.PutReceived(
                                    Result.Failure(
                                        ApiError.Unauthorized("認証に失敗しました。ログインし直してください")
                                    )
                                )
                            )
                        }
                    }
                }
                is Action.CancelTapped -> state to Effect.none()
                is Action.PutReceived -> {
                    when (action.result) {
                        is Result.Success -> state to Effect.none()
                        is Result.Failure -> {
                            state.copy(
                                alert = NoticeAlert.State.error("保存に失敗しました。${(action.result as Result.Failure).error.localizedDescription}")
                            ) to Effect.none()
                        }
                    }
                }
                is Action.OnSpanEdit -> {
                    state.copy(
                        destination = DestinationState.Span(
                            AdminSpanEdit.State(action.item)
                        )
                    ) to Effect.none()
                }
                is Action.OnSpanAdd -> {
                    state.copy(
                        destination = DestinationState.Span(
                            AdminSpanEdit.State()
                        )
                    ) to Effect.none()
                }
                is Action.OnMilestoneEdit -> {
                    state.copy(
                        destination = DestinationState.Milestone(
                            InformationEdit.State(
                                title = "経由地",
                                item = action.item
                            )
                        )
                    ) to Effect.none()
                }
                is Action.OnMilestoneDelete -> {
                    state.item.milestones.removeIf { it.id == action.item.id }
                    state to Effect.none()
                }
                is Action.OnMilestoneAdd -> {
                    state.copy(
                        destination = DestinationState.Milestone(
                            InformationEdit.State(
                                title = "経由地",
                                item = Information(id = java.util.UUID.randomUUID().toString())
                            )
                        )
                    ) to Effect.none()
                }
                is Action.Destination -> {
                    when (val destAction = action.action) {
                        is DestinationAction.Span -> when (destAction.action) {
                            is AdminSpanEdit.Action.DoneTapped -> {
                                if (state.destination is DestinationState.Span) {
                                    val spanState = (state.destination as DestinationState.Span).state
                                    var spans = state.item.spans.replace(spanState.span)
                                    spans = spans.sorted().toList()
                                    state.copy(destination = null, item = state.item.copy(spans = spans)) to Effect.none()
                                }else {
                                    state.copy(destination = null) to Effect.none()
                                }

                            }
                            is AdminSpanEdit.Action.CancelTapped -> state.copy(destination = null) to Effect.none()
                            is AdminSpanEdit.Action.Alert -> {
                                when (destAction.action.action) {
                                    is NoticeAlert.Action.OkTapped -> {
                                        if (state.destination is DestinationState.Span) {
                                            val spanState = state.destination.state
                                            state.item.spans.removeIf { it.id == spanState.id }
                                        }
                                        state.copy(destination = null) to Effect.none()
                                    }
                                    else -> state to Effect.none()
                                }
                            }
                            else -> state to Effect.none()
                        }
                        is DestinationAction.Milestone -> when (destAction.action) {
                            is InformationEdit.Action.DoneTapped -> {
                                if (state.destination is DestinationState.Milestone) {
                                    val milestoneState = (state.destination as DestinationState.Milestone).state
                                    state.item.milestones.replace(milestoneState.item)
                                }
                                state.copy(destination = null) to Effect.none()
                            }
                            is InformationEdit.Action.CancelTapped -> state.copy(destination = null) to Effect.none()
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