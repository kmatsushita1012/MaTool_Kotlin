package com.studiomk.matool.presentation.store_view.auth.change_password

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import io.github.alexzhirkevich.cupertino.CupertinoButton
import com.studiomk.matool.presentation.view.input.CupertinoBorderedTextField
import com.studiomk.matool.presentation.view.others.LoadingOverlay
import com.studiomk.matool.presentation.view.others.TitleView
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun ChangePasswordStoreView(store: StoreOf<ChangePassword.State, ChangePassword.Action>) {
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
            onDismiss = { store.send(ChangePassword.Action.DismissTapped) }
        )
        Spacer(modifier = Modifier.weight(1f))
        CupertinoBorderedTextField(
            value = state.current,
            onValueChange = { store.send(ChangePassword.Action.SetCurrent(it)) },
            placeholder = "現在のパスワード",
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            textStyle = MaterialTheme.typography.titleLarge,
        )
        CupertinoBorderedTextField(
            value = state.new1,
            onValueChange = { store.send(ChangePassword.Action.SetNew1(it)) },
            placeholder = "新しいパスワード",
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            textStyle = MaterialTheme.typography.titleLarge,
        )
        CupertinoBorderedTextField(
            value = state.new2,
            onValueChange = { store.send(ChangePassword.Action.SetNew2(it)) },
            placeholder = "新しいパスワード（確認用）",
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            textStyle = MaterialTheme.typography.titleLarge,
        )
        CupertinoButton(
            onClick = { store.send(ChangePassword.Action.OkTapped) },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Text("パスワードを変更", color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.weight(1f))
    }
    LoadingOverlay(state.isLoading)
    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = ChangePassword.alertKey,
            actionPath = ChangePassword.alertCase
        )
    )
}
