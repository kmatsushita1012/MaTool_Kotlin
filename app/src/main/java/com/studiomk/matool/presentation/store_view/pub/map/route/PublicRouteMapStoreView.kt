package com.studiomk.matool.presentation.store_view.pub.map.route

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.ktca.core.util.Binding
import com.studiomk.ktca.ui.Sheet
import com.studiomk.matool.domain.entities.routes.Point
import com.studiomk.matool.domain.entities.routes.text
import com.studiomk.matool.domain.entities.shared.text
import com.studiomk.matool.presentation.view.items.ToggleOptionItem
import com.studiomk.matool.presentation.view.items.ToggleSelectedItem
import com.studiomk.matool.presentation.view.maps.PublicRouteMapView
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.others.TapOutsideOverlay
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.CupertinoIconButton
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.Xmark


@Composable
fun PublicRouteMapStoreView(store: StoreOf<PublicRouteMap.State, PublicRouteMap.Action>){
    val state by store.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PublicRouteMapView(
            points = state.points,
            segments = state.segments,
            location = state.location,
            region = Binding(
                { state.coordinateRegion },
                { }
            ),
            onPointTap = {
                store.send(PublicRouteMap.Action.PointSelected(it))
            }
        )
        TapOutsideOverlay(
            enable = state.isMenuPresented,
            onOutsideTap = {
                store.send(PublicRouteMap.Action.ToggleChanged(false))
            },
            contentAlignment = Alignment.TopCenter
        ) {
            ToggleMenu(
                items = state.items,
                selection = Binding(
                    { state.selectedItem },
                    { store.send(PublicRouteMap.Action.ItemSelected(it)) }
                ),
                isToggled = Binding(
                    { state.isMenuPresented },
                    { store.send(PublicRouteMap.Action.ToggleChanged(it)) }
                ),
                itemLabel = { it.text("m/d T") },
                modifier = Modifier.padding(all = 16.dp)
            )
        }


        state.selectedPoint?.let {
            TapOutsideOverlay(
                onOutsideTap = {
                    store.send(PublicRouteMap.Action.PointClosed)
                },
                contentAlignment = Alignment.BottomCenter
            ) {
                PointDetail(
                    item = it,
                    onDismiss = {
                        store.send(PublicRouteMap.Action.PointClosed)
                    }
                )
            }


        }
    }
}

@Composable
fun <T> ToggleMenu(
    items: List<T>,
    selection: Binding<T?>,
    isToggled: Binding<Boolean>,
    modifier: Modifier = Modifier,
    itemLabel: @Composable (T) -> String = { it.toString() }
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        val selectedItem = selection.value
        ToggleSelectedItem(
            title =  if(selectedItem!=null) itemLabel(selectedItem) else "",
            isExpanded = isToggled
        )
        if (isToggled.value) {
            items.forEach {
                ToggleOptionItem(
                    title = itemLabel(it)
                ) {
                    selection.set(it)
                }
            }
        }
    }
}


@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun PointDetail(
    item: Point,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.White)
            .fillMaxWidth()
            .height(ScreenWidth()*0.3f)
    ){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd,
        ){
            CupertinoIconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                CupertinoIcon(
                    imageVector = CupertinoIcons.Default.Xmark,
                    contentDescription = "閉じる"
                )
            }
        }


        // 中央にテキスト3行
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            item.title?.let{
                Text(text = it , style = MaterialTheme.typography.titleLarge)
            }
            item.time?.let{
                Text(text = it.text, style = MaterialTheme.typography.titleLarge)
            }
            item.description?.let{
                Text(text = it, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
@Composable
fun ScreenWidth(): Dp {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    return screenWidthDp
}
