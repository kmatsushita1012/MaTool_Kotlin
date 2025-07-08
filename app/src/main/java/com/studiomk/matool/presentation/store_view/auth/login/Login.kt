package com.studiomk.matool.presentation.store_view.auth.login


import SignInResult
import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.domain.entities.shared.*
import com.studiomk.matool.presentation.store_view.auth.confirm_sign_in.ConfirmSignIn
import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.LetScope
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.matool.domain.contracts.local_store.LocalStore
import com.studiomk.matool.di.DefaultValues
import org.koin.core.component.*

object Login: ReducerOf<Login.State,Login.Action>, KoinComponent {

    private val authService: AuthService by inject()
    private val localStore: LocalStore by inject()
    private val defaultValues: DefaultValues by inject()

    data class State(
        val id: String = "",
        val password: String = "",
        val errorMessage: String? = null,
        val isLoading: Boolean = false,
        @ChildState val confirmSignIn: ConfirmSignIn.State? = null
    )

    sealed class Action {
        data class SetId(val value: String) : Action()
        data class SetPassword(val value: String) : Action()
        object SignInTapped : Action()
        data class Received(val result: SignInResult) : Action()
        object HomeTapped : Action()
        @ChildAction data class ConfirmSignIn(val action: ConfirmSignIn.Action) : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        LetScope(
            statePath = confirmSignInKey,
            actionPath = confirmSignInCase,
            reducer = ConfirmSignIn
        )+
        Reduce<State, Action> { state, action ->
            when (action) {
                is Action.SetId -> state.copy(id = action.value) to Effect.none()
                is Action.SetPassword -> state.copy(password = action.value) to Effect.none()
                is Action.SignInTapped ->
                    state.copy(isLoading = true) to Effect.run { send ->
                    val result = authService.signIn(state.id, state.password)
                    send(Action.Received(result))
                }
                is Action.Received ->
                    when (val result = action.result) {
                        is SignInResult.Success -> {
                            when(result.userRole){
                                is UserRole.Region -> {
                                    localStore.setString(result.userRole.id, defaultValues.DEFAULT_LOGIN_ID)
                                    localStore.setString(result.userRole.id, defaultValues.DEFAULT_REGION)
                                    localStore.setString(null, defaultValues.DEFAULT_DISTRICT)
                                }
                                is UserRole.District -> {
                                    localStore.setString(result.userRole.id, defaultValues.DEFAULT_LOGIN_ID)
                                    localStore.setString(result.userRole.id, defaultValues.DEFAULT_DISTRICT)
                                }
                                is UserRole.Guest -> {}
                            }
                            state.copy(errorMessage = null) to Effect.none()
                        }
                        is SignInResult.NewPasswordRequired -> {
                            state.copy(confirmSignIn = ConfirmSignIn.State()) to Effect.none()
                        }
                        is SignInResult.Failure -> {
                            state.copy(errorMessage = action.result.error.localizedDescription) to Effect.none()
                        }
                    }
                is Action.HomeTapped -> state to Effect.none()
                is Action.ConfirmSignIn -> {
                    when (val action = action.action) {
                        is ConfirmSignIn.Action.Received ->
                            when (action.result) {
                                is Result.Success -> state to Effect.none()
                                is Result.Failure -> state to Effect.none()
                            }
                        is ConfirmSignIn.Action.DismissTapped -> state.copy(confirmSignIn = null) to Effect.none()
                        else -> state to Effect.none()
                    }
                }
            }
        }
}