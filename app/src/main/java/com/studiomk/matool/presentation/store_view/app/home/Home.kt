package com.studiomk.matool.presentation.store_view.app.home

import SignInResult
import com.studiomk.matool.application.service.AuthService
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.annotation.ChildFeature
import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.reducer.LetScope
import com.studiomk.matool.domain.contracts.api.ApiError
import com.studiomk.matool.domain.contracts.api.ApiRepository
import com.studiomk.matool.domain.contracts.auth.AuthError
import com.studiomk.matool.domain.contracts.local_store.LocalStore
import com.studiomk.matool.di.DefaultValues
import com.studiomk.matool.domain.entities.districts.PublicDistrict
import com.studiomk.matool.domain.entities.routes.RouteSummary
import com.studiomk.matool.domain.entities.regions.Region
import com.studiomk.matool.domain.entities.shared.UserRole
import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.matool.presentation.store_view.admin.districts.top.AdminDistrictTop
import com.studiomk.matool.presentation.store_view.admin.regions.top.AdminRegionTop
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.studiomk.matool.presentation.store_view.auth.login.Login
import com.studiomk.matool.presentation.store_view.auth.confirm_sign_in.ConfirmSignIn
import com.studiomk.matool.presentation.store_view.app.settings.Settings

object Home : ReducerOf<Home.State, Home.Action>, KoinComponent {

    private val apiRepository: ApiRepository by inject()
    private val authService: AuthService by inject()
    private val localStore: LocalStore by inject()

    sealed class Destination {
//        @ChildFeature(PublicMap::class)
//        object Route : Destination()
        @ChildFeature(com.studiomk.matool.presentation.store_view.pub.info.Info::class)
        object Info : Destination()
        @ChildFeature(com.studiomk.matool.presentation.store_view.auth.login.Login::class)
        object Login : Destination()
        @ChildFeature(AdminDistrictTop::class)
        object AdminDistrict : Destination()
        @ChildFeature(AdminRegionTop::class)
        object AdminRegion : Destination()
        @ChildFeature(com.studiomk.matool.presentation.store_view.app.settings.Settings::class)
        object Settings : Destination()
    }

    data class State(
        var userRole: UserRole = UserRole.Guest,
        var isAuthLoading: Boolean = true,
        var isDestinationLoading: Boolean = false,
        @ChildState var destination: DestinationState? = null,
        @ChildState var alert: NoticeAlert.State? = null
    ) {
        val isLoading: Boolean
            get() = isDestinationLoading
    }

