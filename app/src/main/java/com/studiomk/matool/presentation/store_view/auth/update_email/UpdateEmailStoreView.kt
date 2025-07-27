package com.studiomk.matool.presentation.store_view.auth.update_email

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.matool.presentation.store_view.auth.change_password.ChangePassword
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import com.studiomk.matool.presentation.view.input.CupertinoBorderedTextField
import com.studiomk.matool.presentation.view.others.LoadingOverlay
import com.studiomk.matool.presentation.view.others.TitleView
import io.github.alexzhirkevich.cupertino.CupertinoButton
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi

@Composable
fun UpdateEmailStoreView(store: StoreOf<UpdateEmail.State, UpdateEmail.Action>) {
    val state by store.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TitleView(
            text = "メールアドレス\n変更",
            image = "SettingsBackground",
            onDismiss = { store.send(UpdateEmail.Action.DismissTapped) }
        )
        Spacer(modifier = Modifier.weight(1f))
        when (val step = state.step) {
            is UpdateEmail.State.Step.EnterEmail -> EnterEmailView(state, store)
            is UpdateEmail.State.Step.EnterCode -> EnterCodeView(state, store, step.destination)
        }
        Spacer(modifier = Modifier.weight(1f))
    }
    LoadingOverlay(state.isLoading)
    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = UpdateEmail.errorAlertKey,
            actionPath = UpdateEmail.errorAlertCase
        )
    )
    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = UpdateEmail.completeAlertKey,
            actionPath = UpdateEmail.completeAlertCase
        )
    )
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
private fun EnterEmailView(state: UpdateEmail.State, store: StoreOf<UpdateEmail.State, UpdateEmail.Action>) {
    Column {
        CupertinoBorderedTextField(
            value = state.email,
            onValueChange = { store.send(UpdateEmail.Action.SetEmail(it)) },
            placeholder = "新しいメールアドレス",
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            textStyle = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        CupertinoButton(
            onClick = { store.send(UpdateEmail.Action.OkTapped) },
            modifier = Modifier.fillMaxWidth().height(44.dp)
        ) {
            Text(
                "変更",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
private fun EnterCodeView(state: UpdateEmail.State, store: StoreOf<UpdateEmail.State, UpdateEmail.Action>, destination: String) {
    Column {
        Text(
            text = "入力されたメールアドレス $destination に6桁の確認コードを送信しました。次の画面で入力してください。",
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        CupertinoBorderedTextField(
            value = state.code,
            onValueChange = { store.send(UpdateEmail.Action.SetCode(it)) },
            placeholder = "認証コード（6桁）",
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            textStyle = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        CupertinoButton(
            onClick = { store.send(UpdateEmail.Action.CodeOkTapped) },
            modifier = Modifier.fillMaxWidth().height(44.dp)
        ) {
            Text(
                "認証",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
