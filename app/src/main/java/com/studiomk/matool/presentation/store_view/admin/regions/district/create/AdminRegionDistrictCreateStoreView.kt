package com.studiomk.matool.presentation.store_view.admin.regions.district.create

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import com.studiomk.matool.presentation.view.input.CupertinoTextField
import com.studiomk.matool.presentation.view.others.CupertinoForm
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import com.studiomk.matool.presentation.view.others.LoadingOverlay
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminRegionDistrictCreateStoreView(store: StoreOf<AdminRegionDistrictCreate.State, AdminRegionDistrictCreate.Action>) {
    val state by store.state.collectAsState()

    CupertinoNavigationView(
        toolBar = { modifier ->
            CupertinoToolBar(
                modifier = modifier,
                leading = {
                    CupertinoToolbarLeadingButton(
                        onClick = { store.send(AdminRegionDistrictCreate.Action.CancelTapped) },
                        text = "キャンセル"
                    )
                },
                center = {
                    Text("新規作成", style = MaterialTheme.typography.titleMedium)
                },
                trailing = {
                    TextButton(
                        onClick = { store.send(AdminRegionDistrictCreate.Action.CreateTapped) },
                        enabled = state.name.isNotBlank() && state.email.isNotBlank()
                    ) {
                        Text("作成", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            )
        }
    ) {
        CupertinoForm {
            CupertinoSection(title = { Text("町名") }) {
                CupertinoTextField(
                    value = state.name,
                    onValueChange = { store.send(AdminRegionDistrictCreate.Action.NameChanged(value = it)) },
                    placeholder = "町名を入力",
                )
            }
            CupertinoSection(title = { Text("メールアドレス") }) {
                CupertinoTextField(
                    value = state.email,
                    onValueChange = { store.send(AdminRegionDistrictCreate.Action.EmailChanged(value = it)) },
                    placeholder = "メールアドレスを入力",
                )
            }
        }
    }

    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = AdminRegionDistrictCreate.alertKey,
            actionPath = AdminRegionDistrictCreate.alertCase
        )
    )
    LoadingOverlay(isLoading = state.isLoading)
}