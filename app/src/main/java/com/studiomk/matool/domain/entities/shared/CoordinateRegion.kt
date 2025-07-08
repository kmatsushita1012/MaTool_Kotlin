package com.studiomk.matool.domain.entities.shared

data class CoordinateSpan(
    val latitudeDelta: Double,
    val longitudeDelta: Double
)

data class SimpleRegion(
    val center: Coordinate,
    val span: CoordinateSpan
) 