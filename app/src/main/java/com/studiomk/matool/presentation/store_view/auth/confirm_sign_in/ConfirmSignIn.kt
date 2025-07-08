package com.studiomk.matool.presentation.store_view.auth.confirm_sign_in

import android.util.Log
import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.domain.contracts.auth.AuthError
import com.studiomk.matool.domain.entities.shared.*
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.reducer.LetScope
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.matool.di.DefaultValues
import com.studiomk.matool.domain.contracts.local_store.LocalStore
import org.koin.core.component.*
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert

object ConfirmSignIn: ReducerOf<ConfirmSignIn.State, ConfirmSignIn.Action>, KoinComponent {

    private val authService: AuthService by inject()
    private val localStore: LocalStore by inject()
    private val defaultValues: DefaultValues by inject()

    data class State(
        val password1: String = "",
        val password2: String = "",
        @ChildState var alert: NoticeAlert.State? = null
    )

    sealed class Action {
        data class SetPassword1(val value: String) : Action()
        data class SetPassword2(val value: String) : Action()
        object SubmitTapped : Action()
        object DismissTapped : Action()
        data class Received(val result: Result<UserRole, AuthError>) : Action()
        @ChildAction data class Alert(val action: NoticeAlert.Action) : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        LetScope(
            statePath = alertKey,
            actionPath = alertCase,
            reducer = NoticeAlert
        ) +
        Reduce<State, Action>{ state, action ->
            when (action) {
                is Action.SetPassword1 -> state.copy(password1 = action.value) to Effect.none()
                is Action.SetPassword2 -> state.copy(password2 = action.value) to Effect.none()
                is Action.SubmitTapped -> {
                    if (state.password1 != state.password2) {
                        state.copy(alert = NoticeAlert.State.error("パスワードが一致しません。")) to Effect.none()
                    } else if (!isValidPassword(state.password1)) {
                        state.copy(
                            alert = NoticeAlert.State.error(
                                "パスワードが条件を満たしていません。次の条件を満たしてください。\n 8文字以上 \n 少なくとも 1 つの数字を含む \n 少なくとも 1 つの大文字を含む \n 少なくとも 1 つの小文字を含む"
                            )
                        ) to Effect.none()
                    } else {
                        state to Effect.run { send ->
                            val result = authService.confirmSignIn(state.password1)
                            send(Action.Received(result))
                        }
                    }
                }
                is Action.DismissTapped -> state to Effect.none()
                is Action.Received ->
                    when (val result = action.result) {
                        is Result.Success ->{
                            when(val userRole = result.value){
                                is UserRole.Region -> {
                                    localStore.setString(userRole.id, defaultValues.DEFAULT_LOGIN_ID)
                                    localStore.setString(userRole.id, defaultValues.DEFAULT_REGION)
                                    localStore.setString(null, defaultValues.DEFAULT_DISTRICT)
                                }
                                is UserRole.District -> {
                                    localStore.setString(userRole.id, defaultValues.DEFAULT_LOGIN_ID)
                                    localStore.setString(userRole.id, defaultValues.DEFAULT_DISTRICT)
                                }
                                is UserRole.Guest -> {}
                            }
                            state to Effect.none()
                        }
                        is Result.Failure -> state.copy(
                            alert = NoticeAlert.State.error("認証に失敗しました。 ${action.result.error.localizedDescription}")
                        ) to Effect.none()
                    }
                is Action.Alert -> {
                    when (val action = action.action) {
                        is NoticeAlert.Action.OkTapped -> state.copy(alert = null) to Effect.none()
                    }
                }
            }
        } + Reduce { state,action ->
            Log.d("ConfirmSignIn", "action: $action")
            state to Effect.none()
        }

    private fun isValidPassword(password: String): Boolean {
        val lengthRule = password.length >= 8
        val hasNumber = password.any { it.isDigit() }
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        return lengthRule && hasNumber && hasUppercase && hasLowercase
    }
}