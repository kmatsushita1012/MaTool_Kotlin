package com.studiomk.matool.presentation.store_view.admin.regions.detail.list

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
import com.studiomk.matool.domain.entities.districts.PublicDistrict
import com.studiomk.matool.domain.entities.routes.RouteSummary
import com.studiomk.matool.domain.entities.routes.PublicRoute
import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.matool.presentation.store_view.admin.districts.route.export.AdminRouteExport
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert

object AdminRegionDistrictList : ReducerOf<AdminRegionDistrictList.State, AdminRegionDistrictList.Action>, KoinComponent {

    private val apiRepository: ApiRepository by inject()
    private val authService: AuthService by inject()

    data class State(
        val district: PublicDistrict,
        val routes: List<RouteSummary>,
        var isLoading: Boolean = false,
        @ChildState var export: AdminRouteExport.State? = null,
        @ChildState var alert: NoticeAlert.State? = null
    )

    sealed class Action {
        data class ExportTapped(val route: RouteSummary) : Action()
        data class ExportPrepared(val result: Result<PublicRoute, ApiError>) : Action()
        object DismissTapped : Action()
        @ChildAction data class Export(val action: AdminRouteExport.Action) : Action()
        @ChildAction data class Alert(val action: NoticeAlert.Action) : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        Reduce { state, action ->
            when (action) {
                is Action.ExportTapped -> {
                    state.copy(
                        isLoading = true,
                    ) to Effect.run { send ->
                        val result = apiRepository.getRoute(action.route.id, authService.getAccessToken())
                        send(Action.ExportPrepared(result))
                    }
                }
                is Action.ExportPrepared -> {
                    state.isLoading = false
                    when (val result = action.result) {
                        is Result.Success -> {
                            state.export = AdminRouteExport.State(route = result.value)
                        }
                        is Result.Failure -> {
                            state.alert = NoticeAlert.State.error("情報の取得に失敗しました。\n${result.error.localizedDescription}")
                        }
                    }
                    state to Effect.none()
                }
                is Action.DismissTapped -> state to Effect.none()
                is Action.Export -> {
                    when (action.action) {
                        is AdminRouteExport.Action.DismissTapped -> {
                            state.export = null
                        }
                        else -> {}
                    }
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