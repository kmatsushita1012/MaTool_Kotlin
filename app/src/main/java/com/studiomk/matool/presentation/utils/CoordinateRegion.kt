package com.studiomk.matool.presentation.utils

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.studiomk.matool.domain.entities.shared.Coordinate

data class CoordinateRegion(
    val center: Coordinate,
    val latitudeDelta: Double,
    val longitudeDelta: Double
){
    fun toLatLngBounds(): LatLngBounds {
        var latSpan = latitudeDelta
        var lonSpan = longitudeDelta

        val halfLat = latSpan / 2
        val halfLon = lonSpan / 2

        return LatLngBounds(
            LatLng(center.latitude - halfLat, center.longitude - halfLon),
            LatLng(center.latitude + halfLat, center.longitude + halfLon)
        )
    }

    companion object {
        fun fromLatLngBounds(bounds: LatLngBounds): CoordinateRegion {
            val northeast = bounds.northeast
            val southwest = bounds.southwest
            val latSpan = northeast.latitude  - southwest.latitude
            val lonSpan = northeast.longitude - southwest.longitude

            val centerLat = (northeast.latitude  + southwest.latitude)  / 2
            val centerLon = (northeast.longitude + southwest.longitude) / 2

            return CoordinateRegion(
                center           = Coordinate(centerLat, centerLon),
                latitudeDelta    = latSpan,
                longitudeDelta   = lonSpan
            )
        }
    }
}

fun makeRegion(
    coordinates: List<Coordinate>,
    paddingRatio: Double = 1.1,       // 余白倍率（今までのratioと同じ意味）
    aspectRatio: Double? = null        // 新しく追加。縦横比（横幅 ÷ 縦幅）
): CoordinateRegion? {
    if (coordinates.isEmpty()) return null

    val minLat = coordinates.minOf { it.latitude }
    val maxLat = coordinates.maxOf { it.latitude }
    val minLon = coordinates.minOf { it.longitude }
    val maxLon = coordinates.maxOf { it.longitude }

    val latSpan = maxLat - minLat
    val lonSpan = maxLon - minLon

    // 余白を考慮して拡大
    var latitudeDelta = latSpan * paddingRatio
    var longitudeDelta = lonSpan * paddingRatio

    // 最小値で補正
//    if (latitudeDelta < spanDelta) latitudeDelta = spanDelta
//    if (longitudeDelta < spanDelta) longitudeDelta = spanDelta

    val centerLat = (minLat + maxLat) / 2.0
    val centerLon = (minLon + maxLon) / 2.0

    // 縦横比調整
    if (aspectRatio != null) {
        val currentRatio = longitudeDelta / latitudeDelta
        if (currentRatio > aspectRatio) {
            // 横長 → 縦を広げる
            latitudeDelta = longitudeDelta / aspectRatio
        } else {
            // 縦長 → 横を広げる
            longitudeDelta = latitudeDelta * aspectRatio
        }
    }

    return CoordinateRegion(
        center = Coordinate(centerLat, centerLon),
        latitudeDelta = latitudeDelta,
        longitudeDelta = longitudeDelta
    )
}


fun makeRegion(origin: Coordinate?, spanDelta: Double): CoordinateRegion? {
    return origin?.let {
        CoordinateRegion(
            center = it,
            latitudeDelta = spanDelta,
            longitudeDelta = spanDelta
        )
    }
}