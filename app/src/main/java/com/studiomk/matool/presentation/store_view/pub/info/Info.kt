package com.studiomk.matool.presentation.store_view.pub.info

import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import org.koin.core.component.KoinComponent


object Info: ReducerOf<Info.State, Info.Action>, KoinComponent {
    data class State (
        val text: String = "Info"
    )
    sealed class Action {
        object OnAppear: Action()
    }
    override fun body(): ReducerOf<State, Action> =
        Reduce { state,action -> state to Effect.none() }

}