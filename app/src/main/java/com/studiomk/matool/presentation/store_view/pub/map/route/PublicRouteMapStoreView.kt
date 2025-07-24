package com.studiomk.matool.presentation.store_view.pub.map.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.ktca.core.util.Binding
import com.studiomk.matool.presentation.view.items.ToggleOptionItem
import com.studiomk.matool.presentation.view.items.ToggleSelectedItem
import com.studiomk.matool.presentation.view.maps.PublicRouteMapView


@Composable
fun PublicRouteMapStoreView(store: StoreOf<PublicRouteMap.State, PublicRouteMap.Action>){
    val state by store.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        PublicRouteMapView(
            points = state.points,
            segments = state.segments,
            location = state.location,
            region = Binding(
                { state.coordinateRegion },
                { }
            )
        )
        ToggleMenu(
            items = state.items,
            selection = Binding(
                { state.selectedItem },
                { store.send(PublicRouteMap.Action.ItemSelected(it)) }
            ),
            isToggled = Binding(
                { state.isMenuPresented },
                { store.send(PublicRouteMap.Action.ToggleChanged(it)) }
            )
        )
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

