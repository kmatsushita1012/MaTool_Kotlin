package com.studiomk.matool.presentation.store_view.admin.districts.edit.base

import android.util.Log
import com.studiomk.matool.domain.entities.shared.Coordinate
import com.studiomk.matool.presentation.utils.CoordinateRegion
import com.studiomk.matool.presentation.utils.makeRegion
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.effect.Effect

object AdminBaseEdit : ReducerOf<AdminBaseEdit.State, AdminBaseEdit.Action> {

    data class State(
        var base: Coordinate? = null,
        var region: CoordinateRegion? = null
    ) {
        constructor(base: Coordinate?, origin: Coordinate) : this(
            base = base,
            region = makeRegion(origin, spanDelta = 0.01)
        )
    }

    sealed class Action {
        data class MapTapped(val coordinate: Coordinate) : Action()
        data class RegionChanged(val region: CoordinateRegion?): Action()
        object DismissTapped : Action()
        object DoneTapped : Action()
        object ClearTapped : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        Reduce { state, action ->
            Log.d("AdminBaseEdit", "action: $action")
            when (action) {
                is Action.MapTapped -> {
                    state.copy(base = action.coordinate) to Effect.none()
                }
                is Action.RegionChanged-> state.copy(region = action.region) to Effect.none()
                is Action.DismissTapped -> state to Effect.none()
                is Action.DoneTapped -> state to Effect.none()
                is Action.ClearTapped -> {
                    state.copy(base = null) to Effect.none()
                }
            }
        }
}