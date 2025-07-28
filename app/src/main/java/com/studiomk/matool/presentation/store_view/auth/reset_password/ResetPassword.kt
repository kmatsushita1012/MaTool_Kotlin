package com.studiomk.matool.presentation.store_view.auth.reset_password

import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.domain.contracts.auth.AuthError
import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ResetPassword : ReducerOf<ResetPassword.State, ResetPassword.Action>, KoinComponent {

    private val authService: AuthService by inject()

    data class State(
        val username: String = "",
        val newPassword1: String = "",
        val newPassword2: String = "",
        val code: String = "",
        val isLoading: Boolean = false,
        val step: Step = Step.EnterUsername,
        @ChildState val alert: NoticeAlert.State? = null
    ) {
        enum class Step {
            EnterUsername,
            EnterCode
        }
    }

    sealed class Action {
        data class SetUsername(val value: String) : Action()
        data class SetNewPassword1(val value: String) : Action()
        data class SetNewPassword2(val value: String) : Action()
        data class SetCode(val value: String) : Action()
        object OkTapped : Action()
        object CodeOkTapped : Action()
        object ResendTapped : Action()
        object DismissTapped : Action()
        data class ResetReceived(val result: Result<Unit, AuthError>) : Action()
        data class ConfirmResetReceived(val result: Result<Unit, AuthError>) : Action()
        @ChildAction data class Alert(val action: NoticeAlert.Action) : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        Reduce { state, action ->
            when (action) {
                is Action.SetUsername -> state.copy(username = action.value) to Effect.none()
                is Action.SetNewPassword1 -> state.copy(newPassword1 = action.value) to Effect.none()
                is Action.SetNewPassword2 -> state.copy(newPassword2 = action.value) to Effect.none()
                is Action.SetCode -> state.copy(code = action.value) to Effect.none()
                is Action.OkTapped, is Action.ResendTapped ->
                    state.copy(isLoading = true) to Effect.run { send ->
                        val result = authService.resetPassword(state.username)
                        send(Action.ResetReceived(result))
                    }
                is Action.CodeOkTapped -> {
                    if (state.newPassword1 != state.newPassword2) {
                        state.copy(alert = NoticeAlert.State.error("パスワード（確認用）が一致しません")) to Effect.none()
                    } else if (!authService.isValidPassword(state.newPassword1)) {
                        state.copy(alert = NoticeAlert.State.error("パスワードが条件を満たしていません。次の条件を満たしてください。\n 8文字以上 \n 少なくとも 1 つの数字を含む \n 少なくとも 1 つの大文字を含む \n 少なくとも 1 つの小文字を含む")) to Effect.none()
                    } else {
                        state.copy(isLoading = true) to Effect.run { send ->
                            val result = authService.confirmResetPassword(state.username, state.newPassword1, state.code)
                            send(Action.ConfirmResetReceived(result))
                        }
                    }
                }
                is Action.DismissTapped -> state to Effect.none() // 画面遷移用のEffectが必要ならここで追加
                is Action.ResetReceived ->
                    when (val result = action.result) {
                        is Result.Success -> state.copy(isLoading = false, step = State.Step.EnterCode) to Effect.none()
                        is Result.Failure -> state.copy(isLoading = false, alert = NoticeAlert.State.error("リセットに失敗しました。${result.error.localizedDescription}")) to Effect.none()
                    }
                is Action.ConfirmResetReceived ->
                    when (val result = action.result) {
                        is Result.Success -> state.copy(isLoading = false) to Effect.none()
                        is Result.Failure -> state.copy(isLoading = false, alert = NoticeAlert.State.error("リセットに失敗しました。${result.error.localizedDescription}")) to Effect.none()
                    }
                is Action.Alert -> state.copy(alert = null) to Effect.none()
            }
        }
}
