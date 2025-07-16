package com.studiomk.matool.presentation.store_view.admin.regions.edit.span

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.ktca.core.util.Binding
import com.studiomk.matool.core.binding.localDate
import com.studiomk.matool.presentation.store_view.shared.confirm_alert.ConfirmAlertDialog
import com.studiomk.matool.presentation.view.input.BindableDatePicker
import com.studiomk.matool.presentation.view.input.BindableTimePicker
import com.studiomk.matool.presentation.view.input.ListItemButton
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarTrailingButton
import com.studiomk.matool.presentation.view.others.CupertinoForm
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminSpanEditStoreView(store: StoreOf<AdminSpanEdit.State, AdminSpanEdit.Action>) {
    val state by store.state.collectAsState()

    CupertinoNavigationView(
        toolBar = {
            CupertinoToolBar(
                leading = {
                    CupertinoToolbarLeadingButton(
                        onClick = { store.send(AdminSpanEdit.Action.CancelTapped) },
                        text = "キャンセル"
                    )
                },
                center = {
                    Text("地点編集", fontWeight = FontWeight.Bold)
                },
                trailing = {
                    CupertinoToolbarTrailingButton(
                        onClick = { store.send(AdminSpanEdit.Action.DoneTapped) },
                        text = "完了"
                    )
                }
            )
        }
    ) {
        CupertinoForm {
            // 日付
            CupertinoSection(
                title = { Text("日付") }
            ) {
                BindableDatePicker(
                    selection = Binding(
                        getter = { state.date },
                        setter = { store.send(AdminSpanEdit.Action.DateChanged(value = it)) }
                    ).localDate
                )
            }
            CupertinoSection(
                title = { Text("時刻") }
            ) {
                BindableTimePicker(
                    label = "開始時刻",
                    selection = Binding(
                        getter = { state.start },
                        setter = { store.send(AdminSpanEdit.Action.StartChanged(value = it) )}
                    )
                )
                BindableTimePicker(
                    label = "終了時刻",
                    selection = Binding(
                        getter = { state.end },
                        setter = { store.send(AdminSpanEdit.Action.EndChanged(value = it)) }
                    )
                )
            }
            // 削除ボタン（編集時のみ）
            if (state.mode == AdminSpanEdit.Mode.Edit) {
                CupertinoSection {
                    ListItemButton(
                        onClick = { store.send(AdminSpanEdit.Action.DeleteTapped) },
                        text = "削除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    ConfirmAlertDialog(
        store = store.optionalScope(
            statePath = AdminSpanEdit.alertKey,
            actionPath = AdminSpanEdit.alertCase
        )
    )
}
