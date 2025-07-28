package com.studiomk.matool.presentation.store_view.admin.regions.top

import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.annotation.ChildFeature
import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.reducer.LetScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.studiomk.matool.domain.contracts.api.ApiRepository
import com.studiomk.matool.domain.contracts.api.ApiError
import com.studiomk.matool.domain.contracts.auth.AuthError
import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.domain.contracts.auth.UpdateEmailResult
import com.studiomk.matool.domain.entities.districts.PublicDistrict
import com.studiomk.matool.domain.entities.regions.Region
import com.studiomk.matool.domain.entities.routes.RouteSummary
import com.studiomk.matool.domain.entities.shared.UserRole
import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.matool.presentation.store_view.admin.regions.edit.AdminRegionEdit
import com.studiomk.matool.presentation.store_view.admin.regions.district.list.AdminRegionDistrictList
import com.studiomk.matool.presentation.store_view.admin.regions.district.create.AdminRegionDistrictCreate
import com.studiomk.matool.presentation.store_view.auth.change_password.ChangePassword
import com.studiomk.matool.presentation.store_view.auth.update_email.UpdateEmail
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert

object AdminRegionTop : ReducerOf<AdminRegionTop.State, AdminRegionTop.Action>, KoinComponent {

    // DI
    private val apiRepository: ApiRepository by inject()
    private val authService: AuthService by inject()

    sealed class Destination {
        @ChildFeature(AdminRegionEdit::class)
        object Edit : Destination()
        @ChildFeature(AdminRegionDistrictList::class)
        object DistrictInfo : Destination()
        @ChildFeature(AdminRegionDistrictCreate::class)
        object DistrictCreate : Destination()
        @ChildFeature(com.studiomk.matool.presentation.store_view.auth.change_password.ChangePassword::class)
        object ChangePassword : Destination()
        @ChildFeature(com.studiomk.matool.presentation.store_view.auth.update_email.UpdateEmail::class)
        object UpdateEmail : Destination()
    }

    data class State(
        var region: Region,
        var districts: List<PublicDistrict>,
        var isApiLoading: Boolean = false,
        var isAuthLoading: Boolean = false,
        @ChildState val destination: DestinationState? = null,
        @ChildState val alert: NoticeAlert.State? = null
    ) {
        val isLoading: Boolean
            get() = isApiLoading || isAuthLoading
    }

