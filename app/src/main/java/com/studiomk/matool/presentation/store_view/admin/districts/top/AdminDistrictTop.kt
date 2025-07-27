package com.studiomk.matool.presentation.store_view.admin.districts.top

import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.annotation.ChildFeature
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.domain.contracts.api.ApiError
import com.studiomk.matool.domain.contracts.api.ApiRepository
import com.studiomk.matool.domain.contracts.auth.AuthError
import com.studiomk.matool.domain.entities.districts.DistrictTool
import com.studiomk.matool.domain.entities.districts.PublicDistrict
import com.studiomk.matool.domain.entities.districts.toModel
import com.studiomk.matool.domain.entities.routes.RouteSummary
import com.studiomk.matool.domain.entities.routes.PublicRoute
import com.studiomk.matool.domain.entities.routes.toModel
import com.studiomk.matool.domain.entities.shared.*
import com.studiomk.matool.application.service.LocationService
import com.studiomk.matool.presentation.store_view.admin.districts.edit.AdminDistrictEdit
import com.studiomk.matool.presentation.store_view.admin.districts.route.info.AdminRouteInfo
import com.studiomk.matool.presentation.store_view.admin.districts.route.export.AdminRouteExport
import com.studiomk.matool.presentation.store_view.admin.districts.location.AdminLocation
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert
import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.reducer.LetScope
import com.studiomk.matool.domain.contracts.auth.UpdateEmailResult
import com.studiomk.matool.presentation.store_view.auth.change_password.ChangePassword
import com.studiomk.matool.presentation.store_view.auth.update_email.UpdateEmail


object AdminDistrictTop : ReducerOf<AdminDistrictTop.State, AdminDistrictTop.Action>, KoinComponent {

    // DI
    private val apiRepository: ApiRepository by inject()
    private val locationService: LocationService by inject()
    private val authService: AuthService by inject()

    sealed class Destination {
        @ChildFeature(AdminDistrictEdit::class)
        object Edit : Destination()
        @ChildFeature(AdminRouteInfo::class)
        object Route : Destination()
        @ChildFeature(AdminRouteExport::class)
        object Export : Destination()
        @ChildFeature(AdminLocation::class)
        object Location : Destination()
        @ChildFeature(com.studiomk.matool.presentation.store_view.auth.change_password.ChangePassword::class)
        object ChangePassword: Destination()
        @ChildFeature(com.studiomk.matool.presentation.store_view.auth.update_email.UpdateEmail::class)
        object UpdateEmail: Destination()
    }

    data class State(
        var district: PublicDistrict,
        var routes: List<RouteSummary>,
        var isDistrictLoading: Boolean = false,
        var isRoutesLoading: Boolean = false,
        var isRouteLoading: Boolean = false,
        var isExportLoading: Boolean = false,
        var isAuthLoading: Boolean = false,
        @ChildState val destination: DestinationState? = null,
        @ChildState val alert: NoticeAlert.State? = null
    ) {
        val isLoading: Boolean
            get() = isDistrictLoading || isRoutesLoading || isAuthLoading || isRouteLoading || isExportLoading
    }

