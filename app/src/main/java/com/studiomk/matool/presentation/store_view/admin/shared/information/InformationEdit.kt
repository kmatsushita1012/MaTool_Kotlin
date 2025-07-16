package com.studiomk.matool.presentation.store_view.admin.shared.information

import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.matool.domain.entities.shared.Information

object InformationEdit : ReducerOf<InformationEdit.State, InformationEdit.Action> {

    enum class Mode { Create, Edit }

    data class State(
        val title: String,
        val mode: Mode,
        var item: Information
    ) {
        val isEdit: Boolean = mode == Mode.Edit
        val shouldShowDelete: Boolean = mode == Mode.Edit
    }

    sealed class Action {
        data class NameChanged(val value: String) : Action()
        data class DescriptionChanged(val value: String?) : Action()
        object CancelTapped : Action()
        object DoneTapped : Action()
        object DeleteTapped : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        Reduce { state, action ->
            when (action) {
                is Action.NameChanged -> state.copy(item = state.item.copy(name = action.value)) to Effect.none()
                is Action.DescriptionChanged -> state.copy(item = state.item.copy(description = action.value)) to Effect.none()
                is Action.CancelTapped -> state to Effect.none()
                is Action.DoneTapped -> state to Effect.none()
                is Action.DeleteTapped -> state to Effect.none()
            }
        }
}