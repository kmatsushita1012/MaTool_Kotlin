package com.studiomk.matool.presentation.view.maps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.studiomk.ktca.core.util.Binding
import com.google.maps.android.compose.*
import com.studiomk.matool.domain.entities.routes.Point
import com.studiomk.matool.domain.entities.routes.Segment
import com.studiomk.matool.domain.entities.shared.Coordinate
import com.studiomk.matool.domain.entities.shared.latLng
import com.studiomk.matool.presentation.utils.CoordinateRegion
import com.google.android.gms.maps.model.LatLng
@Composable
fun AdminRouteMapView(
    points: List<Point>,
    segments: List<Segment>,
    onMapLongPress: (Coordinate) -> Unit,
    onPointTapped: (Point) -> Unit,
    onPolylineTapped: (Segment) -> Unit,
    region: Binding<CoordinateRegion?>,
) {
    var cameraPositionState = rememberSyncedCameraState(region)
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLongClick = { latLng ->
            onMapLongPress(Coordinate(latLng.latitude, latLng.longitude))
        },
    ) {
        // マーカー
        points.forEach { point ->
            CupertinoMarker(
                coordinate = point.coordinate.latLng,
                label = point.title , // 通常の title は InfoWindow が非カスタム時用
                onClick = {
                    onPointTapped(point)
                },
            )

        }
        // ポリライン
        segments.forEach { segment ->
            Polyline(
                points = segment.coordinates.map { LatLng(it.latitude, it.longitude) },
                color = Color.Blue,
                width = 8f,
                onClick = {
                    onPolylineTapped(segment)
                }
            )
        }
    }
}