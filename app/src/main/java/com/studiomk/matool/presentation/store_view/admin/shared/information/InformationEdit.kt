package com.studiomk.matool.presentation.store_view.admin.shared.information

import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.matool.domain.entities.shared.Information

object InformationEdit : ReducerOf<InformationEdit.State, InformationEdit.Action> {

    data class State(
        val title: String,
        var item: Information
    )

    sealed class Action {
        data class TitleChanged(val value: String) : Action()
        data class NameChanged(val value: String) : Action()
        object CancelTapped : Action()
        object DoneTapped : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        Reduce { state, action ->
            when (action) {
                is Action.TitleChanged -> state.copy(title = action.value) to Effect.none()
                is Action.NameChanged -> state.copy(item = state.item.copy(name = action.value)) to Effect.none()
                is Action.CancelTapped -> state to Effect.none()
                is Action.DoneTapped -> state to Effect.none()
            }
        }
}