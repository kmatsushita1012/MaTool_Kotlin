package com.studiomk.matool.presentation.store_view.pub.map.root

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.studiomk.ktca.core.store.StoreOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.studiomk.matool.presentation.store_view.pub.map.location.PublicLocationStoreView
import com.studiomk.matool.presentation.store_view.pub.map.route.PublicRouteMapStoreView
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.House

@Composable
fun PublicMapStoreView(store: StoreOf<PublicMap.State, PublicMap.Action>){
    val state by store.state.collectAsState()

    CupertinoNavigationView(
        toolBar = {
            CupertinoToolBar(
                modifier = it,
                leading = { CupertinoToolbarLeadingButton(
                    icon = CupertinoIcons.Default.House,
                    tint = MaterialTheme.colorScheme.onBackground
                ){
                    store.send(PublicMap.Action.DismissTapped())
                } },
                center = { Text(
                    "地図",
                    fontWeight = FontWeight.Bold
                )}
            )
        }
    ) {
        Column {
            PickerMenu(
                items = state.tabItems,
                onSelected = { store.send(PublicMap.Action.TabSelected(it)) },
                selectedItem = state.selectedTab,
                itemLabel = { it.text }
            )

            store.optionalScope(
                statePath = PublicMap.destinationKey + PublicMap.Destination.Location.key,
                actionPath = PublicMap.destinationCase + PublicMap.Destination.Location.case
            )?.let {
                PublicLocationStoreView(
                    store = it
                )
            }
            store.optionalScope(
                statePath = PublicMap.destinationKey + PublicMap.Destination.Route.key,
                actionPath = PublicMap.destinationCase + PublicMap.Destination.Route.case
            )?.let {
                key(state.selectedTab){
                    PublicRouteMapStoreView(
                        store = it
                    )
                }
            }
        }
    }
}

@Composable
fun <T> PickerMenu(
    items: List<T>,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    selectedItem: T? = null,
    itemLabel: @Composable (T) -> String = { it.toString() }
) {
    LazyRow(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items.size) { index ->
            val item = items[index]
            val isSelected = item == selectedItem
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) Color.Blue else Color.LightGray)
                    .clickable { onSelected(item) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = itemLabel(item),
                    color = if (isSelected) Color.White else Color.Black
                )
            }
        }
    }
}
