package com.studiomk.matool.presentation.store_view.admin.shared.information

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.matool.presentation.view.input.CupertinoTextEditor
import com.studiomk.matool.presentation.view.input.CupertinoTextField
import com.studiomk.matool.presentation.view.input.ListItemButton
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarTrailingButton
import com.studiomk.matool.presentation.view.others.CupertinoForm
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun InformationEditStoreView(store: StoreOf<InformationEdit.State, InformationEdit.Action>) {
    val state by store.state.collectAsState()

    CupertinoNavigationView(
        toolBar = {
            CupertinoToolBar(
                leading = {
                    CupertinoToolbarLeadingButton(
                        onClick = { store.send(InformationEdit.Action.CancelTapped) },
                        text = "キャンセル"
                    )
                },
                center = {
                    Text(state.title, fontWeight = FontWeight.Bold)
                },
                trailing = {
                    CupertinoToolbarTrailingButton(
                        onClick = { store.send(InformationEdit.Action.DoneTapped) },
                        text = "完了"
                    )
                }
            )
        }
    ) {
        CupertinoForm {
            CupertinoSection(title = { Text("名称") }) {
                CupertinoTextField(
                    value = state.item.name,
                    onValueChange = { store.send(InformationEdit.Action.NameChanged(it)) },
                    placeholder = "名称を入力",
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
            CupertinoSection(title = { Text("詳細") }) {
                CupertinoTextEditor(
                    value = state.item.description ?: "",
                    onValueChange = { store.send(InformationEdit.Action.DescriptionChanged(it)) },
                    placeholder = "詳細を入力",
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(vertical = 8.dp)
                )
            }
            if(state.shouldShowDelete) {
                CupertinoSection {
                    ListItemButton(
                        text = "削除",
                        onClick = { store.send(InformationEdit.Action.DeleteTapped) },
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}