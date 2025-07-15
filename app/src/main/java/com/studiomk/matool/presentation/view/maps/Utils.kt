package com.studiomk.matool.presentation.view.maps

import android.util.Log
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
    var lastSynced by remember { mutableStateOf<CoordinateRegion?>(null) }

    // region が外部から更新されたときカメラを移動
    LaunchedEffect(region.get()) {
        val ext = region.get() ?: return@LaunchedEffect
        if (ext == lastSynced) return@LaunchedEffect       // ループ防止

        lastSynced = ext                                   // 先に記録
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngBounds(ext.toLatLngBounds(), 0)
        )

        cameraPositionState.projection?.visibleRegion?.latLngBounds?.let { vis ->

            val adjusted = CoordinateRegion.fromLatLngBounds(vis)
            Log.d("MapView", "vis: ${adjusted != lastSynced}")
            if (adjusted != lastSynced) {                  // 画面比率で補正された？
                lastSynced = adjusted
                region.set(adjusted)                       // ★ 送り返す ★
            }
        }
    }

    // ユーザーがカメラを動かしたら region を更新
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds ?: return@LaunchedEffect
            val currentRegion = CoordinateRegion.fromLatLngBounds(bounds)

            if (currentRegion != lastSynced) {
                lastSynced = currentRegion
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
                        modifier = Modifier
                            .height(24.dp)
                            .padding(top = 4.dp)
                    ) {
                        OutlinedText(
                            text = label,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            stroke = Stroke(width = 6f),
                            strokeColor = Color.White,
                        )
                    }
                }
            }
        }
    }
}
