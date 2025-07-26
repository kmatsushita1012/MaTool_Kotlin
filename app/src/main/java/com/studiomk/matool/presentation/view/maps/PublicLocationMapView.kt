package com.studiomk.matool.presentation.view.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.studiomk.ktca.core.util.Binding
import com.studiomk.matool.R
import com.studiomk.matool.domain.entities.locations.PublicLocation
import com.studiomk.matool.presentation.utils.CoordinateRegion


@Composable
fun PublicLocationMapView(
    locations: List<PublicLocation>,
    region: Binding<CoordinateRegion?>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val mapStyle = remember {
        MapStyleOptions.loadRawResourceStyle(
            context,
            R.raw.exportable_map_style
        )
    }
    val mapProps by remember {
        mutableStateOf(
            MapProperties(mapStyleOptions = mapStyle)
        )
    }
    var cameraPositionState = rememberSyncedCameraState(region)
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = mapProps
    ) {

    }
}


