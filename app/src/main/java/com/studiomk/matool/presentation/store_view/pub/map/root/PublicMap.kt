package com.studiomk.matool.presentation.store_view.pub.map.root

import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.annotation.ChildFeature
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.reducer.LetScope
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.matool.presentation.store_view.pub.map.location.PublicLocationMap
import com.studiomk.matool.presentation.store_view.pub.map.route.PublicRouteMap


object PublicMap: ReducerOf<PublicMap.State, PublicMap.Action> {

    sealed class Tab {
        class Location(): Tab()
        data class Route(val districtId: String): Tab()
    }

    sealed class Destination {
        @ChildFeature(PublicLocationMap::class)
        data class Location(val regionId: String): Destination()
        @ChildFeature(PublicRouteMap::class)
        data class Route(val districtId: String): Destination()
    }

    data class State(
        val regionId: String,
        val types: List<Tab>,
        @ChildState val destination: DestinationState? = null
    )
    sealed class Action {
        class OnAppear(): Action()
        data class TabSelected(val value: Tab): Action()
        class DismissTapped(): Action()
        @ChildAction data class Destination(val action: DestinationAction): Action()
    }

    override fun body(): ReducerOf<State, Action> = 
        LetScope(
            statePath = destinationKey,
            actionPath = destinationCase,
            reducer = DestinationReducer
        )+
        Reduce { state, action ->
            when(action){
                is Action.OnAppear -> state to Effect.none()
                is Action.TabSelected -> {
                    state.copy(destination = when(action.value){
                        is Tab.Location -> DestinationState.Location(PublicLocationMap.State(state.regionId))
                        is Tab.Route -> DestinationState.Route(PublicRouteMap.State(action.value.districtId))
                    }) to Effect.none()
                }
                is Action.DismissTapped -> state to Effect.none()
                is Action.Destination -> {
                    state to Effect.none()
                }
            }
        }
}