    sealed class Action {
        object OnEdit : Action()
        data class OnDistrictInfo(val district: PublicDistrict) : Action()
        object OnCreateDistrict : Action()
        object HomeTapped : Action()
        object SignOutTapped : Action()
        object DestinationDismissed : Action()
        object ChangePasswordTapped: Action()
        object UpdateEmailTapped: Action()
        data class RegionReceived(val result: Result<Region, ApiError>) : Action()
        data class DistrictsReceived(val result: Result<List<PublicDistrict>, ApiError>) : Action()
        data class DistrictInfoPrepared(val district: PublicDistrict, val result: Result<List<RouteSummary>, ApiError>) : Action()
        data class SignOutReceived(val result: Result<UserRole, AuthError>) : Action()
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
                is Action.OnEdit -> {
                    state.copy(
                        destination = DestinationState.Edit(
                            AdminRegionEdit.State(item = state.region)
                        )
                    ) to Effect.none()
                }
                is Action.OnDistrictInfo -> {
                    state.copy(isApiLoading = true) to Effect.run { send ->
                        val result = apiRepository.getRoutes(action.district.id, authService.getAccessToken())
                        send(Action.DistrictInfoPrepared(action.district, result))
                    }
                }
                is Action.OnCreateDistrict -> {
                    state.copy(
                        destination = DestinationState.DistrictCreate(
                            AdminRegionDistrictCreate.State(region = state.region)
                        )
                    ) to Effect.none()
                }
                is Action.HomeTapped -> state to Effect.none()
                is Action.SignOutTapped -> {
                    state.copy(isAuthLoading = true) to Effect.run { send ->
                        val result = authService.signOut()
                        send(Action.SignOutReceived(result))
                    }
                }
                is Action.DestinationDismissed -> state.copy(destination = null) to Effect.none()
                is Action.ChangePasswordTapped -> state.copy(destination = DestinationState.ChangePassword(state = ChangePassword.State())) to Effect.none()
                is Action.UpdateEmailTapped -> state.copy(destination = DestinationState.UpdateEmail(state = UpdateEmail.State())) to Effect.none()
                is Action.RegionReceived -> {
                    when (val result = action.result) {
                        is Result.Success -> state.copy(
                            isApiLoading = false,
                            region = result.value
                        ) to Effect.none()
                        is Result.Failure -> state.copy(
                            isApiLoading = false,
                            alert = NoticeAlert.State.error("情報の取得に失敗しました。${result.error.localizedDescription}")
                        ) to Effect.none()
                    }
                }
                is Action.DistrictsReceived -> {
                    when (val result = action.result) {
                        is Result.Success -> state.copy(
                            isApiLoading = false,
                            districts = result.value
                        ) to Effect.none()
                        is Result.Failure -> state.copy(
                            isApiLoading = false,
                            alert = NoticeAlert.State.error("情報の取得に失敗しました。${result.error.localizedDescription}")
                        ) to Effect.none()
                    }
                }
                is Action.DistrictInfoPrepared -> {
                    when (val result = action.result) {
                        is Result.Success -> state.copy(
                            isApiLoading = false,
                            destination = DestinationState.DistrictInfo(
                                AdminRegionDistrictList.State(
                                    district = action.district,
                                    routes = result.value.sorted()
                                )
                            )
                        ) to Effect.none()
                        is Result.Failure -> state.copy(
                            isApiLoading = false,
                            alert = NoticeAlert.State.error("情報の取得に失敗しました。${result.error.localizedDescription}")
                        ) to Effect.none()
                    }
                }
                is Action.SignOutReceived -> {
                    when (val result = action.result) {
                        is Result.Success -> state.copy(isAuthLoading = false) to Effect.none()
                        is Result.Failure -> state.copy(
                            isAuthLoading = false,
                            alert = NoticeAlert.State.error("ログアウトに失敗しました。${result.error.localizedDescription}")
                        ) to Effect.none()
                    }
                }
                is Action.Destination -> {
                    when (val action = action.action) {
                        is DestinationAction.Edit -> when (action.action) {
                            is AdminRegionEdit.Action.PutReceived -> when (action.action.result) {
                                is Result.Success -> {
                                    state.copy(isApiLoading = true, destination = null) to getRegionEffect(state.region.id)
                                }
                                is Result.Failure -> state to Effect.none()
                            }
                            is AdminRegionEdit.Action.CancelTapped -> state.copy(destination = null) to Effect.none()
                            else -> state to Effect.none()
                        }
                        is DestinationAction.DistrictCreate -> when (val action = action.action) {
                            is AdminRegionDistrictCreate.Action.Received -> when (action.result) {
                                is Result.Success -> {
                                    state.copy(isApiLoading = true, destination = null,
                                        alert = NoticeAlert.State.confirm("参加町の追加が完了しました。")
                                    ) to Effect.run { send ->
                                        val result = apiRepository.getDistricts(state.region.id)
                                        send(Action.DistrictsReceived(result))
                                    }
                                }
                                is Result.Failure -> {
                                    state.copy(destination = null,
                                        alert = NoticeAlert.State.error("参加町の追加に失敗しました。")
                                    ) to Effect.none()
                                }
                            }
                            is AdminRegionDistrictCreate.Action.CancelTapped -> state.copy(destination = null) to Effect.none()
                            else -> state to Effect.none()
                        }
                        is DestinationAction.DistrictInfo -> when (action.action) {
                            is AdminRegionDistrictList.Action.DismissTapped -> state.copy(destination = null) to Effect.none()
                            else -> state to Effect.none()
                        }
                        is DestinationAction.ChangePassword -> when (val action = action.action) {
                            is ChangePassword.Action.Received -> {
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
                is Action.Alert -> {
                    when (action.action) {
                        is NoticeAlert.Action.OkTapped -> state.copy(alert = null) to Effect.none()
                    }
                }
            }
        }

    private fun getRegionEffect(id: String): Effect<Action> =
        Effect.run { send ->
            val result = apiRepository.getRegion(id)
            send(Action.RegionReceived(result))
        }
}