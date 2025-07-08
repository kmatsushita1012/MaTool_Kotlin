package com.studiomk.matool.presentation.store_view.admin.districts.edit.performance

import com.studiomk.ktca.core.annotation.ChildState
import com.studiomk.ktca.core.annotation.ChildAction
import com.studiomk.matool.domain.entities.districts.Performance
import com.studiomk.ktca.core.reducer.Reduce
import com.studiomk.ktca.core.reducer.ReducerOf
import com.studiomk.ktca.core.effect.Effect
import com.studiomk.ktca.core.reducer.LetScope
import com.studiomk.matool.presentation.store_view.shared.confirm_alert.ConfirmAlert
import java.util.UUID

object AdminPerformanceEdit : ReducerOf<AdminPerformanceEdit.State, AdminPerformanceEdit.Action> {

    data class State(
        var item: Performance = Performance(id = UUID.randomUUID().toString()),
        @ChildState val deleteAlert: ConfirmAlert.State? = null
    )

    sealed class Action {
        data class NameChanged(val name: String) : Action()
        data class PerformerChanged(val performer: String) : Action()
        data class DescriptionChanged(val description: String) : Action()
        object DeleteTapped : Action()
        object DoneTapped : Action()
        object CancelTapped : Action()
        @ChildAction data class DeleteAlert(val action: ConfirmAlert.Action) : Action()
    }

    override fun body(): ReducerOf<State, Action> =
        LetScope(
            statePath = deleteAlertKey,
            actionPath = deleteAlertCase,
            reducer = ConfirmAlert
        ) +
        Reduce { state, action ->
            when (action) {
                is Action.NameChanged -> {
                    state.copy(item = state.item.copy(name = action.name)) to Effect.none()
                }
                is Action.PerformerChanged -> {
                    state.copy(item = state.item.copy(performer = action.performer)) to Effect.none()
                }
                is Action.DescriptionChanged -> {
                    state.copy(item = state.item.copy(description = action.description)) to Effect.none()
                }
                is Action.DeleteTapped -> {
                    state.copy(
                        deleteAlert = ConfirmAlert.State.delete(message = "このデータを削除してもよろしいですか。元の画面で保存を選択するとこのデータは削除され、操作を取り戻すことはできません。")
                    ) to Effect.none()
                }
                is Action.DoneTapped -> state to Effect.none()
                is Action.CancelTapped -> state to Effect.none()
                is Action.DeleteAlert -> {
                    when (val action = action.action) {
                        is ConfirmAlert.Action.OkTapped -> state.copy(deleteAlert = null) to Effect.none()
                        is ConfirmAlert.Action.CancelTapped -> state.copy(deleteAlert = null) to Effect.none()
                    }
                }
            }
        }
}