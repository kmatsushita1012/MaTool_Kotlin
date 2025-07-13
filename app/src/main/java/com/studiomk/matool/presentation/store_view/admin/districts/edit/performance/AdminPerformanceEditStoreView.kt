package com.studiomk.matool.presentation.store_view.admin.districts.edit.performance

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarTrailingButton
import com.studiomk.matool.presentation.view.others.CupertinoForm
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.matool.presentation.store_view.shared.confirm_alert.ConfirmAlertDialog
import com.studiomk.matool.presentation.view.input.CupertinoTextEditor
import com.studiomk.matool.presentation.view.input.CupertinoTextField
import com.studiomk.matool.presentation.view.input.ListItemButton
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminPerformanceEditView(store: StoreOf<AdminPerformanceEdit.State, AdminPerformanceEdit.Action>) {
    val state by store.state.collectAsState()

    CupertinoNavigationView(
        toolBar = {
            CupertinoToolBar(
                leading = {
                    CupertinoToolbarLeadingButton(
                        onClick = { store.send(AdminPerformanceEdit.Action.CancelTapped) },
                        text = "キャンセル"
                    )
                },

                center = { Text("余興編集", fontWeight = FontWeight.Bold)},
                trailing = {
                    CupertinoToolbarTrailingButton(
                        onClick = { store.send(AdminPerformanceEdit.Action.DoneTapped) },
                        text = "完了"
                    )
                },
            )
        }
    ) {
        CupertinoForm{
            CupertinoSection(title = { Text("演目名") }) {
                CupertinoTextField(
                    value = state.item.name,
                    onValueChange = { store.send(AdminPerformanceEdit.Action.NameChanged(it)) },
                    placeholder = "演目名を入力 (例:〇〇音頭)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                )
            }
            CupertinoSection(title = { Text("演者") }) {
                CupertinoTextField(
                    value = state.item.performer,
                    onValueChange = { store.send(AdminPerformanceEdit.Action.PerformerChanged(it)) },
                    placeholder = "演者を入力 (例:小学生)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                )
            }
            CupertinoSection(title = { Text("紹介文") }) {
                CupertinoTextEditor(
                    value = state.item.description ?: "",
                    onValueChange = { store.send(AdminPerformanceEdit.Action.DescriptionChanged(it)) },
                    placeholder = "紹介文を入力",
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(vertical = 8.dp),
                )
            }
            CupertinoSection {
                ListItemButton(
                    text = "削除",
                    onClick = { store.send(AdminPerformanceEdit.Action.DeleteTapped) },
                    tint = Color.Red
                )
            }
        }
    }
    ConfirmAlertDialog(
        store = store.optionalScope(
            statePath = AdminPerformanceEdit.deleteAlertKey,
            actionPath = AdminPerformanceEdit.deleteAlertCase,
        )
    )
    BackHandler(enabled = true) {
        store.send(AdminPerformanceEdit.Action.CancelTapped)
    }
}

