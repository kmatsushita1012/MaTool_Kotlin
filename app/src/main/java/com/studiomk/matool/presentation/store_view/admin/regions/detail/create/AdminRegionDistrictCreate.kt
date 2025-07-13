package com.studiomk.matool.presentation.store_view.admin.regions.detail.create

import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.effect.Effect
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.studiomk.matool.domain.contracts.api.ApiRepository
import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.domain.contracts.api.ApiError
import com.studiomk.matool.domain.entities.regions.Region
import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert

object AdminRegionDistrictCreate : ReducerOf<AdminRegionDistrictCreate.State, AdminRegionDistrictCreate.Action>, KoinComponent {

    private val apiRepository: ApiRepository by inject()
    private val authService: AuthService by inject()

    data class State(
        val region: Region,
        var name: String = "",
        var email: String = "",
        var isLoading: Boolean = false,
        @ChildState var alert: NoticeAlert.State? = null
    )

    sealed class Action {
        data class NameChanged(val value: String): Action()
        data class EmailChanged(val value: String): Action()
        object CreateTapped : Action()
        object CancelTapped : Action()
        data class Received(val result: Result<String, ApiError>) : Action()
        @ChildAction data class Alert(val action: NoticeAlert.Action) : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        Reduce { state, action ->
            when (action) {
                is Action.NameChanged -> state.copy(name = action.value) to Effect.none()
                is Action.EmailChanged -> state.copy(email = action.value) to Effect.none()
                is Action.CreateTapped -> {
                    if (state.name.isEmpty() || state.email.isEmpty()) {
                        state to Effect.none()
                    } else {
                        state.copy(
                            isLoading = true,
                        ) to Effect.run { send ->
                            val accessToken = authService.getAccessToken()
                            if (accessToken != null) {
                                val result = apiRepository.postDistrict(state.region.id, state.name, state.email, accessToken)
                                send(Action.Received(result))
                            }
                        }
                    }
                }
                is Action.CancelTapped -> state to Effect.none()
                is Action.Received -> {

                    when (val result = action.result) {
                        is Result.Success -> state.copy(
                            isLoading = false,
                        ) to Effect.none()
                        is Result.Failure -> {
                            state.copy(
                                alert = NoticeAlert.State.error("作成に失敗しました。\n${result.error.localizedDescription}")
                            ) to Effect.none()
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