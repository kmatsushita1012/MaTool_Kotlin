package com.studiomk.matool.presentation.view.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Polyline
import com.studiomk.ktca.core.util.Binding
import com.studiomk.matool.R
import com.studiomk.matool.domain.entities.locations.PublicLocation
import com.studiomk.matool.domain.entities.routes.Point
import com.studiomk.matool.domain.entities.routes.Segment
import com.studiomk.matool.domain.entities.shared.latLng
import com.studiomk.matool.domain.entities.shared.text
import com.studiomk.matool.presentation.utils.CoordinateRegion
import kotlin.collections.forEach

@Composable
fun PublicRouteMapView(
    points: List<Point>?,
    segments: List<Segment>?,
    location: PublicLocation?,
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
        points?.let {
                it.forEachIndexed { i, point ->
                CupertinoMarker(
                    coordinate = point.coordinate.latLng,
                    label = buildString {
                        point.title?.let { append(it) }
                    },
                )
            }
        }
        segments?.let {
            it.forEach { segment ->
                Polyline(
                    points = segment.coordinates.map { LatLng(it.latitude, it.longitude) },
                    color = androidx.compose.ui.graphics.Color.Blue,
                    width = 8f
                )
            }
        }
    }
}


