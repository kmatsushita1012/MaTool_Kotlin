package com.studiomk.matool.presentation.store_view.auth.reset_password

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.matool.presentation.store_view.auth.change_password.ChangePassword
import com.studiomk.matool.presentation.store_view.auth.update_email.UpdateEmail
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import com.studiomk.matool.presentation.view.input.CupertinoBorderedTextField
import com.studiomk.matool.presentation.view.others.LoadingOverlay
import com.studiomk.matool.presentation.view.others.TitleView
import io.github.alexzhirkevich.cupertino.CupertinoButton
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi

@Composable
fun ResetPasswordStoreView(store: StoreOf<ResetPassword.State, ResetPassword.Action>) {
    val state by store.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TitleView(
            text = "パスワード変更",
            image = "SettingsBackground",
            onDismiss = { store.send(ResetPassword.Action.DismissTapped) }
        )
        Spacer(modifier = Modifier.weight(1f))
        when (state.step) {
            ResetPassword.State.Step.EnterUsername -> EnterUsernameView(state, store)
            ResetPassword.State.Step.EnterCode -> EnterCodeView(state, store)
        }
        Spacer(modifier = Modifier.weight(1f))
    }
    LoadingOverlay(state.isLoading)
    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = ResetPassword.alertKey,
            actionPath = ResetPassword.alertCase
        )
    )
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
private fun EnterUsernameView(state: ResetPassword.State, store: StoreOf<ResetPassword.State, ResetPassword.Action>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "パスワードをリセットするには、登録済みのメールアドレスに送信された認証コードが必要です。",
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        CupertinoBorderedTextField(
            value = state.username,
            onValueChange = { store.send(ResetPassword.Action.SetUsername(it)) },
            placeholder = "ID",
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            textStyle = MaterialTheme.typography.titleLarge,
        )
        CupertinoButton(
            onClick = { store.send(ResetPassword.Action.OkTapped) },
            modifier = Modifier.fillMaxWidth().height(44.dp)
        ) {
            Text(
                "認証コードを送信",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
private fun EnterCodeView(state: ResetPassword.State, store: StoreOf<ResetPassword.State, ResetPassword.Action>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CupertinoBorderedTextField(
            value = state.newPassword1,
            onValueChange = { store.send(ResetPassword.Action.SetNewPassword1(it)) },
            placeholder = "新しいパスワード",
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            textStyle = MaterialTheme.typography.titleLarge,
        )
        CupertinoBorderedTextField(
            value = state.newPassword2,
            onValueChange = { store.send(ResetPassword.Action.SetNewPassword2(it)) },
            placeholder = "新しいパスワード（確認用）",
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            textStyle = MaterialTheme.typography.titleLarge,
        )
        CupertinoBorderedTextField(
            value = state.code,
            onValueChange = { store.send(ResetPassword.Action.SetCode(it)) },
            placeholder = "認証コード（6桁）",
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            textStyle = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        CupertinoButton(
            onClick = { store.send(ResetPassword.Action.CodeOkTapped) },
            modifier = Modifier.fillMaxWidth().height(44.dp)
        ) {
            Text(
                "パスワードを変更",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
