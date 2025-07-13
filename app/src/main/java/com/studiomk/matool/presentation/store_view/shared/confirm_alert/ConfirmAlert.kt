package com.studiomk.matool.presentation.store_view.shared.confirm_alert

import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf


object ConfirmAlert : ReducerOf<ConfirmAlert.State, ConfirmAlert.Action> {

    data class State(
        val message: String,
        val title: String,
        val buttonText: String = "削除",
    ) {
        companion object {
            fun delete(
                message: String = "このデータを削除してもよろしいですか？この操作は元に戻せません。",
                title: String = "確認",
            ) = State(message = message, title = title)

        }
    }

    sealed class Action {
        object OkTapped : Action()
        object CancelTapped : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        Reduce { state, action ->
            when (action) {
                is Action.OkTapped -> {
                    state to Effect.none()
                }
                is Action.CancelTapped -> {
                    state to Effect.none()
                }
            }
        }
}


