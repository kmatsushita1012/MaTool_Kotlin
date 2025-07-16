package com.studiomk.matool.presentation.store_view.shared.confirm_alert

import androidx.activity.compose.BackHandler
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.studiomk.ktca.core.store.StoreOf
import io.github.alexzhirkevich.cupertino.*

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun ConfirmAlertDialog(
    store: StoreOf<ConfirmAlert.State, ConfirmAlert.Action>?
) {
    if (store != null) {
        val state by store.state.collectAsState()
        CupertinoAlertDialog(
            title = { Text(state.title, fontWeight = FontWeight.Bold) },
            message = { Text(state.message) },
            onDismissRequest = { store.send(ConfirmAlert.Action.OkTapped) }
        ){
            cancel(
                onClick = {
                    store.send(ConfirmAlert.Action.CancelTapped)
                }
            ) {
                Text(
                    "キャンセル",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            destructive(
                onClick = {
                    store.send(ConfirmAlert.Action.OkTapped)
                }
            ) {
                Text(
                    state.buttonText,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        BackHandler(enabled = true) {
            store.send(ConfirmAlert.Action.CancelTapped)
        }
    }
}