package com.studiomk.matool.domain.entities.shared

import kotlinx.serialization.Serializable
import com.google.android.gms.maps.model.LatLng

@Serializable
data class Coordinate(
    val latitude: Double,
    val longitude: Double
){
    companion object
}

val Coordinate.Companion.sample: Coordinate
    get() = Coordinate(latitude = 34.777805, longitude = 138.007211)

val Coordinate.latLng: LatLng
    get() = LatLng(latitude, longitude)
