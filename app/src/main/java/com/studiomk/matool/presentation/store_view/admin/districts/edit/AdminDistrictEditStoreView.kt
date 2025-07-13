package com.studiomk.matool.presentation.store_view.admin.districts.edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.studiomk.ktca.core.util.Binding
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.matool.domain.entities.districts.Visibility
import com.studiomk.matool.domain.entities.districts.label
import com.studiomk.matool.presentation.store_view.admin.districts.edit.area.AdminAreaEditView
import com.studiomk.matool.presentation.store_view.admin.districts.edit.base.AdminBaseEditView
import com.studiomk.matool.presentation.store_view.admin.districts.edit.performance.AdminPerformanceEditView
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import com.studiomk.matool.presentation.view.input.CupertinoTextEditor
import com.studiomk.matool.presentation.view.input.CupertinoTextField
import com.studiomk.matool.presentation.view.input.ListItemButton
import com.studiomk.matool.presentation.view.input.PickerMenu
import com.studiomk.matool.presentation.view.items.EditableListItemView
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarTrailingButton
import com.studiomk.matool.presentation.view.others.CupertinoForm
import com.studiomk.matool.presentation.view.others.LoadingOverlay
import com.studiomk.ktca.ui.FullScreen
import com.studiomk.matool.presentation.view.items.NavigationItem

import io.github.alexzhirkevich.cupertino.CupertinoHorizontalDivider
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.Mappin
import io.github.alexzhirkevich.cupertino.icons.outlined.PlusCircle
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminDistrictEditView(store: StoreOf<AdminDistrictEdit.State, AdminDistrictEdit.Action>) {
    val state by store.state.collectAsState()

    CupertinoNavigationView(
        toolBar = {
            CupertinoToolBar(
                leading = {
                    CupertinoToolbarLeadingButton(
                        onClick = { store.send(AdminDistrictEdit.Action.CancelTapped) },
                        text = "キャンセル"
                    )
                },
                center = {
                    Text("地区情報", fontWeight = FontWeight.Bold)
                },
                trailing = {
                    CupertinoToolbarTrailingButton(
                        onClick = { store.send(AdminDistrictEdit.Action.SaveTapped) },
                        text = "保存"
                    )
                },

            )
        }
    ) {
        CupertinoForm{
            // 町名
            CupertinoSection(title = { Text("町名") }) {
                CupertinoTextField(
                    value = state.item.name,
                    onValueChange = { store.send(AdminDistrictEdit.Action.NameChanged(it) ) }, // ここは仮想Actionで
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    placeholder = "町名を入力",
                    textStyle = MaterialTheme.typography.bodyLarge,
                )
            }
            // 紹介文
            CupertinoSection(title = { Text("紹介文") }) {
                CupertinoTextEditor(
                    value = state.item.description ?: "",
                    onValueChange = { store.send(AdminDistrictEdit.Action.DescriptionChanged(it)) }, // ここも仮想Actionで
                    placeholder = "紹介文を入力",
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(vertical = 8.dp),
                )
            }
            // 会所
            CupertinoSection(title = { Text("会所") }) {
                ListItemButton(
                    onClick = { store.send(AdminDistrictEdit.Action.BaseTapped) },
                    icon = CupertinoIcons.Default.Mappin,
                    text = "地図で選択"
                )
            }
            // 町域
            CupertinoSection(title = { Text("町域") }) {
                ListItemButton(
                    onClick = { store.send(AdminDistrictEdit.Action.AreaTapped) },
                    icon = CupertinoIcons.Default.Mappin,
                    text = "地図で選択"
                )
            }
            // ルート公開範囲
            CupertinoSection(title = { Text("ルート") }) {
                ListItem(
                    headlineContent = { Text("公開範囲を選択") },
                    trailingContent = {
                        PickerMenu(
                            selection = Binding(
                                getter = { state.item.visibility },
                                setter = { store.send(AdminDistrictEdit.Action.VisibilityChanged(it)) },
                            ),
                            items = Visibility.entries,
                            itemLabel = { it.label }
                        )
                    }
                )
            }
            // 余興
            CupertinoSection(title = { Text("余興") }) {
                Column {
                    state.item.performances.forEach { item ->
                        NavigationItem (
                            text = item.name,
                            onTap = { store.send(AdminDistrictEdit.Action.PerformanceEditTapped(item)) }
                        )
                        CupertinoHorizontalDivider()
                    }
                    ListItemButton(
                        onClick = { store.send(AdminDistrictEdit.Action.PerformanceAddTapped) },
                        icon = CupertinoIcons.Default.PlusCircle,
                        text = "追加"
                    )
                }
            }
        }
    }
    // 会所編集
    FullScreen(
        item = store.optionalScope(
            statePath = AdminDistrictEdit.destinationKey + AdminDistrictEdit.Destination.Base.key,
            actionPath = AdminDistrictEdit.destinationCase + AdminDistrictEdit.Destination.Base.case
        )
    ) {
        AdminBaseEditView(
            store = it
        )
    }
    FullScreen(
        item = store.optionalScope(
            statePath = AdminDistrictEdit.destinationKey + AdminDistrictEdit.Destination.Area.key,
            actionPath = AdminDistrictEdit.destinationCase + AdminDistrictEdit.Destination.Area.case
        )
    ) {
        AdminAreaEditView(
            store = it
        )
    }
    FullScreen(
        item = store.optionalScope(
            statePath = AdminDistrictEdit.destinationKey + AdminDistrictEdit.Destination.Performance.key,
            actionPath = AdminDistrictEdit.destinationCase + AdminDistrictEdit.Destination.Performance.case
        )
    ) {
        AdminPerformanceEditView(
            store = it
        )
    }

    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = AdminDistrictEdit.alertKey,
            actionPath = AdminDistrictEdit.alertCase
        )
    )
    LoadingOverlay(isLoading = state.isLoading)
    BackHandler(enabled = true) {
        store.send(AdminDistrictEdit.Action.CancelTapped)
    }
}