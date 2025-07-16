package com.studiomk.matool.presentation.store_view.admin.districts.location


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.studiomk.ktca.core.util.Binding
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import com.studiomk.matool.presentation.view.input.PickerMenu
import com.studiomk.matool.presentation.view.maps.AdminLocationMap
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import com.studiomk.matool.presentation.view.others.CupertinoForm
import com.studiomk.ktca.core.store.StoreOf
import io.github.alexzhirkevich.cupertino.*
import io.github.alexzhirkevich.cupertino.section.CupertinoSection
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.ChevronBackward

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminLocationStoreView(store: StoreOf<AdminLocation.State, AdminLocation.Action>) {
    val state by store.state.collectAsState()
    LaunchedEffect(Unit) {
        store.send(AdminLocation.Action.OnAppear)
    }

    CupertinoNavigationView(
        toolBar = {
            CupertinoToolBar(
                leading = {
                    CupertinoToolbarLeadingButton (
                        onClick = { store.send(AdminLocation.Action.DismissTapped) },
                        icon = CupertinoIcons.Default.ChevronBackward,
                        text = "戻る"
                    )
                },
                center = {
                    Text(
                        "位置情報配信",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) {
        CupertinoForm() {
            CupertinoSection {
                ListItem(
                    headlineContent = { Text("配信") },
                    trailingContent = {
                        CupertinoSwitch(
                            checked = state.isTracking,
                            onCheckedChange = { store.send(AdminLocation.Action.ToggleChanged(it)) }
                        )
                    }
                )
                ListItem(
                    headlineContent = { Text("間隔") },
                    trailingContent = {
                        PickerMenu(
                            selection = Binding(
                                getter = { state.selectedInterval },
                                setter = { store.send(AdminLocation.Action.IntervalChanged(it)) }
                            ),
                            items = state.intervals,
                            itemLabel = { it.label },
                        )
                    }
                )
            }
            Spacer(Modifier.height(16.dp))
            AdminLocationMap(
                location = state.location,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF2F2F7))
                    .border(
                        width = 0.5.dp,
                        color = Color(0xFFBDBDBD),
                        shape = RoundedCornerShape(12.dp)
                    )
            )
            Spacer(Modifier.height(16.dp))
            if (state.history.isNotEmpty()) {
                CupertinoSection(title = { Text("履歴") }) {
                    state.history.takeLast(10).asReversed().forEach { history ->
                        ListItem(
                            headlineContent = { Text(history.text) },
                        )
                    }
                }
            }
        }
    }
    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = AdminLocation.alertKey,
            actionPath = AdminLocation.alertCase
        ),
    )
}
