package com.studiomk.matool.presentation.store_view.app.home


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.studiomk.matool.core.theme.HomeCardColors
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.ktca.ui.FullScreen
import com.studiomk.matool.presentation.store_view.auth.login.LoginStoreView
import com.studiomk.matool.presentation.store_view.admin.districts.top.AdminDistrictTopStoreView
import com.studiomk.matool.presentation.store_view.admin.regions.top.AdminRegionTopStoreView
import com.studiomk.matool.presentation.store_view.app.settings.SettingsStoreView
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.others.LoadingOverlay
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun HomeStoreView(store: StoreOf<Home.State, Home.Action>) {
    val state by store.state.collectAsState()
    CupertinoNavigationView {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(
                    WindowInsets.systemBars
                        .only(WindowInsetsSides.Bottom)
                        .asPaddingValues()
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "MaTool",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
            // 地図
            HomeCard(
                title = "準備中",
                onClick = { },
                foregroundColor =  Color.White,
                backgroundColor = HomeCardColors.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Row(
                Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HomeCard(
                        title = "準備中",
                        onClick = {  },
                        foregroundColor = Color.White,
                        backgroundColor = HomeCardColors.Blue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        HomeCard(
                            title = "管理者用\nページ",
                            onClick = { store.send(Home.Action.AdminTapped) },
                            foregroundColor = Color.White,
                            backgroundColor = HomeCardColors.Orange,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        LoadingOverlay(isLoading = state.isAuthLoading)
                    }

                }
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HomeCard(
                        title = "設定",
                        onClick = { store.send(Home.Action.SettingsTapped) },
                        foregroundColor = Color.Black,
                        backgroundColor = HomeCardColors.Yellow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    HomeCard(
                        title = "準備中",
                        onClick = {},
                        foregroundColor = Color.Black,
                        backgroundColor = HomeCardColors.Green,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }
        }
    }
    // フルスクリーン遷移
//        FullScreen(
//            item = store.optionalScope(
//                statePath = Home.destinationKey + Home.Destination.Route.key,
//                actionPath = Home.destinationCase + Home.Destination.Route.case
//            )
//        ) {
//            PublicMapStoreView(store = it)
//        }
//        FullScreen(
//            item = store.optionalScope(
//                statePath = Home.destinationKey + Home.Destination.Info.key,
//                actionPath = Home.destinationCase + Home.Destination.Info.case
//            )
//        ) {
//            InfoStoreView(store = it)
//        }
    FullScreen(
        item = store.optionalScope(
            statePath = Home.destinationKey + Home.Destination.Login.key,
            actionPath = Home.destinationCase + Home.Destination.Login.case
        ),
        onDismiss = { store.send(Home.Action.DestinationDismissed) }
    ) {
        LoginStoreView(store = it)
    }
    FullScreen(
        item = store.optionalScope(
            statePath = Home.destinationKey + Home.Destination.AdminDistrict.key,
            actionPath = Home.destinationCase + Home.Destination.AdminDistrict.case
        ),
        onDismiss = { store.send(Home.Action.DestinationDismissed) }
    ) {
        AdminDistrictTopStoreView(store = it)
    }
    FullScreen(
        item = store.optionalScope(
            statePath = Home.destinationKey + Home.Destination.AdminRegion.key,
            actionPath = Home.destinationCase + Home.Destination.AdminRegion.case
        ),
        onDismiss = { store.send(Home.Action.DestinationDismissed) }
    ) {
        AdminRegionTopStoreView(store = it)
    }
    FullScreen(
        item = store.optionalScope(
            statePath = Home.destinationKey + Home.Destination.Settings.key,
            actionPath = Home.destinationCase + Home.Destination.Settings.case
        ),
        onDismiss = { store.send(Home.Action.DestinationDismissed) }
    ) {
        SettingsStoreView(store = it)
    }
    LoadingOverlay(isLoading = state.isLoading)

    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = Home.alertKey,
            actionPath = Home.alertCase
        )
    )
    // onAppear
    LaunchedEffect(Unit) {
        store.send(Home.Action.OnAppear)
    }
}

@Composable
fun HomeCard(
    title: String,
    onClick: () -> Unit,
    foregroundColor: Color,
    backgroundColor: Color,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick=onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = title,
                color = foregroundColor,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
                )
        }
    }
}