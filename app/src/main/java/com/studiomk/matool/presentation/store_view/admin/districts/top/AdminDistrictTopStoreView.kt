package com.studiomk.matool.presentation.store_view.admin.districts.top

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import com.studiomk.matool.presentation.store_view.admin.districts.route.info.AdminRouteInfoStoreView
import com.studiomk.matool.domain.entities.routes.text
import com.studiomk.matool.presentation.store_view.admin.districts.edit.AdminDistrictEditView
import com.studiomk.matool.presentation.store_view.admin.districts.location.AdminLocationStoreView
import com.studiomk.matool.presentation.store_view.admin.districts.route.export.AdminRouteExportStoreView
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import com.studiomk.matool.presentation.view.input.ListItemButton
import com.studiomk.matool.presentation.view.items.EditableAndExportableItem
import com.studiomk.matool.presentation.view.items.NavigationItem
import com.studiomk.matool.presentation.view.others.CupertinoForm
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import com.studiomk.matool.presentation.view.others.LoadingOverlay
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.ktca.ui.FullScreen
import io.github.alexzhirkevich.cupertino.CupertinoHorizontalDivider
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.House
import io.github.alexzhirkevich.cupertino.icons.outlined.Mappin
import io.github.alexzhirkevich.cupertino.icons.outlined.PlusCircle
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminDistrictTopStoreView(store: StoreOf<AdminDistrictTop.State, AdminDistrictTop.Action>) {
    val state by store.state.collectAsState()

    CupertinoNavigationView(
        toolBar = {
            CupertinoToolBar(
                modifier = it,
                leading = {
                    CupertinoToolbarLeadingButton(
                        onClick = { store.send(AdminDistrictTop.Action.HomeTapped) },
                        icon = CupertinoIcons.Default.House
                    )
                },
                center = {
                    Text(
                        state.district.name,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) {
        CupertinoForm {
            CupertinoSection {
                NavigationItem(
                    text = "地区情報",
                    icon = Icons.Default.Info,
                    onTap = { store.send(AdminDistrictTop.Action.OnEdit) }
                )
                NavigationItem(
                    text = "位置情報配信",
                    icon = CupertinoIcons.Default.Mappin,
                    onTap = { store.send(AdminDistrictTop.Action.OnLocation) }
                )
            }
            CupertinoSection(title = { Text("行動") }) {
                state.routes.forEach { route ->
                    EditableAndExportableItem(
                        text = route.text("m/d T"),
                        onEdit = { store.send(AdminDistrictTop.Action.OnRouteEdit(route)) },
//                        onExport = { }
                        onExport = { store.send(AdminDistrictTop.Action.OnRouteExport(route)) }
                    )
                    CupertinoHorizontalDivider()
                }
                ListItemButton(
                    onClick = { store.send(AdminDistrictTop.Action.OnRouteAdd) },
                    icon = CupertinoIcons.Default.PlusCircle,
                    text = "追加"
                )
            }
            CupertinoSection {
                ListItemButton(
                    onClick = {store.send(AdminDistrictTop.Action.SignOutTapped)},
                    text = "ログアウト",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
    // フルスクリーン遷移
    FullScreen(
        item = store.optionalScope(
            statePath = AdminDistrictTop.destinationKey + AdminDistrictTop.Destination.Edit.key,
            actionPath = AdminDistrictTop.destinationCase + AdminDistrictTop.Destination.Edit.case
        ),
        onDismiss = {}
    ) {
        AdminDistrictEditView(store = it)
    }
    FullScreen(
        item = store.optionalScope(
            statePath = AdminDistrictTop.destinationKey + AdminDistrictTop.Destination.Location.key,
            actionPath = AdminDistrictTop.destinationCase + AdminDistrictTop.Destination.Location.case
        ),
        onDismiss = { store.send(AdminDistrictTop.Action.DestinationDismissed) }
    ) {
        AdminLocationStoreView(store = it)
    }
    FullScreen(
        item = store.optionalScope(
            statePath = AdminDistrictTop.destinationKey + AdminDistrictTop.Destination.Route.key,
            actionPath = AdminDistrictTop.destinationCase + AdminDistrictTop.Destination.Route.case
        ),
        onDismiss = {}
    ) {
        AdminRouteInfoStoreView(store = it)
    }
    FullScreen(
        item = store.optionalScope(
            statePath = AdminDistrictTop.destinationKey + AdminDistrictTop.Destination.Export.key,
            actionPath = AdminDistrictTop.destinationCase + AdminDistrictTop.Destination.Export.case
        ),
        onDismiss = { store.send(AdminDistrictTop.Action.DestinationDismissed) }
    ) {
        AdminRouteExportStoreView(store = it)
    }
    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = AdminDistrictTop.alertKey,
            actionPath = AdminDistrictTop.alertCase
        )
    )
    LoadingOverlay(isLoading = state.isLoading)
}