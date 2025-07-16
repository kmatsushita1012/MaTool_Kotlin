package com.studiomk.matool.presentation.store_view.admin.regions.district.list

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.ktca.ui.FullScreen
import com.studiomk.matool.domain.entities.routes.text
import com.studiomk.matool.presentation.store_view.admin.districts.route.export.AdminRouteExportStoreView
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import com.studiomk.matool.presentation.view.items.NavigationItem
import com.studiomk.matool.presentation.view.others.CupertinoForm
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import com.studiomk.matool.presentation.view.others.LoadingOverlay
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.ChevronBackward
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminRegionDistrictListStoreView(store: StoreOf<AdminRegionDistrictList.State, AdminRegionDistrictList.Action>) {
    val state by store.state.collectAsState()

    CupertinoNavigationView(
        toolBar = {
            CupertinoToolBar(
                modifier = it,
                leading = {
                    CupertinoToolbarLeadingButton(
                        onClick = { store.send(AdminRegionDistrictList.Action.DismissTapped) },
                        icon = CupertinoIcons.Default.ChevronBackward,
                        text = "戻る"
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
            CupertinoSection(title = { Text("行動") }) {
                state.routes.forEach { route ->
                    NavigationItem(
                        text = route.text("m/d T"),
                        onTap = { store.send(AdminRegionDistrictList.Action.ExportTapped(route)) }
                    )
                }
            }
        }
    }

    // フルスクリーン遷移
    FullScreen(
        item = store.optionalScope(
            statePath = AdminRegionDistrictList.exportKey,
            actionPath = AdminRegionDistrictList.exportCase
        ),
        onDismiss = { store.send(AdminRegionDistrictList.Action.DestinationDismissed) }
    ) {
        AdminRouteExportStoreView(store = it)
    }

    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = AdminRegionDistrictList.alertKey,
            actionPath = AdminRegionDistrictList.alertCase
        )
    )
    LoadingOverlay(isLoading = state.isLoading)
}