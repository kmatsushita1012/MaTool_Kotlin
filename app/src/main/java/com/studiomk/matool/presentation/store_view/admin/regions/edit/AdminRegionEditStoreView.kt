package com.studiomk.matool.presentation.store_view.admin.regions.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.studiomk.ktca.core.util.Binding
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.ktca.ui.FullScreen
import com.studiomk.matool.domain.entities.shared.text
import com.studiomk.matool.presentation.store_view.admin.regions.edit.span.AdminSpanEditStoreView
import com.studiomk.matool.presentation.store_view.admin.shared.information.InformationEditStoreView
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import com.studiomk.matool.presentation.view.input.CupertinoTextEditor
import com.studiomk.matool.presentation.view.input.CupertinoTextField
import com.studiomk.matool.presentation.view.input.ListItemButton
import com.studiomk.matool.presentation.view.items.EditableListItemView
import com.studiomk.matool.presentation.view.items.NavigationItem
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarTrailingButton
import com.studiomk.matool.presentation.view.others.CupertinoForm
import com.studiomk.matool.presentation.view.others.LoadingOverlay
import io.github.alexzhirkevich.cupertino.CupertinoHorizontalDivider
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.PlusCircle
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminRegionEditStoreView(store: StoreOf<AdminRegionEdit.State, AdminRegionEdit.Action>) {
    val state by store.state.collectAsState()

    CupertinoNavigationView(
        toolBar = {
            CupertinoToolBar(
                leading = {
                    CupertinoToolbarLeadingButton(
                        onClick = { store.send(AdminRegionEdit.Action.CancelTapped) },
                        text = "キャンセル"
                    )
                },
                center = {
                    Text("祭典情報", fontWeight = FontWeight.Bold)
                },
                trailing = {
                    CupertinoToolbarTrailingButton(
                        onClick = { store.send(AdminRegionEdit.Action.SaveTapped) },
                        text = "保存"
                    )
                }
            )
        }
    ) {
        CupertinoForm {
            // 説明
            CupertinoSection(title = { Text("説明") }) {
                CupertinoTextEditor(
                    value = state.item.description ?: "",
                    onValueChange = { store.send(AdminRegionEdit.Action.DescriptionChanged(value = it)) },
                    placeholder = "説明を入力",
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(vertical = 8.dp)
                )
            }
            // 都道府県
            CupertinoSection(title = { Text("都道府県") }) {
                CupertinoTextField(
                    value = state.item.prefecture,
                    onValueChange = { store.send(AdminRegionEdit.Action.PrefectureChanged(value = it)) },
                    placeholder = "都道府県を入力",
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
            // 市区町村
            CupertinoSection(title = { Text("市区町村") }) {
                CupertinoTextField(
                    value = state.item.city,
                    onValueChange = { store.send(AdminRegionEdit.Action.CityChanged(value = it)) },
                    placeholder = "市区町村を入力",
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
            // 開催日程
            CupertinoSection(title = { Text("開催日程") }) {
                state.item.spans.forEach { span ->
                    NavigationItem(
                        text = span.text(year = false),
                        onTap = { store.send(AdminRegionEdit.Action.OnSpanEdit(span)) }
                    )
                    CupertinoHorizontalDivider()
                }
                ListItemButton(
                    onClick = { store.send(AdminRegionEdit.Action.OnSpanAdd) },
                    icon = CupertinoIcons.Default.PlusCircle,
                    text = "追加"
                )

            }
            // 経由地
            CupertinoSection(title = { Text("経由地") }) {
                state.item.milestones.forEach { milestone ->
                    NavigationItem(
                        text = milestone.name,
                        onTap = { store.send(AdminRegionEdit.Action.OnMilestoneEdit(milestone)) }
                    )
                    CupertinoHorizontalDivider()
                }
                ListItemButton(
                    onClick = { store.send(AdminRegionEdit.Action.OnMilestoneAdd) },
                    icon = CupertinoIcons.Default.PlusCircle,
                    text = "追加"
                )

            }
        }
    }

    // スパン編集
    FullScreen(
        item = store.optionalScope(
            statePath = AdminRegionEdit.destinationKey + AdminRegionEdit.Destination.Span.key,
            actionPath = AdminRegionEdit.destinationCase + AdminRegionEdit.Destination.Span.case
        ),
        onDismiss = { store.send(AdminRegionEdit.Action.DestinationDismissed) }
    ) {
        AdminSpanEditStoreView(store = it)
    }
    // 経由地編集
    FullScreen(
        item = store.optionalScope(
            statePath = AdminRegionEdit.destinationKey + AdminRegionEdit.Destination.Milestone.key,
            actionPath = AdminRegionEdit.destinationCase + AdminRegionEdit.Destination.Milestone.case
        ),
        onDismiss = { store.send(AdminRegionEdit.Action.DestinationDismissed) }
    ) {
        InformationEditStoreView(store = it)
    }

    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = AdminRegionEdit.alertKey,
            actionPath = AdminRegionEdit.alertCase
        )
    )
    LoadingOverlay(isLoading = false)
}