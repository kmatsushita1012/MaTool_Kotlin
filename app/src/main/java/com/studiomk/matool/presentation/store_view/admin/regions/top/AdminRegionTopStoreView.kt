package com.studiomk.matool.presentation.store_view.admin.regions.top

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.ktca.ui.FullScreen
import com.studiomk.matool.presentation.store_view.admin.districts.top.AdminDistrictTop
import com.studiomk.matool.presentation.store_view.admin.districts.top.case
import com.studiomk.matool.presentation.store_view.admin.districts.top.destinationCase
import com.studiomk.matool.presentation.store_view.admin.districts.top.destinationKey
import com.studiomk.matool.presentation.store_view.admin.districts.top.key
import com.studiomk.matool.presentation.store_view.admin.regions.edit.AdminRegionEditStoreView
import com.studiomk.matool.presentation.store_view.admin.regions.district.list.AdminRegionDistrictListStoreView
import com.studiomk.matool.presentation.store_view.admin.regions.district.create.AdminRegionDistrictCreateStoreView
import com.studiomk.matool.presentation.store_view.auth.change_password.ChangePasswordStoreView
import com.studiomk.matool.presentation.store_view.auth.update_email.UpdateEmailStoreView
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import com.studiomk.matool.presentation.view.items.NavigationItem
import com.studiomk.matool.presentation.view.input.ListItemButton
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import com.studiomk.matool.presentation.view.others.CupertinoForm
import com.studiomk.matool.presentation.view.others.LoadingOverlay
import io.github.alexzhirkevich.cupertino.CupertinoHorizontalDivider
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.House
import io.github.alexzhirkevich.cupertino.icons.outlined.PlusCircle
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminRegionTopStoreView(store: StoreOf<AdminRegionTop.State, AdminRegionTop.Action>) {
    val state by store.state.collectAsState()

    CupertinoNavigationView(
        toolBar = {
            CupertinoToolBar(
                leading = {
                    CupertinoToolbarLeadingButton(
                        onClick = { store.send(AdminRegionTop.Action.HomeTapped) },
                        icon = CupertinoIcons.Default.House
                    )
                },
                center = {
                    Text(
                        state.region.name,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) {
        CupertinoForm {
            CupertinoSection {
                NavigationItem(
                    text = "祭典情報",
                    icon = Icons.Default.Info,
                    onTap = { store.send(AdminRegionTop.Action.OnEdit) }
                )
            }
            CupertinoSection(title = { Text("参加町") }) {
                state.districts.forEach { district ->
                    NavigationItem(
                        text = district.name,
                        onTap = { store.send(AdminRegionTop.Action.OnDistrictInfo(district)) }
                    )
                    CupertinoHorizontalDivider()
                }
                ListItemButton(
                    onClick = { store.send(AdminRegionTop.Action.OnCreateDistrict) },
                    icon =  CupertinoIcons.Default.PlusCircle,
                    text = "追加"
                )
            }
            CupertinoSection {
                ListItemButton(
                    onClick = {store.send(AdminRegionTop.Action.ChangePasswordTapped)},
                    text = "パスワード変更",
                    tint = MaterialTheme.colorScheme.primary
                )
                ListItemButton(
                    onClick = {store.send(AdminRegionTop.Action.UpdateEmailTapped)},
                    text = "メールアドレス変更",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            CupertinoSection {
                ListItemButton(
                    onClick = { store.send(AdminRegionTop.Action.SignOutTapped) },
                    text = "ログアウト",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    // 祭典情報編集
    FullScreen(
        item = store.optionalScope(
            statePath = AdminRegionTop.destinationKey + AdminRegionTop.Destination.Edit.key,
            actionPath = AdminRegionTop.destinationCase + AdminRegionTop.Destination.Edit.case
        ),
        onDismiss = {}
    ) {
        AdminRegionEditStoreView(store = it)
    }
    // 町リスト
    FullScreen(
        item = store.optionalScope(
            statePath = AdminRegionTop.destinationKey + AdminRegionTop.Destination.DistrictInfo.key,
            actionPath = AdminRegionTop.destinationCase + AdminRegionTop.Destination.DistrictInfo.case
        ),
        onDismiss = { store.send(AdminRegionTop.Action.DestinationDismissed) }
    ) {
        AdminRegionDistrictListStoreView(store = it)
    }
    // 町新規作成
    FullScreen(
        item = store.optionalScope(
            statePath = AdminRegionTop.destinationKey + AdminRegionTop.Destination.DistrictCreate.key,
            actionPath = AdminRegionTop.destinationCase + AdminRegionTop.Destination.DistrictCreate.case
        ),
        onDismiss = { store.send(AdminRegionTop.Action.DestinationDismissed) }
    ) {
        AdminRegionDistrictCreateStoreView(store = it)
    }
    FullScreen(
        item = store.optionalScope(
            statePath = AdminRegionTop.destinationKey + AdminRegionTop.Destination.ChangePassword.key,
            actionPath = AdminRegionTop.destinationCase + AdminRegionTop.Destination.ChangePassword.case
        ),
        onDismiss = { store.send(AdminRegionTop.Action.DestinationDismissed) }
    ) {
        ChangePasswordStoreView(store = it)
    }
    FullScreen(
        item = store.optionalScope(
            statePath = AdminRegionTop.destinationKey + AdminRegionTop.Destination.UpdateEmail.key,
            actionPath = AdminRegionTop.destinationCase + AdminRegionTop.Destination.UpdateEmail.case
        ),
        onDismiss = { store.send(AdminRegionTop.Action.DestinationDismissed) }
    ) {
        UpdateEmailStoreView(store = it)
    }

    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = AdminRegionTop.alertKey,
            actionPath = AdminRegionTop.alertCase
        )
    )
    LoadingOverlay(isLoading = state.isLoading)
}