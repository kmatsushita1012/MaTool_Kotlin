package com.studiomk.matool.presentation.store_view.admin.regions.edit.span

import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.matool.domain.entities.shared.SimpleDate
import com.studiomk.matool.domain.entities.shared.SimpleTime
import com.studiomk.matool.domain.entities.shared.Span
import com.studiomk.matool.domain.entities.shared.fromDate
import com.studiomk.matool.presentation.store_view.shared.confirm_alert.ConfirmAlert
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlert
import java.time.LocalDateTime
import java.util.*

object AdminSpanEdit : ReducerOf<AdminSpanEdit.State, AdminSpanEdit.Action> {

    enum class Mode { Create, Edit }

    data class State(
        val mode: Mode,
        val id: String,
        var date: SimpleDate,
        var start: SimpleTime,
        var end: SimpleTime,
        @ChildState var alert: ConfirmAlert.State? = null
    ) {
        val span: Span
            get() = Span(
                id = id,
                start = LocalDateTime.of(
                    date.year,
                    date.month,
                    date.day,
                    start.hour,
                    start.minute,
                ),
                end = LocalDateTime.of(
                    date.year,
                    date.month,
                    date.day,
                    end.hour,
                    end.minute,
                )
            )

        constructor(span: Span) : this(
            mode = Mode.Edit,
            id = span.id,
            date = SimpleDate(year = span.start.year, month = span.start.monthValue, day = span.start.dayOfMonth),
            start = SimpleTime(hour = span.start.hour, minute = span.start.minute),
            end = SimpleTime(hour = span.end.hour, minute = span.end.minute)
        )

        constructor() : this(
            mode = Mode.Create,
            id = UUID.randomUUID().toString(),
            date = SimpleDate.fromDate(LocalDateTime.now()),
            start = SimpleTime(hour = 12, minute = 0),
            end = SimpleTime(hour = 12, minute = 0),
        )
    }

    sealed class Action {
        object DoneTapped : Action()
        object CancelTapped : Action()
        object DeleteTapped : Action()
        data class DateChanged(val value: SimpleDate) : Action()
        data class StartChanged(val value: SimpleTime) : Action()
        data class EndChanged(val value: SimpleTime) : Action()
        @ChildAction data class Alert(val action: ConfirmAlert.Action) : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        Reduce { state, action ->
            when (action) {
                is Action.DoneTapped -> state to Effect.none()
                is Action.CancelTapped -> state to Effect.none()
                is Action.DeleteTapped -> {
                    if (state.mode == Mode.Create) {
                        state to Effect.none()
                    } else {
                        state.copy(
                            alert = ConfirmAlert.State.delete(
                                "このデータを削除してもよろしいですか。元の画面で保存を選択するとこのデータは削除され、操作を取り戻すことはできません。"
                            )
                        ) to Effect.none()
                    }
                }
                is Action.DateChanged -> state.copy(date = action.value) to Effect.none()
                is Action.StartChanged -> state.copy(start = action.value) to Effect.none()
                is Action.EndChanged -> state.copy(end = action.value) to Effect.none()
                is Action.Alert -> {
                    when (action.action) {
                        is ConfirmAlert.Action.OkTapped -> state.copy(alert = null) to Effect.none()
                        is ConfirmAlert.Action.CancelTapped -> state.copy(alert = null) to Effect.none()
                    }
                }
            }
        }
}