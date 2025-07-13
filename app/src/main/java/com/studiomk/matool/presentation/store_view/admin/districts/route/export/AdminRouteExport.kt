package com.studiomk.matool.presentation.store_view.admin.districts.route.export

import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.matool.domain.entities.routes.PublicRoute
import com.studiomk.matool.domain.entities.routes.text
import com.studiomk.matool.domain.entities.routes.Point
import com.studiomk.matool.domain.entities.routes.Segment
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert
import com.studiomk.matool.presentation.utils.CoordinateRegion
import com.studiomk.matool.presentation.utils.makeRegion
import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.reducer.LetScope
import java.util.*

object AdminRouteExport : ReducerOf<AdminRouteExport.State, AdminRouteExport.Action> {

    data class State(
        val route: PublicRoute,
        var region: CoordinateRegion? = makeRegion(coordinates = route.points.map { it.coordinate }),
        @ChildState var alert: NoticeAlert.State? = null
    ) {
        val points: List<Point>
            get() = filterPoints(route)
        val segments: List<Segment>
            get() = route.segments
        val title: String
            get() = route.text(format = "D m/d T")
        val partialPath: String
            get() = "${route.text(format = "D_y-m-d_T")}_part_${Date()}.pdf"
        val wholePath: String
            get() = "${route.text(format = "D_y-m-d_T")}_full.pdf"
    }

    sealed class Action {
        object ExportTapped : Action()
        object DismissTapped : Action()
        data class RegionChanged(val region: CoordinateRegion?) : Action()
        @ChildAction data class Alert(val action: NoticeAlert.Action) : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        LetScope(
            statePath = alertKey,
            actionPath = alertCase,
            reducer = NoticeAlert
        ) +
        Reduce { state, action ->
            when (action) {
                is Action.ExportTapped -> state to Effect.none()
                is Action.DismissTapped -> state to Effect.none()
                is Action.RegionChanged -> state.copy(region = action.region) to Effect.none()
                is Action.Alert -> {
                    when (action.action) {
                        is NoticeAlert.Action.OkTapped -> state.copy(alert = null) to Effect.none()
                    }
                }
            }
        }
    fun filterPoints(route: PublicRoute): List<Point> {
        val newPoints = mutableListOf<Point>()
        val points = route.points
        if (points.firstOrNull()?.shouldExport == false) {
            val first = points.first()
            val tempFirst = first.copy(
                title = "出発",
                time = route.start,
                shouldExport = true
            )
            newPoints.add(tempFirst)
        }
        newPoints.addAll(points.filter { it.shouldExport })
        if (points.size >= 2 && points.lastOrNull()?.shouldExport == false) {
            val last = points.last()
            val tempLast = last.copy(
                title = "到着",
                time = route.goal,
                shouldExport = true
            )
            newPoints.add(tempLast)
        }
        return newPoints
    }
}
