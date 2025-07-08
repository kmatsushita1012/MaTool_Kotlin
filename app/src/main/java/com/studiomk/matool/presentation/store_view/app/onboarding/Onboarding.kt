package com.studiomk.matool.presentation.store_view.app.onboarding


import com.studiomk.matool.domain.entities.districts.PublicDistrict
import com.studiomk.matool.domain.entities.regions.Region
import com.studiomk.matool.domain.contracts.api.*
import com.studiomk.matool.domain.contracts.local_store.LocalStore
import com.studiomk.matool.di.DefaultValues
import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.reducer.LetScope
import org.koin.core.component.*

object Onboarding: ReducerOf<Onboarding.State,Onboarding.Action>, KoinComponent {

    // Koin DI
    private val apiClient: ApiRepository by inject()
    private val localStore: LocalStore by inject()

    data class State(
        val onSet: () -> Unit,
        val regions: List<Region>? = null,
        val selectedRegion: Region? = null,
        val districts: List<PublicDistrict>? = null,
        var showMenu: Boolean = false,
        val isRegionsLoading: Boolean = false,
        val isDistrictsLoading: Boolean = false,
        val regionsErrorMessage: String? = null,
        val districtsErrorMessage: Boolean = false,
        @ChildState var alert: NoticeAlert.State? = null
    ){
        var isLoading = isRegionsLoading || isDistrictsLoading
    }

    sealed class Action {
        data class SetSelectedRegion(val value: Region?) : Action()
        object OnAppear : Action()
        object InternalGuestTapped: Action()
        object ExternalGuestTapped : Action()
        object AdminTapped : Action()
        data class DistrictSelected(val value: PublicDistrict) : Action()
        object MenuDismissTapped : Action()
        data class RegionsReceived(val result: Result<List<Region>, ApiError>) : Action()
        data class DistrictsReceived(val result: Result<List<PublicDistrict>, ApiError>) : Action()
        @ChildAction data class  Alert(val action: NoticeAlert.Action) : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        LetScope(
            statePath = alertKey,
            actionPath = alertCase,
            reducer = NoticeAlert
        ) +
        Reduce<State, Action> { state, action ->
            when (action) {
                is Action.OnAppear -> {
                    state.copy(isRegionsLoading = true) to Effect.run { send ->
                        val result = apiClient.getRegions()
                        send(Action.RegionsReceived(result))
                    }
                }
                is Action.SetSelectedRegion -> {
                    if(action.value==null) state to Effect.none()
                    else {
                        state.copy(
                            selectedRegion = action.value,
                            isDistrictsLoading = true
                        ) to Effect.run { send ->
                            val result = apiClient.getDistricts(action.value.id)
                            send(Action.DistrictsReceived(result))
                        }
                    }
                }
                is Action.InternalGuestTapped -> state.copy(showMenu = true) to Effect.none()
                is Action.ExternalGuestTapped,
                is Action.AdminTapped -> {
                    if (state.selectedRegion == null) state to Effect.none()
                    else {
                        localStore.setString(state.selectedRegion.id, DefaultValues.DEFAULT_REGION)
                        localStore.setBoolean(true, DefaultValues.HAS_LAUNCHED_BEFORE)
                        state.onSet()
                        state to Effect.none()
                    }
                }
                is Action.DistrictSelected -> {
                    if (state.selectedRegion == null) state.copy(showMenu = false) to Effect.none()
                    else {
                        localStore.setString(state.selectedRegion.id, DefaultValues.DEFAULT_REGION)
                        localStore.setString(action.value.id, DefaultValues.DEFAULT_DISTRICT)
                        localStore.setBoolean(true, DefaultValues.HAS_LAUNCHED_BEFORE)
                        state.onSet()
                        state.copy(showMenu = false)  to Effect.none()
                    }
                }
                is Action.MenuDismissTapped -> state.copy(showMenu = false) to Effect.none()
                is Action.RegionsReceived -> when (action.result) {
                    is Result.Success -> state.copy(
                        regions = action.result.value,
                        isRegionsLoading = false
                    ) to Effect.none()
                    is Result.Failure -> state.copy(
                        isRegionsLoading = false,
                        alert = NoticeAlert.State.error("情報の取得に失敗しました。")
                    ) to Effect.none()
                }
                is Action.DistrictsReceived -> when (action.result) {
                    is Result.Success -> state.copy(
                        districts = action.result.value,
                        isDistrictsLoading = false
                    ) to Effect.none()
                    is Result.Failure -> state.copy(
                        isDistrictsLoading = false,
                        alert = NoticeAlert.State.error("情報の取得に失敗しました。")
                    ) to Effect.none()
                }
                is Action.Alert -> {
                    state.copy(alert = null) to Effect.none()
                }
            }
        }

}