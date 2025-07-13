package com.studiomk.matool.presentation.view.maps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.studiomk.ktca.core.util.Binding
import com.studiomk.matool.presentation.utils.CoordinateRegion
import com.studiomk.matool.presentation.view.others.OutlinedText
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.google.android.gms.maps.model.LatLng

@Composable
fun rememberSyncedCameraState(
    region: Binding<CoordinateRegion?>
): CameraPositionState {
    val cameraPositionState = rememberCameraPositionState()
    var lastSyncedRegion by remember { mutableStateOf<CoordinateRegion?>(null) }

    // region が外部から更新されたときカメラを移動
    LaunchedEffect(region.get()) {
        val newRegion = region.get()
        if (newRegion != null && newRegion != lastSyncedRegion) {
            val bounds = newRegion.toLatLngBounds()
            cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            lastSyncedRegion = newRegion
        }
    }

    // ユーザーがカメラを動かしたら region を更新
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds ?: return@LaunchedEffect
            val currentRegion = CoordinateRegion.fromLatLngBounds(bounds)

            if (currentRegion != lastSyncedRegion) {
                lastSyncedRegion = currentRegion
                region.set(currentRegion)
            }
        }
    }

    return cameraPositionState
}

@Composable
fun CupertinoMarker(
    coordinate: LatLng,
    label: String?,
    color: Color = Color.Red,
    onClick: () -> Unit = {}
) {
    key(label) {
        val markerState = rememberUpdatedMarkerState(position = coordinate)
        val hasLabel = label != null
        val anchor = if (hasLabel) Offset(0.5f, 2f/3f) else Offset(0.5f, 1.0f)
        MarkerComposable(
            state = markerState,
            anchor = anchor,
            onClick = {
                onClick()
                true
            }
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
                if (hasLabel) {
                    Box(
                        modifier = Modifier.height(24.dp).padding(top = 4.dp)
                    ) {
                        OutlinedText(
                            text = label,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            stroke = Stroke(width = 6f),
                            strokeColor = Color.White,
                        )
                    }
                }
            }
        }
    }
}
