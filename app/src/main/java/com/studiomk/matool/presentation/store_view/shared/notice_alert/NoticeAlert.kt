package com.studiomk.matool.presentation.store_view.shared.notice_alert

import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf


object NoticeAlert : ReducerOf<NoticeAlert.State, NoticeAlert.Action> {

    data class State(
        val message: String,
        val title: String,
    ) {
        companion object {
            fun confirm(
                message: String,
                title: String = "完了",
            ) = State(message = message, title = title)

            fun error(
                message: String,
                title: String = "エラー",
            ) = State(message = message, title = title)
        }
    }

    sealed class Action {
        object OkTapped : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        Reduce { state, action ->
            when (action) {
                is Action.OkTapped -> {
                    state to Effect.none()
                }
            }
        }
}


