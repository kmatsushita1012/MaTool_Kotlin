package com.studiomk.matool.presentation.view.maps

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.studiomk.matool.core.binding.Binding
import com.studiomk.matool.domain.entities.shared.Coordinate
import com.studiomk.matool.presentation.utils.SimpleRegion
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.*

@Composable
fun AdminDistrictMapView(
    coordinates: List<Coordinate>?,
    isShownPolygon: Boolean,
    region: Binding<SimpleRegion?>,
    onMapLongPress: (Coordinate) -> Unit
) {
    // Region
    var cameraPositionState = rememberSyncedCameraState(region)

    GoogleMap(
        modifier = Modifier,
        cameraPositionState = cameraPositionState,
        onMapLongClick = { latLng ->
            onMapLongPress(Coordinate(latitude = latLng.latitude, longitude = latLng.longitude))
        }
    ) {
        coordinates?.forEach { coord ->
            Marker(
                state = MarkerState(position = LatLng(coord.latitude, coord.longitude))
            )
        }
        if (isShownPolygon && coordinates != null && coordinates.size >= 3) {
            Polygon(
                points = coordinates.map { LatLng(it.latitude, it.longitude) },
                fillColor = Color(0x883498DB), // ← Color(...) に変更
                strokeColor = Color(0xFF3498DB.toInt()),
                strokeWidth = 4f
            )
        }
    }
}