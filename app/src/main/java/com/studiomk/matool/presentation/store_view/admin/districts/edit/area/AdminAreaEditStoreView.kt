package com.studiomk.matool.presentation.store_view.admin.districts.edit.area

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.studiomk.ktca.core.util.Binding
import com.studiomk.matool.presentation.view.maps.AdminDistrictMapView
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarTrailingButton
import com.studiomk.ktca.core.store.StoreOf
import io.github.alexzhirkevich.cupertino.*
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.ArrowUturnLeft

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminAreaEditView(store: StoreOf<AdminAreaEdit.State, AdminAreaEdit.Action>) {
    val state by store.state.collectAsState()

    CupertinoNavigationView(
        toolBar = {
            CupertinoToolBar(
                leading = {
                    CupertinoToolbarLeadingButton(
                        onClick = { store.send(AdminAreaEdit.Action.DismissTapped) },
                        text = "キャンセル"

                    )
                },
                center = { Text("町域編集", fontWeight = FontWeight.Bold) },
                trailing = {
                    CupertinoToolbarTrailingButton(
                        onClick = { store.send(AdminAreaEdit.Action.DoneTapped) },
                        text = "完了"
                    )
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AdminDistrictMapView(
                coordinates = state.coordinates,
                isShownPolygon = true,
                region = Binding(
                    getter = { state.region },
                    setter = { region -> store.send(AdminAreaEdit.Action.RegionChanged(region)) }
                ),
                onMapLongPress = { coordinate -> store.send(AdminAreaEdit.Action.MapTapped(coordinate)) }
            )
            // Undoボタン
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    CupertinoIconButton(
                        modifier = Modifier.size(64.dp),
                        onClick = { store.send(AdminAreaEdit.Action.UndoTapped) },
                        content = {
                            CupertinoIcon(
                                imageVector = CupertinoIcons.Default.ArrowUturnLeft,
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.8f),
                                        shape = CircleShape
                                    )
                                    .padding(8.dp),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
    BackHandler(enabled = true) {
        store.send(AdminAreaEdit.Action.DismissTapped)
    }
}
