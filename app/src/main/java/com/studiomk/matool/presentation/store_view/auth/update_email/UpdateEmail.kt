package com.studiomk.matool.presentation.store_view.auth.update_email

import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.domain.contracts.auth.AuthError
import com.studiomk.matool.domain.contracts.auth.UpdateEmailResult
import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object UpdateEmail : ReducerOf<UpdateEmail.State, UpdateEmail.Action>, KoinComponent {

    private val authService: AuthService by inject()

    data class State(
        val email: String = "",
        val code: String = "",
        val step: Step = Step.EnterEmail,
        val isLoading: Boolean = false,
        val codeDestination: String? = null,
        @ChildState val errorAlert: NoticeAlert.State? = null,
        @ChildState val completeAlert: NoticeAlert.State? = null,
    ) {
        sealed class Step {
            object EnterEmail : Step()
            data class EnterCode(val destination: String) : Step()
        }
    }

    sealed class Action {
        data class SetEmail(val value: String) : Action()
        data class SetCode(val value: String) : Action()
        object OkTapped : Action()
        object ResendTapped : Action()
        object CodeOkTapped : Action()
        object DismissTapped : Action()
        data class UpdateReceived(val result: UpdateEmailResult) : Action()
        data class ConfirmUpdateReceived(val result: Result<Unit, AuthError>) : Action()
        @ChildAction data class ErrorAlert(val action: NoticeAlert.Action) : Action()
        @ChildAction data class CompleteAlert(val action: NoticeAlert.Action) : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        Reduce { state, action ->
            when (action) {
                is Action.SetEmail -> state.copy(email = action.value) to Effect.none()
                is Action.SetCode -> state.copy(code = action.value) to Effect.none()
                is Action.OkTapped, is Action.ResendTapped ->
                    state.copy(isLoading = true) to Effect.run { send ->
                        val result = authService.updateEmail(state.email)
                        send(Action.UpdateReceived(result))
                    }
                is Action.CodeOkTapped ->
                    state.copy(isLoading = true) to Effect.run { send ->
                        val result = authService.confirmUpdateEmail(state.code)
                        send(Action.ConfirmUpdateReceived(result))
                    }
                is Action.UpdateReceived ->
                    when (val result = action.result) {
                        is UpdateEmailResult.Completed ->
                            state.copy(isLoading = false, completeAlert = NoticeAlert.State.confirm("メールアドレスが変更されました")) to Effect.none()
                        is UpdateEmailResult.VerificationRequired ->
                            state.copy(isLoading = false, step = State.Step.EnterCode(result.destination), codeDestination = result.destination) to Effect.none()
                        is UpdateEmailResult.Failure ->
                            state.copy(isLoading = false, errorAlert = NoticeAlert.State.error("変更に失敗しました ${result.error.localizedDescription}")) to Effect.none()
                    }
                is Action.ConfirmUpdateReceived ->
                    when (val result = action.result) {
                        is Result.Success ->
                            state.copy(isLoading = false, completeAlert = NoticeAlert.State.confirm("メールアドレスが変更されました")) to Effect.none()
                        is Result.Failure ->
                            state.copy(isLoading = false, errorAlert = NoticeAlert.State.error("変更に失敗しました ${result.error.localizedDescription}")) to Effect.none()
                    }
                is Action.ErrorAlert ->
                    state.copy(errorAlert = null) to Effect.none()
                is Action.CompleteAlert ->
                    state.copy(completeAlert = null) to Effect.none()
                is Action.DismissTapped ->
                    state to Effect.none() // 画面遷移用のEffectが必要ならここで追加
            }
        }
}