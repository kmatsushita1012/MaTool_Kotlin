package com.studiomk.matool.presentation.store_view.auth.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.studiomk.matool.presentation.store_view.auth.confirm_sign_in.ConfirmSignInView
import com.studiomk.matool.presentation.view.input.CupertinoBorderedTextField
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.ktca.ui.FullScreenNavigation
import io.github.alexzhirkevich.cupertino.CupertinoButton
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.House

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun LoginStoreView(store: StoreOf<Login.State, Login.Action>) {
    val state by store.state.collectAsState()

    CupertinoNavigationView(
        toolBar = {
            CupertinoToolBar(
                center = {
                    Text("ログイン", fontWeight = FontWeight.Bold) },
                leading = {
                    CupertinoToolbarLeadingButton (
                        onClick = { store.send(Login.Action.HomeTapped) },
                        icon = CupertinoIcons.Default.House
                    )
                }
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
                value = state.id,
                onValueChange = { store.send(Login.Action.SetId(it)) },
                placeholder = "ID",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                textStyle = MaterialTheme.typography.titleLarge,
            )
            CupertinoBorderedTextField(
                value = state.password,
                onValueChange = { store.send(Login.Action.SetPassword(it)) },
                placeholder = "パスワード",
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                textStyle = MaterialTheme.typography.titleLarge,
            )
            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage ?: "",
                    color = Color.Red,
                )
            }
            CupertinoButton(
                onClick = { store.send(Login.Action.SignInTapped) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text("ログイン", color = Color.White, style = MaterialTheme.typography.titleMedium,)
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
    // ConfirmSignInのフルスクリーン表示
    FullScreenNavigation(
        item = store.optionalScope(
            statePath = Login.confirmSignInKey,
            actionPath = Login.confirmSignInCase
        )
    ) {
        ConfirmSignInView(it)
    }
}