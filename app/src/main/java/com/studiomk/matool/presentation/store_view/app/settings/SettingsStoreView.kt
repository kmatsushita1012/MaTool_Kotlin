package com.studiomk.matool.presentation.store_view.app.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.studiomk.ktca.core.util.Binding
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import com.studiomk.matool.presentation.view.others.LoadingOverlay
import com.studiomk.matool.presentation.view.others.TitleView
import com.studiomk.matool.presentation.view.input.MenuSelector
import io.github.alexzhirkevich.cupertino.CupertinoButton
import io.github.alexzhirkevich.cupertino.CupertinoButtonDefaults.filledButtonColors
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun SettingsStoreView(store: StoreOf<Settings.State, Settings.Action>) {
    val state by store.state.collectAsState()
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TitleView(
            imageName = "SettingsBackground",
            titleText = "MaTool",
            isDismissEnabled = state.isDismissEnabled,
            onDismiss = { store.send(Settings.Action.DismissTapped) }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MenuSelector(
                header = "祭典を変更",
                items = state.regions,
                selection = Binding(
                    getter = { state.selectedRegion },
                    setter = { store.send(Settings.Action.RegionSelected(it)) }
                ),
                textForItem = { it?.name ?: "未設定" }
            )
            MenuSelector(
                header = "参加町を変更",
                items = state.districts,
                selection = Binding(
                    getter = { state.selectedDistrict },
                    setter = { store.send(Settings.Action.DistrictSelected(it)) }
                ),
                textForItem = { it?.name ?: "未設定" }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { uriHandler.openUri(state.userGuide) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
//                Image(
//                    painter = painterResource("LeftDoubleArrow"),
//                    contentDescription = null,
//                    modifier = Modifier.size(20.dp)
//                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "MaToolの使い方",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { uriHandler.openUri(state.contact) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
//                Image(
//                    painter = painterResource("LeftDoubleArrow"),
//                    contentDescription = null,
//                    modifier = Modifier.size(20.dp)
//                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "お問い合わせ",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        CupertinoButton(
            onClick = { store.send(Settings.Action.SignOutTapped)},
            colors = filledButtonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(
                "強制ログアウト",
                color = MaterialTheme.colorScheme.onError,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = Settings.alertKey,
            actionPath = Settings.alertCase,
        )
    )
    LoadingOverlay(isLoading = state.isLoading)
    BackHandler(enabled = true) {
        store.send(Settings.Action.DismissTapped)
    }
}