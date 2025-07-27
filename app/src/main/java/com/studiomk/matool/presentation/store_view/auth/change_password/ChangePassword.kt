package com.studiomk.matool.presentation.store_view.auth.change_password

import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.domain.contracts.auth.AuthError
import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ChangePassword : ReducerOf<ChangePassword.State, ChangePassword.Action>, KoinComponent {

    private val authService: AuthService by inject()

    data class State(
        val current: String = "",
        val new1: String = "",
        val new2: String = "",
        val isLoading: Boolean = false,
        @ChildState var alert: NoticeAlert.State? = null
    )

    sealed class Action {
        data class SetCurrent(val value: String) : Action()
        data class SetNew1(val value: String) : Action()
        data class SetNew2(val value: String) : Action()
        object OkTapped : Action()
        object DismissTapped : Action()
        data class Received(val result: Result<Unit, AuthError>) : Action()
        @ChildAction data class Alert(val action: NoticeAlert.Action) : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        Reduce { state, action ->
            when (action) {
                is Action.SetCurrent -> state.copy(current = action.value) to Effect.none()
                is Action.SetNew1 -> state.copy(new1 = action.value) to Effect.none()
                is Action.SetNew2 -> state.copy(new2 = action.value) to Effect.none()
                is Action.OkTapped -> {
                    if (state.new1 != state.new2) {
                        state.copy(alert = NoticeAlert.State.error("パスワード（確認用）が一致しません")) to Effect.none()
                    } else if (!authService.isValidPassword(state.new1)) {
                        state.copy(alert = NoticeAlert.State.error("パスワードが条件を満たしていません。次の条件を満たしてください。\n 8文字以上 \n 少なくとも 1 つの数字を含む \n 少なくとも 1 つの大文字を含む \n 少なくとも 1 つの小文字を含む")) to Effect.none()
                    } else {
                        state.copy(isLoading = true, alert = null) to Effect.run { send ->
                            val result = authService.changePassword(state.current, state.new1)
                            send(Action.Received(result))
                        }
                    }
                }
                is Action.DismissTapped -> state to Effect.none() // 画面遷移用のEffectが必要ならここで追加
                is Action.Received ->
                    when (val result = action.result) {
                        is Result.Success -> state.copy(isLoading = false) to Effect.none()
                        is Result.Failure -> state.copy(isLoading = false, alert = NoticeAlert.State.error("変更に失敗しました。${result.error.localizedDescription}")) to Effect.none()
                    }
                is Action.Alert -> state.copy(alert = null) to Effect.none()
            }
        }
}
