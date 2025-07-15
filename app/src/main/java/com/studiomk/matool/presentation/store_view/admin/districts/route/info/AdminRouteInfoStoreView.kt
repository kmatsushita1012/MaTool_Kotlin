package com.studiomk.matool.presentation.store_view.admin.districts.route.info


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.studiomk.ktca.core.util.Binding
import com.studiomk.matool.core.binding.localDate
import com.studiomk.matool.presentation.store_view.admin.districts.route.map.AdminRouteMapStoreView
import com.studiomk.matool.presentation.view.input.BindableDatePicker
import com.studiomk.matool.presentation.view.input.BindableTimePicker
import com.studiomk.matool.presentation.view.input.ListItemButton
import com.studiomk.matool.presentation.view.others.CupertinoForm
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarTrailingButton
import com.studiomk.matool.presentation.view.others.LoadingOverlay
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.ktca.ui.FullScreen
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.Mappin
import io.github.alexzhirkevich.cupertino.section.CupertinoSection
import androidx.compose.ui.text.font.FontWeight
import com.studiomk.matool.presentation.store_view.shared.confirm_alert.ConfirmAlertDialog
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import com.studiomk.matool.presentation.view.input.CupertinoTextEditor
import com.studiomk.matool.presentation.view.input.CupertinoTextField

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminRouteInfoStoreView(store: StoreOf<AdminRouteInfo.State, AdminRouteInfo.Action>) {
    val state by store.state.collectAsState()

    CupertinoNavigationView(
        toolBar = {
            CupertinoToolBar(
                leading = {
                    CupertinoToolbarLeadingButton (
                        onClick = { store.send(AdminRouteInfo.Action.CancelTapped) },
                        text = "キャンセル"
                    )
                },
                center = {
                    Text(
                        if (state.mode.isCreate) "新規作成" else "編集",
                        fontWeight = FontWeight.Bold
                    )
                },
                trailing = {
                    CupertinoToolbarTrailingButton(
                        onClick = { store.send(AdminRouteInfo.Action.SaveTapped) },
                        text = "保存"
                    )
                }
            )
        }
    ) {
        CupertinoForm {
            CupertinoSection(title = { Text("日付") }) {
                BindableDatePicker(
                    selection = Binding(
                        getter = { state.route.date },
                        setter = { store.send(AdminRouteInfo.Action.DateChanged(it)) }
                    ).localDate
                )
            }
            // タイトル
            CupertinoSection(title = { Text("タイトル") }) {
                CupertinoTextField(
                    value = state.route.title,
                    onValueChange = { store.send(AdminRouteInfo.Action.TitleChanged(it)) },
                    placeholder = "タイトルを入力（例：土曜午前）",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textStyle = MaterialTheme.typography.titleMedium,
                )
            }
            // 説明
            CupertinoSection(title = { Text("説明") }) {
                CupertinoTextEditor(
                    value = state.route.description ?: "",
                    onValueChange = { store.send(AdminRouteInfo.Action.DescriptionChanged(it)) },
                    placeholder = "説明を入力",
                    textStyle = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(vertical = 8.dp),
                )
            }
            // 経路
            CupertinoSection(title = { Text("経路") }) {
                ListItemButton(
                    onClick = { store.send(AdminRouteInfo.Action.MapTapped) },
                    icon = CupertinoIcons.Default.Mappin,
                    text = "地図で編集"
                )
            }
            CupertinoSection(title = { Text("時刻") }) {
                BindableTimePicker(
                    label = "開始時刻",
                    selection = Binding(
                        getter = { state.route.start },
                        setter = { store.send(AdminRouteInfo.Action.StartChanged(it)) }
                    )
                )
                BindableTimePicker(
                    label = "終了時刻",
                    selection = Binding(
                        getter = { state.route.goal },
                        setter = { store.send(AdminRouteInfo.Action.GoalChanged(it)) }
                    )
                )
            }
            // 削除
            if (!state.mode.isCreate) {
                CupertinoSection {
                    ListItem(
                        modifier = Modifier.clickable { store.send(AdminRouteInfo.Action.DeleteTapped) },
                        headlineContent = {
                            Text("削除", color = Color.Red)
                        }
                    )
                }
            }
        }
    }
    FullScreen (
        item = store.optionalScope(
            statePath = AdminRouteInfo.destinationKey + AdminRouteInfo.Destination.Map.key,
            actionPath = AdminRouteInfo.destinationCase + AdminRouteInfo.Destination.Map.case
        )
    ) {
        AdminRouteMapStoreView(
            store = it
        )
    }

    // アラート
    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = AdminRouteInfo.alertKey + AdminRouteInfo.AlertDestination.Notice.key,
            actionPath = AdminRouteInfo.alertCase + AdminRouteInfo.AlertDestination.Notice.case,
        )
    )
    ConfirmAlertDialog(
        store = store.optionalScope(
            statePath = AdminRouteInfo.alertKey + AdminRouteInfo.AlertDestination.Delete.key,
            actionPath = AdminRouteInfo.alertCase + AdminRouteInfo.AlertDestination.Delete.case,
        )
    )

    LoadingOverlay(isLoading = state.isLoading)

    BackHandler(enabled = true) {
        store.send(AdminRouteInfo.Action.CancelTapped)
    }
}