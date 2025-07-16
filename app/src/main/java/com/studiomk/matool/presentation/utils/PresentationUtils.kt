package com.studiomk.matool.presentation.utils

import android.content.Context
import android.util.TypedValue

import com.google.android.gms.maps.model.*
/**
 * 指定した pt 値を px に変換して返す拡張関数
 *
 * @param ptSize 変換したいポイント値
 * @return       ピクセル値 (Float)
 */
fun Context.ptToPx(ptSize: Float): Float =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PT,
        ptSize,
        resources.displayMetrics
    )



/**
 * この Bounds を画像のアスペクト比に収まるよう
 * 最小限の拡張を行って返す。
 *
 * @param targetRatio 画像の width / height
 */
fun LatLngBounds.fitToAspect(targetRatio: Double): LatLngBounds {
    val south = southwest.latitude
    val north = northeast.latitude
    val west  = southwest.longitude
    val east  = northeast.longitude

    val latSpan = north - south
    val lngSpan = east - west

    // 現在のアスペクト比（幅 / 高さ）
    val currentRatio = lngSpan / latSpan

    // 拡張後の上下左右
    var newSouth = south
    var newNorth = north
    var newWest  = west
    var newEast  = east

    if (currentRatio < targetRatio) {
        // 縦長すぎる → 幅を広げる
        val newLngSpan = latSpan * targetRatio
        val extra = (newLngSpan - lngSpan) / 2
        newWest  -= extra
        newEast  += extra
    } else if (currentRatio > targetRatio) {
        // 横長すぎる → 高さを広げる
        val newLatSpan = lngSpan / targetRatio
        val extra = (newLatSpan - latSpan) / 2
        newSouth -= extra
        newNorth += extra
    }
    return LatLngBounds(
        LatLng(newSouth, newWest),
        LatLng(newNorth, newEast)
    )
}

