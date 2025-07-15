package com.studiomk.matool.presentation.store_view.app.onboarding


import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.studiomk.ktca.core.util.Binding
import com.studiomk.matool.presentation.view.others.LoadingOverlay
import com.studiomk.matool.presentation.view.input.MenuSelector
import io.github.alexzhirkevich.cupertino.*
import com.studiomk.ktca.core.store.Store
import io.github.alexzhirkevich.cupertino.CupertinoButtonDefaults.plainButtonColors

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun OnboardingStoreView(store: Store<Onboarding.State, Onboarding.Action>) {
    val state by store.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.weight(2f))

        MenuSelector(
            header = "祭典",
            items = state.regions,
            selection = Binding(
                getter = { state.selectedRegion },
                setter = { store.send(Onboarding.Action.SetSelectedRegion(it)) }
            ),
            textForItem = { it?.name ?: "未設定" },
            errorMessage = state.regionsErrorMessage
        )
        Spacer(modifier = Modifier.weight(1f))
        // 参加町の方
        CupertinoButton(
            onClick = {
                store.send(Onboarding.Action.InternalGuestTapped)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Box {
                Text(
                    "参加町の方",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                DropdownMenu(
                    expanded = state.showMenu,
                    onDismissRequest = { store.send(Onboarding.Action.MenuDismissTapped) },
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    state.districts?.forEach { district ->
                        DropdownMenuItem(
                            onClick = { store.send(Onboarding.Action.DistrictSelected(district)) },
                            text = { Text(district.name) },
                        )
                    }
                }
            }
        }

        // 参加町以外
        CupertinoButton(
            onClick = { store.send(Onboarding.Action.ExternalGuestTapped) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            border = BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary
            ),
            colors = plainButtonColors()
        ) {
            Text(
                "参加町以外からお越しの方",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // 管理者
        CupertinoButton(
            onClick = { store.send(Onboarding.Action.AdminTapped) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            border = BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary
            ),
            colors = plainButtonColors()
        ) {
            Text(
                "参加町代表者、管理者の方",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        Spacer(modifier = Modifier.weight(2f))
    }
    LoadingOverlay(isLoading = state.isLoading)
    // onAppear
    LaunchedEffect(Unit) {
        store.send(Onboarding.Action.OnAppear)
    }
}

