package com.studiomk.matool.presentation.store_view.admin.districts.edit.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.studiomk.matool.core.binding.Binding
import com.studiomk.matool.presentation.view.maps.AdminDistrictMapView
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarTrailingButton
import com.studiomk.ktca.core.store.StoreOf
import io.github.alexzhirkevich.cupertino.*

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminBaseEditView(store: StoreOf<AdminBaseEdit.State, AdminBaseEdit.Action>) {
    val state by store.state.collectAsState()

    CupertinoNavigationView(
        toolBar = {
            CupertinoToolBar(
                leading = {
                    CupertinoToolbarLeadingButton(
                        onClick = { store.send(AdminBaseEdit.Action.DismissTapped) },
                        text = "キャンセル"
                    )
                },
                center = { Text("会所位置編集", fontWeight = FontWeight.Bold)},
                trailing = {
                    CupertinoToolbarTrailingButton(
                        onClick = { store.send(AdminBaseEdit.Action.DoneTapped) },
                        text = "完了"

                    )
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 地図表示（AdminDistrictMapはCompose用に別途実装してください）
            AdminDistrictMapView (
                coordinates = state.base?.let { listOf(it) } ?: emptyList(),
                isShownPolygon = false,
                region = Binding(
                    getter = { state.region },
                    setter = { region -> store.send(AdminBaseEdit.Action.RegionChanged(region)) }
                ),
                onMapLongPress = { coordinate ->
                    store.send(AdminBaseEdit.Action.MapTapped(coordinate))
                }
            )
            // Eraserボタン
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
                        onClick = { store.send(AdminBaseEdit.Action.ClearTapped) },
                        content = {
                            CupertinoIcon(
                                imageVector = Icons.Default.Delete,
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.8f),
                                        shape = CircleShape
                                    )
                                    .padding(8.dp),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
