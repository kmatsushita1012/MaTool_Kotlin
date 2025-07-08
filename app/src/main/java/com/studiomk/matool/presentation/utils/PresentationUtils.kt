package com.studiomk.matool.presentation.utils

import com.studiomk.matool.domain.entities.shared.Coordinate
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds



// region系はComposeMap等の型に合わせて適宜調整してください
data class SimpleRegion(
    val center: Coordinate,
    val latitudeDelta: Double,
    val longitudeDelta: Double
){
    fun toLatLngBounds(): LatLngBounds {
        val halfLat = latitudeDelta / 2
        val halfLng = longitudeDelta / 2

        val southWest = LatLng(
            center.latitude - halfLat,
            center.longitude - halfLng
        )
        val northEast = LatLng(
            center.latitude + halfLat,
            center.longitude + halfLng
        )
        return LatLngBounds(southWest, northEast)
    }

    companion object {
        fun fromLatLngBounds(bounds: LatLngBounds): SimpleRegion {
            val centerLat = (bounds.northeast.latitude + bounds.southwest.latitude) / 2
            val centerLng = (bounds.northeast.longitude + bounds.southwest.longitude) / 2

            val latDelta = bounds.northeast.latitude - bounds.southwest.latitude
            val lngDelta = bounds.northeast.longitude - bounds.southwest.longitude

            return SimpleRegion(
                center = Coordinate(centerLat, centerLng),
                latitudeDelta = latDelta,
                longitudeDelta = lngDelta
            )
        }
    }
}

fun makeRegion(
    coordinates: List<Coordinate>,
    ratio: Double = 1.1,
    spanDelta: Double = 0.01
): SimpleRegion? {
    if (coordinates.isEmpty()) return null
    val minLat = coordinates.minOf { it.latitude }
    val maxLat = coordinates.maxOf { it.latitude }
    val minLon = coordinates.minOf { it.longitude }
    val maxLon = coordinates.maxOf { it.longitude }

    val center = Coordinate(
        latitude = (minLat + maxLat) / 2,
        longitude = (minLon + maxLon) / 2
    )
    val latitudeDelta = (maxLat - minLat) * ratio
    val longitudeDelta = (maxLon - minLon) * ratio

    val finalLatDelta = if (spanDelta > latitudeDelta) spanDelta else latitudeDelta
    val finalLonDelta = if (spanDelta > longitudeDelta) spanDelta else longitudeDelta

    return SimpleRegion(
        center = center,
        latitudeDelta = finalLatDelta,
        longitudeDelta = finalLonDelta
    )
}

fun makeRegion(origin: Coordinate?, spanDelta: Double): SimpleRegion? {
    return origin?.let {
        SimpleRegion(
            center = it,
            latitudeDelta = spanDelta,
            longitudeDelta = spanDelta
        )
    }
}


