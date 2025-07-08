package com.studiomk.matool.presentation.store_view.admin.regions.top

import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import org.koin.core.component.KoinComponent

object AdminRegionTop: ReducerOf<AdminRegionTop.State, AdminRegionTop.Action>, KoinComponent {

    data class State (
        val text: String = "AdminRegionTop"
    )
    sealed class Action {
        object OnAppear : Action()
    }

    override fun body(): ReducerOf<State, Action> = Reduce { state,action -> state to Effect.none() }
}