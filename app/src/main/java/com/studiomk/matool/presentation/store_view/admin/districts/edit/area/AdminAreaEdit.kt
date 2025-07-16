package com.studiomk.matool.presentation.store_view.admin.districts.edit.area

import com.studiomk.matool.domain.entities.shared.Coordinate
import com.studiomk.matool.presentation.utils.CoordinateRegion
import com.studiomk.matool.presentation.utils.makeRegion
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.effect.Effect

object AdminAreaEdit : ReducerOf<AdminAreaEdit.State, AdminAreaEdit.Action> {

    data class State(
        var coordinates: List<Coordinate>,
        var region: CoordinateRegion? = null
    ) {
        constructor(coordinates: List<Coordinate>, origin: Coordinate? = null) : this(
            coordinates = coordinates,
            region = makeRegion(origin, spanDelta = 0.01)
        )
    }

    sealed class Action {
        data class MapTapped(val coordinate: Coordinate) : Action()
        data class RegionChanged(val region: CoordinateRegion?): Action()
        object DismissTapped : Action()
        object DoneTapped : Action()
        object UndoTapped : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        Reduce { state, action ->
            when (action) {
                is Action.MapTapped -> {
                    state.copy(coordinates = state.coordinates + action.coordinate) to Effect.none()
                }
                is Action.RegionChanged->{
                    state.copy(region = action.region) to Effect.none()
                }
                is Action.DoneTapped -> state to Effect.none()
                is Action.DismissTapped -> state to Effect.none()
                is Action.UndoTapped -> {
                    if (state.coordinates.isNotEmpty()) {
                        val newCoordinates = state.coordinates.dropLast(1)
                        state.copy(coordinates = newCoordinates) to Effect.none()
                    } else {
                        state to Effect.none()
                    }
                }
            }
        }
}