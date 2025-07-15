package com.studiomk.matool.presentation.view.maps

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.studiomk.matool.domain.entities.routes.Point
import com.studiomk.matool.domain.entities.routes.Segment
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng


@Composable
fun AdminRouteExportMapView(
    points: List<Point>,
    segments: List<Segment>,
    cameraPositionState: CameraPositionState,
    modifier: Modifier = Modifier,

) {
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
    ) {
        // マーカー
        points.forEach { point ->
            Marker(
                state = MarkerState(
                    position = LatLng(point.coordinate.latitude, point.coordinate.longitude)
                ),
                title = point.title ?: "",
                snippet = point.description
            )
        }
        // ポリライン
        segments.forEach { segment ->
            Polyline(
                points = segment.coordinates.map { LatLng(it.latitude, it.longitude) },
                color = androidx.compose.ui.graphics.Color.Blue,
                width = 8f
            )
        }
    }
    
}