    sealed class Action {
        object OnEdit : Action()
        object OnRouteAdd : Action()
        data class OnRouteEdit(val route: RouteSummary) : Action()
        data class OnRouteExport(val route: RouteSummary) : Action()
        object OnLocation : Action()
        object SignOutTapped : Action()
        object HomeTapped : Action()
        object DestinationDismissed : Action()
        object ChangePasswordTapped: Action()
        object UpdateEmailTapped: Action()
        data class GetDistrictReceived(val result: Result<PublicDistrict, ApiError>) : Action()
        data class GetRoutesReceived(val result: Result<List<RouteSummary>, ApiError>) : Action()
        data class EditPrepared(val result: Result<DistrictTool, ApiError>) : Action()
        data class RouteEditPrepared(
            val routeResult: Result<PublicRoute, ApiError>,
            val toolResult: Result<DistrictTool, ApiError>
        ) : Action()
        data class RouteCreatePrepared(val result: Result<DistrictTool, ApiError>) : Action()
        data class ExportPrepared(val result: Result<PublicRoute, ApiError>) : Action()
        data class SignOutReceived(val result: Result<UserRole, AuthError>) : Action()
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
                is Action.OnEdit -> {
                    state.copy(
                        isDistrictLoading = true
                    ) to Effect.run{ send ->
                        val result = apiRepository.getTool(state.district.id, authService.getAccessToken())
                        send(Action.EditPrepared(result))
                    }
                }
                is Action.OnRouteAdd -> {
                    state.copy(
                        isRouteLoading = true
                    ) to Effect.run { send ->
                        val result = apiRepository.getTool(state.district.id, authService.getAccessToken())
                        send(Action.RouteCreatePrepared(result))
                    }
                }
                is Action.OnRouteEdit -> {
                    state.copy(
                        isRouteLoading = true
                    ) to Effect.run { send ->
                        val routeResult = apiRepository.getRoute(action.route.id, authService.getAccessToken())
                        val toolResult = apiRepository.getTool(action.route.districtId, authService.getAccessToken())
                        send(Action.RouteEditPrepared(routeResult, toolResult))
                    }
                }
                is Action.OnRouteExport -> {
                    state.copy(
                        isExportLoading = true
                    ) to Effect.run { send ->
                        val result = apiRepository.getRoute(action.route.id, authService.getAccessToken())
                        send(Action.ExportPrepared(result))
                    }
                }
                is Action.ChangePasswordTapped -> state.copy(
                    destination = DestinationState.ChangePassword(
                        ChangePassword.State())
                ) to Effect.none()
                is Action.UpdateEmailTapped -> state.copy(
                    destination = DestinationState.UpdateEmail(
                        UpdateEmail.State())
                ) to Effect.none()
                is Action.DestinationDismissed -> {
                    state.copy(destination = null) to Effect.none()
                }
                is Action.GetDistrictReceived -> {
                    when (val result = action.result) {
                        is Result.Success ->
                            state.copy(
                                district = result.value,
                                isDistrictLoading = false
                            ) to Effect.none()
                        is Result.Failure ->
                            state.copy(
                                alert = NoticeAlert.State.error("情報の取得に失敗しました。 ${result.error.localizedDescription}"),
                                isDistrictLoading = false
                            ) to Effect.none()
                    }
                }
                is Action.GetRoutesReceived -> {
                    when (val result = action.result) {
                        is Result.Success ->
                            state.copy(
                                routes = result.value.sortedBy { it },
                                isRoutesLoading = false
                            ) to Effect.none()
                        is Result.Failure ->
                            state.copy(
                                alert = NoticeAlert.State.error("情報の取得に失敗しました。 ${result.error.localizedDescription}"),
                                isRoutesLoading = false
                            ) to Effect.none()
                    }
                }
                is Action.EditPrepared -> {
                    when (val result = action.result) {
                        is Result.Success ->
                            state.copy(
                                isDistrictLoading = false,
                                destination = DestinationState.Edit(
                                    AdminDistrictEdit.State(
                                        item = state.district.toModel(),
                                        tool = result.value
                                    )
                                )
                            ) to Effect.none()

                        is Result.Failure -> {
                            state.copy(
                                isDistrictLoading = false,
                                alert = NoticeAlert.State.error("情報の取得に失敗しました。 ${result.error.localizedDescription}")
                            ) to Effect.none()
                        }
                    }
                }
                is Action.RouteEditPrepared -> {
                    val routeResult = action.routeResult
                    val toolResult = action.toolResult
                    if (routeResult is Result.Success && toolResult is Result.Success) {
                        state.copy(
                            destination = DestinationState.Route(
                                AdminRouteInfo.State(
                                    mode = AdminRouteInfo.Mode.Edit(routeResult.value.toModel()),
                                    events = toolResult.value.milestones,
                                    origin = toolResult.value.base
                                ),
                            ),
                            isRouteLoading = false
                        ) to Effect.none()
                    } else {
                        state.copy(
                            alert = NoticeAlert.State.error("情報の取得に失敗しました。"),
                            isRouteLoading = false
                        ) to Effect.none()
                    }
                }
                is Action.RouteCreatePrepared -> {
                    when (val result = action.result) {
                        is Result.Success -> state.copy(
                            destination = DestinationState.Route(
                                AdminRouteInfo.State(
                                    mode = AdminRouteInfo.Mode.Create(state.district.id, result.value.spans.firstOrNull() ?: Span.sample),
                                    events = result.value.milestones,
                                    origin = result.value.base
                                )
                            ),
                            isRouteLoading = false
                        ) to Effect.none()
                        is Result.Failure ->
                            state.copy(
                                alert = NoticeAlert.State.error("情報の取得に失敗しました。 ${result.error.localizedDescription}"),
                                isRouteLoading = false
                            ) to Effect.none()
                    }
                }
                is Action.ExportPrepared -> {
                    when (val result = action.result) {
                        is Result.Success ->
                            state.copy(
                                destination = DestinationState.Export(AdminRouteExport.State(route = result.value)),
                                isExportLoading = false
                            ) to Effect.none()
                        is Result.Failure ->
                            state.copy(
                                alert = NoticeAlert.State.error("情報の取得に失敗しました。 ${result.error.localizedDescription}"),
                                isExportLoading = false
                            ) to Effect.none()
                    }
                }
                is Action.OnLocation -> {
                    state.copy(destination = DestinationState.Location(AdminLocation.State(id = state.district.id, isTracking = locationService.isTracking))) to Effect.none()
                }
                is Action.Destination -> {
                    when (val action = action.action) {
                        is DestinationAction.Edit -> when (action.action) {
                            is AdminDistrictEdit.Action.CancelTapped -> state.copy(destination = null) to Effect.none()
                            is AdminDistrictEdit.Action.PostReceived -> state.copy(destination = null, isDistrictLoading = true) to
                                Effect.run { send ->
                                    val result = apiRepository.getDistrict(state.district.id)
                                    send(Action.GetDistrictReceived(result))
                                }
                            else -> state to Effect.none()
                        }
                        is DestinationAction.Route -> when (action.action) {
                            is AdminRouteInfo.Action.CancelTapped,
                            is AdminRouteInfo.Action.PostReceived,
                            is AdminRouteInfo.Action.DeleteReceived -> state.copy(destination = null, isRoutesLoading = true) to
                                Effect.run { send ->
                                    val result = apiRepository.getRoutes(state.district.id, authService.getAccessToken())
                                    send(Action.GetRoutesReceived(result))
                                }
                            else -> state to Effect.none()
                        }
                        is DestinationAction.Location -> when (action.action) {
                            is AdminLocation.Action.DismissTapped -> state.copy(destination = null) to Effect.none()
                            else -> state to Effect.none()
                        }
                        is DestinationAction.Export -> when (action.action) {
                            is AdminRouteExport.Action.DismissTapped -> state.copy(destination = null) to Effect.none()
                            else -> state to Effect.none()
                        }
                        is DestinationAction.ChangePassword -> when (val action = action.action) {
                            is ChangePassword.Action.Received ->{
                                when(action.result){
                                    is Result.Success -> state.copy(
                                        destination = null,
                                        alert = NoticeAlert.State.confirm("パスワードが変更されました")
                                    ) to Effect.none()
                                    is Result.Failure -> state to Effect.none()
                                }
                            }
                            is ChangePassword.Action.DismissTapped -> state.copy(destination = null) to Effect.none()
                            else -> state to Effect.none()
                        }
                        is DestinationAction.UpdateEmail -> when (val action = action.action) {
                            is UpdateEmail.Action.ConfirmUpdateReceived -> {
                                when (action.result) {
                                    is Result.Success -> state.copy(
                                        destination = null,
                                        alert = NoticeAlert.State.confirm("メールアドレスが変更されました")
                                    ) to Effect.none()
                                    is Result.Failure -> state to Effect.none()
                                }
                            }
                            is UpdateEmail.Action.UpdateReceived -> {
                                when (action.result) {
                                    is UpdateEmailResult.Completed -> state.copy(
                                        destination = null,
                                        alert = NoticeAlert.State.confirm("メールアドレスが変更されました")
                                    ) to Effect.none()
                                    else -> state to Effect.none()
                                }
                            }
                            is UpdateEmail.Action.DismissTapped -> state.copy(destination = null) to Effect.none()
                            else -> state to Effect.none()
                        }
                    }
                }
                is Action.SignOutTapped -> {
                    state.copy(
                        isAuthLoading = true
                    ) to Effect.run { send ->
                        val result = authService.signOut()
                        send(Action.SignOutReceived(result))
                    }
                }
                is Action.SignOutReceived -> {
                    when(val result = action.result){
                        is Result.Success -> {
                            state.copy(isAuthLoading = false) to Effect.none()
                        }
                        is Result.Failure -> {
                            state.copy(
                                isAuthLoading = false,
                                alert = NoticeAlert.State.error("サインアウトに失敗しました。 ${result.error.localizedDescription}")
                            ) to Effect.none()
                        }
                    }
                }
                is Action.HomeTapped -> state to Effect.none()
                is Action.Alert -> {
                    when (action.action) {
                        is NoticeAlert.Action.OkTapped -> state.copy(alert = null) to Effect.none()
                    }
                }
            }
        }
}