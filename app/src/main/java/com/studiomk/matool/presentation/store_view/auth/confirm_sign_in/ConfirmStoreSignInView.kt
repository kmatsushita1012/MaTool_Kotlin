package com.studiomk.matool.presentation.store_view.auth.confirm_sign_in

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.matool.presentation.view.input.CupertinoBorderedTextField
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import io.github.alexzhirkevich.cupertino.CupertinoButton
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.ChevronBackward

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun ConfirmSignInView(store: StoreOf<ConfirmSignIn.State, ConfirmSignIn.Action>) {
    val state by store.state.collectAsState()

    CupertinoNavigationView(
        toolBar = {
            CupertinoToolBar(
                leading = {
                    CupertinoToolbarLeadingButton (
                        onClick = { store.send(ConfirmSignIn.Action.DismissTapped) },
                        text = "戻る",
                        icon = CupertinoIcons.Default.ChevronBackward,
                    )
                },
                center = { Text("パスワード変更", fontWeight = FontWeight.Bold) },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            CupertinoBorderedTextField(
                value = state.password1,
                onValueChange = { store.send(ConfirmSignIn.Action.SetPassword1(it)) },
                placeholder = "新しいパスワード",
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            )
            CupertinoBorderedTextField(
                value = state.password2,
                onValueChange = { store.send(ConfirmSignIn.Action.SetPassword2(it)) },
                placeholder = "新しいパスワード（確認用）",
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            )
            CupertinoButton(
                onClick = { store.send(ConfirmSignIn.Action.SubmitTapped) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text("送信", color = Color.White)
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        // アラート
        NoticeAlertDialog(
            store = store.optionalScope(
                statePath = ConfirmSignIn.alertKey,
                actionPath = ConfirmSignIn.alertCase
            )
        )
    }
}