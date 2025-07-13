package com.studiomk.matool.presentation.store_view.shared.notice_alert

import androidx.activity.compose.BackHandler
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.matool.presentation.store_view.shared.confirm_alert.ConfirmAlert
import io.github.alexzhirkevich.cupertino.*

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun NoticeAlertDialog(
    store: StoreOf<NoticeAlert.State, NoticeAlert.Action>?
) {
    if (store != null) {
        val state by store.state.collectAsState()
        CupertinoAlertDialog(
            title = { Text(state.title, fontWeight = FontWeight.Bold) },
            message = { Text(state.message) },
            onDismissRequest = { store.send(NoticeAlert.Action.OkTapped) }
        ){
            default(
                onClick = {
                    store.send(NoticeAlert.Action.OkTapped)
                }
            ) {
                Text(
                    "OK",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        BackHandler(enabled = true) {
            store.send(NoticeAlert.Action.OkTapped)
        }
    }
}