    sealed class Action {
        object OnAppear : Action()
        object RouteTapped : Action()
        object InfoTapped : Action()
        object AdminTapped : Action()
        object SettingsTapped : Action()
        object DestinationDismissed: Action()
        data class AuthInitializeReceived(val result: Result<UserRole, AuthError>) : Action()
        data class AdminDistrictPrepared(
            val districtResult: Result<PublicDistrict, ApiError>,
            val routesResult: Result<List<RouteSummary>, ApiError>
        ) : Action()
        data class AdminRegionPrepared(
            val regionResult: Result<Region, ApiError>,
            val districtsResult: Result<List<PublicDistrict>, ApiError>
        ) : Action()
        data class SettingsPrepared(
            val regionsResult: Result<List<Region>, ApiError>,
            val regionResult: Result<Region?, ApiError>,
            val districtsResult: Result<List<PublicDistrict>, ApiError>,
            val districtResult: Result<PublicDistrict?, ApiError>
        ) : Action()
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
                is Action.OnAppear -> {
                    state.copy(isAuthLoading = true) to Effect.run { send ->
                        val result = authService.initialize()
                        send(Action.AuthInitializeReceived(result))
                    }
                }
                is Action.AuthInitializeReceived -> {
                    state.copy(
                        userRole = action.result.value ?: UserRole.Guest,
                        isAuthLoading = false
                    ) to Effect.none()
                }
                is Action.RouteTapped -> {
//                    state.copy(destination = DestinationState.Route) to Effect.none()
                    state to Effect.none()
                }
                is Action.InfoTapped -> {
//                    state.destination = DestinationState.Info(Info.State())
                    state to Effect.none()
                }
                is Action.DestinationDismissed -> {
                    state.copy(destination = null) to Effect.none()
                }
                is Action.AdminTapped -> {
                    when (val role = state.userRole) {
                        is UserRole.Region -> {
                            state.copy(isDestinationLoading = true) to adminRegionEffect(role.id)
                        }
                        is UserRole.District -> {
                            state.copy(isDestinationLoading = true) to adminDistrictEffect(role.id)
                        }
                        UserRole.Guest -> {
                            state.copy(destination = DestinationState.Login(Login.State())) to Effect.none()
                        }
                    }
                }
                is Action.SettingsTapped -> {
                    val regionId = localStore.getString(DefaultValues.DEFAULT_REGION)
                    val districtId = localStore.getString(DefaultValues.DEFAULT_DISTRICT)
                    state.copy(isDestinationLoading = true) to settingsEffect(regionId, districtId)
                }
                is Action.AdminDistrictPrepared -> {
                    val (districtResult, routesResult) = action
                    if (districtResult is Result.Success && routesResult is Result.Success) {
                        state.copy(
                            isDestinationLoading = false,
                            destination = DestinationState.AdminDistrict(
                                AdminDistrictTop.State(
                                    district = districtResult.value,
                                    routes = routesResult.value.sorted()
                                )
                            )
                        ) to Effect.none()
                    } else {
                        state.copy(
                            isDestinationLoading = false,
                            alert = NoticeAlert.State.error("情報の取得に失敗しました")
                        ) to Effect.none()
                    }
                }
                is Action.AdminRegionPrepared -> {
                    val (regionResult, districtsResult) = action
                    if (regionResult is Result.Success && districtsResult is Result.Success) {
                        state.copy(
                            isDestinationLoading = false,
                            destination = DestinationState.AdminRegion(
                                AdminRegionTop.State(
                                    region = regionResult.value,
                                    districts = districtsResult.value
                                )
                            )
                        ) to Effect.none()
                    } else {
                        state.copy(
                            isDestinationLoading = false,
                            alert = NoticeAlert.State.error("情報の取得に失敗しました"),
                        ) to Effect.none()
                    }

                }
                is Action.SettingsPrepared -> {
                    val (regionsResult, regionResult, districtsResult, districtResult) = action
                    if (regionsResult is Result.Success &&
                        regionResult is Result.Success &&
                        districtsResult is Result.Success &&
                        districtResult is Result.Success
                    ) {
                        state.copy(
                            isDestinationLoading = false,
                            destination = DestinationState.Settings(
                                Settings.State(
                                    regions = regionsResult.value,
                                    selectedRegion = regionResult.value,
                                    districts = districtsResult.value,
                                    selectedDistrict = districtResult.value
                                )
                            )
                        ) to Effect.none()
                    } else {
                        state.copy(
                            isDestinationLoading = false,
                            alert = NoticeAlert.State.error("情報の取得に失敗しました"),
                        ) to Effect.none()
                    }
                }
                //TODO
                is Action.Destination -> {
                    when(val action = action.action){
                        is DestinationAction.Login->{
                            when(val action = action.action){
                                is Login.Action.HomeTapped->
                                    state.copy(destination = null) to Effect.none()
                                is Login.Action.Received->{
                                    when(val result = action.result){
                                        is SignInResult.Success-> {
                                            when (val userRole = result.userRole) {
                                                is UserRole.Region -> state to Effect.none()
                                                is UserRole.District -> state.copy(
                                                    isDestinationLoading = true
                                                ) to adminDistrictEffect(id = userRole.id)
                                                is UserRole.Guest -> state to Effect.none()
                                            }
                                        }
                                        is SignInResult.NewPasswordRequired-> state to Effect.none()
                                        is SignInResult.Failure-> state to Effect.none()
                                    }
                                }
                                is Login.Action.ConfirmSignIn -> {
                                    when(val action = action.action){
                                        is ConfirmSignIn.Action.Received->{
                                           when(val result = action.result){
                                               is Result.Success ->  {
                                                   when (val userRole = result.value) {
                                                       is UserRole.Region -> state to Effect.none()
                                                       is UserRole.District -> state.copy(
                                                           isDestinationLoading = true
                                                       ) to adminDistrictEffect(id = userRole.id)
                                                       is UserRole.Guest -> state to Effect.none()
                                                   }
                                               }
                                               is Result.Failure -> state to Effect.none()
                                           }
                                        }
                                        else -> state to Effect.none()
                                    }
                                }
                                else -> state to Effect.none()
                            }
                        }
                        is DestinationAction.AdminDistrict -> {
                            when (val action = action.action) {
                                is AdminDistrictTop.Action.HomeTapped -> state.copy(
                                    destination = null
                                ) to Effect.none()
                                is AdminDistrictTop.Action.SignOutReceived ->{
                                    when (val result = action.result) {
                                        is Result.Success -> state.copy(userRole = UserRole.Guest, destination = null) to Effect.none()
                                        is Result.Failure -> state to Effect.none()
                                    }
                                }
                                else -> state to Effect.none()
                            }
                        }
                        is DestinationAction.Settings ->{
                            when (val action = action.action) {
                                is Settings.Action.DismissTapped -> state.copy(
                                    destination = null
                                ) to Effect.none()
                                is Settings.Action.SignOutReceived-> {
                                    when (val result = action.result) {
                                        is Result.Success -> state.copy(userRole = UserRole.Guest) to Effect.none()
                                        is Result.Failure -> state to Effect.none()
                                    }
                                }
                                else -> state to Effect.none()
                            }
                        }
                        else -> state to Effect.none()
                    }
                }
                is Action.Alert -> {
                    when (action.action) {
                        is NoticeAlert.Action.OkTapped -> state.copy(alert = null) to Effect.none()
                    }
                }
            }
        }

    private fun adminDistrictEffect(id: String): Effect<Action> =
        Effect.run { send ->
            val accessToken = authService.getAccessToken()
            val districtResult = apiRepository.getDistrict(id)
            val routesResult = apiRepository.getRoutes(id, accessToken)
            send(Action.AdminDistrictPrepared(districtResult, routesResult))
        }

    private fun adminRegionEffect(id: String): Effect<Action> =
        Effect.run { send ->
            val regionResult = apiRepository.getRegion(id)
            val districtsResult = apiRepository.getDistricts(id)
            send(Action.AdminRegionPrepared(regionResult, districtsResult))
        }

    private fun settingsEffect(regionId: String?, districtId: String?): Effect<Action> =
        Effect.run { send ->
            val regionsResult = apiRepository.getRegions()
            val regionResult = regionId?.let { apiRepository.getRegion(it).map { r -> r } } ?: Result.Success(null)
            val districtsResult = regionId?.let { apiRepository.getDistricts(it) } ?: Result.Success(emptyList())
            val districtResult = districtId?.let { apiRepository.getDistrict(it).map { d -> d } } ?: Result.Success(null)
            send(Action.SettingsPrepared(regionsResult, regionResult, districtsResult, districtResult))
        }
        
}