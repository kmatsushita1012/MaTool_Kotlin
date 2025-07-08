package com.studiomk.matool.presentation.store_view.admin.districts.route.map.point

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.studiomk.matool.core.binding.Binding
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.matool.presentation.view.input.BindableTimePicker
import com.studiomk.matool.presentation.view.input.CupertinoTextEditor
import com.studiomk.matool.presentation.view.input.CupertinoTextField
import com.studiomk.matool.presentation.view.input.ListItemButton
import com.studiomk.matool.presentation.view.others.CupertinoForm
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarTrailingButton
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.CupertinoIconButton
import io.github.alexzhirkevich.cupertino.CupertinoSwitch
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.Ellipsis
import io.github.alexzhirkevich.cupertino.icons.outlined.PlusCircle
import io.github.alexzhirkevich.cupertino.section.CupertinoSection


//TODO
@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminPointEditView(store: StoreOf<AdminPointEdit.State, AdminPointEdit.Action>) {
    val state by store.state.collectAsState()

    CupertinoNavigationView(
        isFullScreen = false,
        toolBar = {
            CupertinoToolBar(
                leading = {
                    CupertinoToolbarLeadingButton(
                        onClick = { store.send(AdminPointEdit.Action.CancelTapped) },
                        text = "キャンセル"
                    )
                },
                center = {
                    Text("地点編集", fontWeight = FontWeight.Bold)
                },
                trailing = {
                    CupertinoToolbarTrailingButton(
                        onClick = {store.send(AdminPointEdit.Action.DoneTapped)},
                        text = "完了"
                    )
                }
            )
        }
    ) {
        CupertinoForm {
            // イベントセクション
            CupertinoSection(title = { Text("イベント") }) {
                CupertinoTextField(
                    value = state.item.title ?: "",
                    onValueChange = { store.send(AdminPointEdit.Action.TitleChanged(it)) },
                    placeholder = "イベントを入力",
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.titleMedium,
                    trailing = {
                        CupertinoIconButton(
                            onClick = { store.send(AdminPointEdit.Action.MenuTapped) },
                            content = {
                                CupertinoIcon(
                                    imageVector = CupertinoIcons.Default.Ellipsis,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        DropdownMenu(
                            expanded = state.showPopover,
                            onDismissRequest = { store.send(AdminPointEdit.Action.MenuDismissed) },
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            state.events.forEach {
                                DropdownMenuItem(
                                    text = { Text(it.name) },
                                    onClick = {
                                        store.send(AdminPointEdit.Action.TitleOptionSelected(it))
                                    },
                                    colors = MenuDefaults.itemColors(
                                        textColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                )
            }


            CupertinoSection(
                title = { Text("詳細") },
                caption = {}
                ) {
                CupertinoTextEditor(
                    value = state.item.description ?: "",
                    onValueChange = { store.send(AdminPointEdit.Action.DescriptionChanged(it)) },
                    placeholder = "説明を入力",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                )
            }

            // 時刻セクション
            CupertinoSection(title = { Text("時刻と出力") }) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    ListItem(
                        headlineContent = { Text("時刻を設定") },
                        trailingContent = {
                            CupertinoSwitch(
                                checked = state.hasTime,
                                onCheckedChange = {
                                    store.send(
                                        AdminPointEdit.Action.TimeSwitchChanged(it)
                                    )
                                }
                            )
                        }
                    )
                }
                state.item.time?.let{
                    BindableTimePicker(
                        label = "時刻を選択",
                        selection = Binding(
                            getter = { it },
                            setter = { store.send(AdminPointEdit.Action.TimeChanged(it)) }
                        )
                    )
                }
                ListItem(
                    headlineContent = { Text("経路図（PDF）への出力") },
                    trailingContent = {
                        CupertinoSwitch(
                            checked = state.item.shouldExport,
                            onCheckedChange = {
                                store.send(AdminPointEdit.Action.ShouldExportChanged(it))
                            }
                        )
                    }
                )
            }

            // 操作セクション
            CupertinoSection {
                ListItemButton(
                    onClick = { store.send(AdminPointEdit.Action.MoveTapped) },
                    icon =  Icons.Default.ArrowOutward,
                    text = "この地点を移動"
                )
                ListItemButton(
                    onClick = { store.send(AdminPointEdit.Action.InsertTapped) },
                    icon =  CupertinoIcons.Default.PlusCircle,
                    text = "この地点の前に新しい地点を挿入"
                )
            }
            CupertinoSection {
                ListItemButton(
                    onClick = { store.send(AdminPointEdit.Action.DeleteTapped) },
                    icon = Icons.Default.Delete,
                    text = "この地点を削除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = AdminPointEdit.alertKey,
            actionPath = AdminPointEdit.alertCase
        )
    )
}