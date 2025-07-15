package com.studiomk.matool.presentation.view.maps

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.studiomk.matool.domain.entities.routes.Point
import com.studiomk.matool.domain.entities.routes.Segment
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.studiomk.matool.domain.entities.shared.latLng
import com.studiomk.matool.domain.entities.shared.text
import kotlin.text.buildString
import com.studiomk.matool.R


@Composable
fun AdminRouteExportMapView(
    points: List<Point>,
    segments: List<Segment>,
    cameraPositionState: CameraPositionState,
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
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = mapProps
    ) {
        points.forEachIndexed { i, point ->
            CupertinoMarker(
                coordinate = point.coordinate.latLng,
                label = buildString {
                    append("${i+1}")
                    point.title?.let { append(":$it") }
                    point.time?.text?.let { append(it) }
                },
            )
        }
        segments.forEach { segment ->
            Polyline(
                points = segment.coordinates.map { LatLng(it.latitude, it.longitude) },
                color = androidx.compose.ui.graphics.Color.Blue,
                width = 8f
            )
        }
    }
    
}


