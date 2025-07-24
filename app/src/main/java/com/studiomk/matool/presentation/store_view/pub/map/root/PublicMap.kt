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
        data class Route(val id: String, val name: String): Tab()
    }

    sealed class Destination {
        @ChildFeature(PublicLocationMap::class)
        object Location: Destination()
        @ChildFeature(PublicRouteMap::class)
        object Route: Destination()
    }

    data class State(
        val regionId: String,
        val tabItems: List<Tab>,
        val selectedTab: Tab?,
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
        ) +
        Reduce { state, action ->
            when(action){
                is Action.OnAppear -> state to Effect.none()
                is Action.TabSelected -> {
                    state.copy(destination = when(action.value){
                        is Tab.Location -> DestinationState.Location(PublicLocationMap.State(state.regionId))
                        is Tab.Route -> DestinationState.Route(PublicRouteMap.State(action.value.id))
                    }) to Effect.none()
                }
                is Action.DismissTapped -> state to Effect.none()
                is Action.Destination -> {
                    state to Effect.none()
                }
            }
        }
}

val PublicMap.Tab.text: String
    get() = when (this) {
        is PublicMap.Tab.Location -> "全町"
        is PublicMap.Tab.Route -> name
    }
