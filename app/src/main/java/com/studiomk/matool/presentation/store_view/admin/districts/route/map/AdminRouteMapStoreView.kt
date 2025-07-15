package com.studiomk.matool.presentation.store_view.admin.districts.route.map


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.studiomk.ktca.core.util.Binding
import com.studiomk.matool.presentation.store_view.admin.districts.route.map.point.AdminPointEditView
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import com.studiomk.matool.presentation.view.maps.AdminRouteMapView
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarTrailingButton
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.ktca.ui.Sheet
import io.github.alexzhirkevich.cupertino.*
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.ArrowUturnLeft
import io.github.alexzhirkevich.cupertino.icons.outlined.ArrowUturnRight

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminRouteMapStoreView(store: StoreOf<AdminRouteMap.State, AdminRouteMap.Action>) {
    val state by store.state.collectAsState()

    CupertinoNavigationView (
        toolBar = {
            CupertinoToolBar(
                leading = {
                    CupertinoToolbarLeadingButton(
                        onClick = { store.send(AdminRouteMap.Action.CancelTapped) },
                        text = "キャンセル"
                    )
                },
                center = { Text(
                        "ルート編集",
                       fontWeight = FontWeight.Bold
                    )
                },
                trailing = {
                    CupertinoToolbarTrailingButton(
                        onClick = { store.send(AdminRouteMap.Action.DoneTapped) },
                        text = "完了"
                    )
                },

            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AdminRouteMapView(
                points = state.points,
                segments = state.segments,
                onMapLongPress = { coordinate -> store.send(AdminRouteMap.Action.MapLongPressed(coordinate)) },
                onPointTapped = { point -> store.send(AdminRouteMap.Action.AnnotationTapped(point)) },
                onPolylineTapped = { segment -> store.send(AdminRouteMap.Action.PolylineTapped(segment)) },
                region = Binding(
                    getter = { state.region },
                    setter = { region -> store.send(AdminRouteMap.Action.RegionChanged(region)) }
                )
            )
            // 操作バー
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.operation.text,
                        modifier = Modifier
                            .background(
                                color = Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.White,
                        fontStyle = MaterialTheme.typography.bodyLarge.fontStyle
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    CupertinoIconButton(
                        onClick = { store.send(AdminRouteMap.Action.UndoTapped) },
                        enabled = state.canUndo,
                        content = {
                            CupertinoIcon(
                                imageVector = CupertinoIcons.Default.ArrowUturnLeft,
                                tint = Color.White,
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .padding(8.dp),
                                contentDescription = null
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    CupertinoIconButton(
                        onClick = { store.send(AdminRouteMap.Action.RedoTapped) },
                        enabled = state.canRedo,
                        content = {
                            CupertinoIcon(
                                imageVector = CupertinoIcons.Default.ArrowUturnRight,
                                tint = Color.White,
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .padding(8.dp),
                                contentDescription = null
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }

    Sheet (
        item = store.optionalScope(
            statePath = AdminRouteMap.destinationKey + AdminRouteMap.Destination.Point.key,
            actionPath = AdminRouteMap.destinationCase + AdminRouteMap.Destination.Point.case
        ),
        onDismiss = { store.send(AdminRouteMap.Action.DestinationDismissed) }
    ) {
        AdminPointEditView(
            store = it
        )
    }
    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = AdminRouteMap.alertKey,
            actionPath = AdminRouteMap.alertCase
        )
    )
}

// Operationのtext拡張
val AdminRouteMap.Operation.text: String
    get() = when (this) {
        is AdminRouteMap.Operation.Add -> "長押しで地点を追加"
        is AdminRouteMap.Operation.Insert -> "長押しで地点を挿入"
        is AdminRouteMap.Operation.Move -> "長押しで地点を移動"
    }

