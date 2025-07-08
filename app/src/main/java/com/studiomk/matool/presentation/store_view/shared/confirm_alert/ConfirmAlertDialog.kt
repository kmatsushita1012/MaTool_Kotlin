package com.studiomk.matool.presentation.store_view.shared.confirm_alert

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
            default(
                onClick = {
                    store.send(ConfirmAlert.Action.OkTapped)
                }
            ) {
                Text(
                    "OK",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}