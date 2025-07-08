package com.studiomk.matool.presentation.store_view.admin.districts.route.map

import android.util.Log
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.matool.core.others.EditManager
import com.studiomk.matool.core.others.add
import com.studiomk.matool.core.others.delete
import com.studiomk.matool.core.others.firstIndex
import com.studiomk.matool.core.others.insert
import com.studiomk.matool.core.others.replace
import com.studiomk.matool.core.others.set
import com.studiomk.matool.domain.entities.routes.*
import com.studiomk.matool.domain.entities.shared.Coordinate
import com.studiomk.matool.domain.entities.shared.Information
import com.studiomk.matool.presentation.utils.SimpleRegion
import com.studiomk.matool.presentation.store_view.admin.districts.route.map.point.AdminPointEdit
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert
import com.studiomk.matool.presentation.utils.makeRegion
import com.studiomk.ktca.core.annotation.ChildFeature
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.reducer.LetScope
import java.util.UUID


object AdminRouteMap : ReducerOf<AdminRouteMap.State, AdminRouteMap.Action> {

    sealed class Destination {
        @ChildFeature(AdminPointEdit::class)
        object Point : Destination()
    }

    sealed class Operation {
        object Add: Operation()
        data class Move(val index: Int): Operation()
        data class Insert(val index: Int): Operation()
    }

    data class State(
        val manager: EditManager<Route>,
        val operation: Operation = Operation.Add,
        val events: List<Information>,
        val region: SimpleRegion?,
        @ChildState val destination: DestinationState? = null,
        @ChildState val alert: NoticeAlert.State? = null
    ) {
        val canUndo: Boolean get() = manager.canUndo
        val canRedo: Boolean get() = manager.canRedo
        val points: List<Point> get() = manager.value.points
        val segments: List<Segment> get() = manager.value.segments
        val route: Route get() = manager.value

        constructor(route: Route, events: List<Information>, origin: Coordinate?): this (
            manager = EditManager(route),
            events = events,
            region =
                if (route.points.isEmpty()){
                    makeRegion(origin = origin, spanDelta = 0.01)
                }else {
                    makeRegion(coordinates = route.points.map { it.coordinate })
                }
        )
    }

