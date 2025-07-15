package com.studiomk.matool.presentation.store_view.admin.districts.route.map.point

import com.studiomk.matool.domain.entities.routes.Point
import com.studiomk.matool.domain.entities.shared.SimpleTime
import com.studiomk.matool.domain.entities.shared.fromLocalTime
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.LetScope
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import java.time.LocalTime

import com.studiomk.matool.domain.entities.shared.Information


object AdminPointEdit : ReducerOf<AdminPointEdit.State, AdminPointEdit.Action> {

    data class State(
        val item: Point,
        val events: List<Information>,
        val showPopover: Boolean = false,
        @ChildState var alert: NoticeAlert.State? = null
    ) {
        val hasTime = item.time != null
    }

    sealed class Action {
        data class TitleChanged(val value: String) : Action()
        data class DescriptionChanged(val value: String?) : Action()
        data class TimeSwitchChanged(val value: Boolean) : Action()
        data class TimeChanged(val value: SimpleTime?) : Action()
        data class ExportChanged(val value: Boolean): Action()
        object MenuTapped : Action()
        object MenuDismissed : Action()
        data class ShouldExportChanged(val value: Boolean) : Action()
        object DoneTapped : Action()
        object CancelTapped : Action()
        object MoveTapped : Action()
        object InsertTapped : Action()
        object DeleteTapped : Action()
        object TitleFieldFocused : Action()
        data class TitleOptionSelected(val option: Information) : Action()
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
                is Action.TitleChanged -> state.copy(item = state.item.copy(title = action.value)) to Effect.none()
                is Action.DescriptionChanged -> state.copy(item = state.item.copy(description = action.value)) to Effect.none()
                is Action.TimeSwitchChanged -> state.copy(
                    item = state.item.copy(
                        time =
                            if (action.value) SimpleTime.fromLocalTime(LocalTime.now())
                            else null
                    )
                ) to Effect.none()
                is Action.TimeChanged -> state.copy(item = state.item.copy(time = action.value)) to Effect.none()
                is Action.ExportChanged -> state.copy(item = state.item.copy(shouldExport = action.value)) to Effect.none()
                is Action.MenuTapped -> state.copy(showPopover = true) to Effect.none()
                is Action.MenuDismissed -> state.copy(showPopover = false) to Effect.none()
                is Action.ShouldExportChanged -> state.copy(item = state.item.copy(shouldExport = action.value)) to Effect.none()
                is Action.DoneTapped -> state to Effect.none()
                is Action.CancelTapped -> state to Effect.none()
                is Action.MoveTapped -> state to Effect.none()
                is Action.InsertTapped -> state to Effect.none()
                is Action.DeleteTapped -> state to Effect.none()
                is Action.TitleFieldFocused -> state.copy(showPopover = true) to Effect.none()
                is Action.TitleOptionSelected -> state.copy(
                    item = state.item.copy(
                        title = action.option.name,
                        description = action.option.description
                    ),
                    showPopover = false
                ) to Effect.none()
                is Action.Alert -> {
                    when (action.action) {
                        is NoticeAlert.Action.OkTapped -> state.copy(alert = null) to Effect.none()
                    }
                }
            }
        } + Reduce{ state, action ->
            Log.d("AdminPointEdit", "Action: $action, Menu: ${state.showPopover} Events ${state.events.size}")
            state to Effect.none()
        }
}