package com.studiomk.matool.presentation.store_view.app.settings

import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.domain.contracts.api.ApiError
import com.studiomk.matool.domain.contracts.api.ApiRepository
import com.studiomk.matool.domain.contracts.auth.AuthError
import com.studiomk.matool.domain.contracts.local_store.LocalStore
import com.studiomk.matool.di.DefaultValues
import com.studiomk.matool.domain.entities.districts.PublicDistrict
import com.studiomk.matool.domain.entities.regions.Region
import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.matool.domain.entities.shared.UserRole
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.annotation.ChildAction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue


object Settings: ReducerOf<Settings.State, Settings.Action>, KoinComponent {
    private val authService: AuthService by inject()
    private val apiRepository: ApiRepository by inject()
    private val localStore: LocalStore by inject()

    data class State (
        var regions: List<Region> = emptyList(),
        var selectedRegion: Region? = null,
        var districts: List<PublicDistrict> = emptyList(),
        var selectedDistrict: PublicDistrict? = null,
        var isLoading: Boolean = false,
        @ChildState var alert: NoticeAlert.State? = null
    ){
        var isDismissEnabled: Boolean = selectedRegion != null || regions.isEmpty()
        var userGuide: String = "https://s3.ap-northeast-1.amazonaws.com/studiomk.documents/userguides/matool.pdf"
        var contact: String = "https://forms.gle/ppaAwkqrFPKiC9mr8"
    }
    sealed class Action {
        data class RegionSelected(val region: Region?): Action()
        data class DistrictsPrepared(val result: Result<List<PublicDistrict>, ApiError>): Action()
        data class DistrictSelected(val district: PublicDistrict?): Action()
        object SignOutTapped: Action()
        data class SignOutReceived(val result: Result<UserRole, AuthError>): Action()
        object DismissTapped: Action()
        @ChildAction data class Alert(val action: NoticeAlert.Action): Action()
    }

    override fun body(): ReducerOf<State, Action> =
        Reduce { state,action ->
            when(action){
                is Action.RegionSelected->{
                    if(action.region == null) state to Effect.none()
                    else {
                        localStore.setString(action.region.id, DefaultValues.DEFAULT_REGION)
                        state.copy(
                            selectedRegion = action.region,
                            selectedDistrict = null,
                            isLoading = true
                        ) to Effect.run { send ->
                            val result = apiRepository.getDistricts(action.region.id)
                            send(Action.DistrictsPrepared(result))
                        }
                    }

                }
                is Action.DistrictsPrepared-> state.copy(
                        districts = action.result.value ?: emptyList(),
                        alert = if (action.result.error != null) {
                            NoticeAlert.State.error("情報の取得に失敗しました${action.result.error?.localizedDescription}")
                        } else null,
                        isLoading = false
                    ) to Effect.none()
                is Action.DistrictSelected->{
                    val id = action.district?.id
                    localStore.setString(id, DefaultValues.DEFAULT_DISTRICT)
                    state.copy(
                        selectedDistrict = action.district
                    ) to Effect.none()
                }
                is Action.SignOutTapped->
                    state to Effect.run { send ->
                        val result = authService.signOut()
                        send(Action.SignOutReceived(result))
                    }
                is Action.SignOutReceived-> {
                    when (val result = action.result) {
                        is Result.Success -> {
                            state.copy(
                                alert = NoticeAlert.State.confirm("サインアウトしました")
                            ) to Effect.none()
                        }
                        is Result.Failure -> state.copy(
                            alert = NoticeAlert.State.error("サインアウトに失敗しました")
                        ) to Effect.none()
                    }
                }
                is Action.DismissTapped-> state to Effect.none()
                is Action.Alert-> state.copy(alert = null) to Effect.none()
            }
    }
}