    sealed class Action {
        data class MapLongPressed(val coordinate: Coordinate) : Action()
        data class AnnotationTapped(val point: Point) : Action()
        data class PolylineTapped(val segment: Segment) : Action()
        data class RegionChanged(val region: SimpleRegion?) : Action()
        object UndoTapped : Action()
        object RedoTapped : Action()
        object DoneTapped : Action()
        object CancelTapped : Action()
        @ChildAction data class Alert(val action: NoticeAlert.Action) : Action()
        @ChildAction data class Destination(val action: DestinationAction) : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        LetScope(
            statePath = destinationKey,
            actionPath = destinationCase,
            reducer = DestinationReducer
        ) +
        LetScope(
            statePath = alertKey,
            actionPath = alertCase,
            reducer = NoticeAlert
        ) +
        Reduce { state, action ->
            when (action) {
                is Action.MapLongPressed -> {
                    val coordinate = action.coordinate
                    when (state.operation) {
                        is Operation.Add -> {
                            val point = Point(
                                id = UUID.randomUUID().toString(),
                                coordinate = coordinate
                            )
                            val last = state.points.lastOrNull()
                            val manager = state.manager.apply {
                                var points = it.points.add(point)
                                if (last != null) {
                                    val segment = Segment(
                                        id = UUID.randomUUID().toString(),
                                        start = last.coordinate,
                                        end = coordinate
                                    )
                                    val segments = it.segments.add(segment)
                                    it.copy(
                                        points = points,
                                        segments = segments
                                    )
                                } else {
                                    it.copy( points = points )
                                }
                            }
                            state.copy(
                                manager = manager,
                                operation = Operation.Add
                            ) to Effect.none()
                        }
                        is Operation.Move -> {
                            val index = state.operation.index
                            if (index < 0 || index >= state.route.points.size) state to Effect.none()
                            else {
                                val manager = state.manager.apply {
                                    val point = it.points[index].copy(coordinate = coordinate)
                                    val points = it.points.replace(point)
                                    var segments = it.segments
                                    if (index > 0) {
                                        Log.d("AdminRouteMap", "move1 index: $index")
                                        val segment = Segment(
                                            id = UUID.randomUUID().toString(),
                                            start = it.points[index - 1].coordinate,
                                            end = coordinate
                                        )
                                        segments = segments.set(index-1, segment)
                                    }
                                    if (index < it.segments.size) {
                                        Log.d("AdminRouteMap", "move2 index: $index")
                                        val segment = Segment(
                                            id = UUID.randomUUID().toString(),
                                            start = coordinate,
                                            end = it.points[index + 1].coordinate
                                        )
                                        segments = segments.set(index, segment)
                                    }
                                    it.copy(
                                        points = points,
                                        segments = segments
                                    )
                                }
                                state.copy(
                                    manager = manager,
                                    operation = Operation.Add
                                ) to Effect.none()
                            }
                        }
                        is Operation.Insert -> {
                            val index = state.operation.index
                            if (index < 0 || index >= state.route.points.size)
                                state.copy(
                                    operation = Operation.Add
                                )to Effect.none()
                            else {
                                val point = Point (
                                    id= UUID.randomUUID().toString(),
                                    coordinate= coordinate
                                )
                                val manager = state.manager.apply {
                                    var segments = it.segments
                                    if (index > 0) {
                                        val segment = Segment (
                                            id= UUID.randomUUID().toString(),
                                            start= it.points[index-1].coordinate,
                                            end= coordinate
                                        )
                                        Log.d("AdminRouteMap", "insert1 index: $index")
                                        segments = segments.set(index-1, segment)
                                    }
                                    var segment = Segment (
                                        id= UUID.randomUUID().toString(),
                                        start= coordinate,
                                        end= it.points[index].coordinate
                                    )
                                    segments = if (index < segments.size) {
                                        segments.insert(index,segment)
                                    }else{
                                        segments.add(segment)
                                    }
                                    val points = it.points.insert(index, point)
                                    it.copy(
                                        points = points,
                                        segments = segments
                                    )
                                }
                                state.copy(
                                    operation = Operation.Add,
                                    manager = manager
                                )to Effect.none()
                            }
                        }
                    }
                }
                is Action.AnnotationTapped -> {
                    state.copy(
                        destination = DestinationState.Point(
                            AdminPointEdit.State(
                                item = action.point,
                                events = state.events
                            )
                        ),
                        operation = Operation.Add
                    ) to Effect.none()
                }
                is Action.PolylineTapped -> {
                    state to Effect.none()
                }
                is Action.RegionChanged -> state.copy(region = action.region) to Effect.none()
                is Action.UndoTapped -> {
                    state.copy(manager = state.manager.undo(), operation = Operation.Add) to Effect.none()
                }
                is Action.RedoTapped -> {
                    state.copy(manager = state.manager.redo(), operation = Operation.Add) to Effect.none()
                }
                is Action.DoneTapped -> state to Effect.none()
                is Action.CancelTapped -> state to Effect.none()
                is Action.Destination -> {
                    when (val destination = action.action) {
                        is DestinationAction.Point -> {
                            when (val child = destination.action) {
                                is AdminPointEdit.Action.DoneTapped -> {
                                    val pointState = (destinationKey+Destination.Point.key).get(state)
                                    if (pointState != null){
                                        val manager = state.manager.apply {
                                            val points = it.points.replace(pointState.item)
                                            it.copy(points = points)
                                        }
                                        state.copy(manager = manager, destination = null) to Effect.none()
                                    }
                                    else state.copy(destination = null) to Effect.none()
                                }
                                is AdminPointEdit.Action.CancelTapped -> state.copy(destination = null) to Effect.none()
                                is AdminPointEdit.Action.MoveTapped -> {
                                    val pointState = (destinationKey+Destination.Point.key).get(state)
                                    if (pointState != null){
                                        val index = state.points.firstIndex(pointState.item.id)
                                        val manager = state.manager.apply {
                                            val points = it.points.replace(pointState.item)
                                            it.copy(points = points)
                                        }
                                        state.copy(
                                            manager = manager,
                                            destination = null,
                                            operation = Operation.Move(index)
                                        ) to Effect.none()
                                    }
                                    else state to Effect.none()
                                }
                                is AdminPointEdit.Action.InsertTapped -> {
                                    val pointState = (destinationKey+Destination.Point.key).get(state)
                                    if (pointState != null){
                                        val index = state.points.firstIndex(pointState.item.id)
                                        val manager = state.manager.apply {
                                            val points = it.points.replace(pointState.item)
                                            it.copy(points = points)
                                        }
                                        state.copy(
                                            manager = manager,
                                            destination = null,
                                            operation = Operation.Insert(index)
                                        ) to Effect.none()
                                    }
                                    else state to Effect.none()
                                }
                                is AdminPointEdit.Action.DeleteTapped -> {
                                    val pointState = (destinationKey+Destination.Point.key).get(state)
                                    if (pointState != null){
                                        val index = state.points.firstIndex(pointState.item.id)
                                        val manager = state.manager.apply {
                                            var segments = it.segments
                                            if (index < it.segments.size) {
                                                segments = segments.delete(index)
                                            }
                                            if (index > 0 && index < it.points.size-1){
                                                val segment = Segment(
                                                    id = UUID.randomUUID().toString(),
                                                    start = it.points[index-1].coordinate,
                                                    end= it.points[index+1].coordinate
                                                )
                                                segments = segments.set(
                                                    index-1,
                                                    segment
                                                )
                                            }else if(index == it.points.size-1 ){
                                                segments = segments.delete(index-1)
                                            }
                                            val points = it.points.delete(index)
                                            it.copy(
                                                points = points,
                                                segments = segments
                                            )
                                        }
                                        state.copy(
                                            destination = null,
                                            operation = Operation.Add,
                                            manager = manager
                                        ) to Effect.none()
                                    }
                                    else state.copy(
                                        destination = null
                                    ) to Effect.none()
                                }
                                else -> state to Effect.none()
                            }
                        }
                    }
                }
                is Action.Alert -> {
                    when (action.action) {
                        is NoticeAlert.Action.OkTapped -> state.copy(alert = null) to Effect.none()
                    }
                }
            }
        }
}