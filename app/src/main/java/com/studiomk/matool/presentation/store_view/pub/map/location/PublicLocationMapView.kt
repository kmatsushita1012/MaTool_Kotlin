package com.studiomk.matool.presentation.store_view.pub.map.location

import androidx.compose.runtime.*
import com.studiomk.ktca.core.store.StoreOf
import com.studiomk.ktca.core.util.Binding
import com.studiomk.matool.presentation.view.maps.PublicLocationMapView

@Composable
fun PublicLocationStoreView(store: StoreOf<PublicLocationMap.State, PublicLocationMap.Action>){
    val state by store.state.collectAsState()

    PublicLocationMapView(
        locations = state.locations,
        region = Binding(
            { state.coordinateRegion },
            { }
        )
    )
}