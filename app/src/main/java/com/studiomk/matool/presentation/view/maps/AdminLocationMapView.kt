package com.studiomk.matool.presentation.view.maps

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.studiomk.matool.domain.entities.locations.Location
import com.studiomk.matool.presentation.utils.CoordinateRegion
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import io.github.alexzhirkevich.cupertino.CupertinoActivityIndicator
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPermissionsApi::class, ExperimentalCupertinoApi::class)
@Composable
fun AdminLocationMap(
    location: Location?,
    modifier: Modifier = Modifier,
    onRegionChange: (CoordinateRegion?) -> Unit = {},
) {
    //TODO
    val defaultLocation = LatLng(35.681236, 139.767125)
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val hasPermission = permissionState.status.isGranted

    var isLoading by remember { mutableStateOf(true) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    // パーミッションリクエスト
    LaunchedEffect(Unit) {
        if (!hasPermission) permissionState.launchPermissionRequest()
    }

    // 現在地取得（初回 or パーミッション許可後）
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            try {
                val location = fusedLocationClient
                    .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .await()

                currentLocation = location?.let {
                    LatLng(it.latitude, it.longitude)
                }
            } catch (e: Exception) {
            }
        }
        isLoading = false // 成功・失敗問わず完了とみなす
    }

    if (isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CupertinoActivityIndicator()
        }
    } else {
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                currentLocation ?: defaultLocation,
                16f
            )
        }

        GoogleMap(
            modifier = modifier,
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasPermission
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true
            )
        )
    }
}